package entity;

import engine.GamePanel;
import engine.KeyHandler;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

    GamePanel gp;
    KeyHandler keyH;

    public int xLocation, yLocation;
    public int speed;

    // Additional original database traits
    private int id;
    private double gpa;
    private int energy;
    private int stress;
    private int karma;
    private int hp;
    private int maxHp;

    public BufferedImage[][] walkFrames;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 0; // The current frame
    public boolean collisionOn = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    // Keep constructors for Database Compatibility
    public Player() {
    }

    public Player(int id, double gpa, int energy, int stress, int karma, int xLocation, int yLocation) {
        this.id = id;
        this.gpa = gpa;
        this.energy = energy;
        this.stress = stress;
        this.karma = karma;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    public void setDefaultValues() {
        xLocation = 384; // Center of screen
        yLocation = 350; // Safely below the bookshelves
        speed = 4;
        direction = "down";
        maxHp = 100;
        hp = maxHp;
        energy = 100;
        stress = 0;
        gpa = 4.0;
        karma = 50;
    }

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

    public void update() {
        boolean isMoving = false;

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
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
            switch (direction) {
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

    // -- Database Getters and Setters omitted for brevity but remain functional!
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getXLocation() {
        return xLocation;
    }

    public void setXLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getYLocation() {
        return yLocation;
    }

    public void setYLocation(int yLocation) {
        this.yLocation = yLocation;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
}
