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
    public CampusMap campusMap = new CampusMap();
    Thread gameThread;
    public Player player = new Player(this, keyH);
    public UI ui = new UI(this);

    // Live HUD Data
    public String nearbyDoorName = "";

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
            } else if (keyH.mPressed) {
                gameState = mapState;
                keyH.mPressed = false;
            } else {
                player.update();
                checkZoneTransitions();
                checkInteractions();
                updateLiveLocation();
            }
        } else if (gameState == pauseState) {
            ui.updatePauseScreen();
        } else if (gameState == optionsState) {
            ui.updateOptionsScreen();
        } else if (gameState == cafeMenuState) {
            ui.updateCafeMenu();
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
            player.xLocation = 2 * tileSize;
            player.yLocation = (maxScreenRow / 2) * tileSize;
        } else if (currentZone == ZoneType.CORRIDOR) {
            player.xLocation = 2 * tileSize;
            player.yLocation = (maxScreenRow / 2) * tileSize;
        } else {
            // Fallback safe spawn
            player.xLocation = 2 * tileSize;
            player.yLocation = 2 * tileSize;
        }
        
        gameState = playState;
        clearKeys();
    }

    private void updateLiveLocation() {
        nearbyDoorName = "";
        int pCol = (player.xLocation + tileSize / 2) / tileSize;
        int pRow = (player.yLocation + tileSize / 2) / tileSize;

        // Special: Corridor classroom/AI Lab doors (not in CampusMap)
        if (currentZone == ZoneType.CORRIDOR) {
            if (pRow <= 1 || pRow >= maxScreenRow - 2) {
                if (Math.abs(pCol - 4) <= 1 || Math.abs(pCol - 10) <= 1) {
                    nearbyDoorName = "Classroom";
                    return;
                }
                if (Math.abs(pCol - 16) <= 1 || Math.abs(pCol - 20) <= 1) {
                    nearbyDoorName = (pRow <= 1) ? "AI Lab" : "Classroom";
                    return;
                }
            }
        }

        // Special: Inside a Classroom/AI Lab - exit door
        if (currentZone == ZoneType.CLASSROOM || currentZone == ZoneType.AI_LAB) {
            if (pRow >= maxScreenRow - 2 && Math.abs(pCol - maxScreenCol / 2) <= 1) {
                nearbyDoorName = "Academic Corridor";
                return;
            }
        }

        // Dynamic OO Routing from CampusMap
        Zone current = campusMap.getZone(currentZone);
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
        int playerCol = (player.xLocation + tileSize / 2) / tileSize;
        int playerRow = (player.yLocation + tileSize / 2) / tileSize;

        // Dynamic OO Routing
        Zone current = campusMap.getZone(currentZone);
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
                        
                        player.xLocation = (int)loc.getTargetSpawnPosition().getX();
                        player.yLocation = (int)loc.getTargetSpawnPosition().getY();
                        clearKeys();
                        return; // Transition handled
                    }
                }
            }
        }

        // Special case for Classrooms (dynamic multiple doors)
        if (currentZone == ZoneType.CORRIDOR) {
            boolean isTopDoor = (playerRow <= 1);
            boolean isBottomDoor = (playerRow >= maxScreenRow - 2);
            if ((isTopDoor && keyH.upPressed) || (isBottomDoor && keyH.downPressed)) {
                // Check if near any classroom door column (within 1 tile)
                int nearDoorCol = -1;
                int[] doorCols = {4, 10, 16, 20};
                for (int dc : doorCols) {
                    if (Math.abs(playerCol - dc) <= 1) { nearDoorCol = dc; break; }
                }
                if (nearDoorCol != -1) {
                    currentClassroomId = nearDoorCol + (isTopDoor ? 100 : 200);
                    enteredFromDoorCol = nearDoorCol;
                    enteredFromTopDoor = isTopDoor;

                    if (isTopDoor && (nearDoorCol == 16 || nearDoorCol == 20)) {
                        currentZone = ZoneType.AI_LAB;
                        tileM.loadMap();
                        objM.loadAILabObjects();
                    } else {
                        currentZone = ZoneType.CLASSROOM;
                        tileM.loadMap();
                        objM.loadClassroomObjects();
                    }
                    
                    // Spawn player safely inside
                    player.xLocation = (maxScreenCol / 2) * tileSize;
                    player.yLocation = (maxScreenRow - 3) * tileSize;
                    clearKeys();
                }
            }
        } else if ((currentZone == ZoneType.CLASSROOM || currentZone == ZoneType.AI_LAB) && playerRow >= maxScreenRow - 2 && Math.abs(playerCol - maxScreenCol / 2) <= 1 && keyH.downPressed) {
            currentZone = ZoneType.CORRIDOR;
            tileM.loadMap();
            objM.clearObjects();
            
            // Place back at the correct corridor door
            player.xLocation = enteredFromDoorCol * tileSize;
            player.yLocation = enteredFromTopDoor ? (tileSize * 2) : (maxScreenRow - 3) * tileSize;
            clearKeys();
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
