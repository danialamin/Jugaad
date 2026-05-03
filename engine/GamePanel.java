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
    public final int controlsState = 10; // NEW!

    public PhaseOneState phaseOneState = PhaseOneState.CS_CLASS_REQUIRED;
    public ZoneType activeLectureZone = null;
    public boolean prayedThisPhase = false;
    public int phaseEndingTicks = 0;
    public boolean csClassroomLocked = false; // Permanently locks after leaving CS-101
    public int libraryStudyStage = 0; // Progressive tiredness: 0=not started, 1-3=studying, 4=sleep
    public int cafeteriaUncleStage = 0; // Progressive uncle dialogue: 0-4 stages

    public void resetPhaseOneFlow() {
        phaseOneState = PhaseOneState.CS_CLASS_REQUIRED;
        activeLectureZone = null;
        prayedThisPhase = false;
        phaseEndingTicks = 0;
        csClassroomLocked = false;
        libraryStudyStage = 0;
        cafeteriaUncleStage = 0;
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
        switch (phaseOneState) {
            case CS_CLASS_REQUIRED:
                return "Goal: reach Sir Shehryrar's CS class";
            case AI_LAB_REQUIRED:
                return "Goal: go to AI Lab with Sir Shams Farooq";
            case FREE_ROAM_OPTIONAL:
                return "Optional: pray, then study in the library";
            default:
                return "";
        }
    }

    public String getPhaseHintText() {
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

            if (phaseOneState == PhaseOneState.PHASE_1_ENDING) {
                phaseEndingTicks++;
                if (phaseEndingTicks > FPS * 3) {
                    phaseOneState = PhaseOneState.PHASE_1_DONE;
                }
                clearKeys();
            } else if (phaseOneState == PhaseOneState.PHASE_1_DONE) {
                clearKeys();
            } else if (keyH.escapePressed) {
                gameState = pauseState;
                keyH.escapePressed = false;
            } else if (keyH.pPressed) {
                // UC3: phone from Normal Mode gameplay (diagram: Player owns Phone).
                keyH.pPressed = false;
                session.getPlayer().getPhone().open();
                gameUI.openPhoneMenu();
                gameState = phoneState;
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
            gameUI.updateQuizScreen();
        } else if (gameState == phoneState) {
            gameUI.updatePhoneMenu(session);
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
                        ZoneType target = loc.getTargetZone();

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
                                gameUI.startDialogue("You|Leaving before class? Bold plan. Terrible plan.");
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

                        currentZone = target;
                        tileM.loadMap();
                        objM.clearObjects();

                        if (currentZone == ZoneType.CAFETERIA) objM.loadCafeteriaObjects();
                        if (currentZone == ZoneType.LIBRARY) objM.loadLibraryObjects();
                        if (currentZone == ZoneType.CLASSROOM) objM.loadClassroomObjects();
                        if (currentZone == ZoneType.PRAYER_AREA) objM.loadPrayerAreaObjects();
                        if (currentZone == ZoneType.SERVER_ROOM) objM.loadServerRoomObjects();
                        if (currentZone == ZoneType.AI_LAB) objM.loadAILabObjects();
                        if (currentZone == ZoneType.GROUND) objM.loadGroundObjects();
                        
                        // >>> START NEW CODE
                        session.getPlayer().xLocation = (int)loc.getTargetSpawnPosition().getX();
                        session.getPlayer().yLocation = (int)loc.getTargetSpawnPosition().getY();
                        session.getPlayer().setCurrentZoneId(currentZone.ordinal());

                        // Start New Zone Background Music
                        soundM.playZoneMusic(currentZone);
                        clearKeys();

                        if (currentZone == ZoneType.CLASSROOM) {
                            gameUI.startDialogue("Classroom|CS-101 with Shehryrar Rashid. Find an empty seat before the attendance portal becomes dramatic.");
                            gameState = dialogueState;
                        } else if (currentZone == ZoneType.AI_LAB) {
                            gameUI.startDialogue("AI Lab|Welcome to Artificial Intelligence. Grab a seat quickly.");
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

            // Interacting with Cafe Counter (Top side)
            if (currentZone == ZoneType.CAFETERIA && session.getPlayer().yLocation < tileSize * 2) {
                gameState = cafeMenuState;
                gameUI.commandNum = 0;
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
                                String indexStr = f.name.replace("student_desk_", "");
                                int index = Integer.parseInt(indexStr);
                                String[] studentDialogues = {
                                    "Student|Bhai, seat taken. My GPA is already on life support.",
                                    "Student|Did you understand the assignment? I understood the font.",
                                    "Student|Careful, my laptop is compiling and I am emotionally involved.",
                                    "Student|Quiz already? I opened the slides in spirit.",
                                    "Student|Flex is slow again. Tradition is alive.",
                                    "Student|Move along, please. This chair is reserved for stress.",
                                    "Student|I slept two hours. If I blink, wake me after finals.",
                                    "Student|The TA said it is easy, which means it is not.",
                                    "Student|Cafe biryani sold out. Campus morale is down 30 percent.",
                                    "Student|I just followed the crowd and hoped it was my class."
                                };
                                String[] postCsDialogues = {
                                    "Student|AI Lab next. First door on the top side. Very treasure-hunt energy.",
                                    "Student|Sir Shams is waiting. Take the AI Lab door before it gets crowded.",
                                    "Student|CS survived. AI Lab now. My brain has filed a complaint.",
                                    "Student|Go to AI Lab, bhai. First door on the top side.",
                                    "Student|I am going to pretend I revised search algorithms.",
                                    "Student|Sir Shehryrar said AI Lab. I am not arguing with attendance.",
                                    "Student|The AI Lab door is open now. Campus logic is beautiful.",
                                    "Student|See you in AI Lab. Bring whatever confidence is left."
                                };
                                String[] lines = currentZone == ZoneType.AI_LAB ? studentDialogues : studentDialogues;
                                if (phaseOneState == PhaseOneState.AI_LAB_REQUIRED) lines = postCsDialogues;
                                String msg = (index < lines.length) ? lines[index] : "Student|Find an empty seat, bhai.";
                                gameUI.startDialogue(msg);
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

        // Use lecture queue system like classroom lectures
        gameUI.startLibrarySleepSequence();
        session.getPlayer().getStats().updateEnergy(-30); // Total energy drain from studying
        session.getPlayer().getStats().updateStress(-5);  // Stress relief from resting
        gameUI.queuePhaseOneEnding(); // Queue phase ending after the sequence
        gameState = dialogueState;
    }

    private void interactWithLibrarian() {
        entity.Furniture f = findNearbyNamedFurniture("librarian", tileSize * 3);
        if (f == null) return;
        String[] librarianDialogues = {
            "Librarian|Shh! This is FAST-NU Islamabad library, not your gaming lounge. Rs. 2000 fine if I catch you playing games!",
            "Librarian|Books are due back in two weeks. Late fees apply. We accept cash, not your grandmother's excuses.",
            "Librarian|The CS section is in the back. Try not to spill coffee on the books like last semester's batch did.",
            "Librarian|I've been working here since 2005. I've seen students come and go. Most of them go to switch their major to BBA.",
            "Librarian|The WiFi password changes every semester. Last one was 'FASTNU2024'. Ask IT, not me.",
            "Librarian|No sleeping on the tables! This is a library, not a hostel. Go to the prayer area if you're that tired.",
            "Librarian|Group study is allowed, but if I hear you discussing PUBG strategies, you're out."
        };
        int index = (int)(Math.random() * librarianDialogues.length);
        gameUI.startDialogue(librarianDialogues[index]);
        gameState = dialogueState;
    }

    private void interactWithHaiderRamzan() {
        entity.Furniture f = findNearbyNamedFurniture("haider_ramzan", tileSize * 3);
        if (f == null) return;
        String[] haiderDialogues = {
            "Haider Ramzan|Yo! Just grinding some code on my laptop. The floor is surprisingly comfortable once your legs go numb.",
            "Haider Ramzan|Have you seen the new commits on the class repo? Someone actually documented their code. Legend.",
            "Haider Ramzan|I'm skipping the next class. Not to sleep, just... strategic energy management.",
            "Haider Ramzan|My laptop battery is at 15%. This is my villain origin story.",
            "Haider Ramzan|Bro, help me with this assignment. I'll buy you biryani from the cafe. Deal?"
        };
        int index = (int)(Math.random() * haiderDialogues.length);
        gameUI.startDialogue(haiderDialogues[index]);
        gameState = dialogueState;
    }

    private void interactWithCafeteriaUncle() {
        entity.Furniture f = findNearbyNamedFurniture("cafeteria_uncle", tileSize * 3);
        if (f == null) return;

        // Use lecture queue system like classroom lectures for Iran-US war backstory
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
