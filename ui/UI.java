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

    // CAFE MENU
    public int commandNum = 0;
    private BufferedImage[] foodIcons;
    private String[] foodNames = {
        "Biryani (Healthy)", 
        "Fruit Shake (Healthy)", 
        "Fruit Chat (Healthy)", 
        "Samosa (Unhealthy)", 
        "Soda (Unhealthy)", 
        "Lays (Unhealthy)", 
        "Fries (Unhealthy)"
    };
    // Cooldown to prevent rapid key scrolling
    private int keyCooldown = 0;

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

        foodIcons = new BufferedImage[7];
        foodIcons[0] = safeLoad("assets/foods/biryani.png");
        foodIcons[1] = safeLoad("assets/foods/fruit_shake.png");
        foodIcons[2] = safeLoad("assets/foods/fruit_chat.png");
        foodIcons[3] = safeLoad("assets/foods/samosa.png");
        foodIcons[4] = safeLoad("assets/foods/soda.png");
        foodIcons[5] = safeLoad("assets/foods/lays.png");
        foodIcons[6] = safeLoad("assets/foods/fries.png");
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

        if (gp.gameState == gp.cafeMenuState) {
            drawCafeMenu(g2);
        }
    }

    public void updateCafeMenu() {
        if (keyCooldown > 0) {
            keyCooldown--;
            return;
        }

        if (gp.keyH.upPressed) {
            commandNum--;
            if (commandNum < 0) commandNum = foodNames.length - 1;
            keyCooldown = 10;
        }
        if (gp.keyH.downPressed) {
            commandNum++;
            if (commandNum >= foodNames.length) commandNum = 0;
            keyCooldown = 10;
        }
        if (gp.keyH.escapePressed) {
            gp.gameState = gp.playState;
            keyCooldown = 10;
        }
        if (gp.keyH.enterPressed) {
            consumeFood(commandNum);
            gp.gameState = gp.playState;
            keyCooldown = 10;
        }
    }

    private void consumeFood(int index) {
        // 0: Biryani, 1: Fruit Shake, 2: Fruit Chat -> HEALTHY
        // 3: Samosa, 4: Soda, 5: Lays, 6: Fries -> UNHEALTHY
        
        boolean isHealthy = (index <= 2);

        if (isHealthy) {
            gp.player.setHp(Math.min(gp.player.getMaxHp(), gp.player.getHp() + 20));
            gp.player.setEnergy(Math.min(100, gp.player.getEnergy() + 20));
            gp.player.setStress(Math.max(0, gp.player.getStress() - 10));
            System.out.println("Ate " + foodNames[index] + " - Healthy! Stats improved.");
        } else {
            gp.player.setHp(Math.min(gp.player.getMaxHp(), gp.player.getHp() + 10)); // Half HP
            gp.player.setEnergy(Math.max(0, gp.player.getEnergy() - 10)); // Decrease Energy
            System.out.println("Ate " + foodNames[index] + " - Unhealthy! Energy dropped.");
        }
    }

    private void drawCafeMenu(Graphics2D g2) {
        int frameX = gp.screenWidth / 2 - 150;
        int frameY = gp.screenHeight / 2 - 200;
        int frameWidth = 300;
        int frameHeight = 400;

        // Draw Menu Background
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);
        g2.setColor(Color.white);
        g2.setStroke(new java.awt.BasicStroke(3));
        g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);

        // Title
        g2.setFont(arial_20);
        String title = "CAFE MENU";
        int titleX = getXForCenteredText(g2, title, frameX, frameWidth);
        g2.drawString(title, titleX, frameY + 35);
        g2.drawLine(frameX + 20, frameY + 45, frameX + frameWidth - 20, frameY + 45);

        // Options
        g2.setFont(arial_14);
        int textX = frameX + 70;
        int textY = frameY + 80;
        int lineHeight = 40;

        for (int i = 0; i < foodNames.length; i++) {
            // Draw Icon
            if (foodIcons[i] != null) {
                g2.drawImage(foodIcons[i], frameX + 20, textY - 24, 32, 32, null);
            }
            
            // Draw Text
            g2.drawString(foodNames[i], textX, textY);
            
            // Draw Cursor
            if (commandNum == i) {
                g2.drawString(">", frameX + 10, textY);
            }
            
            textY += lineHeight;
        }

        g2.drawString("Press ENTER to buy, ESC to close.", frameX + 20, frameY + frameHeight - 20);
    }

    private int getXForCenteredText(Graphics2D g2, String text, int frameX, int frameWidth) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return frameX + (frameWidth / 2) - (length / 2);
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
