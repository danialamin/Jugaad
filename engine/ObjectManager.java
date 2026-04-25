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
    
    // Library Objects
    private BufferedImage libDeskImg;
    private BufferedImage libBookshelfImg;
    private BufferedImage libGlobeImg;
    private BufferedImage libClockImg;
    private BufferedImage libTableImg;
    
    // Micro Objects
    private BufferedImage bookStack1Img;
    private BufferedImage bookStack2Img;
    private BufferedImage bookRedImg;
    private BufferedImage bookBlueImg;
    private BufferedImage libChestImg;

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
            
            File libFile = new File("assets/LibraryCompeteSet.png");
            if (libFile.exists()) {
                BufferedImage libSheet = ImageIO.read(libFile);
                
                // --- EXACT CROPS ---
                try { libDeskImg = makeTransparent(libSheet.getSubimage(541, 229, 191, 107), Color.BLACK); } catch (Exception e) {}
                try { libBookshelfImg = makeTransparent(libSheet.getSubimage(28, 384, 88, 120), Color.BLACK); } catch (Exception e) {}
                try { libGlobeImg = makeTransparent(libSheet.getSubimage(146, 228, 92, 108), Color.BLACK); } catch (Exception e) {}
                try { libClockImg = makeTransparent(libSheet.getSubimage(79, 24, 58, 120), Color.BLACK); } catch (Exception e) {}
                try { libTableImg = makeTransparent(libSheet.getSubimage(362, 216, 164, 96), Color.BLACK); } catch (Exception e) {}
                
                try { bookStack1Img = makeTransparent(libSheet.getSubimage(240, 96, 30, 48), Color.BLACK); } catch (Exception e) {}
                try { bookStack2Img = makeTransparent(libSheet.getSubimage(288, 115, 48, 29), Color.BLACK); } catch (Exception e) {}
                try { bookRedImg = makeTransparent(libSheet.getSubimage(240, 168, 9, 24), Color.BLACK); } catch (Exception e) {}
                try { bookBlueImg = makeTransparent(libSheet.getSubimage(264, 168, 9, 24), Color.BLACK); } catch (Exception e) {}
                try { libChestImg = makeTransparent(libSheet.getSubimage(168, 84, 48, 36), Color.BLACK); } catch (Exception e) {}
            }
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

    public void loadLibraryObjects() {
        furnitureList.clear();
        
        int tileSize = gp.tileSize; // 32
        double scale = 0.7;

        // Back Wall Bookshelves
        for (int i = 1; i < gp.maxScreenCol - 2; i += 3) {
            if (i != 10 && i != 13) { // Skip the center area to make room for the clock
                if (libBookshelfImg != null) {
                    int bx = i * tileSize;
                    int by = tileSize;
                    furnitureList.add(new Furniture(bx, by, libBookshelfImg, scale));
                    
                    // Add books to shelves!
                    int shelf1Y = by + (int)(32 * scale);
                    int shelf2Y = by + (int)(68 * scale);
                    int shelf3Y = by + (int)(103 * scale);
                    
                    // Randomly place books
                    if (Math.random() > 0.4 && bookStack1Img != null) {
                        furnitureList.add(new Furniture(bx + 5, shelf1Y - (int)(48 * scale), bookStack1Img, scale));
                    }
                    if (Math.random() > 0.3 && bookStack2Img != null) {
                        furnitureList.add(new Furniture(bx + 15, shelf2Y - (int)(29 * scale), bookStack2Img, scale));
                    }
                    if (Math.random() > 0.2 && bookRedImg != null) {
                        furnitureList.add(new Furniture(bx + 30, shelf3Y - (int)(24 * scale), bookRedImg, scale));
                        if (bookBlueImg != null) {
                            furnitureList.add(new Furniture(bx + (int)(40 * scale), shelf3Y - (int)(24 * scale), bookBlueImg, scale));
                        }
                    }
                }
            }
        }

        // Grandfather Clock in center back
        if (libClockImg != null) {
            furnitureList.add(new Furniture((gp.maxScreenCol / 2) * tileSize - (tileSize/2), tileSize, libClockImg, scale));
        }

        // Center Desk
        if (libDeskImg != null) {
            furnitureList.add(new Furniture((gp.maxScreenCol / 2) * tileSize - (int)(95 * scale), gp.maxScreenRow / 2 * tileSize - 20, libDeskImg, scale));
        }

        // Globe at bottom left
        if (libGlobeImg != null) {
            furnitureList.add(new Furniture(3 * tileSize, (gp.maxScreenRow - 4) * tileSize, libGlobeImg, scale));
        }

        // Tables at bottom right
        if (libTableImg != null) {
            int tableX = (gp.maxScreenCol - 6) * tileSize;
            int tableY = (gp.maxScreenRow - 4) * tileSize;
            furnitureList.add(new Furniture(tableX, tableY, libTableImg, scale));
        }
        
        // Add a chest somewhere
        if (libChestImg != null) {
            furnitureList.add(new Furniture((gp.maxScreenCol - 4) * tileSize, 2 * tileSize, libChestImg, scale));
        }
    }

    public void clearObjects() {
        furnitureList.clear();
    }

    public void draw(Graphics2D g2) {
        if (gp.currentZone == ZoneType.CAFETERIA || gp.currentZone == ZoneType.LIBRARY) {
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
