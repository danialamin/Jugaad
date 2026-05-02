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
import map.CampusMap;
import map.Location;
import map.Zone;

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
    public int currentClassroomId = -1;
    public int enteredFromDoorCol = -1;
    public boolean enteredFromTopDoor = false;

    TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public ObjectManager objM = new ObjectManager(this);
    Thread gameThread;
    
    public controller.GameSession session;
    public ui.UI gameUI = new UI(this);

    // Live HUD Data
    public String nearbyDoorName = "";

    // SOUND MANAGER
    public SoundManager soundM = new SoundManager();

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int cafeMenuState = 2;
    public final int pauseState = 3;
    public final int optionsState = 4;
    public final int mapState = 5;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        session = new controller.GameSession();
        session.startNewGame();
        
        // Connect player back to panel and keyboard
        session.getPlayer().setEngineComponents(this, keyH);
        
        // Initial zone setup
        if (currentZone == ZoneType.CAFETERIA) {
            objM.loadCafeteriaObjects();
        } else if (currentZone == ZoneType.LIBRARY) {
            objM.loadLibraryObjects();
        }

        // Play zone BGM Initially 
        soundM.playZoneMusic(currentZone);

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
            soundM.playCustomMusic("assets/sound/startMenu.wav");
            gameUI.updateTitleScreen();
        } else if (gameState == playState) {
            // Restore proper Zone music if coming back from pause/title
            soundM.playZoneMusic(currentZone);

            if (keyH.escapePressed) {
                gameState = pauseState;
                keyH.escapePressed = false;
            } else if (keyH.mPressed) {
                gameState = mapState;
                keyH.mPressed = false;
            } else {
                session.getPlayer().update();
                checkZoneTransitions();
                checkInteractions();
                updateLiveLocation();
            }
        } else if (gameState == pauseState) {
            soundM.playCustomMusic("assets/sound/pauseTheme.wav");
            gameUI.updatePauseScreen();
        } else if (gameState == optionsState) {
            soundM.playCustomMusic("assets/sound/pauseTheme.wav"); // Keep pause theme rolling in options
            gameUI.updateOptionsScreen();
        } else if (gameState == cafeMenuState) {
            gameUI.updateCafeMenu();
        } else if (gameState == mapState) {
            updateFastTravel();
        }
    }

    private void clearKeys() {
        keyH.upPressed = false;
        keyH.downPressed = false;
        keyH.leftPressed = false;
        keyH.rightPressed = false;
        keyH.ePressed = false;
        keyH.enterPressed = false;
        keyH.mPressed = false;
        keyH.num1Pressed = false;
        keyH.num2Pressed = false;
        keyH.num3Pressed = false;
    }

    private void updateFastTravel() {
        if (keyH.escapePressed || keyH.mPressed) {
            gameState = playState;
            clearKeys();
            return;
        }

        if (keyH.num1Pressed) {
            teleportToZone(ZoneType.GROUND);
        } else if (keyH.num2Pressed) {
            teleportToZone(ZoneType.CORRIDOR);
        }
    }

    private void teleportToZone(ZoneType target) {
        currentZone = target;
        tileM.loadMap();
        objM.clearObjects();
        
        if (currentZone == ZoneType.CAFETERIA) objM.loadCafeteriaObjects();
        if (currentZone == ZoneType.LIBRARY) objM.loadLibraryObjects();
        if (currentZone == ZoneType.CLASSROOM) objM.loadClassroomObjects();
        if (currentZone == ZoneType.PRAYER_AREA) objM.loadPrayerAreaObjects();
        if (currentZone == ZoneType.SERVER_ROOM) objM.loadServerRoomObjects();

        // Safe Spawn Coordinates for Fast Travel
        if (currentZone == ZoneType.GROUND) {
            session.getPlayer().xLocation = 2 * tileSize;
            session.getPlayer().yLocation = (maxScreenRow / 2) * tileSize;
        } else if (currentZone == ZoneType.CORRIDOR) {
            session.getPlayer().xLocation = 2 * tileSize;
            session.getPlayer().yLocation = (maxScreenRow / 2) * tileSize;
        } else {
            // Fallback safe spawn
            session.getPlayer().xLocation = 2 * tileSize;
            session.getPlayer().yLocation = 2 * tileSize;
        }
        
        gameState = playState;
        clearKeys();
    }

    private void updateLiveLocation() {
        nearbyDoorName = "";
        int pCol = (session.getPlayer().xLocation + tileSize / 2) / tileSize;
        int pRow = (session.getPlayer().yLocation + tileSize / 2) / tileSize;

        // Dynamic OO Routing from CampusMap
        Zone current = session.getCampusMap().getZone(currentZone);
        if (current == null) return;

        for (Location loc : current.getLocations()) {
            int locCol = (int) loc.getPosition().getX();
            int locRow = (int) loc.getPosition().getY();

            int dx = Math.abs(pCol - locCol);
            int dy = Math.abs(pRow - locRow);

            if (dx <= 2 && dy <= 2) {
                nearbyDoorName = loc.getName();
                return;
            }
        }
    }

    private void checkZoneTransitions() {
        int playerCol = (session.getPlayer().xLocation + tileSize / 2) / tileSize;
        int playerRow = (session.getPlayer().yLocation + tileSize / 2) / tileSize;

        // Dynamic OO Routing
        Zone current = session.getCampusMap().getZone(currentZone);
        if (current != null) {
            for (Location loc : current.getLocations()) {
                int locCol = (int) loc.getPosition().getX();
                int locRow = (int) loc.getPosition().getY();

                // Wider radius: within 1 tile of the door
                boolean hit = (Math.abs(playerCol - locCol) <= 1 && Math.abs(playerRow - locRow) <= 1);

                if (hit) {
                    boolean dirMatch = false;
                    if (loc.getRequiredDirection().equals("up") && keyH.upPressed) dirMatch = true;
                    if (loc.getRequiredDirection().equals("down") && keyH.downPressed) dirMatch = true;
                    if (loc.getRequiredDirection().equals("left") && keyH.leftPressed) dirMatch = true;
                    if (loc.getRequiredDirection().equals("right") && keyH.rightPressed) dirMatch = true;

                    if (dirMatch) {
                        currentZone = loc.getTargetZone();
                        tileM.loadMap();
                        objM.clearObjects();
                        
                        if (currentZone == ZoneType.CAFETERIA) objM.loadCafeteriaObjects();
                        if (currentZone == ZoneType.LIBRARY) objM.loadLibraryObjects();
                        if (currentZone == ZoneType.CLASSROOM) objM.loadClassroomObjects();
                        if (currentZone == ZoneType.PRAYER_AREA) objM.loadPrayerAreaObjects();
                        if (currentZone == ZoneType.SERVER_ROOM) objM.loadServerRoomObjects();
                        if (currentZone == ZoneType.AI_LAB) objM.loadAILabObjects();
                        
                        // >>> START NEW CODE
                        session.getPlayer().xLocation = (int)loc.getTargetSpawnPosition().getX();
                        session.getPlayer().yLocation = (int)loc.getTargetSpawnPosition().getY();
                        session.getPlayer().setCurrentZoneId(currentZone.ordinal());
                        
                        // Start New Zone Background Music Undertale-style
                        soundM.playZoneMusic(currentZone);
                        clearKeys();
                        return; // Transition handled
                    }
                }
            }
        }
    }

    private void checkInteractions() {
        if (keyH.ePressed) {
            session.onInteract(); // Forward to session
            
            // Interacting with Cafe Counter (Top side)
            if (currentZone == ZoneType.CAFETERIA && session.getPlayer().yLocation < tileSize * 2) {
                gameState = cafeMenuState;
                gameUI.commandNum = 0; // Reset menu selection
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
            gameUI.draw(g2);
        } else {
            tileM.draw(g2);
            objM.draw(g2);
            session.getPlayer().draw(g2);
            gameUI.draw(g2);
        }
        
        g2.dispose();
    }
}
