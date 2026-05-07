package engine;

// Classroom seating + staged lecture/quiz orchestration aligns with UC2 «Attend Class & Solve Quiz»
// and Classroom→Quiz linkage in SummarizedData/ClassDiagramPUML.txt (diagram band 3, activity).

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
    // Phase 1 runtime flow follows UC2, UC5, UC8 and the ClassDiagram responsibilities:
    // GamePanel coordinates UI/zone input while GameSession owns Player, CampusMap, stats and KarmaTracker.
    public enum PhaseOneState {
        CS_CLASS_REQUIRED,
        AI_LAB_REQUIRED,
        FREE_ROAM_OPTIONAL,
        PHASE_1_ENDING,
        PHASE_1_DONE
    }

    public enum PhaseTwoState {
        CUTSCENE,       // Transition cutscene playing
        EXPLORE,        // Free roam in zombie world
        IN_COMBAT,      // Fighting a zombie
        IN_QUIZ,        // Answering a quiz question
        BOSS_INTRO,     // Final boss cutscene
        BOSS_DODGE,     // Dodging boss projectiles
        BOSS_FIGHT,     // Final boss in server room
        IN_TERMINATE_MINIGAME, // Terminate attack timing game
        GAME_ENDING     // Epilogue screen
    }

    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale; // 32x32
    public final int maxScreenCol = 24; // 24 * 32 = 768 pixels
    public final int maxScreenRow = 18; // 18 * 32 = 576 pixels
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    public static final int FPS = 60;

    public ZoneType currentZone = ZoneType.LIBRARY;
    public int currentClassroomId = -1;
    public int enteredFromDoorCol = -1;
    public boolean enteredFromTopDoor = false;

    public TileManager tileM = new TileManager(this);
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
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int cafeMenuState = 4;
    public final int optionsState = 5;
    public final int introDialogueState = 6;
    public final int mapState = 7;
    public final int quizState = 8;
    public final int phoneState = 9;
    public final int controlsState = 10;
    public final int combatState = 11;
    public final int endingState = 12;
    public final int gameOverState = 13;

    public PhaseOneState phaseOneState = PhaseOneState.CS_CLASS_REQUIRED;
    public ZoneType activeLectureZone = null;
    public boolean prayedThisPhase = false;
    public int phaseEndingTicks = 0;
    public boolean csClassroomLocked = false;
    public int libraryStudyStage = 0;
    public int cafeteriaUncleStage = 0;
    public boolean cafeUncleDefeated = false;
    public boolean faizanDefeated = false;
    public boolean javeriaDefeated = false;
    public boolean serverRoomUnlocked = false;
    public boolean loreComputerRead = false;
    public boolean easterEggTriggered = false;
    public int haiderInteractionCount = 0;
    // Phase 2 intro state
    public int phase2IntroPart = 0; // 0=not started, 1=woke up, 2=scream, 3=lobby, 4=done

    // --- PHASE 2 (ZOMBIE MODE) STATE ---
    public boolean zombieMode = false;
    public PhaseTwoState phaseTwoState = PhaseTwoState.CUTSCENE;
    public int zombieCutsceneTicks = 0;
    public int bossIntroTicks = 0;
    public int bossDodgeTicks = 0;
    public entity.Furniture hiddenBoss = null;
    public java.util.List<entity.Flare> flares = new java.util.concurrent.CopyOnWriteArrayList<>();
    
    public entity.Enemy currentCombatEnemy = null;
    public String combatEnemyDisplayName = "";
    public String combatEnemyInternalName = ""; // For tracking defeated zombies
    public combat.CombatChallenge currentChallenge;
    public controller.CombatController combatController = new controller.CombatController();
    public java.util.Set<String> defeatedZombies = new java.util.HashSet<>();
    public int serverBossHp = 300;

    public void resetPhaseOneFlow() {
        phaseOneState = PhaseOneState.CS_CLASS_REQUIRED;
        activeLectureZone = null;
        prayedThisPhase = false;
        phaseEndingTicks = 0;
        csClassroomLocked = false;
        libraryStudyStage = 0;
        cafeteriaUncleStage = 0;
    }

    public void resetZombieModeState() {
        zombieMode = false;
        phaseTwoState = PhaseTwoState.CUTSCENE;
        zombieCutsceneTicks = 0;
        bossIntroTicks = 0;
        bossDodgeTicks = 0;
        hiddenBoss = null;
        flares.clear();
        currentCombatEnemy = null;
        combatEnemyDisplayName = "";
        combatEnemyInternalName = "";
        currentChallenge = null;
        defeatedZombies.clear();
        serverBossHp = 300;
        phase2IntroPart = 0;
        soundM.setZombieMode(false);
    }

    public void beginLectureForCurrentZone() {
        activeLectureZone = currentZone;
    }

    public void completeCurrentLectureQuiz() {
        if (activeLectureZone == ZoneType.CLASSROOM) {
            phaseOneState = PhaseOneState.AI_LAB_REQUIRED;
        } else if (activeLectureZone == ZoneType.AI_LAB) {
            phaseOneState = PhaseOneState.FREE_ROAM_OPTIONAL;
        }
        activeLectureZone = null;
    }

    public void startPhaseOneEnding() {
        phaseOneState = PhaseOneState.PHASE_1_ENDING;
        phaseEndingTicks = 0;
        gameState = playState;
        clearKeys();
    }

    public String getPhaseGoalText() {
        if (zombieMode) {
            switch (phaseTwoState) {
                case EXPLORE:
                    if (session.getPlayer().getInventory().hasItem(2)) {
                        return "Use Server Card at Server Room";
                    } else if (session.getPlayer().getInventory().hasItem(1)) {
                        return "Corridor: defeat TA for Server Card";
                    } else if (javeriaDefeated) {
                        return "Corridor: find the Server Card";
                    } else if (faizanDefeated) {
                        return "Defeat Miss Javeria (CS classroom)";
                    } else {
                        return "Go to CS classroom in Corridor";
                    }
                case IN_COMBAT: return "COMBAT: 1=Debug  2=Terminate";
                case IN_QUIZ: return "QUIZ: press 1, 2, 3 or 4";
                case BOSS_FIGHT: return "BOSS: Defeat the Corrupted AI";
                case GAME_ENDING: return "";
                default: return "";
            }
        }
        switch (phaseOneState) {
            case CS_CLASS_REQUIRED:  return "Go to CS class (Sir Shehryar)";
            case AI_LAB_REQUIRED:    return "Go to AI Lab (Sir Shams)";
            case FREE_ROAM_OPTIONAL: return "Optional: pray / study in library";
            default: return "";
        }
    }

    public String getPhaseHintText() {
        if (zombieMode) {
            return "Your choices define how this ends. Choose wisely.";
        }
        switch (phaseOneState) {
            case CS_CLASS_REQUIRED:
                return "FAST-NU Islamabad waits for nobody.";
            case AI_LAB_REQUIRED:
                return "AI Lab door is open now.";
            case FREE_ROAM_OPTIONAL:
                return "Free roam is open. Prayer is optional.";
            default:
                return "";
        }
    }
    // ─── DEBUG: Set to true to skip title + Phase 1 and jump straight to Zombie Mode ───
    public static final boolean DEBUG_SKIP_TO_ZOMBIE = false;  // ← flip to true to skip to Phase 2 for testing

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        session = new controller.GameSession();
        session.startNewGame();
        
        // SD-UC9/UC10/UC12: CombatController needs a Player reference
        combatController.setPlayer(session.getPlayer());
        
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

        if (DEBUG_SKIP_TO_ZOMBIE) {
            // Skip everything — jump straight into Zombie Mode for testing
            phaseOneState = PhaseOneState.PHASE_1_DONE;
            zombieMode = true;
            phaseTwoState = PhaseTwoState.EXPLORE;
            session.switchToZombieMode();
            currentZone = ZoneType.SERVER_ROOM;
            tileM.loadMap();
            objM.loadZombieServerRoomObjects();
            objM.filterDefeatedZombies(defeatedZombies);
            session.getPlayer().xLocation = tileSize * 2; // Spawn near door
            session.getPlayer().yLocation = (maxScreenRow / 2) * tileSize;
            gameUI.startDialogue("System|WARNING: You have entered the Server Room.\n" +
                                 "System|The Corrupted AI Boss is heavily armored.\n" +
                                 "System|Keep dodging it and interact with the Main Server (press E or Enter) repeatedly to WEAKEN it before combat!");
            gameState = dialogueState;  // Skip title screen too
            System.out.println("[DEBUG] Skipped to Zombie Mode in Server Room");
        } else {
            gameState = titleState;
        }
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

            if (phaseOneState == PhaseOneState.PHASE_1_ENDING) {
                phaseEndingTicks++;
                if (phaseEndingTicks > FPS * 3) {
                    phaseOneState = PhaseOneState.PHASE_1_DONE;
                }
                clearKeys();
            } else if (phaseOneState == PhaseOneState.PHASE_1_DONE && !zombieMode) {
                zombieCutsceneTicks++;
                if (zombieCutsceneTicks > FPS * 5) {
                    zombieMode = true;
                    phase2IntroPart = 1;
                    phaseTwoState = PhaseTwoState.CUTSCENE;
                    session.switchToZombieMode();
                    soundM.setZombieMode(true);
                    currentZone = ZoneType.LIBRARY;
                    tileM.loadMap();
                    objM.loadZombieLibraryObjects();
                    objM.filterDefeatedZombies(defeatedZombies);
                    session.getPlayer().xLocation = (maxScreenCol / 2) * tileSize;
                    session.getPlayer().yLocation = (maxScreenRow / 2) * tileSize;
                    soundM.playZoneMusic(currentZone);
                    startPhase2IntroSequence();
                }
                clearKeys();
            } else if (zombieMode && phaseTwoState == PhaseTwoState.GAME_ENDING) {
                clearKeys(); // Ending screen handles itself
            } else if (keyH.escapePressed) {
                gameState = pauseState;
                keyH.escapePressed = false;
            } else if (keyH.pPressed && !zombieMode) {
                keyH.pPressed = false;
                session.getPlayer().getPhone().open();
                gameUI.openPhoneMenu();
                gameState = phoneState;
            } else {
                session.getPlayer().update();
                checkZoneTransitions();
                if (zombieMode) {
                    // Update zombie chasing AI every frame
                    int px = session.getPlayer().xLocation + tileSize / 2;
                    int py = session.getPlayer().yLocation + tileSize / 2;
                    objM.updateZombies(px, py);
                    // Check if any zombie touched the player
                    checkZombieTouchEncounters();
                }
                checkInteractions();
                updateLiveLocation();
            }
        } else if (gameState == pauseState) {
            soundM.playCustomMusic("assets/sound/pauseTheme.wav");
            gameUI.updatePauseScreen();
        } else if (gameState == optionsState) {
            soundM.playCustomMusic("assets/sound/pauseTheme.wav");
            gameUI.updateOptionsScreen();
        } else if (gameState == controlsState) {
            soundM.playCustomMusic("assets/sound/pauseTheme.wav");
            gameUI.updateControlsScreen(); // Add this method
        } else if (gameState == cafeMenuState) {
            gameUI.updateCafeMenu();
        } else if (gameState == dialogueState || gameState == introDialogueState) {
            gameUI.updateDialogueScreen();
        } else if (gameState == quizState) {
            // SD-UC2 line 36-37 / SD-UC3: player may open phone DURING quiz (cheat path)
            if (keyH.pPressed && session.isLectureQuizActive()) {
                keyH.pPressed = false;
                session.getPlayer().getPhone().open();
                // Phone used during quiz: isDuringQuiz=true → cheat penalty applied via beginAiAssistSubphase
                gameUI.openPhoneMenu();
                gameState = phoneState;
            } else {
                gameUI.updateQuizScreen();
            }
        } else if (gameState == phoneState) {
            gameUI.updatePhoneMenu(session);
        } else if (gameState == combatState) {
            gameUI.updateCombatScreen();
        } else if (gameState == gameOverState) {
            if (keyH.enterPressed || keyH.ePressed || keyH.escapePressed) {
                keyH.enterPressed = false;
                keyH.ePressed = false;
                keyH.escapePressed = false;
                gameState = titleState;
            }
        } else if (gameState == endingState) {
            if (keyH.enterPressed || keyH.ePressed || keyH.escapePressed) {
                keyH.enterPressed = false;
                keyH.ePressed = false;
                keyH.escapePressed = false;
                gameState = titleState;
            }
        }
    }

    private void clearKeys() {
        keyH.upPressed = false;
        keyH.downPressed = false;
        keyH.leftPressed = false;
        keyH.rightPressed = false;
        keyH.ePressed = false;
        keyH.enterPressed = false;
        keyH.num1Pressed = false;
        keyH.num2Pressed = false;
        keyH.num3Pressed = false;
        keyH.mPressed = false;
        keyH.pPressed = false;
        keyH.lPressed = false;
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

        // Also check nearby furniture (zombies, NPCs, objects) for proximity labels
        int pX = session.getPlayer().xLocation + tileSize / 2;
        int pY = session.getPlayer().yLocation + tileSize / 2;
        for (entity.Furniture f : objM.furnitureList) {
            if (f.name == null || f.name.isEmpty()) continue;
            int fCX = f.x + f.width / 2;
            int fCY = f.y + f.height / 2;
            if (Math.abs(pX - fCX) < tileSize * 3 && Math.abs(pY - fCY) < tileSize * 3) {
                // Convert internal names to display names
                String display = f.name.replace("_", " ");
                if (f.name.startsWith("zombie_")) display = "\u26A0 " + display;
                else if (f.name.equals("final_boss")) display = "\u26A0 FINAL BOSS";
                nearbyDoorName = display;
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
                        ZoneType target = loc.getTargetZone();

                        // ZOMBIE MODE: Skip all Phase 1 gating, apply zombie-specific blocks
                        if (zombieMode) {
                            // Phase 2 intro: intercept first lobby exit to show blood decals then pull back
                            if (phase2IntroPart == 2 && (target == ZoneType.GROUND || target == ZoneType.WALKWAY)) {
                                currentZone = target;
                                tileM.loadMap();
                                objM.loadZombieGroundObjects();
                                session.getPlayer().xLocation = (int) loc.getTargetSpawnPosition().getX();
                                session.getPlayer().yLocation = (int) loc.getTargetSpawnPosition().getY();
                                phase2IntroPart = 3;
                                gameUI.startPhase2LobbyBloodSequence();
                                gameState = dialogueState;
                                clearKeys();
                                return;
                            }
                            // Block AI Lab only (Classroom and Cafe are accessible)
                            if (target == ZoneType.AI_LAB) {
                                gameUI.startDialogue("You|The AI Lab is locked tight. Something's wrong in there.");
                                pushPlayerBack(loc.getRequiredDirection());
                                gameState = dialogueState;
                                clearKeys();
                                return;
                            }
                            if (target == ZoneType.PRAYER_AREA) {
                                gameUI.startDialogue("You|The prayer area is in ruins. I shouldn't go in there.");
                                pushPlayerBack(loc.getRequiredDirection());
                                gameState = dialogueState;
                                clearKeys();
                                return;
                            }
                            if (target == ZoneType.CAFETERIA) {
                                // Cafe is OPEN in zombie mode
                            }
                            // Key locks
                            if (target == ZoneType.CORRIDOR && !session.getPlayer().getInventory().hasItem(1)) {
                                gameUI.startDialogue("System|ACCESS DENIED. Requires: Corridor Keycard.");
                                pushPlayerBack(loc.getRequiredDirection());
                                gameState = dialogueState;
                                clearKeys();
                                return;
                            }
                            if (target == ZoneType.SERVER_ROOM && !session.getPlayer().getInventory().hasItem(2)) {
                                gameUI.startDialogue("System|ACCESS DENIED. Requires: Server Access Card.");
                                pushPlayerBack(loc.getRequiredDirection());
                                gameState = dialogueState;
                                clearKeys();
                                return;
                            }
                            if (target == ZoneType.CLASSROOM) {
                                // Enforce killing librarian before entering CS-101
                                if (loc.getName().equals("CS-101 Classroom")) {
                                    if (!defeatedZombies.contains("zombie_librarian")) {
                                        gameUI.startDialogue("You|The door is jammed. Maybe Mr Amir Rehman back in the Library dropped something useful.");
                                        pushPlayerBack(loc.getRequiredDirection());
                                        gameState = dialogueState;
                                        clearKeys();
                                        return;
                                    }
                                }
                            }
                            // Allow: GROUND, WALKWAY, CORRIDOR, LIBRARY, SERVER_ROOM
                            // (fall through to zone transition below)
                        } else {
                            // ─── NORMAL MODE (Phase 1) ONLY GATING ───
                            
                            if (currentZone == ZoneType.CORRIDOR && target == ZoneType.CLASSROOM && loc.getName().equals("AI Lab")
                                    && phaseOneState == PhaseOneState.AI_LAB_REQUIRED) {
                                target = ZoneType.AI_LAB;
                            }

                            if (currentZone == ZoneType.CORRIDOR && phaseOneState == PhaseOneState.CS_CLASS_REQUIRED) {
                                if (target == ZoneType.CLASSROOM && loc.getName().equals("CS-101 Classroom")) {
                                    gameUI.showGoal = false;
                                } else if (target == ZoneType.CLASSROOM) {
                                    gameUI.startDialogue("You|Wrong room. Sir Shehryrar's CS class is the one you need right now.");
                                    gameState = dialogueState;
                                    clearKeys();
                                    return;
                                }
                            }

                            if (currentZone == ZoneType.CORRIDOR && phaseOneState == PhaseOneState.AI_LAB_REQUIRED) {
                                if (target == ZoneType.AI_LAB) {
                                    // AI Lab is now open for the lecture.
                                } else if (target == ZoneType.CLASSROOM && loc.getName().equals("CS-101 Classroom")) {
                                    gameUI.startDialogue("You|CS-101 is over. That door has entered post-class lockdown.");
                                    gameState = dialogueState;
                                    clearKeys();
                                    return;
                                } else if (target == ZoneType.CLASSROOM) {
                                    gameUI.startDialogue("You|Not this one. Sir Shams Farooq's AI Lab is the first classroom door.");
                                    gameState = dialogueState;
                                    clearKeys();
                                    return;
                                }
                            }

                            if (phaseOneState == PhaseOneState.CS_CLASS_REQUIRED
                                    && (target == ZoneType.CAFETERIA || target == ZoneType.PRAYER_AREA || target == ZoneType.LIBRARY || target == ZoneType.SERVER_ROOM || target == ZoneType.AI_LAB)) {
                                gameUI.startDialogue("You|Tempting, but no. Sir Shehryrar's class is already starting.");
                                gameState = dialogueState;
                                clearKeys();
                                return;
                            }

                            if (phaseOneState == PhaseOneState.AI_LAB_REQUIRED
                                    && (target == ZoneType.CAFETERIA || target == ZoneType.PRAYER_AREA || target == ZoneType.LIBRARY || target == ZoneType.SERVER_ROOM)) {
                                gameUI.startDialogue("You|After CS-101, the AI Lab is next. Freedom is scheduled after Sir Shams.");
                                gameState = dialogueState;
                                clearKeys();
                                return;
                            }

                            if ((currentZone == ZoneType.CLASSROOM || currentZone == ZoneType.AI_LAB) && target == ZoneType.CORRIDOR) {
                                if ((currentZone == ZoneType.CLASSROOM && phaseOneState == PhaseOneState.CS_CLASS_REQUIRED)
                                        || (currentZone == ZoneType.AI_LAB && phaseOneState == PhaseOneState.AI_LAB_REQUIRED)) {
                                    // SD-UC2 line 110-120: skip-class penalty: gpa-0.1, karma-10
                                    entity.StatModifierImpl skipModifier = new entity.StatModifierImpl(-0.1, 0, -10);
                                    session.getPlayer().getStats().applyModifier(skipModifier);
                                    session.getKarmaTracker().deduct(10, "Skipped class");
                                    gameUI.startDialogue("You|Leaving before class? Bold plan. Terrible plan. GPA takes the hit.");
                                    gameState = dialogueState;
                                    clearKeys();
                                    return;
                                }
                            }

                            if ((currentZone == ZoneType.CLASSROOM || currentZone == ZoneType.AI_LAB) && target != currentZone) {
                                session.getPlayer().setSeatedInClass(false);
                                // Lock CS classroom permanently after leaving (if lecture was completed)
                                if (currentZone == ZoneType.CLASSROOM && phaseOneState == PhaseOneState.AI_LAB_REQUIRED) {
                                    csClassroomLocked = true;
                                }
                            }

                            // Block re-entry to locked CS classroom
                            if (target == ZoneType.CLASSROOM && csClassroomLocked) {
                                gameUI.startDialogue("You|CS-101 is over. That classroom has entered post-class lockdown. You cannot go back.");
                                gameState = dialogueState;
                                clearKeys();
                                return;
                            }
                        }

                        currentZone = target;
                        tileM.loadMap();
                        objM.clearObjects();

                        if (zombieMode) {
                            if (currentZone == ZoneType.GROUND) objM.loadZombieGroundObjects();
                            else if (currentZone == ZoneType.WALKWAY) objM.loadZombieWalkwayObjects();
                            else if (currentZone == ZoneType.CORRIDOR) objM.loadZombieCorridorObjects();
                            else if (currentZone == ZoneType.LIBRARY) objM.loadZombieLibraryObjects();
                            else if (currentZone == ZoneType.SERVER_ROOM) objM.loadZombieServerRoomObjects();
                            else if (currentZone == ZoneType.CAFETERIA) objM.loadZombieCafeObjects();
                            else if (currentZone == ZoneType.CLASSROOM) objM.loadZombieClassroomObjects();

                            objM.filterDefeatedZombies(defeatedZombies);

                            if (currentZone == ZoneType.SERVER_ROOM && !defeatedZombies.contains("final_boss")) {
                                gameUI.startDialogue("System|WARNING: You have entered the Server Room.\n" +
                                                     "System|The Corrupted AI Boss is heavily armored.\n" +
                                                     "System|Keep dodging it and interact with the Main Server (press E or Enter) repeatedly to WEAKEN it before combat!");
                                gameState = dialogueState;
                                clearKeys();
                            }
                        } else {
                            if (currentZone == ZoneType.CAFETERIA) objM.loadCafeteriaObjects();
                            if (currentZone == ZoneType.LIBRARY) objM.loadLibraryObjects();
                            if (currentZone == ZoneType.CLASSROOM) objM.loadClassroomObjects();
                            if (currentZone == ZoneType.PRAYER_AREA) objM.loadPrayerAreaObjects();
                            if (currentZone == ZoneType.SERVER_ROOM) objM.loadServerRoomObjects();
                            if (currentZone == ZoneType.AI_LAB) objM.loadAILabObjects();
                            if (currentZone == ZoneType.GROUND) objM.loadGroundObjects();
                        }
                        
                        // >>> START NEW CODE
                        session.getPlayer().xLocation = (int)loc.getTargetSpawnPosition().getX();
                        session.getPlayer().yLocation = (int)loc.getTargetSpawnPosition().getY();
                        session.getPlayer().setCurrentZoneId(currentZone.ordinal());

                        // Start New Zone Background Music
                        soundM.playZoneMusic(currentZone);
                        clearKeys();

                        if (!zombieMode && currentZone == ZoneType.CLASSROOM) {
                            gameUI.startDialogue("Classroom|CS-101 with Sir Shehryrar Rashid. Find an empty seat.");
                            gameState = dialogueState;
                        } else if (!zombieMode && currentZone == ZoneType.AI_LAB) {
                            gameUI.startDialogue("AI Lab|AI Lab with Sir Shams Farooq. Grab a seat.");
                            gameState = dialogueState;
                        } else if (zombieMode && currentZone == ZoneType.CAFETERIA) {
                            if (!cafeUncleDefeated) {
                                gameUI.startZombieCafeIntroSequence();
                                gameState = dialogueState;
                            }
                        } else if (zombieMode && currentZone == ZoneType.CLASSROOM) {
                            if (!faizanDefeated) {
                                gameUI.startZombieClassroomIntroSequence();
                                gameState = dialogueState;
                            }
                        } else if (zombieMode && currentZone == ZoneType.SERVER_ROOM) {
                            gameUI.startServerRoomArrivalSequence();
                            gameState = dialogueState;
                        }

                        return; // Transition handled
                    }
                }
            }
        }
    }

    private void checkInteractions() {
        if (keyH.ePressed || keyH.enterPressed) {
            session.onInteract(); // Forward to session

            // Checkpoint interaction
            entity.Furniture cpF = findNearbyNamedFurniture("checkpoint", tileSize * 2);
            if (cpF != null) {
                checkpointSave();
                keyH.ePressed = false;
                keyH.enterPressed = false;
                return;
            }

            // Cafe counter (zombie mode food, normal mode menu)
            if (currentZone == ZoneType.CAFETERIA) {
                entity.Furniture counter = findNearbyNamedFurniture("cafe_counter", tileSize * 3);
                if (counter != null && zombieMode) {
                    if (cafeUncleDefeated) {
                        int healed = 30;
                        session.getPlayer().heal(healed);
                        session.getPlayer().getStats().updateStress(-5);
                        gameUI.startDialogue("You|Food left on the counter. You eat quickly. (+" + healed + " HP)");
                    } else {
                        gameUI.startDialogue("You|Something's blocking the counter...");
                    }
                    gameState = dialogueState;
                    keyH.ePressed = false;
                    keyH.enterPressed = false;
                    return;
                }
                if (!zombieMode && session.getPlayer().yLocation < tileSize * 2) {
                    gameState = cafeMenuState;
                    gameUI.commandNum = 0;
                }
            }

            // Server room hack mechanic
            if (currentZone == ZoneType.SERVER_ROOM && zombieMode) {
                entity.Furniture server = findNearbyNamedFurniture("main_server", tileSize * 2);
                if (server != null) {
                    if (serverBossHp > 0) {
                        serverBossHp -= 50;
                        if (serverBossHp <= 0) {
                            // Killed via hacking — no combat needed!
                            serverBossHp = 0;
                            // Remove physical boss sprite
                            objM.furnitureList.removeIf(f -> "final_boss".equals(f.name));
                            defeatedZombies.add("final_boss");
                            session.getKarmaTracker().add(30, "Hacked the AI to death — pure intellect");
                            gameUI.startDialogue("System|SYSTEM FAILURE: Corrupted AI destroyed via terminal exploit.\nYou|I... I actually killed it from the server. No combat needed.");
                            gameState = dialogueState;
                            // Trigger ending after dialogue
                            phaseTwoState = PhaseTwoState.GAME_ENDING;
                            resolveEnding();
                        } else {
                            gameUI.startDialogue("System|Terminal bypassed. Corrupted AI health compromised (-50 HP). Remaining: " + serverBossHp);
                            gameState = dialogueState;
                        }
                    } else {
                        gameUI.startDialogue("System|Terminal locked. AI is already at critical levels.");
                        gameState = dialogueState;
                    }
                    keyH.ePressed = false;
                    keyH.enterPressed = false;
                    return;
                }
                
                entity.Furniture lore = findNearbyNamedFurniture("lore_computer", tileSize * 2);
                if (lore != null) {
                    if (!loreComputerRead) {
                        loreComputerRead = true;
                        gameUI.startLoreComputerSequence();
                    } else {
                        gameUI.startDialogue("System|FAST-FLEX AI v2.1 — logs already read.");
                    }
                    gameState = dialogueState;
                    keyH.ePressed = false;
                    keyH.enterPressed = false;
                    return;
                }
                entity.Furniture terminal = findNearbyNamedFurniturePrefixed("server_terminal_", tileSize * 2);
                if (terminal != null) {
                    gameUI.startDialogue("Terminal|This workstation is locked. No response.");
                    gameState = dialogueState;
                    keyH.ePressed = false;
                    keyH.enterPressed = false;
                    return;
                }
            }

            if ((currentZone == ZoneType.CLASSROOM && phaseOneState == PhaseOneState.CS_CLASS_REQUIRED)
                    || (currentZone == ZoneType.AI_LAB && phaseOneState == PhaseOneState.AI_LAB_REQUIRED)) {
                int pX = session.getPlayer().xLocation + tileSize / 2;
                int pY = session.getPlayer().yLocation + tileSize / 2;

                for (entity.Furniture f : objM.furnitureList) {
                    if (f.name != null && !f.name.isEmpty()) {
                        int fCenterX = f.x + f.width / 2;
                        int fCenterY = f.y + f.height / 2;
                        // Checking distance
                        if (Math.abs(pX - fCenterX) < tileSize * 1.5 && Math.abs(pY - fCenterY) < tileSize * 1.5) {
                            if (f.name.equals("teacher")) {
                                if (currentZone == ZoneType.AI_LAB) {
                                    gameUI.startDialogue("Sir Shams Farooq|Find an empty seat. The lab PCs are awake, so we should be too.");
                                } else {
                                    gameUI.startDialogue("Shehryrar Rashid|Find a seat, please. FAST attendance has no sympathy setting.");
                                }
                                gameState = dialogueState;
                            } else if (f.name.startsWith("student_desk_")) {
                                String rawName = f.name.replace("student_desk_", "").replace("_", " ");
                                String[] studentDialogues = {
                                    rawName + "|Bhai, seat taken. My GPA is already on life support.",
                                    rawName + "|Did you understand the assignment? I understood the font.",
                                    rawName + "|Careful, my laptop is compiling and I am emotionally involved.",
                                    rawName + "|Quiz already? I opened the slides in spirit.",
                                    rawName + "|Flex is slow again. Tradition is alive.",
                                    rawName + "|Move along. This chair is reserved for stress.",
                                    rawName + "|I slept two hours. If I blink, wake me after finals.",
                                    rawName + "|The TA said it is easy. Classic.",
                                    rawName + "|Cafe biryani sold out. Campus morale: critical.",
                                    rawName + "|I just followed the crowd and hoped it was my class."
                                };
                                String[] postCsDialogues = {
                                    rawName + "|AI Lab next. First door on the top side.",
                                    rawName + "|Sir Shams is waiting. Go before it gets crowded.",
                                    rawName + "|CS survived. AI Lab now. My brain has filed a complaint.",
                                    rawName + "|Go to AI Lab, bhai. Top side, first door."
                                };
                                String[] lines = (phaseOneState == PhaseOneState.AI_LAB_REQUIRED) ? postCsDialogues : studentDialogues;
                                int idx = Math.abs(rawName.hashCode()) % lines.length;
                                gameUI.startDialogue(lines[idx]);
                                gameState = dialogueState;
                            } else if (f.name.equals("empty_desk")) {
                                int ts = tileSize;
                                if (currentZone == ZoneType.AI_LAB) {
                                    // AI Lab: position player 2 steps behind (below) the table
                                    session.getPlayer().setXLocation(f.x + (f.width - ts) / 2);
                                    session.getPlayer().setYLocation(f.y + f.height + 2 * ts); // 2 tiles below table
                                    session.getPlayer().direction = "up"; // facing the desk
                                } else {
                                    // Regular classroom: position on top of desk
                                    session.getPlayer().setXLocation(f.x + (f.width - ts) / 2);
                                    session.getPlayer().setYLocation(f.y + f.height - ts);
                                    session.getPlayer().direction = "down";
                                }
                                session.getPlayer().setSeatedInClass(true);
                                beginLectureForCurrentZone();

                                if(currentZone == ZoneType.AI_LAB) {
                                    gameUI.startAILectureSequence();
                                } else {
                                    gameUI.startClassroomLectureSequence();
                                }

                                gameState = dialogueState;
                            }
                            break;
                        }
                    }
                }
            }

            if (currentZone == ZoneType.CLASSROOM && phaseOneState == PhaseOneState.AI_LAB_REQUIRED) {
                interactWithPostCsClassroom();
            }

            if (currentZone == ZoneType.AI_LAB && phaseOneState == PhaseOneState.FREE_ROAM_OPTIONAL) {
                interactWithPostAILab();
            }

            if (currentZone == ZoneType.PRAYER_AREA && phaseOneState == PhaseOneState.FREE_ROAM_OPTIONAL) {
                interactWithOptionalPrayer();
            }

            if (currentZone == ZoneType.LIBRARY && phaseOneState == PhaseOneState.FREE_ROAM_OPTIONAL) {
                interactWithLibraryStudySpot();
            }

            // Librarian interaction in library (available anytime)
            if (currentZone == ZoneType.LIBRARY) {
                interactWithLibrarian();
            }

            // Haider Ramzan interaction in Ground/Walkway
            if (currentZone == ZoneType.GROUND || currentZone == ZoneType.WALKWAY) {
                interactWithHaiderRamzan();
            }

            // Zombie mode zombie-specific interactions
            if (zombieMode) {
                checkZombieInteractables();
            }

            // Cafeteria Uncle interaction
            if (currentZone == ZoneType.CAFETERIA) {
                interactWithCafeteriaUncle();
            }

            // Server Room Guard interaction - blocks everything
            if (currentZone == ZoneType.SERVER_ROOM) {
                interactWithServerRoomGuard();
            }

            // Consume the key press to avoid rapid firing
            keyH.ePressed = false; 
            keyH.enterPressed = false;
        }
    }

    private void interactWithPostCsClassroom() {
        entity.Furniture f = findNearbyFurniture(tileSize * 2);
        if (f == null || f.name == null || f.name.isEmpty()) return;
        if (f.name.equals("teacher")) {
            gameUI.startDialogue("Shehryrar Rashid|Class is done. Go to Sir Shams Farooq's AI Lab before the corridor gets ideas.");
        } else if (f.name.startsWith("student_desk_")) {
            gameUI.startDialogue("Student|AI Lab next. First door on the top side. Small miracle it is open.");
        } else if (f.name.equals("empty_desk")) {
            gameUI.startDialogue("You|You already survived this seat. No need to emotionally attach.");
        } else {
            return;
        }
        gameState = dialogueState;
    }

    private void interactWithPostAILab() {
        entity.Furniture f = findNearbyFurniture(tileSize * 2);
        if (f == null || f.name == null || f.name.isEmpty()) return;
        if (f.name.equals("teacher")) {
            gameUI.startDialogue("Sir Shams Farooq|The quiz is done. Go pray if you wish, then meet your friends in the library.");
        } else if (f.name.startsWith("student_desk_")) {
            String[] postLabDialogues = {
                "Student|Finally done. The library has better WiFi anyway.",
                "Student|Going to pray first, then hitting the library. See you there?",
                "Student|That heuristic question got me good. Hope you did better.",
                "Student|Sir Shams said the library next. Something about meeting friends there."
            };
            int index = Math.abs(f.name.hashCode()) % postLabDialogues.length;
            gameUI.startDialogue(postLabDialogues[index]);
        } else if (f.name.equals("empty_desk")) {
            gameUI.startDialogue("You|That desk served you well. Time to move on.");
        } else {
            return;
        }
        gameState = dialogueState;
    }

    private void interactWithOptionalPrayer() {
        entity.Furniture f = findNearbyNamedFurniture("empty_prayer_mat", tileSize * 2);
        if (f == null) return;
        if (prayedThisPhase) {
            gameUI.startDialogue("You|You already prayed. The prayer mat does not need a replay button.");
        } else {
            prayedThisPhase = true;
            session.getPlayer().getStats().updateKarma(20);
            session.getKarmaTracker().add(20, "Prayer at Prayer Area");
            gameUI.startDialogue("You|You pray quietly. The campus noise feels a little farther away.");
        }
        gameState = dialogueState;
    }

    private void interactWithLibraryStudySpot() {
        entity.Furniture f = findNearbyNamedFurniture("library_study_spot", tileSize * 2);
        if (f == null) return;
        gameUI.startLibrarySleepSequence();
        session.getPlayer().getStats().updateStress(-5);
        gameUI.queuePhaseOneEnding();
        gameState = dialogueState;
    }

    private void interactWithLibrarian() {
        entity.Furniture f = findNearbyNamedFurniture("librarian", tileSize * 3);
        if (f == null) return;
        String[] librarianDialogues = {
            "Mr Amir Rehman|Shh! This is FAST-NU Islamabad library, not your gaming lounge. Rs. 2000 fine if I catch you playing games!",
            "Mr Amir Rehman|Books are due back in two weeks. Late fees apply. We accept cash, not your grandmother's excuses.",
            "Mr Amir Rehman|The CS section is in the back. Try not to spill coffee on the books like last semester's batch did.",
            "Mr Amir Rehman|I've been working here since 2005. I've seen students come and go. Most of them go to switch their major to BBA.",
            "Mr Amir Rehman|The WiFi password changes every semester. Last one was 'FASTNU2024'. Ask IT, not me.",
            "Mr Amir Rehman|No sleeping on the tables! This is a library, not a hostel. Go to the prayer area if you're that tired.",
            "Mr Amir Rehman|Group study is allowed, but if I hear you discussing PUBG strategies, you're out."
        };
        int index = (int)(Math.random() * librarianDialogues.length);
        gameUI.startDialogue(librarianDialogues[index]);
        gameState = dialogueState;
    }

    private void interactWithHaiderRamzan() {
        entity.Furniture f = findNearbyNamedFurniture("haider_ramzan", tileSize * 3);
        if (f == null) return;
        haiderInteractionCount++;
        if (!easterEggTriggered && haiderInteractionCount >= 6) {
            easterEggTriggered = true;
            gameUI.startEasterEggDialogue("Haider Ramzan");
        } else {
            String[] haiderDialogues = {
                "Haider Ramzan|Yo! Just grinding some code. The floor is surprisingly comfortable once your legs go numb.",
                "Haider Ramzan|Have you seen the class repo? Someone documented their code. Legend.",
                "Haider Ramzan|I'm skipping next class. Strategic energy management.",
                "Haider Ramzan|Laptop battery at 15%. This is my villain origin story.",
                "Haider Ramzan|Help me with this assignment. I'll buy you biryani. Deal?"
            };
            int idx = (int)(Math.random() * haiderDialogues.length);
            gameUI.startDialogue(haiderDialogues[idx]);
        }
        gameState = dialogueState;
    }

    private void interactWithCafeteriaUncle() {
        if (zombieMode) return; // Zombie mode cafe handled separately
        entity.Furniture f = findNearbyNamedFurniture("cafeteria_uncle", tileSize * 3);
        if (f == null) return;
        gameUI.startCafeteriaUncleSequence();
        gameState = dialogueState;
    }

    private void interactWithServerRoomGuard() {
        // Check if player is near the guard first
        entity.Furniture guard = findNearbyNamedFurniture("server_room_guard", tileSize * 4);
        if (guard != null) {
            // First interaction with guard - use queue for separate dialogue boxes
            gameUI.startServerRoomGuardSequence();
            gameState = dialogueState;
            return;
        }

        // Check if player is trying to touch any server room furniture
        entity.Furniture nearbyFurniture = findNearbyFurniture(tileSize * 2);
        if (nearbyFurniture != null && nearbyFurniture.name != null) {
            // Guard screams if you try to touch anything
            if (nearbyFurniture.name.equals("server_rack")) {
                gameUI.startServerRoomGuardRackSequence();
                gameState = dialogueState;
                session.getPlayer().getStats().updateStress(10); // Stress from being yelled at
            } else if (nearbyFurniture.name.equals("server_room_desk")) {
                gameUI.startServerRoomGuardDeskSequence();
                gameState = dialogueState;
                session.getPlayer().getStats().updateStress(10);
            }
            // Wires are small and might not be noticed immediately
        }
    }

    private entity.Furniture findNearbyNamedFurniture(String name, int range) {
        int pX = session.getPlayer().xLocation + tileSize / 2;
        int pY = session.getPlayer().yLocation + tileSize / 2;
        for (entity.Furniture f : objM.furnitureList) {
            if (name.equals(f.name)) {
                int fCenterX = f.x + f.width / 2;
                int fCenterY = f.y + f.height / 2;
                if (Math.abs(pX - fCenterX) < range && Math.abs(pY - fCenterY) < range) {
                    return f;
                }
            }
        }
        return null;
    }

    private entity.Furniture findNearbyFurniture(int range) {
        int pX = session.getPlayer().xLocation + tileSize / 2;
        int pY = session.getPlayer().yLocation + tileSize / 2;
        for (entity.Furniture f : objM.furnitureList) {
            int fCenterX = f.x + f.width / 2;
            int fCenterY = f.y + f.height / 2;
            if (Math.abs(pX - fCenterX) < range && Math.abs(pY - fCenterY) < range) {
                return f;
            }
        }
        return null;
    }

    // ═══════════════════════════════════════════════
    //  ZOMBIE MODE — Combat encounter detection
    // ═══════════════════════════════════════════════

    private void checkZombieTouchEncounters() {
        if (phaseTwoState != PhaseTwoState.EXPLORE) return;
        
        int pX = session.getPlayer().xLocation + tileSize / 2;
        int pY = session.getPlayer().yLocation + tileSize / 2;
        
        entity.Furniture touchedZombie = objM.getZombieTouchingPlayer(
            session.getPlayer().xLocation, session.getPlayer().yLocation, 
            tileSize, tileSize
        );
        
        if (touchedZombie != null) {
            startZombieCombat(touchedZombie);
        }
    }

    private void startZombieCombat(entity.Furniture zombieFurniture) {
        String zombieName = zombieFurniture.name;

        if (zombieName.equals("final_boss")) {
            startFinalBoss();
            return;
        }

        int hp;
        String displayName;
        switch (zombieName) {
            case "zombie_librarian":          displayName = "Mr Amir Rehman (Librarian)";   hp = 60;  break;
            case "zombie_ibtassam_amjad":    displayName = "Ibtassam Amjad (Zombie)";       hp = 80;  break;
            case "zombie_dyen":              displayName = "Dyen Asif (Zombie)";            hp = 80;  break;
            case "zombie_ahmad":             displayName = "Ahmad Hussain — The Grader";    hp = 120; break;
            case "zombie_hooud":             displayName = "Hooud Bin Jawad (Zombie)";      hp = 50;  break;
            case "zombie_cafe_uncle":          displayName = "Cafeteria Uncle (Zombie)";      hp = 90;  break;
            case "zombie_faizan":              displayName = "Faizan — TA (Zombie)";          hp = 100; break;
            case "zombie_javeria":             displayName = "Miss Javeria Imtiaz (Zombie)";  hp = 130; break;
            default:                           displayName = "Unknown Zombie";                hp = 70;  break;
        }

        entity.ZombieEnemy enemy = new entity.ZombieEnemy(displayName, currentZone.ordinal(), hp);
        currentCombatEnemy = enemy;
        combatEnemyDisplayName = displayName;
        combatEnemyInternalName = zombieName;
        combatController.initiateCombat(enemy);
        phaseTwoState = PhaseTwoState.IN_COMBAT;
        objM.furnitureList.remove(zombieFurniture);
        soundM.playFightMusic(zombieName);
        gameUI.startPreFightDialogue(zombieName, hp);
        gameState = dialogueState;
        clearKeys();
    }

    public void beginActualCombat() {
        if (currentCombatEnemy == null) return;
        gameUI.startCombatUI(currentCombatEnemy.getName(), currentCombatEnemy.getMaxHp());
        gameState = combatState;
    }

    public void startPhase2IntroSequence() {
        gameUI.startPhase2WakeUpSequence();
        gameState = dialogueState;
    }

    public void beginJaveriaFight() {
        entity.Furniture javeria = null;
        for (entity.Furniture f : objM.furnitureList) {
            if ("zombie_javeria".equals(f.name)) { javeria = f; break; }
        }
        if (javeria != null) {
            startZombieCombat(javeria);
        } else {
            phaseTwoState = PhaseTwoState.EXPLORE;
            gameState = playState;
        }
    }

    public void completedPhase2WakeupDialogue() {
        phase2IntroPart = 2;
        phaseTwoState = PhaseTwoState.EXPLORE;
        gameState = playState;
    }

    public void completedPhase2LobbyDialogue() {
        currentZone = ZoneType.LIBRARY;
        tileM.loadMap();
        objM.loadZombieLibraryObjects();
        objM.filterDefeatedZombies(defeatedZombies);
        session.getPlayer().xLocation = (maxScreenCol / 2) * tileSize;
        session.getPlayer().yLocation = (maxScreenRow / 2) * tileSize;
        soundM.playZoneMusic(currentZone);
        phase2IntroPart = 4;
        gameUI.startDialogue("You|What am I doing out there. Something is still in the library. I need to deal with it first.");
        gameState = dialogueState;
    }

    public void checkpointSave() {
        state.GameState saveState = buildFullGameState();
        session.saveGameState(saveState);
        gameUI.startDialogue("System|Checkpoint reached. Progress saved.");
        gameState = dialogueState;
    }

    public void restoreFromState(state.GameState gs) {
        if (gs == null) return;
        
        // Restore zone
        try {
            currentZone = gs.getCurrentZoneName() != null 
                ? map.ZoneType.valueOf(gs.getCurrentZoneName()) 
                : map.ZoneType.LIBRARY;
        } catch (IllegalArgumentException e) {
            currentZone = map.ZoneType.LIBRARY;
        }
        
        // Restore story phase
        zombieMode = gs.isZombieMode();
        try {
            phaseOneState = gs.getPhaseOneState() != null 
                ? PhaseOneState.valueOf(gs.getPhaseOneState()) 
                : PhaseOneState.CS_CLASS_REQUIRED;
        } catch (IllegalArgumentException e) {
            phaseOneState = PhaseOneState.CS_CLASS_REQUIRED;
        }
        try {
            phaseTwoState = gs.getPhaseTwoState() != null 
                ? PhaseTwoState.valueOf(gs.getPhaseTwoState()) 
                : PhaseTwoState.CUTSCENE;
        } catch (IllegalArgumentException e) {
            phaseTwoState = PhaseTwoState.CUTSCENE;
        }
        phase2IntroPart = gs.getPhase2IntroPart();
        defeatedZombies = gs.getDefeatedZombies() != null 
            ? new java.util.HashSet<>(gs.getDefeatedZombies()) 
            : new java.util.HashSet<>();
        serverBossHp = gs.getServerBossHp() > 0 ? gs.getServerBossHp() : 300;
        prayedThisPhase = gs.isPrayedThisPhase();
        
        // Restore player position
        session.getPlayer().setXLocation((int) gs.getPosX());
        session.getPlayer().setYLocation((int) gs.getPosY());
        session.getPlayer().setEngineComponents(this, keyH);
        
        // Set up sound mode
        soundM.setZombieMode(zombieMode);
        
        // Load correct map + objects for zone
        tileM.loadMap();
        objM.clearObjects();
        if (zombieMode) {
            if (currentZone == map.ZoneType.GROUND) objM.loadZombieGroundObjects();
            else if (currentZone == map.ZoneType.WALKWAY) objM.loadZombieWalkwayObjects();
            else if (currentZone == map.ZoneType.CORRIDOR) objM.loadZombieCorridorObjects();
            else if (currentZone == map.ZoneType.LIBRARY) objM.loadZombieLibraryObjects();
            else if (currentZone == map.ZoneType.SERVER_ROOM) objM.loadZombieServerRoomObjects();
            else if (currentZone == map.ZoneType.CAFETERIA) objM.loadZombieCafeObjects();
            else if (currentZone == map.ZoneType.CLASSROOM) objM.loadZombieClassroomObjects();
            objM.filterDefeatedZombies(defeatedZombies);
        } else {
            if (currentZone == map.ZoneType.CAFETERIA) objM.loadCafeteriaObjects();
            if (currentZone == map.ZoneType.LIBRARY) objM.loadLibraryObjects();
            if (currentZone == map.ZoneType.CLASSROOM) objM.loadClassroomObjects();
            if (currentZone == map.ZoneType.PRAYER_AREA) objM.loadPrayerAreaObjects();
            if (currentZone == map.ZoneType.SERVER_ROOM) objM.loadServerRoomObjects();
        }
        
        soundM.playZoneMusic(currentZone);
        gameState = playState;
    }

    public state.GameState buildFullGameState() {
        state.GameState gs = new state.GameState();
        gs.setSaveId(1);
        gs.setPosX(session.getPlayer().getXLocation());
        gs.setPosY(session.getPlayer().getYLocation());
        gs.setCurrentZoneName(currentZone.name());
        gs.setModeSnapshot(zombieMode ? mode.GameModeType.ZOMBIE : mode.GameModeType.NORMAL);
        gs.setZombieMode(zombieMode);
        gs.setPhaseOneState(phaseOneState.name());
        gs.setPhaseTwoState(phaseTwoState.name());
        gs.setPhase2IntroPart(phase2IntroPart);
        gs.setDefeatedZombies(new java.util.HashSet<>(defeatedZombies));
        gs.setServerBossHp(serverBossHp);
        gs.setPrayedThisPhase(prayedThisPhase);
        
        gs.setHp(session.getPlayer().getHp());
        gs.setMaxHp(session.getPlayer().getMaxHp());
        if (session.getPlayer().getStats() != null) {
            gs.setGpa(session.getPlayer().getStats().getGPA());
            gs.setEnergy(session.getPlayer().getStats().getEnergy());
            gs.setStress(session.getPlayer().getStats().getStress());
            gs.setKarma(session.getPlayer().getStats().getKarma());
        }
        gs.setTimestamp(new java.util.Date().toString());
        return gs;
    }

    private void startFinalBoss() {
        entity.FinalBoss boss = new entity.FinalBoss();
        boss.takeDamage(boss.getMaxHp() - serverBossHp); // Match the lowered HP from hacking
        currentCombatEnemy = boss;
        combatEnemyDisplayName = "CORRUPTED AI SYSTEM";
        combatController.initiateCombat(boss);
        
        phaseTwoState = PhaseTwoState.IN_COMBAT;
        
        // Remove physical boss
        for (entity.Furniture f : objM.furnitureList) {
            if ("final_boss".equals(f.name)) { objM.furnitureList.remove(f); break; }
        }
        
        soundM.playFightMusic("final_boss");
        gameUI.startPreFightDialogue("final_boss", boss.getHp());
        gameState = dialogueState;
        clearKeys();
    }

    public void startFinalBossAfterLore() {
        startFinalBoss();
    }

    public void handleCombatChoice(combat.CombatMove move) {
        if (currentCombatEnemy == null) return;
        
        if (move == combat.CombatMove.DEBUG) {
            // Start quiz phase
            if (currentCombatEnemy instanceof entity.FinalBoss) {
                int hp = currentCombatEnemy.getHp();
                int phase = hp > 200 ? 1 : hp > 100 ? 2 : 3;
                currentChallenge = combat.CombatChallenge.forBossPhase(phase);
            } else {
                currentChallenge = new combat.CombatChallenge(currentCombatEnemy.getHp() >= 70 ? 3 : currentCombatEnemy.getHp() >= 50 ? 2 : 1);
            }
            phaseTwoState = PhaseTwoState.IN_QUIZ;
            return;
        }

        // TERMINATE execution - start minigame
        if (move == combat.CombatMove.TERMINATE) {
            phaseTwoState = PhaseTwoState.IN_TERMINATE_MINIGAME;
            gameUI.initTerminateMinigame();
            return;
        }

        executeCombatStrategy(move, false);
    }

    public void handleQuizAnswer(int index) {
        if (currentChallenge == null || currentCombatEnemy == null) return;
        boolean passed = currentChallenge.evaluate(index);
        executeCombatStrategy(combat.CombatMove.DEBUG, passed);
    }

    public void handleTerminateMinigame(boolean passed) {
        if (currentCombatEnemy == null) return;
        executeCombatStrategy(combat.CombatMove.TERMINATE, passed);
    }

    private void executeCombatStrategy(combat.CombatMove move, boolean passedQuiz) {
        combatController.initiateCombat(currentCombatEnemy);
        boolean isBoss = currentCombatEnemy instanceof entity.FinalBoss;
        combat.CombatResult result = combatController.executeCombatMove(move, passedQuiz, isBoss);
        combatController.applyResult(result);

        // ── ALWAYS sync the bottom HUD bar immediately after any combat action ──
        if (isBoss && currentCombatEnemy != null) {
            serverBossHp = Math.max(0, currentCombatEnemy.getHp());
        }

        // Apply karma
        if (result.getKarmaChange() != 0) {
            session.getPlayer().getStats().updateKarma(result.getKarmaChange());
            String reason = result.wasDebugUsed() ? "Debug - Pacifist" : "Terminate - Brute Force";
            if (result.getKarmaChange() > 0) {
                session.getKarmaTracker().add(result.getKarmaChange(), reason);
            } else {
                session.getKarmaTracker().deduct(Math.abs(result.getKarmaChange()), reason);
            }
        }
        
        if (result.isVictory()) {
            // ── Final Boss: single HP pool, no phases ──
            if (currentCombatEnemy instanceof entity.FinalBoss) {
                entity.FinalBoss boss = (entity.FinalBoss) currentCombatEnemy;
                serverBossHp = Math.max(0, boss.getHp());
                if (boss.isDefeated()) {
                    // Boss is dead — trigger the ending
                    serverBossHp = 0;
                    resolveEnding();
                    return;
                } else {
                    // Boss still alive — return to overworld chase, fight again when caught
                    gameUI.startDialogue("You|The AI staggers but holds. It's still active. HP: " + serverBossHp);
                    phaseTwoState = PhaseTwoState.BOSS_FIGHT;
                    gameState = dialogueState;
                    return;
                }
            }
            
            // Normal zombie defeated
            String victoryMsg = result.wasDebugUsed()
                ? "You|You solved the challenge. " + combatEnemyDisplayName + " fades back to normal."
                : "You|You overpowered " + combatEnemyDisplayName + " by force.";

            String internalNameCopy = combatEnemyInternalName;
            // Check for key item drops (use internal name — was previously bugged)
            checkZombieDrops(internalNameCopy);

            // Permanently mark this zombie as defeated
            defeatedZombies.add(internalNameCopy);

            currentCombatEnemy = null;
            combatEnemyInternalName = "";
            phaseTwoState = PhaseTwoState.EXPLORE;

            // Special post-fight sequences
            if (internalNameCopy.equals("zombie_librarian")) {
                gameUI.startLibrarianLootSequence();
                gameState = dialogueState;
            } else if (internalNameCopy.equals("zombie_faizan")) {
                // Javeria taunts, then her fight starts automatically
                gameUI.startJaveriaTauntSequence();
                gameState = dialogueState;
            } else {
                gameUI.startDialogue(victoryMsg);
                gameState = dialogueState;
            }
        } else {
            // Player failed — enemy counter-attacks via dodge phase
            String failMsg = result.wasDebugUsed()
                ? "You|Wrong answer. " + combatEnemyDisplayName + " is counter-attacking — dodge!"
                : "You|" + combatEnemyDisplayName + " is still standing. Brace for their counter-attack!";
            gameUI.combatDodgeEnemyType = combatEnemyInternalName;
            gameUI.combatDodgePending = true;
            gameUI.showCombatResult(failMsg);
            phaseTwoState = PhaseTwoState.IN_COMBAT;
        }
    }

    private void checkZombieDrops(String internalName) {
        switch (internalName) {
            case "zombie_ibtassam_amjad":
                session.getPlayer().getInventory().addItem(new inventory.KeyItem(1, "Corridor Keycard", "Unlocks the Corridor"));
                break;
            case "zombie_javeria":
                session.getPlayer().getInventory().addItem(new inventory.KeyItem(2, "Server Access Card", "Grants access to the Server Room"));
                javeriaDefeated = true;
                break;
            case "zombie_faizan":
                faizanDefeated = true;
                break;
            case "zombie_cafe_uncle":
                cafeUncleDefeated = true;
                break;
        }
    }

    private void pushPlayerBack(String dir) {
        int pushDist = tileSize / 2;
        if (dir.equals("up")) session.getPlayer().yLocation += pushDist;
        if (dir.equals("down")) session.getPlayer().yLocation -= pushDist;
        if (dir.equals("left")) session.getPlayer().xLocation += pushDist;
        if (dir.equals("right")) session.getPlayer().xLocation -= pushDist;
    }

    private void checkZombieInteractables() {
        // nothing extra needed — lore_computer and server_terminal handled above
    }

    private entity.Furniture findNearbyNamedFurniturePrefixed(String prefix, int radius) {
        int px = session.getPlayer().xLocation + tileSize / 2;
        int py = session.getPlayer().yLocation + tileSize / 2;
        for (entity.Furniture f : objM.furnitureList) {
            if (f.name != null && f.name.startsWith(prefix)) {
                int fx = f.x + f.width / 2;
                int fy = f.y + f.height / 2;
                if (Math.abs(px - fx) < radius && Math.abs(py - fy) < radius) return f;
            }
        }
        return null;
    }

    private void resolveEnding() {
        phaseTwoState = PhaseTwoState.GAME_ENDING;
        int karma = session.getKarmaTracker().getTotal();
        String endingType;
        String[] endingQueue;

        if (karma >= 70) {
            endingType = "PACIFIST";
            endingQueue = new String[] {
                "You|The containment patch compiles without a single error.",
                "System|ALARM DEACTIVATED. ALL NODES STABLE. CAMPUS NETWORK REBOOTING...",
                "You|The flicker in the corridor lights dies down. One by one — they come back on.",
                "You|Miss Javeria emerges from Lab-1, confused but alive. A TA is helping Faizan off the floor.",
                "You|Haider Ramzan is at the prayer area, making chai. Mr Amir Rehman is back at his desk arguing with a student.",
                "You|FAST-NU Islamabad is chaotic again. And somehow — that means everything is okay."
            };
        } else if (karma >= 30) {
            endingType = "MIXED";
            endingQueue = new String[] {
                "You|The patch goes through — barely. Half the terminal warnings stay red.",
                "System|PARTIAL RECOVERY. SOME NODES REMAIN UNSTABLE. MONITORING REQUIRED.",
                "You|Miss Javeria and a TA are sitting silently in the corridor. Alive. Not talking.",
                "You|Some doors don't open. Some rooms are empty that shouldn't be.",
                "You|You walk out into sunlight. The campus recovers. Slowly."
            };
        } else {
            endingType = "BAD";
            endingQueue = new String[] {
                "You|The patch deploys but the system keeps rejecting it. Error after error.",
                "System|CRITICAL INSTABILITY. ROLLBACK FAILED. CORRUPTION SPREADING TO BACKUP NODES.",
                "You|The building is dark and empty. Your footsteps echo in the corridor.",
                "You|A sign on the CS-101 door reads: 'They were never here. You were never here.'",
                "You|GPA: 0.0. Karma: " + karma + ". The campus forgets you ever existed."
            };
            soundM.setBadEndingMode(true);
            soundM.playZoneMusic(currentZone);
        }

        String epilogue;
        if (endingType.equals("PACIFIST")) {
            epilogue = "You chose intellect and mercy at every turn. The campus healed completely. Karma: " + karma;
        } else if (endingType.equals("MIXED")) {
            epilogue = "A mix of force and compassion. The campus recovered — but some scars remain. Karma: " + karma;
        } else {
            epilogue = "Force was your only answer. The corruption spread further. Karma: " + karma;
        }

        gameUI.setLectureQueue(endingQueue, false);
        gameUI.startDialogue(endingQueue[0]);
        gameUI.showEndingScreen(endingType, epilogue, karma);
        gameState = dialogueState;
        currentCombatEnemy = null;
    }

    public void finalizeEnding() {
        state.EndingResolver resolver = new state.EndingResolver();
        state.EndingResolver.EndingType ending = resolver.resolve(session.getKarmaTracker());
        String epilogue = resolver.buildEpilogueText(ending);
        gameUI.showEndingScreen(ending.name(), epilogue, session.getKarmaTracker().getTotal());
        gameState = endingState;
    }

    private void updateFlares() {
        if (Math.random() < 0.10) { // Spawn rate
            float sx = (maxScreenCol / 2.0f) * tileSize;
            float sy = tileSize * 5.0f;
            float targetX = session.getPlayer().xLocation + tileSize / 2.0f;
            float targetY = session.getPlayer().yLocation + tileSize / 2.0f;
            float dx = targetX - sx;
            float dy = targetY - sy;
            float len = (float)Math.sqrt(dx*dx + dy*dy);
            if (len > 0) {
                dx = (dx/len) * 5.0f;
                dy = (dy/len) * 5.0f;
            } else {
                dy = 5.0f;
            }
            flares.add(new entity.Flare(sx, sy, dx, dy));
        }
        
        java.awt.geom.Rectangle2D.Float pRect = new java.awt.geom.Rectangle2D.Float(
            session.getPlayer().xLocation + 8, session.getPlayer().yLocation + 8, tileSize - 16, tileSize - 16
        );
        
        for (entity.Flare f : flares) {
            f.update();
            if (f.x < 0 || f.x > screenWidth || f.y < 0 || f.y > screenHeight) {
                flares.remove(f);
            } else if (f.getBounds().intersects(pRect)) {
                flares.remove(f);
                session.getPlayer().takeDamage(5);
                if (!session.getPlayer().isAlive()) {
                    flares.clear();
                    gameState = gameOverState;
                    break;
                }
            }
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
            for (entity.Flare f : flares) {
                f.draw(g2);
            }
            
            // Draw boss manually if hidden but we are in dodge phase
            if (zombieMode && phaseTwoState == PhaseTwoState.BOSS_DODGE && hiddenBoss != null) {
                hiddenBoss.draw(g2);
            }
            
            gameUI.draw(g2);
        }

        if (gameState == gameOverState) {
            gameUI.drawGameOver(g2);
        }
        
        g2.dispose();
    }
}
