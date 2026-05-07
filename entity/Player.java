package entity;

import engine.GamePanel;
import engine.KeyHandler;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

    public GamePanel gp;
    public KeyHandler keyH;

    public void setEngineComponents(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        getPlayerImage(); // Reload images with new gp tileSize if needed
    }

    public int xLocation, yLocation;
    public int speed;
    public boolean isImmortal = false;

    private String name = "Student";
    private int hp;
    private int maxHp;
    private int spawnZoneId;
    private int currentZoneId;
    
    private entity.PlayerStats stats;
    private inventory.Inventory inventory;
    private activity.Phone phone;
    private entity.Position position;

    public BufferedImage[][] walkFrames;
    public String direction;
    /** Seated at classroom desk — always renders front-facing (down) sprite. */
    private boolean seatedInClass;
    public int spriteCounter = 0;
    public int spriteNum = 0; // The current frame
    public boolean collisionOn = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public Player() {
        this.stats = new entity.PlayerStats();
        this.inventory = new inventory.Inventory();
        this.phone = new activity.Phone();
        this.position = new entity.Position(0, 0);
        setDefaultValues();
    }

    public Player(String name, int spawnZoneId) {
        this();
        this.name = name;
        this.spawnZoneId = spawnZoneId;
        this.currentZoneId = spawnZoneId;
    }

    public void setDefaultValues() {
        xLocation = 384; // Center of screen
        yLocation = 350; // Safely below the bookshelves
        if (this.position == null) this.position = new entity.Position(xLocation, yLocation);
        else { this.position.setX(xLocation); this.position.setY(yLocation); }
        
        speed = 4;
        direction = "down";
        maxHp = 50;
        hp = maxHp;
        
        if (this.stats == null) this.stats = new entity.PlayerStats();
    }

    public void takeDamage(int amount) {
        if (isImmortal) return;
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public void heal(int amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void respawn() {
        hp = maxHp;
    }

    public entity.PlayerStats getStats() { return stats; }
    public inventory.Inventory getInventory() { return inventory; }
    public activity.Phone getPhone() { return phone; }
    public entity.Position getPosition() { return position; }
    public void moveTo(float x, float y) {
        this.position.setX(x);
        this.position.setY(y);
        this.xLocation = (int) x;
        this.yLocation = (int) y;
    }
    public int getCurrentZoneId() { return currentZoneId; }
    public void setCurrentZoneId(int zoneId) { this.currentZoneId = zoneId; }

    public void getPlayerImage() {
        try {
            File walkFile = new File("assets/player_walk.png");
            if (walkFile.exists()) {
                BufferedImage walkSheet = ImageIO.read(walkFile);
                // The loaded sheet is 4 rows by 6 columns
                int frameWidth = walkSheet.getWidth() / 6;
                int frameHeight = walkSheet.getHeight() / 4;

                walkFrames = new BufferedImage[4][6];

                // Typical layouts: Row 0=Down, Row 1=Left/Right, etc. For now we will map
                // blindly.
                // Assuming standard RPG Maker order: 0=Down, 1=Left, 2=Right, 3=Up
                for (int row = 0; row < 4; row++) {
                    for (int col = 0; col < 6; col++) {
                        walkFrames[row][col] = walkSheet.getSubimage(col * frameWidth, row * frameHeight, frameWidth,
                                frameHeight);
                    }
                }
            } else {
                System.out.println("Could not find player_walk.png! Check assets folder.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSeatedInClass() {
        return seatedInClass;
    }

    public void setSeatedInClass(boolean seated) {
        this.seatedInClass = seated;
        if (seated) {
            direction = "down";
            spriteNum = 0;
            spriteCounter = 0;
        }
    }

    public void update() {
        boolean isMoving = false;

        if (!seatedInClass && (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed)) {
            if (keyH.upPressed)
                direction = "up";
            else if (keyH.downPressed)
                direction = "down";
            else if (keyH.leftPressed)
                direction = "left";
            else if (keyH.rightPressed)
                direction = "right";

            // CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObjectCollision(this); // New object collision check

            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if (!collisionOn) {
                switch (direction) {
                    case "up":
                        yLocation -= speed;
                        break;
                    case "down":
                        yLocation += speed;
                        break;
                    case "left":
                        xLocation -= speed;
                        break;
                    case "right":
                        xLocation += speed;
                        break;
                }
                position.setX(xLocation);
                position.setY(yLocation);
            }

            isMoving = true;
        }

        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 8) { // Frame swap interval
                spriteNum++;
                if (spriteNum >= 6) { // 6 frames total per row
                    spriteNum = 0;
                }
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0; // idle frame is usually column 0
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (walkFrames != null) {
            int row = 0;
            if (seatedInClass) {
                row = 0;
            } else switch (direction) {
                case "down":
                    row = 0;
                    break;
                case "left":
                    row = 1;
                    break;
                case "right":
                    row = 2;
                    break;
                case "up":
                    row = 3;
                    break;
                default:
                    row = 0;
            }
            // Add safe bounds for spriteNum just in case
            if (spriteNum >= 6)
                spriteNum = 0;
            image = walkFrames[row][spriteNum];
        }

        if (image != null) {
            int drawSize = (int)(gp.tileSize * 2.0);
            int drawX = xLocation - (drawSize - gp.tileSize) / 2;
            int drawY = yLocation - (drawSize - gp.tileSize); // anchor at feet
            g2.drawImage(image, drawX, drawY, drawSize, drawSize, null);
        } else {
            // Placeholder rect if image failed to load
            g2.setColor(java.awt.Color.magenta);
            g2.fillRect(xLocation, yLocation, gp.tileSize, gp.tileSize);
        }
    }

    // -- Accessors for GamePanel compatibility
    public int getXLocation() { return xLocation; }
    public void setXLocation(int xLocation) { 
        this.xLocation = xLocation; 
        if (position != null) position.setX(xLocation);
    }

    public int getYLocation() { return yLocation; }
    public void setYLocation(int yLocation) { 
        this.yLocation = yLocation; 
        if (position != null) position.setY(yLocation);
    }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    
    // Fallback for UI if it still expects getters directly from Player
    public double getGpa() { return stats != null ? stats.getGPA() : 4.0; }
    public int getEnergy() { return 0; }
    public int getStress() { return stats != null ? stats.getStress() : 0; }
    public int getKarma() { return stats != null ? stats.getKarma() : 50; }
    public int getId() { return 1; }
    public void setId(int id) {}
}
