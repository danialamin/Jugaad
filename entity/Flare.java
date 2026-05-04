package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Flare {
    public float x, y;
    public float vx, vy;
    public boolean dead = false;
    public int size = 16;

    public Flare(float x, float y, float vx, float vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(255, 100, 0, 150)); // Outer glow
        g2.fillOval((int)x - 4, (int)y - 4, size + 8, size + 8);
        g2.setColor(new Color(255, 50, 0)); // Core
        g2.fillOval((int)x, (int)y, size, size);
        g2.setColor(Color.YELLOW); // Inner hot core
        g2.fillOval((int)x + size/4, (int)y + size/4, size/2, size/2);
    }

    public Rectangle2D.Float getBounds() {
        return new Rectangle2D.Float(x, y, size, size);
    }
}
