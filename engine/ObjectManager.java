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
import java.util.concurrent.CopyOnWriteArrayList;

public class ObjectManager {

    GamePanel gp;
    public List<Furniture> furnitureList = new CopyOnWriteArrayList<>();
    
    private BufferedImage tableImg;
    private BufferedImage chairImg;
    private BufferedImage chairMirrorImg;
    
    // Library Objects
    private BufferedImage libDeskImg;
    private BufferedImage libBookshelfImg;
    private BufferedImage libGlobeImg;
    private BufferedImage libClockImg;
    private BufferedImage libTableImg;
    private BufferedImage libMainTableImg;
    
    // Micro Objects
    private BufferedImage bookStack1Img;
    private BufferedImage bookStack2Img;
    private BufferedImage bookRedImg;
    private BufferedImage bookBlueImg;
    private BufferedImage libChestImg;

    // Classroom Objects
    private BufferedImage studentDeskImg;
    private BufferedImage emptyStudentDeskImg;
    private BufferedImage teacherDeskImg;

    // Prayer Room Objects
    private BufferedImage prayerMatImg;
    private BufferedImage prayerMatPersonImg;
    private BufferedImage shoeRackImg;

    // Server Room & AI Lab Objects
    private BufferedImage aiDeskImg;
    private BufferedImage aiDeskPersonImg;
    private BufferedImage serverImg;
    private BufferedImage cableCrapImg;

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

            // Classroom and Library Tables (Cropped perfectly to their non-transparent bounding boxes)
            try { 
                BufferedImage temp = ImageIO.read(new File("assets/classrooom/StudentTablechair.png"));
                studentDeskImg = makeTransparent(temp.getSubimage(1020, 295, 776, 1011), Color.BLACK); 
            } catch (Exception e) {}
            try { 
                BufferedImage temp = ImageIO.read(new File("assets/classrooom/EmptyStudentChairtable.png"));
                emptyStudentDeskImg = makeTransparent(temp.getSubimage(379, 101, 765, 1183), Color.BLACK); 
            } catch (Exception e) {}
            try { 
                BufferedImage temp = ImageIO.read(new File("assets/classrooom/TeacherDesk.png"));
                teacherDeskImg = makeTransparent(temp.getSubimage(353, 235, 823, 491), Color.BLACK); 
            } catch (Exception e) {}
            try {
                BufferedImage temp = ImageIO.read(new File("assets/LibraryTable.png"));
                libMainTableImg = makeTransparent(temp.getSubimage(753, 111, 1312, 1316), Color.BLACK);
            } catch (Exception e) {}

        } catch (IOException e) {
            e.printStackTrace();
        }

        // --- NEW ASSETS (Prayer Room & Server Room) ---
        // User has already cropped these PNGs manually, so load them directly.
        try {
            prayerMatPersonImg = cropImage(ImageIO.read(new File("assets/PrayerRoom/Matwithperson.png")), 0, 0, -1, -1);
            System.out.println("Loaded Matwithperson.png: " + prayerMatPersonImg.getWidth() + "x" + prayerMatPersonImg.getHeight());
        } catch (Exception e) { System.err.println("Failed to load Matwithperson: " + e.getMessage()); }
        try {
            prayerMatImg = cropImage(ImageIO.read(new File("assets/PrayerRoom/pryaerMat.png")), 0, 0, -1, -1);
            System.out.println("Loaded pryaerMat.png: " + prayerMatImg.getWidth() + "x" + prayerMatImg.getHeight());
        } catch (Exception e) { System.err.println("Failed to load pryaerMat: " + e.getMessage()); }
        try {
            shoeRackImg = cropImage(ImageIO.read(new File("assets/PrayerRoom/ShoeRack.png")), 0, 0, -1, -1);
            System.out.println("Loaded ShoeRack.png: " + shoeRackImg.getWidth() + "x" + shoeRackImg.getHeight());
        } catch (Exception e) { System.err.println("Failed to load ShoeRack: " + e.getMessage()); }

        try {
            aiDeskImg = cropImage(ImageIO.read(new File("assets/ServerRoom/DesktopTable.png")), 0, 0, -1, -1);
            System.out.println("Loaded DesktopTable.png: " + aiDeskImg.getWidth() + "x" + aiDeskImg.getHeight());
        } catch (Exception e) { System.err.println("Failed to load DesktopTable: " + e.getMessage()); }
        try {
            aiDeskPersonImg = cropImage(ImageIO.read(new File("assets/ServerRoom/DesktopTablewithPerson.png")), 0, 0, -1, -1);
            System.out.println("Loaded DesktopTablewithPerson.png: " + aiDeskPersonImg.getWidth() + "x" + aiDeskPersonImg.getHeight());
        } catch (Exception e) { System.err.println("Failed to load DesktopTablewithPerson: " + e.getMessage()); }
        try {
            serverImg = cropImage(ImageIO.read(new File("assets/ServerRoom/Server.png")), 0, 0, -1, -1);
            System.out.println("Loaded Server.png: " + serverImg.getWidth() + "x" + serverImg.getHeight());
        } catch (Exception e) { System.err.println("Failed to load Server: " + e.getMessage()); }
        try {
            cableCrapImg = cropImage(ImageIO.read(new File("assets/ServerRoom/cablecrap.png")), 0, 0, -1, -1);
            System.out.println("Loaded cablecrap.png: " + cableCrapImg.getWidth() + "x" + cableCrapImg.getHeight());
        } catch (Exception e) { System.err.println("Failed to load cablecrap: " + e.getMessage()); }
    }

    /**
     * Copies an image region into a NEW standalone TYPE_INT_ARGB BufferedImage.
     * If w or h is -1, uses the full image dimensions (no cropping).
     */
    private BufferedImage cropImage(BufferedImage src, int x, int y, int w, int h) {
        if (w == -1) w = src.getWidth();
        if (h == -1) h = src.getHeight();
        BufferedImage cropped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = cropped.createGraphics();
        g.drawImage(src, 0, 0, w, h, x, y, x + w, y + h, null);
        g.dispose();
        return cropped;
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
                
                // Top Chair - DE-RANDOMIZED (Checkerboard pattern)
                if ((row + col) % 2 == 0) {
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
                    
                    // DE-RANDOMIZED books (Fixed configuration per shelf)
                    if (bookStack1Img != null && i % 2 == 0) {
                        furnitureList.add(new Furniture(bx + 5, shelf1Y - (int)(48 * scale), bookStack1Img, scale));
                    }
                    if (bookStack2Img != null && i % 3 != 0) {
                        furnitureList.add(new Furniture(bx + 15, shelf2Y - (int)(29 * scale), bookStack2Img, scale));
                    }
                    if (bookRedImg != null && (i == 4 || i == 16)) {
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
        
        // New Main Library Tables
        if (libMainTableImg != null) {
            double libTableScale = 102.0 / libMainTableImg.getWidth(); // Reduced by ~20%
            furnitureList.add(new Furniture(4 * tileSize, 6 * tileSize, libMainTableImg, libTableScale));
            furnitureList.add(new Furniture(16 * tileSize, 6 * tileSize, libMainTableImg, libTableScale));
            
            // Third table placed further below the center red desk and slid left
            int thirdTableX = (gp.maxScreenCol / 2) * tileSize - 91; // Slid to the left
            int thirdTableY = (gp.maxScreenRow / 2) * tileSize - 20 + 145; // Brought down further
            furnitureList.add(new Furniture(thirdTableX, thirdTableY, libMainTableImg, libTableScale));
        }
        
        // Add a chest somewhere
        if (libChestImg != null) {
            furnitureList.add(new Furniture((gp.maxScreenCol - 4) * tileSize, 2 * tileSize, libChestImg, scale));
        }
    }

    public void loadClassroomObjects() {
        furnitureList.clear();
        
        int tileSize = gp.tileSize; // 32
        
        // Teacher's Desk at top center (target width ~ 3 tiles = 96 pixels)
        if (teacherDeskImg != null) {
            double teacherScale = 96.0 / teacherDeskImg.getWidth();
            furnitureList.add(new Furniture((gp.maxScreenCol / 2) * tileSize - 48, 2 * tileSize, teacherDeskImg, teacherScale));
        }
        
        // Student desks in a grid (target width ~ 2 tiles = 64 pixels)
        int startX = 4 * tileSize;
        int startY = 6 * tileSize;
        int gapX = 5 * tileSize;
        int gapY = 3 * tileSize;
        
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                int deskX = startX + (col * gapX);
                int deskY = startY + (row * gapY);
                
                // DE-RANDOMIZED Mix empty and full desks (Pattern based)
                BufferedImage deskImg = (row % 2 == 0 || col % 2 == 0) ? studentDeskImg : emptyStudentDeskImg;
                
                if (deskImg != null) {
                    double deskScale = 31.5 / deskImg.getWidth(); // Reduced by another 30% (from 45)
                    furnitureList.add(new Furniture(deskX, deskY, deskImg, deskScale));
                }
            }
        }
    }

    public void clearObjects() {
        furnitureList.clear();
    }

    public void draw(Graphics2D g2) {
        for (Furniture f : furnitureList) {
            f.draw(g2);
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

    public void loadPrayerAreaObjects() {
        int ts = gp.tileSize; // 32
        
        // Shoe Rack near the door (bottom right)
        if (shoeRackImg != null) {
            int rackW = 160;
            int rackH = 100;
            furnitureList.add(new Furniture(shoeRackImg, (gp.maxScreenCol - 7) * ts, (gp.maxScreenRow - 4) * ts, rackW, rackH));
        }

        // Prayer mats in neat rows - 3 rows of 4, properly spaced
        if (prayerMatImg != null) {
            int matW = 100;
            int matH = 130;
            int gapX = 30;  // Gap between mats
            int gapY = 15;  // Gap between rows
            
            // Center the grid horizontally
            int totalGridW = 4 * matW + 3 * gapX;
            int startX = (gp.screenWidth - totalGridW) / 2;
            int startY = 2 * ts;

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 4; col++) {
                    // DE-RANDOMIZED (Checkerboard)
                    BufferedImage matToUse = (prayerMatPersonImg != null && (row + col) % 2 != 0) ? prayerMatPersonImg : prayerMatImg;
                    int mx = startX + col * (matW + gapX);
                    int my = startY + row * (matH + gapY);
                    furnitureList.add(new Furniture(matToUse, mx, my, matW, matH));
                }
            }
        }
    }

    public void loadServerRoomObjects() {
        int ts = gp.tileSize; // 32
        
        // Large Server rack centered at the back wall
        if (serverImg != null) {
            int serverW = 200;
            int serverH = 110;
            int serverX = (gp.screenWidth - serverW) / 2;
            int serverY = ts + 10; // Near top wall
            furnitureList.add(new Furniture(serverImg, serverX, serverY, serverW, serverH));
        }

        // Desktop Tables in 2 horizontal rows (center of room), well spaced
        if (aiDeskImg != null) {
            int deskW = 100;
            int deskH = 65;
            int gapX = 25;
            int gapY = 20;
            
            // Center the grid
            int cols = 4;
            int rows = 2;
            int totalGridW = cols * deskW + (cols - 1) * gapX;
            int startX = (gp.screenWidth - totalGridW) / 2;
            int startY = 5 * ts; // Below the server

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    // DE-RANDOMIZED (Top row full, bottom row alternating)
                    BufferedImage deskToUse = (aiDeskPersonImg != null && (row == 0 || col % 2 == 0)) ? aiDeskPersonImg : aiDeskImg;
                    int dx = startX + col * (deskW + gapX);
                    int dy = startY + row * (deskH + gapY + ts);
                    furnitureList.add(new Furniture(deskToUse, dx, dy, deskW, deskH));
                }
            }
        }

        // DE-RANDOMIZED crap wires scattered (Fixed positions with a seeded random for consistency)
        if (cableCrapImg != null) {
            // Using a fixed seed so it always generates the exact same positions every time it loads!
            java.util.Random rng = new java.util.Random(12345);
            for (int i = 0; i < 10; i++) {
                int cableW = 30;
                int cableH = 24;
                int cx = 2 * ts + rng.nextInt(gp.screenWidth - 6 * ts);
                int cy = 2 * ts + rng.nextInt(gp.screenHeight - 5 * ts);
                // Skip if too close to exit door on right wall
                if (cx > gp.screenWidth - 4 * ts && Math.abs(cy - (gp.maxScreenRow / 2) * ts) < 3 * ts) continue;
                furnitureList.add(new Furniture(cableCrapImg, cx, cy, cableW, cableH));
            }
        }
    }

    public void loadAILabObjects() {
        int ts = gp.tileSize; // 32
        
        if (aiDeskImg != null) {
            int deskW = 100;
            int deskH = 65;
            int gapX = 20;
            int gapY = 15;
            
            int cols = 4;
            int rows = 3;
            int totalGridW = cols * deskW + (cols - 1) * gapX;
            int startX = (gp.screenWidth - totalGridW) / 2;
            int startY = 2 * ts;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    // DE-RANDOMIZED (Checkerboard)
                    BufferedImage deskToUse = (aiDeskPersonImg != null && (row + col) % 2 == 0) ? aiDeskPersonImg : aiDeskImg;
                    int dx = startX + col * (deskW + gapX);
                    int dy = startY + row * (deskH + gapY + ts);
                    furnitureList.add(new Furniture(deskToUse, dx, dy, deskW, deskH));
                }
            }
        }
    }
}
