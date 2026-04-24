package engine;

import entity.Furniture;
import map.TileManager;
import map.ZoneType;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectManager {

    GamePanel gp;
    public List<Furniture> furnitureList = new ArrayList<>();
    
    private BufferedImage tableImg;
    private BufferedImage chairImg;
    private BufferedImage chairMirrorImg;

    public ObjectManager(GamePanel gp) {
        this.gp = gp;
        loadObjectImages();
    }

    private void loadObjectImages() {
        try {
            BufferedImage rawTable = ImageIO.read(new File("assets/table.png"));
            BufferedImage rawChair = ImageIO.read(new File("assets/chair.png"));
            BufferedImage rawChairMirror = ImageIO.read(new File("assets/chair mirror.png"));
            
            tableImg = makeTransparent(rawTable, Color.BLACK);
            chairImg = makeTransparent(rawChair, Color.BLACK);
            chairMirrorImg = makeTransparent(rawChairMirror, Color.BLACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCafeteriaObjects() {
        furnitureList.clear();
        
        // 12 tables in a 4x3 grid creating straight alleys
        int cols = 4;
        int rows = 3;
        
        int startX = 100;
        int gapX = 170;
        
        int startY = 120;
        int gapY = 130;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int tableX = startX + (col * gapX);
                int tableY = startY + (row * gapY);
                
                Furniture table = new Furniture(tableX, tableY, tableImg);
                furnitureList.add(table);
                
                int chairW = chairImg != null ? chairImg.getWidth() : 20;
                int chairH = chairImg != null ? chairImg.getHeight() : 20;

                // Left Chair (Standard)
                furnitureList.add(new Furniture(tableX - chairW - 5, tableY + 2, chairImg));
                
                // Right Chair (Mirrored)
                furnitureList.add(new Furniture(tableX + table.width + 5, tableY + 2, chairMirrorImg));
                
                // Top Chair (50% chance, standard)
                if (Math.random() > 0.5) {
                    furnitureList.add(new Furniture(tableX + (table.width / 2) - (chairW / 2), tableY - chairH - 5, chairImg));
                }
            }
        }
    }

    public void clearObjects() {
        furnitureList.clear();
    }

    public void draw(Graphics2D g2) {
        if (gp.currentZone == ZoneType.CAFETERIA) {
            for (Furniture f : furnitureList) {
                f.draw(g2);
            }
        }
    }

    private BufferedImage makeTransparent(BufferedImage img, Color color) {
        if (img == null) return null;
        BufferedImage dimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int targetRGB = color.getRGB();
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                int pixelRGB = img.getRGB(j, i);
                if (pixelRGB == targetRGB) {
                    dimg.setRGB(j, i, 0x00000000);
                } else {
                    dimg.setRGB(j, i, pixelRGB);
                }
            }
        }
        return dimg;
    }
}
