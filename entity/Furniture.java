package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Furniture {
    public double worldX, worldY; // Use double to avoid truncation jitter
    public int x, y; // Keep these for legacy integer access if needed (will sync with worldX/Y)
    public int width, height;
    public BufferedImage image;
    public Rectangle solidArea;
    public String name = "";

    /** Optional color for placeholder boxes (null = use image). */
    public java.awt.Color placeholderColor = null;

    // ─── Zombie Chasing AI ───
    public double originX, originY;    // Patrol home position
    public float moveSpeed = 0;        // 0 = static, >0 = chases player
    public int detectionRadius = 0;    // Pixel radius for detecting player
    public boolean isChasing = false;
    public boolean defeated = false;   // If true, don't draw or update

    // ─── Animation Support ───
    public BufferedImage[] idleFrames;   // frames for idle/patrol
    public BufferedImage[] walkFrames;   // frames for chasing
    private int animFrame = 0;
    private int animTick = 0;
    private static final int ANIM_SPEED = 8; // game ticks per frame advance

    public Furniture(int x, int y, BufferedImage image) {
        this(x, y, image, 1.0);
    }

    public Furniture(BufferedImage image, int x, int y, int width, int height) {
        this.worldX = x;
        this.worldY = y;
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = width;
        this.height = height;
        this.solidArea = new Rectangle(x, y, width, height);
        this.originX = x;
        this.originY = y;
    }

    public Furniture(int x, int y, BufferedImage image, double scale) {
        this.worldX = x;
        this.worldY = y;
        this.x = x;
        this.y = y;
        this.image = image;
        if (image != null) {
            this.width = (int)(image.getWidth() * scale);
            this.height = (int)(image.getHeight() * scale);
        } else {
            this.width = (int)(32 * scale);
            this.height = (int)(32 * scale);
        }
        this.solidArea = new Rectangle(x, y, width, height);
        this.originX = x;
        this.originY = y;
    }

    /** Update AI: chase player or return to patrol point. */
    public void updateAI(int playerX, int playerY) {
        if (defeated || moveSpeed <= 0) return;

        double cx = worldX + width / 2.0;
        double cy = worldY + height / 2.0;
        double dist = Math.sqrt((playerX - cx) * (playerX - cx) + (playerY - cy) * (playerY - cy));

        if (dist < detectionRadius) {
            isChasing = true;
            // Move toward player
            double dx = playerX - cx;
            double dy = playerY - cy;
            double len = Math.sqrt(dx * dx + dy * dy);
            if (len > 2) { 
                worldX += (moveSpeed * dx / len);
                worldY += (moveSpeed * dy / len);
            }
        } else {
            isChasing = false;
            // Patrol around origin
            double t = System.currentTimeMillis() * 0.001;
            double targetX = originX + (Math.sin(t * 0.8) * 80);
            double targetY = originY + (Math.cos(t * 0.5) * 80);
            
            double dx = targetX - worldX;
            double dy = targetY - worldY;
            // Smooth interpolation towards the patrol point
            worldX += (dx * 0.03);
            worldY += (dy * 0.03);
        }

        // Advance animation
        animTick++;
        if (animTick >= ANIM_SPEED) {
            animTick = 0;
            BufferedImage[] frames = (moveSpeed > 0 && walkFrames != null && walkFrames.length > 0) ? walkFrames : idleFrames;
            if (frames != null && frames.length > 0) {
                animFrame = (animFrame + 1) % frames.length;
            }
        }

        // Sync integer coordinates for drawing and collision
        this.x = (int)worldX;
        this.y = (int)worldY;
        solidArea.x = x;
        solidArea.y = y;
    }

    /** Check if player is touching this furniture. */
    public boolean isTouchingPlayer(int playerX, int playerY, int playerW, int playerH) {
        return x < playerX + playerW && x + width > playerX
            && y < playerY + playerH && y + height > playerY;
    }

    public void draw(Graphics2D g2) {
        if (defeated) return;

        // Animated sprite rendering — use walk anim for any moving entity (patrol or chase)
        boolean useWalk = moveSpeed > 0 && walkFrames != null && walkFrames.length > 0;
        BufferedImage[] activeFrames = useWalk ? walkFrames : idleFrames;
        if (activeFrames != null && activeFrames.length > 0) {
            BufferedImage frame = activeFrames[Math.abs(animFrame) % activeFrames.length];
            g2.drawImage(frame, x, y, width, height, null);
            if (isChasing) {
                g2.setColor(java.awt.Color.RED);
                g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 18));
                g2.drawString("!", x + width / 2 - 4, y - 5);
            }
            return;
        }

        if (image != null) {
            g2.drawImage(image, x, y, width, height, null);
        } else if (placeholderColor != null) {
            // Draw colored placeholder box
            java.awt.Color drawColor = placeholderColor;
            if (isChasing) {
                // Pulse red when chasing
                int pulse = (int)(Math.sin(System.currentTimeMillis() * 0.01) * 40 + 40);
                drawColor = new java.awt.Color(
                    Math.min(255, drawColor.getRed() + pulse),
                    Math.max(0, drawColor.getGreen() - pulse / 2),
                    Math.max(0, drawColor.getBlue() - pulse / 2),
                    drawColor.getAlpha()
                );
            }
            g2.setColor(drawColor);
            g2.fillRoundRect(x, y, width, height, 10, 10);
            g2.setColor(new java.awt.Color(0, 0, 0, 120));
            g2.drawRoundRect(x, y, width, height, 10, 10);

            // Draw name label
            if (name != null && !name.isEmpty()) {
                g2.setColor(java.awt.Color.WHITE);
                g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 10));
                String label = name.startsWith("zombie_") ? name.substring(7) : name;
                label = label.replace("_", " ").toUpperCase();
                int textW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, x + (width - textW) / 2, y + height / 2 + 4);
            }

            // Draw "!" when chasing
            if (isChasing) {
                g2.setColor(java.awt.Color.RED);
                g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 18));
                g2.drawString("!", x + width / 2 - 4, y - 5);
            }
        }
    }
}
