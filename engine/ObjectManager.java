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

    public static final String[] STUDENT_NAMES = {
        "Muhammad Fasih Ul Mubashir", "Fatima Hashmat", "Hooud Bin Jawad",
        "Zunnoon Bin Jawad", "Waseed E Mustafa", "Raja Shehryar Ameer",
        "Muhammad Saad", "Abdullah Aamir", "Muhammad Hanzalah Aman",
        "Aminullah Khan", "Muhammad Dyen Asif", "Hassan Rizwan",
        "Huzaifa Amin", "Muhammad Taha Nasim", "Ahmad Hussain",
        "Muhammad Arham Manzoor", "Alina Farooq", "Haleema Sadia",
        "Muhammad Huzaifa Tayyab", "Ali Ather", "Ayaan Aman",
        "Muhammad Danial Amin", "Jawwad Najeeb", "Saad Ahmed",
        "Muhammad Ammar", "Muhammad Sohaib Saeed", "Kumail Raza",
        "Muhammad Saim Nawaz", "Hanzla Zafran", "Talha Iftikhar Abbasi",
        "Muhammad Ahmed", "Arwa Javed", "Muhammad Hamza Saeed",
        "Zainab Nisar", "Sabia Munir", "Aon Mohammad Awan",
        "Muhammad Hassaan Ul Mustafa", "Abdullah Malik", "Fatima Mazhar"
    };

    public static final String[] USED_NAMES = new String[39];
    private static int usedCount = 0;

    public static String pickUnusedName(int seed) {
        if (usedCount >= STUDENT_NAMES.length) usedCount = 0;
        int idx = (seed * 7 + 3) % STUDENT_NAMES.length;
        for (int attempt = 0; attempt < STUDENT_NAMES.length; attempt++) {
            boolean taken = false;
            for (int u = 0; u < usedCount; u++) {
                if (STUDENT_NAMES[idx].equals(USED_NAMES[u])) { taken = true; break; }
            }
            if (!taken) {
                if (usedCount < USED_NAMES.length) USED_NAMES[usedCount++] = STUDENT_NAMES[idx];
                return STUDENT_NAMES[idx];
            }
            idx = (idx + 1) % STUDENT_NAMES.length;
        }
        return STUDENT_NAMES[seed % STUDENT_NAMES.length];
    }

    public static void resetUsedNames() { usedCount = 0; }
    
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

        // Cafeteria Uncle NPC - behind the counter at the top
        BufferedImage uncleImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Uncle = uncleImg.createGraphics();
        g2Uncle.setColor(new Color(160, 82, 45)); // Sienna uniform
        g2Uncle.fillRect(0, 0, 32, 32);
        g2Uncle.setColor(Color.WHITE);
        g2Uncle.drawRect(0, 0, 31, 31);
        g2Uncle.dispose();
        // Place uncle behind the counter (top center area)
        int ts = gp.tileSize;
        Furniture uncle = new Furniture(uncleImg, (gp.maxScreenCol / 2) * ts - 16, ts + 10, 32, 32);
        uncle.name = "cafeteria_uncle";
        furnitureList.add(uncle);

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
        // Phase 1 save checkpoint — far bottom-right corner
        furnitureList.add(makeStatic("checkpoint", (gp.maxScreenCol - 2) * ts, (gp.maxScreenRow - 3) * ts, ts, ts, CHECKPOINT_TEAL));
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

        // Center Desk (Librarian Counter)
        if (libDeskImg != null) {
            int deskX = (gp.maxScreenCol / 2) * tileSize - (int)(95 * scale);
            int deskY = gp.maxScreenRow / 2 * tileSize - 20;
            furnitureList.add(new Furniture(deskX, deskY, libDeskImg, scale));

            // Librarian NPC behind the counter
            BufferedImage librarianImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2Librarian = librarianImg.createGraphics();
            g2Librarian.setColor(new Color(139, 69, 19)); // Brown uniform
            g2Librarian.fillRect(0, 0, 32, 32);
            g2Librarian.setColor(Color.WHITE);
            g2Librarian.drawRect(0, 0, 31, 31);
            g2Librarian.dispose();
            Furniture librarian = new Furniture(deskX + 40, deskY - 25, librarianImg, 1.0);
            librarian.name = "librarian";
            furnitureList.add(librarian);
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
            Furniture studySpot = new Furniture(thirdTableX, thirdTableY, libMainTableImg, libTableScale);
            studySpot.name = "library_study_spot";
            furnitureList.add(studySpot);
        }
        
        // Add a chest somewhere
        if (libChestImg != null) {
            furnitureList.add(new Furniture((gp.maxScreenCol - 4) * tileSize, 2 * tileSize, libChestImg, scale));
        }
        // Phase 1 save checkpoint — far bottom-left corner
        furnitureList.add(makeStatic("checkpoint", 2 * tileSize, (gp.maxScreenRow - 3) * tileSize, tileSize, tileSize, CHECKPOINT_TEAL));
    }

    public void loadClassroomObjects() {
        furnitureList.clear();
        resetUsedNames();
        int tileSize = gp.tileSize;

        if (teacherDeskImg != null) {
            double teacherScale = 96.0 / teacherDeskImg.getWidth();
            furnitureList.add(new Furniture((gp.maxScreenCol / 2) * tileSize - 48, 2 * tileSize, teacherDeskImg, teacherScale));
        }

        BufferedImage teacherImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Img = teacherImg.createGraphics();
        g2Img.setColor(Color.RED);
        g2Img.fillRect(0, 0, 32, 32);
        g2Img.setColor(Color.WHITE);
        g2Img.drawRect(0, 0, 31, 31);
        g2Img.dispose();
        Furniture teacherNPC = new Furniture((gp.maxScreenCol / 2) * tileSize - 16, 2 * tileSize - 20, teacherImg, 1.0);
        teacherNPC.name = "teacher";
        furnitureList.add(teacherNPC);

        int startX = 4 * tileSize;
        int startY = 6 * tileSize;
        int gapX = 5 * tileSize;
        int gapY = 3 * tileSize;

        int studentIndex = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                int deskX = startX + (col * gapX);
                int deskY = startY + (row * gapY);
                BufferedImage deskImg = (row % 2 == 0 || col % 2 == 0) ? studentDeskImg : emptyStudentDeskImg;
                if (deskImg != null) {
                    double deskScale = 31.5 / deskImg.getWidth();
                    Furniture f = new Furniture(deskX, deskY, deskImg, deskScale);
                    if (deskImg == studentDeskImg) {
                        String sName = pickUnusedName(studentIndex + 1);
                        f.name = "student_desk_" + sName.replace(" ", "_");
                        studentIndex++;
                    } else {
                        f.name = "empty_desk";
                    }
                    furnitureList.add(f);
                }
            }
        }
        // Phase 1 save checkpoint — far bottom-left corner
        furnitureList.add(makeStatic("checkpoint", 2 * tileSize, (gp.maxScreenRow - 3) * tileSize, tileSize, tileSize, CHECKPOINT_TEAL));
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
        furnitureList.clear();

        // Shoe Rack near the door (bottom right)
        if (shoeRackImg != null) {
            int rackW = 160;
            int rackH = 100;
            furnitureList.add(new Furniture(shoeRackImg, (gp.maxScreenCol - 7) * ts, (gp.maxScreenRow - 4) * ts, rackW, rackH));
        }

        // Prayer mats in neat rows - 3 rows of 4, with larger gaps for navigation
        if (prayerMatImg != null) {
            int matW = 64;   // Smaller mats (was 100)
            int matH = 80;   // Smaller mats (was 130)
            int gapX = 48;   // Larger gap for walking between mats (was 30)
            int gapY = 32;   // Larger vertical gap (was 15)

            // Center the grid horizontally
            int totalGridW = 4 * matW + 3 * gapX;
            int startX = (gp.screenWidth - totalGridW) / 2;
            int startY = 3 * ts; // Start a bit lower for better access

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 4; col++) {
                    // DE-RANDOMIZED (Checkerboard) - half occupied, half free
                    boolean hasPerson = (prayerMatPersonImg != null && (row + col) % 2 != 0);
                    BufferedImage matToUse = hasPerson ? prayerMatPersonImg : prayerMatImg;
                    int mx = startX + col * (matW + gapX);
                    int my = startY + row * (matH + gapY);
                    Furniture mat = new Furniture(matToUse, mx, my, matW, matH);
                    if (!hasPerson) {
                        mat.name = "empty_prayer_mat";
                    }
                    furnitureList.add(mat);
                }
            }
        }
        // Phase 1 save checkpoint — far top-right corner
        furnitureList.add(makeStatic("checkpoint", (gp.maxScreenCol - 2) * ts, 2 * ts, ts, ts, CHECKPOINT_TEAL));
    }

    public void loadServerRoomObjects() {
        furnitureList.clear();
        int ts = gp.tileSize; // 32

        // Server Room Guard NPC - blocks player from touching anything
        BufferedImage guardImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Guard = guardImg.createGraphics();
        g2Guard.setColor(new Color(80, 80, 80)); // Grey uniform
        g2Guard.fillRect(0, 0, 32, 32);
        g2Guard.setColor(Color.RED);
        g2Guard.drawRect(0, 0, 31, 31);
        g2Guard.dispose();
        // Place guard near the entrance (center, blocking the room)
        Furniture guard = new Furniture(guardImg, (gp.maxScreenCol / 2) * ts - 16, (gp.maxScreenRow / 2) * ts, 32, 32);
        guard.name = "server_room_guard";
        furnitureList.add(guard);

        // Large Server rack centered at the back wall
        if (serverImg != null) {
            int serverW = 200;
            int serverH = 110;
            int serverX = (gp.screenWidth - serverW) / 2;
            int serverY = ts + 10; // Near top wall
            Furniture server = new Furniture(serverImg, serverX, serverY, serverW, serverH);
            server.name = "server_rack";
            furnitureList.add(server);
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
                    Furniture desk = new Furniture(deskToUse, dx, dy, deskW, deskH);
                    desk.name = "server_room_desk";
                    furnitureList.add(desk);
                }
            }
        }

        // DE-RANDOMIZED crap wires scattered (Fixed positions with a seeded random for consistency)
        if (cableCrapImg != null) {
            java.util.Random rng = new java.util.Random(12345);
            for (int i = 0; i < 10; i++) {
                int cableW = 30;
                int cableH = 24;
                int cx = 2 * ts + rng.nextInt(gp.screenWidth - 6 * ts);
                int cy = 2 * ts + rng.nextInt(gp.screenHeight - 5 * ts);
                if (cx > gp.screenWidth - 4 * ts && Math.abs(cy - (gp.maxScreenRow / 2) * ts) < 3 * ts) continue;
                furnitureList.add(new Furniture(cableCrapImg, cx, cy, cableW, cableH));
            }
        }
        // Phase 1 save checkpoint — far top-left corner
        furnitureList.add(makeStatic("checkpoint", 2 * ts, 2 * ts, ts, ts, CHECKPOINT_TEAL));
    }

    public void loadGroundObjects() {
        furnitureList.clear();
        int ts = gp.tileSize;

        // Haider Ramzan - friend sitting on floor with laptop
        BufferedImage haiderImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Haider = haiderImg.createGraphics();
        g2Haider.setColor(new Color(60, 120, 180)); // Blue shirt
        g2Haider.fillRect(0, 0, 32, 32);
        g2Haider.setColor(Color.WHITE);
        g2Haider.drawRect(0, 0, 31, 31);
        g2Haider.dispose();
        Furniture haider = new Furniture(haiderImg, 16 * ts, 10 * ts, 32, 32);
        haider.name = "haider_ramzan";
        furnitureList.add(haider);
        // Phase 1 save checkpoint — far top-left corner (opposite of Haider)
        furnitureList.add(makeStatic("checkpoint", 2 * ts, 2 * ts, ts, ts, CHECKPOINT_TEAL));
    }

    public void loadAILabObjects() {
        furnitureList.clear();
        resetUsedNames();
        int ts = gp.tileSize;

        BufferedImage teacherImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Img = teacherImg.createGraphics();
        g2Img.setColor(new Color(70, 110, 190));
        g2Img.fillRect(0, 0, 32, 32);
        g2Img.setColor(Color.WHITE);
        g2Img.drawRect(0, 0, 31, 31);
        g2Img.dispose();
        Furniture teacherNPC = new Furniture((gp.maxScreenCol / 2) * ts - 16, ts + 8, teacherImg, 1.0);
        teacherNPC.name = "teacher";
        furnitureList.add(teacherNPC);

        if (aiDeskImg != null) {
            int deskW = 100, deskH = 65, gapX = 20, gapY = 15;
            int cols = 4, rows = 3;
            int totalGridW = cols * deskW + (cols - 1) * gapX;
            int startX = (gp.screenWidth - totalGridW) / 2;
            int startY = 2 * ts;
            int studentIndex = 0;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    BufferedImage deskToUse = (aiDeskPersonImg != null && (row + col) % 2 == 0) ? aiDeskPersonImg : aiDeskImg;
                    int dx = startX + col * (deskW + gapX);
                    int dy = startY + row * (deskH + gapY + ts);
                    Furniture desk = new Furniture(deskToUse, dx, dy, deskW, deskH);
                    if (deskToUse == aiDeskImg) {
                        desk.name = "empty_desk";
                    } else {
                        String sName = pickUnusedName(studentIndex + 5);
                        desk.name = "student_desk_" + sName.replace(" ", "_");
                        studentIndex++;
                    }
                    furnitureList.add(desk);
                }
            }
        }
        // Phase 1 save checkpoint — far bottom-right corner
        furnitureList.add(makeStatic("checkpoint", (gp.maxScreenCol - 2) * ts, (gp.maxScreenRow - 3) * ts, ts, ts, CHECKPOINT_TEAL));
    }

    // ═══════════════════════════════════════════════
    //  ZOMBIE MODE — Placeholder objects per zone
    // ═══════════════════════════════════════════════

    private static final Color ZOMBIE_RED = new Color(180, 40, 40, 200);
    private static final Color NPC_GREEN = new Color(40, 160, 60, 200);
    private static final Color BOSS_PURPLE = new Color(120, 30, 180, 220);
    private static final Color OBSTACLE_ORANGE = new Color(200, 120, 30, 200);
    private static final Color CHECKPOINT_TEAL = new Color(50, 220, 180, 200);

    /** Creates a zombie placeholder with chasing AI. */
    private Furniture makeZombie(String name, int x, int y, Color color, float speed, int detectRadius) {
        int ts = gp.tileSize;
        Furniture f = new Furniture(null, x, y, (int)(ts * 1.2), (int)(ts * 1.2));
        f.name = name;
        f.placeholderColor = color;
        f.moveSpeed = speed;
        f.detectionRadius = detectRadius;
        return f;
    }

    /** Creates a static NPC/object placeholder (no chasing). */
    private Furniture makeStatic(String name, int x, int y, int w, int h, Color color) {
        Furniture f = new Furniture(null, x, y, w, h);
        f.name = name;
        f.placeholderColor = color;
        return f;
    }

    /** Call every frame to update zombie AI movement. */
    public void updateZombies(int playerX, int playerY) {
        for (Furniture f : furnitureList) {
            f.updateAI(playerX, playerY);
        }
    }

    /** Find the zombie touching the player, if any. */
    public Furniture getZombieTouchingPlayer(int px, int py, int pw, int ph) {
        for (Furniture f : furnitureList) {
            if (f.defeated || f.moveSpeed <= 0) continue;
            if (f.name == null) continue;
            if (!f.name.startsWith("zombie_") && !f.name.equals("final_boss")) continue;
            if (f.isTouchingPlayer(px, py, pw, ph)) return f;
        }
        return null;
    }

    // ─── Per-zone zombie loading ───

    public void loadZombieLibraryObjects() {
        furnitureList.clear();
        int ts = gp.tileSize;
        if (libClockImg != null) {
            furnitureList.add(new Furniture((gp.maxScreenCol / 2) * ts - (ts/2), ts, libClockImg, 0.5));
        }
        // Zombie Librarian — the ONLY entity inside the library (Library Isolation Rule)
        furnitureList.add(makeZombie("zombie_librarian", 4 * ts, 5 * ts, ZOMBIE_RED, 0.8f, ts * 3));
        // Checkpoint in far corner (bottom-right)
        furnitureList.add(makeStatic("checkpoint", (gp.maxScreenCol - 2) * ts, (gp.maxScreenRow - 3) * ts, ts, ts, new Color(50, 220, 180, 200)));
    }

    public void loadZombieWalkwayObjects() {
        furnitureList.clear();
        int ts = gp.tileSize;
        // Waseed E Mustafa — drops Corridor Keycard
        furnitureList.add(makeZombie("zombie_waseed", 10 * ts, 8 * ts, ZOMBIE_RED, 1.0f, ts * 3));
        // Checkpoint
        furnitureList.add(makeStatic("checkpoint", 3 * ts, (gp.maxScreenRow - 3) * ts, ts, ts, new Color(50, 220, 180, 200)));
    }

    public void loadZombieGroundObjects() {
        furnitureList.clear();
        int ts = gp.tileSize;
        // Hooud Bin Jawad — easy, slow
        furnitureList.add(makeZombie("zombie_hooud", 8 * ts, 8 * ts, ZOMBIE_RED, 0.7f, ts * 2));
        // Haider Ramzan — friendly NPC (green, static)
        furnitureList.add(makeStatic("npc_haider", 14 * ts, 4 * ts, (int)(ts * 1.2), (int)(ts * 1.2), NPC_GREEN));
        // Checkpoint near entry
        furnitureList.add(makeStatic("checkpoint", 18 * ts, (gp.maxScreenRow / 2) * ts, ts, ts, new Color(50, 220, 180, 200)));
    }

    public void loadZombieCorridorObjects() {
        furnitureList.clear();
        int ts = gp.tileSize;
        // Dyen Asif — medium blocker
        furnitureList.add(makeZombie("zombie_dyen", 12 * ts, 5 * ts, ZOMBIE_RED, 1.0f, ts * 3));
        // Ahmad Hussain — The Strict Grader, hard, aggressive chaser
        furnitureList.add(makeZombie("zombie_ahmad", 4 * ts, 12 * ts, ZOMBIE_RED, 1.3f, ts * 4));
        // Checkpoint near exit to library
        furnitureList.add(makeStatic("checkpoint", (gp.maxScreenCol / 2) * ts, (gp.maxScreenRow - 3) * ts, ts, ts, new Color(50, 220, 180, 200)));
    }

    public void loadZombieServerRoomObjects() {
        furnitureList.clear();
        int ts = gp.tileSize;
        if (serverImg != null) {
            int serverW = 200, serverH = 110;
            furnitureList.add(new Furniture(serverImg, (gp.screenWidth - serverW) / 2, ts + 10, serverW, serverH));
        }
        // Interactive server terminal — lore
        if (aiDeskImg != null) {
            int deskW = 100, deskH = 65;
            int startX = (gp.screenWidth - (4 * deskW + 3 * 25)) / 2;
            for (int col = 0; col < 4; col++) {
                Furniture desk = new Furniture(aiDeskImg, startX + col * (deskW + 25), 5 * ts, deskW, deskH);
                desk.name = "server_terminal_" + col;
                furnitureList.add(desk);
            }
        }
        // Lore computer (glowing, interactable)
        furnitureList.add(makeStatic("lore_computer", (gp.maxScreenCol / 2) * ts - ts, 3 * ts, ts * 2, ts * 2, new Color(0, 180, 255, 200)));
        // FINAL BOSS — The Corrupted AI
        Furniture boss = makeZombie("final_boss", 8 * ts, 6 * ts, BOSS_PURPLE, 1.8f, ts * 15);
        boss.width = ts * 2;
        boss.height = ts * 2;
        furnitureList.add(boss);
    }

    public void loadZombieCafeObjects() {
        furnitureList.clear();
        int ts = gp.tileSize;
        // Zombie Cafe Uncle — ambush enemy, only appears if not yet defeated
        BufferedImage uncleImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = uncleImg.createGraphics();
        g2.setColor(new Color(160, 60, 20));
        g2.fillRect(0, 0, 32, 32);
        g2.setColor(Color.WHITE);
        g2.drawRect(0, 0, 31, 31);
        g2.dispose();
        Furniture uncle = makeZombie("zombie_cafe_uncle", (gp.maxScreenCol / 2) * ts - 16, ts + 10, new Color(160, 60, 20, 220), 1.2f, ts * 8);
        uncle.width = 32; uncle.height = 32;
        furnitureList.add(uncle);
        // Food pickup spot using same counter name as normal mode (for interaction consistency)
        furnitureList.add(makeStatic("cafe_counter", (gp.maxScreenCol / 2) * ts - 16, ts + 10, 32, 32, new Color(160, 60, 20, 0)));
        // Checkpoint near cafe entrance
        furnitureList.add(makeStatic("checkpoint", 3 * ts, (gp.maxScreenRow - 3) * ts, ts, ts, CHECKPOINT_TEAL));
    }

    public void loadZombieClassroomObjects() {
        furnitureList.clear();
        int ts = gp.tileSize;
        // Faizan the TA — first fight in classroom
        Furniture faizan = makeZombie("zombie_faizan", (gp.maxScreenCol / 2) * ts - 3 * ts, 4 * ts, new Color(200, 100, 30, 220), 1.1f, ts * 5);
        furnitureList.add(faizan);
        // Miss Javeria — second fight, drops library server room key
        Furniture javeria = makeZombie("zombie_javeria", (gp.maxScreenCol / 2) * ts + ts, 4 * ts, new Color(180, 50, 120, 220), 1.3f, ts * 5);
        furnitureList.add(javeria);
        // Teacher desk visual
        if (teacherDeskImg != null) {
            double teacherScale = 96.0 / teacherDeskImg.getWidth();
            furnitureList.add(new Furniture((gp.maxScreenCol / 2) * ts - 48, 2 * ts, teacherDeskImg, teacherScale));
        }
    }

    /**
     * Removes any furniture whose name is in the defeated set.
     * Called after each zone load to prevent dead zombies from respawning.
     */
    public void filterDefeatedZombies(java.util.Set<String> defeated) {
        furnitureList.removeIf(f -> defeated.contains(f.name));
    }
}
