package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Furniture {
    public int x, y;
    public int width, height;
    public BufferedImage image;
    public Rectangle solidArea;

    public Furniture(int x, int y, BufferedImage image) {
        this(x, y, image, 1.0);
    }

    public Furniture(BufferedImage image, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = width;
        this.height = height;
        this.solidArea = new Rectangle(x, y, width, height);
    }

    public Furniture(int x, int y, BufferedImage image, double scale) {
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
        
        // Solid area for collision (matches the image size)
        this.solidArea = new Rectangle(x, y, width, height);
    }

    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, width, height, null);
        }
    }
}
