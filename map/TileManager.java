package map;

import engine.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    public BufferedImage cafeCounterWide;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[30]; // Increased capacity for Library tiles
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        try {
            File file = new File("assets/floor.png");
            if (!file.exists()) {
                System.out.println("Could not find assets/floor.png. Make sure you placed it!");
                return;
            }
            BufferedImage spriteSheet = ImageIO.read(file);
            
            // The user's image is a 3x3 grid of tiles.
            int tileColW = spriteSheet.getWidth() / 3;
            int tileRowH = spriteSheet.getHeight() / 3;

            // Load all 9 tiles into our tile array
            int index = 0;
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    tile[index] = new Tile();
                    tile[index].image = spriteSheet.getSubimage(col * tileColW, row * tileRowH, tileColW, tileRowH);
                    index++;
                }
            }

            // Load Custom Tiles with Transparency
            tile[5] = new Tile();
            tile[5].image = ImageIO.read(new File("assets/cafe floor aquatic.png"));

            tile[6] = new Tile();
            tile[6].image = ImageIO.read(new File("assets/cafe floor white.png"));

            tile[11] = new Tile();
            tile[11].image = makeTransparent(ImageIO.read(new File("assets/door.png")), java.awt.Color.BLACK);
            // Door is NOT solid so player can walk into it to switch zones

            cafeCounterWide = makeTransparent(ImageIO.read(new File("assets/Cafe counter.png")), java.awt.Color.BLACK);

            tile[12] = new Tile();
            tile[12].image = null; // Don't draw as repeating tile
            tile[12].collision = true;

            // Load Library Sprite Sheet
            File libFile = new File("assets/LibraryCompeteSet.png");
            if (libFile.exists()) {
                BufferedImage libSheet = ImageIO.read(libFile);
                
                // 13: Library Floor (Dark Wood Floor) 
                tile[13] = new Tile();
                try { tile[13].image = ImageIO.read(new File("assets/dark_wood_floor2.png")); } catch (Exception e) { e.printStackTrace(); }

                // 14: Library Wall (Blue Wallpaper) 
                tile[14] = new Tile();
                try { tile[14].image = libSheet.getSubimage(960, 50, 32, 32); } catch (Exception e) {}
                tile[14].collision = true;

                // 15: Library Carpet (Red Runner)
                tile[15] = new Tile();
                try { tile[15].image = libSheet.getSubimage(765, 80, 32, 32); } catch (Exception e) {}
                
            } else {
                System.out.println("Warning: assets/LibraryCompeteSet.png not found.");
            }

            // Load Prayer Area Floor
            tile[16] = new Tile();
            try { tile[16].image = ImageIO.read(new File("assets/PrayerRoom/floor.jpg")); } catch (Exception e) {}

            // ─── Zombie Mode Tiles ───
            // Tile 17: Zombie Floor (crop 128x128 from brick texture, pre-scale to 32x32)
            File zFloorFile = new File("assets/ZombieAssets/ZombieFloor.png");
            if (zFloorFile.exists()) {
                BufferedImage zFloorFull = ImageIO.read(zFloorFile);
                tile[17] = new Tile();
                BufferedImage zCrop = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D gc = zCrop.createGraphics();
                gc.drawImage(zFloorFull, 0, 0, 32, 32, 200, 150, 328, 278, null);
                gc.dispose();
                tile[17].image = zCrop;
            }

            // Tile 18: Zombie Wall/Border (48x48 dark red accent)
            File zWallFile = new File("assets/ZombieAssets/ZombieFloor2.png");
            if (zWallFile.exists()) {
                tile[18] = new Tile();
                tile[18].image = ImageIO.read(zWallFile);
                tile[18].collision = true;
            }

            // Tile 19: Bloody Zombie Floor (composite: zombie floor + blood spatter)
            File bloodFile = new File("assets/ZombieAssets/Blood spatter.png");
            if (bloodFile.exists() && tile[17] != null && tile[17].image != null) {
                BufferedImage blood = ImageIO.read(bloodFile);
                BufferedImage composite = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D gc2 = composite.createGraphics();
                gc2.drawImage(tile[17].image, 0, 0, 32, 32, null);
                gc2.drawImage(blood, 0, 0, 32, 32, null);
                gc2.dispose();
                tile[19] = new Tile();
                tile[19].image = composite;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage makeTransparent(BufferedImage img, java.awt.Color color) {
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

    public void loadMap() {
        if (gp.currentZone == ZoneType.CAFETERIA) {
            loadCafeteriaMap();
        } else if (gp.currentZone == ZoneType.GROUND) {
            loadGroundMap();
        } else if (gp.currentZone == ZoneType.LIBRARY) {
            loadLibraryMap();
        } else if (gp.currentZone == ZoneType.CORRIDOR) {
            loadCorridorMap();
        } else if (gp.currentZone == ZoneType.CLASSROOM) {
            loadClassroomMap();
        } else if (gp.currentZone == ZoneType.PRAYER_AREA) {
            loadPrayerAreaMap();
        } else if (gp.currentZone == ZoneType.SERVER_ROOM) {
            loadServerRoomMap();
        } else if (gp.currentZone == ZoneType.AI_LAB) {
            loadAILabMap();
        } else if (gp.currentZone == ZoneType.WALKWAY) {
            loadWalkwayMap();
        }

        // Apply zombie-mode tile overrides (deterministic, no randomization)
        if (gp.zombieMode) {
            zombifyCurrentMap();
        }
    }

    /**
     * Deterministically replaces floor/wall tiles with zombie variants.
     * ~70% of floor tiles become zombie floor, ~30% keep room vibe.
     * Walls become ZombieFloor2. Doors are NEVER touched.
     * Blood spatters at fixed positions.
     */
    private void zombifyCurrentMap() {
        int zombieFloor = 17;   // The main zombie floor tile
        int zombieWall  = 18;   // Border / wall tile (collision = true)
        int bloodyFloor = 19;   // Blood-splatter composite (just visual accent)
        int doorTile    = 11;

        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (mapTileNum[col][row] == doorTile) continue; // Never touch doors

                boolean isBorder = (row == 0 || row == gp.maxScreenRow - 1 ||
                                    col == 0 || col == gp.maxScreenCol - 1);

                if (isBorder) {
                    mapTileNum[col][row] = zombieWall;
                } else {
                    // All interior tiles: zombie floor only.
                    // Sprinkle a small amount of blood spatters (deterministic, sparse)
                    boolean bloodHere = ((col * 7 + row * 13) % 40 == 0);
                    mapTileNum[col][row] = (bloodHere && tile[19] != null) ? bloodyFloor : zombieFloor;
                }
            }
        }
    }

    private void loadCafeteriaMap() {
        int floorTileOrange = 5;
        int floorTileWhite = 6;
        int counterTile = 12;
        int doorTile = 11;

        // Fill with checkerboard floor
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if ((col + row) % 2 == 0) {
                    mapTileNum[col][row] = floorTileOrange;
                } else {
                    mapTileNum[col][row] = floorTileWhite;
                }
            }
        }

        // Place Counter at the top
        for (int col = 0; col < gp.maxScreenCol; col++) {
            mapTileNum[col][0] = counterTile;
        }

        // Place Single Door at bottom center
        mapTileNum[gp.maxScreenCol/2][gp.maxScreenRow - 1] = doorTile;
    }

    private void loadGroundMap() {
        int grassTile = 1;   // Grass floor
        int wallTile  = 0;   // Default wall tile from floor.png (first tile)
        int doorTile  = 11;

        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                boolean isBorder = (row == 0 || row == gp.maxScreenRow - 1 ||
                                    col == 0 || col == gp.maxScreenCol - 1);
                if (isBorder) {
                    // Doors: Cafe (top center), Prayer Area (top center+5), Walkway (left mid)
                    if (row == 0 && (col == gp.maxScreenCol / 2 || col == gp.maxScreenCol / 2 + 5)) {
                        mapTileNum[col][row] = doorTile;
                    } else if (col == 0 && row == gp.maxScreenRow / 2) {
                        mapTileNum[col][row] = doorTile;
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                } else {
                    mapTileNum[col][row] = grassTile;
                }
            }
        }
    }

    private void loadLibraryMap() {
        int floorTile = 13;
        int wallTile = 14;
        int doorTile = 11;

        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                // Top row is wall
                if (row == 0) {
                    mapTileNum[col][row] = wallTile;
                }
                // Bottom row: door to Walkway at center
                else if (row == gp.maxScreenRow - 1) {
                    if (col == gp.maxScreenCol / 2) {
                        mapTileNum[col][row] = doorTile; // Exit to Walkway
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                }
                // Left edge: door to Server Room at mid-height
                else if (col == 0) {
                    if (row == gp.maxScreenRow / 2) {
                        mapTileNum[col][row] = doorTile; // Server Room
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                }
                // Right edge is wall
                else if (col == gp.maxScreenCol - 1) {
                    mapTileNum[col][row] = wallTile;
                }
                // Everything else is wooden floor
                else {
                    mapTileNum[col][row] = floorTile;
                }
            }
        }
    }

    private void loadCorridorMap() {
        int floorTile = 13; // Dark wood floor 
        int doorTile = 11;
        int wallTile = 14;

        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (row == 0 || row == gp.maxScreenRow - 1) {
                    // Place doors at intervals for classrooms/AI labs
                    if (col == 4 || col == 10 || col == 16 || col == 20) {
                        mapTileNum[col][row] = doorTile;
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                } else if (col == 0) {
                    // Left edge: door to Walkway at mid-height
                    if (row == gp.maxScreenRow / 2) {
                        mapTileNum[col][row] = doorTile; // Stairs to Walkway
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                } else if (col == gp.maxScreenCol - 1) {
                    mapTileNum[col][row] = wallTile; // Right wall
                } else {
                    mapTileNum[col][row] = floorTile; 
                }
            }
        }
    }

    private void loadClassroomMap() {
        int floorTile = 2; // 3rd tile, 1st row from floor.png
        int doorTile = 11;
        int wallTile = 14;

        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (row == 0 || col == 0 || col == gp.maxScreenCol - 1) {
                    mapTileNum[col][row] = wallTile;
                } else if (row == gp.maxScreenRow - 1) {
                    if (col == gp.maxScreenCol / 2) {
                        mapTileNum[col][row] = doorTile; // Exit to Corridor
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                } else {
                    mapTileNum[col][row] = floorTile;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        // Draw the map based on the map file/array
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            int tileNum = mapTileNum[col][row];

            if (tile[tileNum] != null && tile[tileNum].image != null) {
                g2.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
            }

            col++;
            x += gp.tileSize;

            if (col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }

        // Draw single wide cafe counter over the top row
        if (gp.currentZone == ZoneType.CAFETERIA && cafeCounterWide != null) {
            g2.drawImage(cafeCounterWide, 0, 0, gp.screenWidth, gp.tileSize, null);
        }
    }

    private void loadPrayerAreaMap() {
        int floorTile = 16;
        int wallTile = 14;
        int doorTile = 11;
        
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (row == 0 || col == 0 || col == gp.maxScreenCol - 1) {
                    mapTileNum[col][row] = wallTile;
                } else if (row == gp.maxScreenRow - 1) {
                    if (col == gp.maxScreenCol / 2) {
                        mapTileNum[col][row] = doorTile; 
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                } else {
                    mapTileNum[col][row] = floorTile;
                }
            }
        }
    }

    private void loadServerRoomMap() {
        int floorTile = 2; // Classroom floor
        int wallTile = 14;
        int doorTile = 11;
        
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (row == 0 || row == gp.maxScreenRow - 1 || col == 0 || col == gp.maxScreenCol - 1) {
                    if (col == gp.maxScreenCol - 1 && row == gp.maxScreenRow / 2) {
                        mapTileNum[col][row] = doorTile; // Door to Library (right wall)
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                } else {
                    mapTileNum[col][row] = floorTile;
                }
            }
        }
    }

    private void loadAILabMap() {
        int floorTile = 2; // Classroom floor
        int wallTile = 14;
        int doorTile = 11;

        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (row == 0 || col == 0 || col == gp.maxScreenCol - 1) {
                    mapTileNum[col][row] = wallTile;
                } else if (row == gp.maxScreenRow - 1) {
                    if (col == gp.maxScreenCol / 2) {
                        mapTileNum[col][row] = doorTile; // Exit to Corridor
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                } else {
                    mapTileNum[col][row] = floorTile;
                }
            }
        }
    }

    private void loadWalkwayMap() {
        int floorTile = 13; // Dark wood floor / Dark tile theme
        int wallTile = 14;
        int doorTile = 11;

        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (row == 0 || row == gp.maxScreenRow - 1 || col == 0 || col == gp.maxScreenCol - 1) {
                    // Doors on Top, Bottom, and Right
                    if ((col == gp.maxScreenCol / 2 && row == 0) || 
                        (col == gp.maxScreenCol / 2 && row == gp.maxScreenRow - 1) || 
                        (col == gp.maxScreenCol - 1 && row == gp.maxScreenRow / 2)) {
                        mapTileNum[col][row] = doorTile;
                    } else {
                        mapTileNum[col][row] = wallTile;
                    }
                } else {
                    mapTileNum[col][row] = floorTile;
                }
            }
        }
    }
}
