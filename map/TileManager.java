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

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10]; // Can hold up to 10 types of tiles right now
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap() {
        // Just fill the screen with a mixture of tile 1 (Grass) for now to have a basic map
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                // E.g., index 1 is usually middle-top if it goes row by row (0-based)
                // We'll just randomly scatter some for variety, mostly Grass (1) and some Sand (3)
                int r = (int)(Math.random() * 10);
                if (r < 8) {
                    mapTileNum[col][row] = 1; // 1st row middle -> Grass
                } else if (r == 8) {
                    mapTileNum[col][row] = 3; // 2nd row left -> Sand
                } else {
                    mapTileNum[col][row] = 0; // 1st row left -> Stone
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
    }
}
