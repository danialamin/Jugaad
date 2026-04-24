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
        tile = new Tile[20]; // Increased capacity
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
        int grassTile = 1;
        int doorTile = 11;

        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                if (row == 0 && (col == gp.maxScreenCol/2)) {
                    mapTileNum[col][row] = doorTile;
                } else {
                    mapTileNum[col][row] = grassTile;
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
}
