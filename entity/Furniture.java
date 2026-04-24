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
        this.x = x;
        this.y = y;
        this.image = image;
        if (image != null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        } else {
            this.width = 32;
            this.height = 32;
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
