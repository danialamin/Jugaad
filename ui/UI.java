package ui;

import engine.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UI {

    GamePanel gp;
    Font arial_20, arial_14;
    BufferedImage barFrame;
    BufferedImage hpIcon, energyIcon, stressIcon, gpaIcon, karmaIcon;

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_20 = new Font("Arial", Font.BOLD, 20);
        arial_14 = new Font("Arial", Font.PLAIN, 14);
        loadImages();
    }

    private void loadImages() {
        hpIcon = safeLoad("assets/health-health.png");
        energyIcon = safeLoad("assets/energy icon.png");
        stressIcon = safeLoad("assets/stress.png");
        gpaIcon = safeLoad("assets/gpa icon.png");
        karmaIcon = safeLoad("assets/karma icon.png");
    }

    private BufferedImage safeLoad(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return ImageIO.read(file);
            } else {
                System.out.println("Warning: Asset not found: " + path);
            }
        } catch (IOException e) {
            System.err.println("Error loading " + path + ": " + e.getMessage());
        }
        return null;
    }

    public void draw(Graphics2D g2) {
        // Pushing further right to minimize empty space
        int x = gp.screenWidth - 110; 
        int y = 15;
        int verticalGap = 25; // Increased from 20

        // Draw Stats
        drawStatBar(g2, x, y, hpIcon, gp.player.getHp(), gp.player.getMaxHp(), new Color(200, 0, 0));
        y += verticalGap;
        drawStatBar(g2, x, y, energyIcon, gp.player.getEnergy(), 100, new Color(0, 150, 255)); 
        y += verticalGap;
        drawStatBar(g2, x, y, stressIcon, gp.player.getStress(), 100, new Color(150, 0, 150)); 
        y += verticalGap;
        drawStatBar(g2, x, y, gpaIcon, (int)(gp.player.getGpa() * 25), 100, new Color(255, 215, 0)); 
        y += verticalGap;
        drawStatBar(g2, x, y, karmaIcon, gp.player.getKarma(), 100, new Color(255, 255, 255)); 
    }

    private void drawStatBar(Graphics2D g2, int x, int y, BufferedImage icon, int current, int max, Color barColor) {
        int barWidth = 100; 
        int barHeight = 6; // Halved from 12
        int iconSize = 20;

        // 1. Draw Icon
        if (icon != null) {
            g2.drawImage(icon, x - 30, y - 7, iconSize, iconSize, null);
        } else {
            g2.setColor(barColor);
            g2.fillOval(x - 28, y - 2, 12, 12); 
        }

        // 2. Draw Bar Background
        g2.setColor(new Color(50, 50, 50, 200)); 
        g2.fillRect(x, y, barWidth, barHeight);

        // 3. Draw Bar Fill
        double oneUnit = (double)barWidth / max; 
        int fillWidth = (int)(oneUnit * current);

        g2.setColor(barColor);
        g2.fillRect(x, y, fillWidth, barHeight);

        // 4. Draw thin border
        g2.setColor(Color.white);
        g2.drawRect(x, y, barWidth, barHeight);
    }
}
