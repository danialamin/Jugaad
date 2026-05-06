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
    private int stuckTicks = 0;
    private double stuckDx = 0;
    private double stuckDy = 0;
    private int teleportTick = 0;  // counts up to trigger corner teleport
    private int lastCorner = -1;   // last corner used, to avoid repeat

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

    /** Update AI: chase player or patrol. Obstacle avoidance via axis-split + wander. */
    public void updateAI(int playerX, int playerY, java.util.List<Furniture> allFurniture) {
        if (defeated || moveSpeed <= 0) return;

        double cx = worldX + width / 2.0;
        double cy = worldY + height / 2.0;
        double dist = Math.sqrt((playerX - cx) * (playerX - cx) + (playerY - cy) * (playerY - cy));

        // ── Boss corner teleport every 4 seconds (240 ticks at 60 fps) ────────────
        if ("final_boss".equals(name)) {
            teleportTick++;
            if (teleportTick >= 240) {
                teleportTick = 0;
                teleportToRandomCorner();
                stuckTicks = 0; // reset wander after teleport
            }
        }

        // ── Compute desired velocity ──────────────────────────────────────────────
        double vx, vy;

        if (stuckTicks > 0) {
            // Wander mode: ignore player, use stored direction
            stuckTicks--;
            vx = stuckDx;
            vy = stuckDy;
            isChasing = true;
        } else if (dist < detectionRadius) {
            // Chase player
            isChasing = true;
            double dx = playerX - cx;
            double dy = playerY - cy;
            double len = Math.sqrt(dx * dx + dy * dy);
            if (len < 1) len = 1;
            vx = moveSpeed * dx / len;
            vy = moveSpeed * dy / len;
        } else {
            // Patrol
            isChasing = false;
            double offset = Math.abs(name != null ? name.hashCode() : hashCode()) % 1000;
            double t = (System.currentTimeMillis() * 0.001) + offset;
            double targetX = originX + Math.sin(t * (0.5 + (offset % 5) * 0.1)) * 120;
            double targetY = originY + Math.cos(t * (0.3 + (offset % 4) * 0.1)) * 100;
            vx = (targetX - worldX) * 0.03;
            vy = (targetY - worldY) * 0.03;
        }

        // ── Try to apply velocity, with axis-split fallback ───────────────────────
        boolean movedX = tryMove(worldX + vx, worldY,         allFurniture);
        boolean movedY = tryMove(worldX,       worldY + vy,   allFurniture);
        boolean movedFull = false;
        if (!movedX && !movedY) {
            // Also try the full diagonal as a last chance
            movedFull = tryMove(worldX + vx, worldY + vy, allFurniture);
        }

        // ── If completely stuck while chasing, enter wander mode ──────────────────
        if (!movedX && !movedY && !movedFull && isChasing && stuckTicks <= 0) {
            stuckTicks = 30 + (int)(Math.random() * 30);
            double angle = Math.random() * Math.PI * 2;
            stuckDx = Math.cos(angle) * moveSpeed;
            stuckDy = Math.sin(angle) * moveSpeed;
        }

        // If wander direction itself is blocked, pick a new one
        if (stuckTicks > 0 && !movedX && !movedY && !movedFull) {
            double angle = Math.random() * Math.PI * 2;
            stuckDx = Math.cos(angle) * moveSpeed;
            stuckDy = Math.sin(angle) * moveSpeed;
        }

        // ── Sync integer coords and advance animation ─────────────────────────────
        this.x = (int)worldX;
        this.y = (int)worldY;
        solidArea.x = x;
        solidArea.y = y;

        animTick++;
        if (animTick >= ANIM_SPEED) {
            animTick = 0;
            BufferedImage[] frames = (walkFrames != null && walkFrames.length > 0) ? walkFrames : idleFrames;
            if (frames != null && frames.length > 0) animFrame = (animFrame + 1) % frames.length;
        }
    }

    /** Instantly move to a random screen corner (avoids repeating last corner). */
    private void teleportToRandomCorner() {
        // Corner padding: 2 tiles in from each edge
        int pad = 64; // pixels — stays safely inside the zombie-wall border
        // Estimate screen bounds from origin + typical screen size
        // We use a fixed screen assumption (768 x 576 for 24x18 tiles @ 32px)
        int sw = 768, sh = 576;
        int[][] corners = {
            {pad,          pad},
            {sw - pad - width,  pad},
            {pad,          sh - pad - height},
            {sw - pad - width,  sh - pad - height}
        };
        // Pick a corner that isn't the last one
        int pick;
        do { pick = (int)(Math.random() * 4); } while (pick == lastCorner);
        lastCorner = pick;

        worldX = corners[pick][0];
        worldY = corners[pick][1];
        x = (int)worldX;
        y = (int)worldY;
        if (solidArea != null) { solidArea.x = x; solidArea.y = y; }
    }

    /**
     * Attempt to move to (nx, ny). Returns true and updates position if no static obstacle
     * intersects the proposed bounds. Returns false and leaves position unchanged otherwise.
     */
    private boolean tryMove(double nx, double ny, java.util.List<Furniture> allFurniture) {
        java.awt.Rectangle proposed = new java.awt.Rectangle((int)nx, (int)ny, width, height);
        java.awt.Rectangle current  = new java.awt.Rectangle((int)worldX, (int)worldY, width, height);
        for (Furniture f : allFurniture) {
            if (f == this) continue;
            if (f.moveSpeed > 0) continue;
            if (f.name == null) continue;
            if (f.name.equals("checkpoint") || f.name.startsWith("cafe_")) continue;
            java.awt.Rectangle staticBounds = new java.awt.Rectangle(f.x, f.y, f.width, f.height);
            if (!proposed.intersects(staticBounds)) continue; // no collision with this one
            // Collision with this object. But if we are ALREADY overlapping it, allow the move
            // only if it reduces the overlap (i.e., we are escaping). Otherwise block.
            if (current.intersects(staticBounds)) {
                // Already overlapping — allow any move that reduces center distance
                double curDist = centerDist(current, staticBounds);
                double newDist = centerDist(proposed, staticBounds);
                if (newDist >= curDist) return false; // moving deeper in — block
                // Moving away — allow (escape)
            } else {
                return false; // clean collision — block
            }
        }
        worldX = nx;
        worldY = ny;
        return true;
    }

    private double centerDist(java.awt.Rectangle a, java.awt.Rectangle b) {
        double ax = a.x + a.width / 2.0,  ay = a.y + a.height / 2.0;
        double bx = b.x + b.width / 2.0,  by = b.y + b.height / 2.0;
        return Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
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
