package engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import entity.Player;
import map.TileManager;
import map.ZoneType;
import ui.UI;
import engine.CollisionChecker;
import engine.ObjectManager;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale; // 32x32
    public final int maxScreenCol = 24; // 24 * 32 = 768 pixels
    public final int maxScreenRow = 18; // 18 * 32 = 576 pixels
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    int FPS = 60;

    public ZoneType currentZone = ZoneType.LIBRARY;
    TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public ObjectManager objM = new ObjectManager(this);
    Thread gameThread;
    public Player player = new Player(this, keyH);
    public UI ui = new UI(this);

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int cafeMenuState = 2;
    public final int pauseState = 3;
    public final int optionsState = 4;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        // Initial zone setup
        if (currentZone == ZoneType.CAFETERIA) {
            objM.loadCafeteriaObjects();
        } else if (currentZone == ZoneType.LIBRARY) {
            objM.loadLibraryObjects();
        }

        gameState = titleState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (gameState == titleState) {
            ui.updateTitleScreen();
        } else if (gameState == playState) {
            if (keyH.escapePressed) {
                gameState = pauseState;
                keyH.escapePressed = false;
            } else {
                player.update();
                checkZoneTransitions();
                checkInteractions();
            }
        } else if (gameState == pauseState) {
            ui.updatePauseScreen();
        } else if (gameState == optionsState) {
            ui.updateOptionsScreen();
        } else if (gameState == cafeMenuState) {
            // Menu updates handled in UI or here
            ui.updateCafeMenu();
        }
    }

    private void checkZoneTransitions() {
        int doorCol = maxScreenCol / 2;
        int playerCol = (player.xLocation + tileSize / 2) / tileSize;
        int playerRow = (player.yLocation + tileSize / 2) / tileSize;

        // Cafeteria to Ground (Bottom Door)
        if (currentZone == ZoneType.CAFETERIA && playerRow >= maxScreenRow - 1 && playerCol == doorCol) {
            currentZone = ZoneType.GROUND;
            tileM.loadMap();
            objM.clearObjects();
            player.yLocation = tileSize; 
            player.xLocation = doorCol * tileSize;
        }
        // Ground to Cafeteria (Top Door)
        else if (currentZone == ZoneType.GROUND && playerRow <= 0 && playerCol == doorCol) {
            currentZone = ZoneType.CAFETERIA;
            tileM.loadMap();
            objM.loadCafeteriaObjects();
            player.yLocation = (maxScreenRow - 2) * tileSize;
            player.xLocation = doorCol * tileSize;
        }
        // Ground to Library (Right Edge)
        else if (currentZone == ZoneType.GROUND && playerCol >= maxScreenCol - 1) {
            currentZone = ZoneType.LIBRARY;
            tileM.loadMap();
            objM.loadLibraryObjects();
            player.yLocation = (maxScreenRow - 2) * tileSize; // Spawns near bottom
            player.xLocation = doorCol * tileSize;
        }
        // Library to Ground (Bottom Edge)
        else if (currentZone == ZoneType.LIBRARY && playerRow >= maxScreenRow - 1) {
            currentZone = ZoneType.GROUND;
            tileM.loadMap();
            objM.clearObjects();
            player.yLocation = (maxScreenRow / 2) * tileSize;
            player.xLocation = (maxScreenCol - 2) * tileSize; // Back at right edge
        }
    }

    private void checkInteractions() {
        if (keyH.ePressed) {
            // Interacting with Cafe Counter (Top side)
            // Giving a bit of leeway (tileSize * 2) so they don't have to be perfectly against the wall
            if (currentZone == ZoneType.CAFETERIA && player.yLocation < tileSize * 2) {
                gameState = cafeMenuState;
                ui.commandNum = 0; // Reset menu selection
            }
            // Consume the key press to avoid rapid firing
            keyH.ePressed = false; 
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        if (gameState == titleState) {
            ui.draw(g2);
        } else {
            tileM.draw(g2);
            objM.draw(g2);
            player.draw(g2);
            ui.draw(g2);
        }
        
        g2.dispose();
    }
}
