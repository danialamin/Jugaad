package ui;

// Lecture paging, Quiz encounter, Phone overlay: UC2 + UC3 in SummarizedData/CampusFlex_Use_Cases_Summary.md;
// Classroom / Quiz / Phone roles per SummarizedData/ClassDiagramPUML.txt GRASP bundle.

import engine.GamePanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import entity.Player;

public class UI {

    GamePanel gp;
    Font arial_20, arial_14;
    Font titleFont, titleOptionFont;
    /** Speaker label in dialogue (left of `|` in startDialogue strings). */
    private String dialogueSpeakerLabel;
    private Font dialogueSpeakerFont;
    private Font dialogueBodyFont;
    BufferedImage barFrame;
    BufferedImage hpIcon, energyIcon, stressIcon, gpaIcon;

    // TITLE MENU
    public int titleCommandNum = 0;

    public boolean showGoal = false;
    public String currentDialogueMessage = "";
    public String displayedDialogueMessage = "";
    public int charIndex = 0;
    public int dialogueDelayCounter = 0;
    public boolean isTyping = false;
    public boolean transitionToQuiz = false;
    private boolean phaseEndingAfterDialogue = false;

    // QUIZ: pick mode -> honest attempt OR phone-help route -> multiple choice (Classroom->Quiz, UC2).
    public int quizCommandNum = 0;
    public static final int QUIZ_PHASE_PICK = 0;
    public static final int QUIZ_PHASE_AI_SIM = 1;
    public static final int QUIZ_PHASE_DODGE = 2;
    public static final int QUIZ_PHASE_QUESTIONS = 3;
    public int quizPhase = QUIZ_PHASE_PICK;
    /** Classroom quiz used phone-help path; UC2/UC3 apply a Karma penalty for cheating. */
    private boolean quizAiAssistPath;
    private int aiSimTicksRemaining;
    public String quizQuestion = "";
    public String[] quizOptions = {"", "", "", ""};
    private int currentQuizRound;
    private int quizCorrectRoundCount;
    private int dodgeTicksLeft;
    private int dodgeIframe;
    private int dodgeStressHits;
    private int quizBulletSpawnTicker;
    private final List<QuizBolt> quizBolts = new ArrayList<>();
    private Random quizBoltRng = new Random(20260503);
    private float dodgeBoxX, dodgeBoxY, dodgeBoxW = 272, dodgeBoxH = 128;
    private float soulCentreX, soulCentreY;
    private static final int SOUL_SIZE = 11;
    private static final float SOUL_MOVE = 3.2f;
    /** UC2 lecture pages (dialogue chaining before quiz transition). */
    private String[] lectureQueue;
    private int lectureQueueIndex;
    private boolean lectureQueueTriggersQuiz = false; // true for pre-lecture, false for post-quiz
    /** Harder CS quiz bank; indices {@link #quizRoundIndices}. */
    private static final String[] HARD_QUIZ_PROMPTS = {
        "* ROUND 2 - Contracts.\nSubstitutability without surprise is called:",
        "* ROUND 3 - Java defaults.\nIf two interfaces disagree, the class should:",
        "* ROUND 4 - GRASP.\nGive responsibility to the object that knows the work best:",
        "* ROUND 1 - Memory.\nIn Java, identity and value equality mean:",
        "* ROUND EXTRA - Debugging.\nA minimal failing example should:"
    };
    private static final String[][] HARD_QUIZ_CHOICES = {
        {"Open/Closed only", "Liskov Substitution", "CPU cache encapsulation", "The Single Import Rule"},
        {"Ignore both defaults", "Override and choose clearly", "Delete one interface", "Use abstract classes forever"},
        {"Low coupling, high cohesion", "Make every class huge", "Let one god object decide", "Copy-paste and pray"},
        {"== checks identity; equals checks value", "Primitives behave like objects", "== always compares String text", "new Integer boxes are always =="},
        {"Shrink it until the bug is obvious", "Add another framework", "Restart first, ask later", "Comment out random files"},
    };
    private static final int[] HARD_QUIZ_CORRECT = {1, 1, 0, 0, 0};

    /** AI Lab quiz bank - different from CS-101. */
    private static final String[] AI_QUIZ_PROMPTS = {
        "* ROUND 1 - Search.\nWhat property makes A* optimal and complete?",
        "* ROUND 2 - Heuristics.\nAn admissible heuristic never:",
        "* ROUND 3 - Agents.\nA rational agent always selects the action that:",
        "* ROUND 4 - State Space.\nThe branching factor represents:",
        "* ROUND 5 - Algorithms.\nGreedy best-first search uses which evaluation?"
    };
    private static final String[][] AI_QUIZ_CHOICES = {
        {"Admissible heuristic", "Consistent heuristic", "Both of the above", "Neither"},
        {"Overestimates the true cost", "Underestimates the true cost", "Equals the true cost", "Ignores the goal"},
        {"Maximizes expected utility", "Minimizes computation time", "Uses the most memory", "Explores randomly"},
        {"Number of actions from initial state", "Maximum depth of the search tree", "Number of successors per state", "Total number of states"},
        {"f(n) = g(n) + h(n)", "f(n) = h(n) only", "f(n) = g(n) only", "f(n) = random"},
    };
    private static final int[] AI_QUIZ_CORRECT = {2, 0, 0, 2, 1};

    private int[] quizRoundIndices = {3, 0, 2};
    
    // PAUSE MENU
    public int pauseCommandNum = 0;
    
    // OPTIONS MENU
    public int optionsCommandNum = 0;
    public boolean musicOn = true;
    public boolean sfxOn = true;

    /** UC3 Phone menu */
    private int phoneCommandNum = 0;

    // CAFE MENU
    public int commandNum = 0;
    private BufferedImage[] foodIcons;
    private String[] foodNames = {
        "Biryani (Healthy)", 
        "Fruit Shake (Healthy)", 
        "Fruit Chat (Healthy)", 
        "Samosa (Unhealthy)", 
        "Soda (Unhealthy)", 
        "Lays (Unhealthy)", 
        "Fries (Unhealthy)"
    };
    // Cooldown to prevent rapid key scrolling
    private int keyCooldown = 0;

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_20 = new Font("Arial", Font.BOLD, 20);
        arial_14 = new Font("Arial", Font.PLAIN, 14);
        titleFont = new Font("Monospaced", Font.BOLD, 72);
        titleOptionFont = new Font("Monospaced", Font.BOLD, 30);
        dialogueSpeakerFont = new Font(Font.MONOSPACED, Font.BOLD, 26);
        dialogueBodyFont = new Font(Font.SERIF, Font.ITALIC, 16);
        loadImages();
    }

    private void loadImages() {
        hpIcon = safeLoad("assets/health-health.png");
        energyIcon = safeLoad("assets/energy icon.png");
        stressIcon = safeLoad("assets/stress.png");
        gpaIcon = safeLoad("assets/gpa icon.png");

        foodIcons = new BufferedImage[7];
        foodIcons[0] = safeLoad("assets/foods/biryani.png");
        foodIcons[1] = safeLoad("assets/foods/fruit_shake.png");
        foodIcons[2] = safeLoad("assets/foods/fruit_chat.png");
        foodIcons[3] = safeLoad("assets/foods/samosa.png");
        foodIcons[4] = safeLoad("assets/foods/soda.png");
        foodIcons[5] = safeLoad("assets/foods/lays.png");
        foodIcons[6] = safeLoad("assets/foods/fries.png");
    }

    private BufferedImage safeLoad(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return ImageIO.read(file);
            } else {
                System.out.println("Warning: Asset not found: " + path);
            }
        } catch (IOException e) {
            System.err.println("Error loading " + path + ": " + e.getMessage());
        }
        return null;
    }

    public void draw(Graphics2D g2) {
        if (gp.gameState == gp.titleState) {
            drawTitleScreen(g2);
            return;
        }

        // Draw Live HUD
        if (gp.gameState == gp.playState) {
            drawLiveHUD(g2);
        } else if (gp.gameState == gp.dialogueState || gp.gameState == gp.introDialogueState) {
            drawLiveHUD(g2);
            drawDialogueScreen(g2);
        } else if (gp.gameState == gp.quizState) {
            drawLiveHUD(g2);
            drawQuizScreen(g2);
        } else if (gp.gameState == gp.phoneState) {
            drawLiveHUD(g2);
            drawPhoneMenu(g2);
        }

        int x = gp.screenWidth - 130;
        int y = 20;
        int verticalGap = 28;

        drawStatBar(g2, x, y, hpIcon, "HP", gp.session.getPlayer().getHp(), gp.session.getPlayer().getMaxHp(), new Color(255, 50, 80)); // Neon Red
        y += verticalGap;
        drawStatBar(g2, x, y, energyIcon, "ENG", gp.session.getPlayer().getStats().getEnergy(), 100, new Color(50, 220, 255)); // Neon Cyan
        y += verticalGap;
        drawStatBar(g2, x, y, stressIcon, "STR", gp.session.getPlayer().getStats().getStress(), 100, new Color(255, 80, 255)); // Neon Pink
        y += verticalGap;
        drawStatBar(g2, x, y, gpaIcon, gp.zombieMode ? "KRM" : "GPA", gp.zombieMode ? gp.session.getPlayer().getStats().getKarma() : (int)(gp.session.getPlayer().getStats().getGPA() * 25), 100, new Color(255, 230, 50));

        if (gp.zombieMode) {
            y += verticalGap + 10;
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString("INVENTORY:", x - 25, y);
            y += 18;
            
            boolean hasKey1 = gp.session.getPlayer().getInventory().hasItem(1);
            boolean hasKey2 = gp.session.getPlayer().getInventory().hasItem(2);
            
            if (!hasKey1 && !hasKey2) {
                g2.setColor(new Color(150, 150, 150));
                g2.drawString("- Empty", x - 20, y);
            } else {
                if (hasKey1) {
                    g2.setColor(new Color(100, 255, 100));
                    g2.drawString("• Corridor Keycard", x - 20, y);
                    y += 16;
                }
                if (hasKey2) {
                    g2.setColor(new Color(255, 200, 50));
                    g2.drawString("• Server Card", x - 20, y);
                }
            }
        }

        drawBottomHints(g2);

        if (gp.gameState == gp.cafeMenuState) {
            drawCafeMenu(g2);
        } else if (gp.gameState == gp.pauseState) {
            drawPauseScreen(g2);
        } else if (gp.gameState == gp.optionsState) {
            drawOptionsScreen(g2);
        } else if (gp.gameState == gp.controlsState) {
            drawControlsScreen(g2);
        } else if (gp.gameState == gp.combatState) {
            drawCombatScreen(g2);
        } else if (gp.gameState == gp.endingState) {
            drawEndingScreen(g2);
        }

        if (gp.zombieMode && gp.phaseTwoState == engine.GamePanel.PhaseTwoState.BOSS_INTRO) {
            drawBossIntro(g2);
        }

        drawPhaseOneEnding(g2);
    }

    private void drawBottomHints(Graphics2D g2) {
        // Kept as a no-op so the HUD no longer covers bottom doors and room exits.
    }

    public void startDialogue(String message) {
        startDialogue(message, false);
    }

    public void startDialogue(String message, boolean quizNext) {
        dialogueSpeakerLabel = null;
        String payload = message;
        int pipe = payload.indexOf('|');
        if (pipe >= 0) {
            dialogueSpeakerLabel = payload.substring(0, pipe).trim();
            payload = payload.substring(pipe + 1).trim();
        }
        currentDialogueMessage = payload;
        displayedDialogueMessage = "";
        charIndex = 0;
        dialogueDelayCounter = 0;
        isTyping = true;
        transitionToQuiz = quizNext;
        gp.soundM.stopTextSound(); // Reset sound before starting
        gp.soundM.playTextSound();
    }

public void updateCafeMenu() {
    if (keyCooldown > 0) {
        keyCooldown--;
        return;
    }

    if (gp.keyH.upPressed) {
        commandNum--;
        if (commandNum < 0) commandNum = foodNames.length - 1;
        keyCooldown = 10;
    }
    if (gp.keyH.downPressed) {
        commandNum++;
        if (commandNum >= foodNames.length) commandNum = 0;
        keyCooldown = 10;
    }
    if (gp.keyH.escapePressed) {
        gp.gameState = gp.playState;
        keyCooldown = 10;
    }
    if (gp.keyH.enterPressed) {
        consumeFood(commandNum);
        gp.gameState = gp.playState;
        keyCooldown = 10;
    }
}

private void consumeFood(int index) {
    // 0: Biryani, 1: Fruit Shake, 2: Fruit Chat -> HEALTHY
    // 3: Samosa, 4: Soda, 5: Lays, 6: Fries -> UNHEALTHY
    
    boolean isHealthy = (index <= 2);

    if (isHealthy) {
        gp.session.getPlayer().heal(20);
        gp.session.getPlayer().getStats().updateEnergy(20);
        gp.session.getPlayer().getStats().updateStress(-10);
        System.out.println("Ate " + foodNames[index] + " - Healthy! Stats improved.");
    } else {
        gp.session.getPlayer().heal(10);
        gp.session.getPlayer().getStats().updateEnergy(-10);
        System.out.println("Ate " + foodNames[index] + " - Unhealthy! Energy dropped.");
    }
}

private void drawCafeMenu(Graphics2D g2) {
    int frameX = gp.screenWidth / 2 - 150;
    int frameY = gp.screenHeight / 2 - 200;
    int frameWidth = 300;
    int frameHeight = 400;

    // Draw Menu Background
    g2.setColor(new Color(0, 0, 0, 210));
    g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);
    g2.setColor(Color.white);
    g2.setStroke(new java.awt.BasicStroke(3));
    g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);

    // Title
    g2.setFont(arial_20);
    String title = "CAFE MENU";
    int titleX = getXForCenteredText(g2, title, frameX, frameWidth);
    g2.drawString(title, titleX, frameY + 35);
    g2.drawLine(frameX + 20, frameY + 45, frameX + frameWidth - 20, frameY + 45);

    // Options
    g2.setFont(arial_14);
    int textX = frameX + 70;
    int textY = frameY + 80;
    int lineHeight = 40;

    for (int i = 0; i < foodNames.length; i++) {
        // Draw Icon
        if (foodIcons[i] != null) {
            g2.drawImage(foodIcons[i], frameX + 20, textY - 24, 32, 32, null);
        }

        // Draw Text
        drawFittedString(g2, foodNames[i], textX, textY, frameX + frameWidth - 24 - textX);
        
        // Draw Cursor
        if (commandNum == i) {
            g2.drawString(">", frameX + 10, textY);
        }
        
        textY += lineHeight;
    }

    drawFittedString(g2, "ENTER buy | ESC close", frameX + 20, frameY + frameHeight - 20, frameWidth - 40);
}

private int getXForCenteredText(Graphics2D g2, String text, int frameX, int frameWidth) {
    int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
    return frameX + (frameWidth / 2) - (length / 2);
}

private String trimHud(String s, int maxLen) {
    if (s == null || s.length() <= maxLen) return s;
    return s.substring(0, maxLen - 1) + "...";
}

private void drawFittedString(Graphics2D g2, String text, int x, int y, int maxW) {
    if (text == null) return;
    FontMetrics fm = g2.getFontMetrics();
    if (fm.stringWidth(text) <= maxW) {
        g2.drawString(text, x, y);
        return;
    }
    String ellipsis = "...";
    int allowed = Math.max(0, maxW - fm.stringWidth(ellipsis));
    String fit = text;
    while (!fit.isEmpty() && fm.stringWidth(fit) > allowed) {
        fit = fit.substring(0, fit.length() - 1);
    }
    g2.drawString(fit + ellipsis, x, y);
}

private void drawStatBar(Graphics2D g2, int x, int y, BufferedImage icon, String label, int current, int max, Color barColor) {
    int barWidth = 92;
    int barHeight = 8;
    int iconSize = 20;

    // 1. Draw Icon
    if (icon != null) {
        g2.drawImage(icon, x - 60, y - 7, iconSize, iconSize, null);
    } else {
        g2.setColor(barColor);
        g2.fillOval(x - 60, y - 2, 12, 12); 
    }

    // 1b. Draw Label
    g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    g2.setColor(barColor); // Match label to the neon bar color
    g2.drawString(label, x - 35, y + 8);

    // 2. Draw Bar Background
    g2.setColor(new Color(50, 50, 50, 200)); 
    g2.fillRect(x, y, barWidth, barHeight);

    // 3. Draw Bar Fill
    double oneUnit = (double)barWidth / max; 
    int fillWidth = (int)(oneUnit * current);

    g2.setColor(barColor);
    g2.fillRect(x, y, fillWidth, barHeight);

    // 4. Draw thin border
    g2.setColor(Color.white);
    g2.drawRect(x, y, barWidth, barHeight);
}

public void updateTitleScreen() {
    if (keyCooldown > 0) {
        keyCooldown--;
        return;
    }

    if (gp.keyH.upPressed) {
        titleCommandNum--;
        if (titleCommandNum < 0) {
            titleCommandNum = 2; // 3 options
        }
        keyCooldown = 12;
    }
    if (gp.keyH.downPressed) {
        titleCommandNum++;
        if (titleCommandNum > 2) {
            titleCommandNum = 0;
        }
        keyCooldown = 12;
    }

    if (gp.keyH.enterPressed) {
        if (titleCommandNum == 0) {
            // NEW GAME
            gp.session = new controller.GameSession();
            gp.session.startNewGame();
            gp.session.getPlayer().setEngineComponents(gp, gp.keyH);
            gp.resetPhaseOneFlow();
            
            // Set explicitly to Ground C Block Right side
            gp.currentZone = map.ZoneType.GROUND;
            gp.session.getPlayer().setXLocation(21 * gp.tileSize);
            gp.session.getPlayer().setYLocation((gp.maxScreenRow / 2) * gp.tileSize);
            gp.session.getPlayer().direction = "left";
            
            gp.tileM.loadMap();
            gp.objM.clearObjects();
            gp.soundM.playZoneMusic(gp.currentZone);

            gp.gameState = gp.introDialogueState;
            startDialogue("Late Start|You overslept. Classic FAST-NU Islamabad speedrun.\nSir Shehryrar Rashid's CS class is starting, and attendance is not known for mercy.");
        } else if (titleCommandNum == 1) {
            // LOAD GAME
            gp.session.loadFromSave(1);
            gp.session.getPlayer().setEngineComponents(gp, gp.keyH);
            gp.gameState = gp.playState;
        } else if (titleCommandNum == 2) {
            // QUIT
            System.exit(0);
        }
        keyCooldown = 15;
    }
}

private void drawTitleScreen(Graphics2D g2) {
    // Background color
    g2.setColor(new Color(20, 20, 30));
    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

    // TITLE NAME
    g2.setFont(titleFont);
    String text = "CampusFlex";
    int x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    int y = gp.tileSize * 3;

    // Drop Shadow
    g2.setColor(Color.black);
    g2.drawString(text, x + 5, y + 5);

    // Main Color
    g2.setColor(new Color(0, 255, 127)); // Spring Green / Retro feel
    g2.drawString(text, x, y);

    // SUBTITLE
    g2.setFont(arial_20);
    g2.setColor(Color.white);
    text = "A 2D Survival Adventure";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize;
    g2.drawString(text, x, y);

    // MENU OPTIONS
    g2.setFont(titleOptionFont);

    text = "NEW GAME";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 4;
    g2.drawString(text, x, y);
    if (titleCommandNum == 0) {
        g2.drawString(">", x - gp.tileSize, y);
    }

    text = "LOAD GAME";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 1.5;
    g2.drawString(text, x, y);
    if (titleCommandNum == 1) {
        g2.drawString(">", x - gp.tileSize, y);
    }

    text = "QUIT";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 1.5;
    g2.drawString(text, x, y);
    if (titleCommandNum == 2) {
        g2.drawString(">", x - gp.tileSize, y);
    }
}

public void updatePauseScreen() {
    if (keyCooldown > 0) {
        keyCooldown--;
        return;
    }

    if (gp.keyH.escapePressed) {
        gp.gameState = gp.playState;
        gp.keyH.escapePressed = false;
        keyCooldown = 12;
        return;
    }

    if (gp.keyH.upPressed) { pauseCommandNum--; if(pauseCommandNum < 0) pauseCommandNum = 4; keyCooldown = 12;}
    if (gp.keyH.downPressed) { pauseCommandNum++; if(pauseCommandNum > 4) pauseCommandNum = 0; keyCooldown = 12;}

    if (gp.keyH.enterPressed) {
        if (pauseCommandNum == 0) gp.gameState = gp.playState;
        else if (pauseCommandNum == 1) { gp.session.getSaveStrategy().save(gp.session.buildCurrentGameState()); System.out.println("Game Saved!"); }
        else if (pauseCommandNum == 2) gp.gameState = gp.controlsState; // CONTROLS
        else if (pauseCommandNum == 3) gp.gameState = gp.optionsState;
        else if (pauseCommandNum == 4) gp.gameState = gp.titleState;
        keyCooldown = 15;
    }
}

private void drawPauseScreen(Graphics2D g2) {
    g2.setColor(new Color(0, 0, 0, 150));
    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

    g2.setFont(titleFont);
    g2.setColor(Color.white);
    String text = "PAUSED";
    int x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    int y = gp.tileSize * 4;
    g2.drawString(text, x, y);

    g2.setFont(titleOptionFont);

    text = "RESUME";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 3;
    g2.drawString(text, x, y);
    if (pauseCommandNum == 0) g2.drawString(">", x - gp.tileSize, y);

    text = "SAVE GAME";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 1.5;
    g2.drawString(text, x, y);
    if (pauseCommandNum == 1) g2.drawString(">", x - gp.tileSize, y);

    text = "CONTROLS";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 1.5;
    g2.drawString(text, x, y);
    if (pauseCommandNum == 2) g2.drawString(">", x - gp.tileSize, y);

    text = "OPTIONS";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 1.5;
    g2.drawString(text, x, y);
    if (pauseCommandNum == 3) g2.drawString(">", x - gp.tileSize, y);

    text = "QUIT TO TITLE";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 1.5;
    g2.drawString(text, x, y);
    if (pauseCommandNum == 4) g2.drawString(">", x - gp.tileSize, y);
}

public void updateOptionsScreen() {
    if (keyCooldown > 0) {
        keyCooldown--;
        return;
    }

    if (gp.keyH.escapePressed) {
        gp.gameState = gp.pauseState;
        gp.keyH.escapePressed = false;
        keyCooldown = 12;
        return;
    }

    if (gp.keyH.upPressed) {
        optionsCommandNum--;
        if (optionsCommandNum < 0) {
            optionsCommandNum = 2; // 3 options
        }
        keyCooldown = 12;
    }
    if (gp.keyH.downPressed) {
        optionsCommandNum++;
        if (optionsCommandNum > 2) {
            optionsCommandNum = 0;
        }
        keyCooldown = 12;
    }

    if (gp.keyH.enterPressed) {
        if (optionsCommandNum == 0) {
            // Toggle Music
            gp.soundM.musicOn = !gp.soundM.musicOn;
            if (!gp.soundM.musicOn) {
                gp.soundM.stopMusic();
            } else {
                gp.soundM.playCustomMusic("assets/sound/pauseTheme.wav");
            }
        } else if (optionsCommandNum == 1) {
            // Toggle SFX
            gp.soundM.sfxOn = !gp.soundM.sfxOn;
        } else if (optionsCommandNum == 2) {
            // BACK
            gp.gameState = gp.pauseState;
        }
        keyCooldown = 15;
    }
}

private void drawOptionsScreen(Graphics2D g2) {
    g2.setColor(new Color(0, 0, 0, 200));
    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

    g2.setFont(titleFont);
    g2.setColor(Color.white);
    String text = "OPTIONS";
    int x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    int y = gp.tileSize * 3;
    g2.drawString(text, x, y);

    g2.setFont(titleOptionFont);

    // Music
    text = "MUSIC < " + (gp.soundM.musicOn ? "ON" : "OFF") + " >";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 4;
    g2.drawString(text, x, y);
    if (optionsCommandNum == 0) g2.drawString(">", x - gp.tileSize, y);

    // SFX
    text = "SFX   < " + (sfxOn ? "ON" : "OFF") + " >";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 1.5;
    g2.drawString(text, x, y);
    if (optionsCommandNum == 1) g2.drawString(">", x - gp.tileSize, y);

    // BACK
    text = "BACK";
    x = getXForCenteredText(g2, text, 0, gp.screenWidth);
    y += gp.tileSize * 2;
    g2.drawString(text, x, y);
    if (optionsCommandNum == 2) g2.drawString(">", x - gp.tileSize, y);
}

private void drawLiveHUD(Graphics2D g2) {
    int x = 12;
    int y = 10;
    int width = Math.min(320, gp.screenWidth / 3 + 108);
    int height = 112;

    g2.setColor(new Color(0, 0, 0, 168));
    g2.fillRoundRect(x, y, width, height, 10, 10);
    g2.setColor(new Color(220, 230, 255));
    g2.setStroke(new java.awt.BasicStroke(2));
    g2.drawRoundRect(x, y, width, height, 10, 10);

    g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
    map.Zone currentZone = gp.session.getCampusMap().getZone(gp.currentZone);
    String zoneName = (currentZone != null) ? currentZone.getName() : gp.currentZone.toString();
    if (zoneName.length() > 22) zoneName = zoneName.substring(0, 21) + "…";
    g2.drawString("Where: " + zoneName, x + 14, y + 28);

    g2.setFont(arial_14);
    String nearbyName = gp.nearbyDoorName;
    String doorCue;
    if (nearbyName.isEmpty()) {
        doorCue = "Nearby: nothing";
        g2.setColor(new Color(155, 155, 155));
    } else if (nearbyName.startsWith("\u26A0")) {
        doorCue = nearbyName;
        g2.setColor(new Color(255, 100, 100)); // Red for zombies
    } else {
        doorCue = "Nearby: " + trimHud(nearbyName, 28);
        g2.setColor(new Color(135, 255, 182));
    }
    drawFittedString(g2, doorCue, x + 14, y + 50, width - 28);

    String goalText = gp.getPhaseGoalText();
    if (goalText != null && !goalText.isEmpty()) {
        g2.setColor(Color.yellow);
        drawFittedString(g2, goalText, x + 14, y + 72, width - 28);
        g2.setColor(Color.orange);
        drawFittedString(g2, gp.getPhaseHintText(), x + 14, y + 96, width - 28);
    } else {
        g2.setColor(new Color(205, 215, 225));
        drawFittedString(g2, "E/Enter interact | P phone | Esc pause", x + 14, y + 74, width - 28);
    }
}

private void drawPhaseOneEnding(Graphics2D g2) {
    if (gp.phaseOneState != GamePanel.PhaseOneState.PHASE_1_ENDING
            && gp.phaseOneState != GamePanel.PhaseOneState.PHASE_1_DONE) {
        return;
    }
    if (gp.zombieMode) return; // Don't draw over zombie mode

    int alpha = gp.phaseOneState == GamePanel.PhaseOneState.PHASE_1_DONE
            ? 255
            : Math.min(255, 40 + gp.phaseEndingTicks * 4);
    g2.setColor(new Color(0, 0, 0, alpha));
    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    if (alpha > 210) {
        if (gp.phaseOneState == GamePanel.PhaseOneState.PHASE_1_DONE) {
            // Zombie transition cutscene
            int t = gp.zombieCutsceneTicks;
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            g2.setColor(new Color(255, 60, 60));
            String line1 = "ERROR: Flex Portal Infinite Loop Detected.";
            g2.drawString(line1, getXForCenteredText(g2, line1, 0, gp.screenWidth), gp.screenHeight / 2 - 60);
            if (t > 60) {
                g2.setColor(new Color(255, 100, 100));
                String line2 = "AI Lab containment failure. Campus lockdown.";
                g2.drawString(line2, getXForCenteredText(g2, line2, 0, gp.screenWidth), gp.screenHeight / 2 - 20);
            }
            if (t > 120) {
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
                g2.setColor(new Color(255, 200, 200));
                String line3 = "ZOMBIE MODE";
                g2.drawString(line3, getXForCenteredText(g2, line3, 0, gp.screenWidth), gp.screenHeight / 2 + 40);
            }
            if (t > 200) {
                g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
                g2.setColor(new Color(180, 180, 180));
                String line4 = "You wake up in the Library. Something is very wrong.";
                g2.drawString(line4, getXForCenteredText(g2, line4, 0, gp.screenWidth), gp.screenHeight / 2 + 80);
            }
        } else {
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            g2.setColor(new Color(235, 240, 245));
            String text = "END OF PHASE 1";
            g2.drawString(text, getXForCenteredText(g2, text, 0, gp.screenWidth), gp.screenHeight / 2 - 10);
        }
    }
}

public void updateDialogueScreen() {
    if (keyCooldown > 0) keyCooldown--;
    
    if (isTyping) {
        dialogueDelayCounter++;
        if (dialogueDelayCounter > 1) { // Typing speed
            dialogueDelayCounter = 0;
            if (charIndex < currentDialogueMessage.length()) {
                displayedDialogueMessage += currentDialogueMessage.charAt(charIndex);
                charIndex++;
            } else {
                isTyping = false;
                gp.soundM.stopTextSound();
            }
        }
    }

    if (gp.keyH.enterPressed && keyCooldown == 0) {
        if (isTyping) {
            // Skip typing
            displayedDialogueMessage = currentDialogueMessage;
            charIndex = currentDialogueMessage.length();
            isTyping = false;
            gp.soundM.stopTextSound();
            gp.keyH.enterPressed = false;
            keyCooldown = 15;
        } else {
            if (lectureQueue != null) {
                // Check if current dialogue is the LAST one BEFORE incrementing
                boolean currentIsLast = lectureQueueIndex == lectureQueue.length - 1;
                if (currentIsLast && lectureQueueTriggersQuiz) {
                    // Last dialogue and should trigger quiz - set flag and let normal flow handle it
                    transitionToQuiz = true;
                }
                if (lectureQueueIndex < lectureQueue.length - 1) {
                    lectureQueueIndex++;
                    startDialogue(lectureQueue[lectureQueueIndex], false);
                    gp.keyH.enterPressed = false;
                    keyCooldown = 15;
                    return;
                }
                // Reached end of queue
                lectureQueue = null;
                lectureQueueIndex = 0;
                lectureQueueTriggersQuiz = false;
                // Note: transitionToQuiz may still be true if this was the last dialogue
            }
            if (gp.gameState == gp.introDialogueState) {
                showGoal = true;
            }
            if (transitionToQuiz) {
                gp.session.setLectureQuizActive(true);
                beginQuizEncounter();
                gp.gameState = gp.quizState;
                quizCommandNum = 0;
                transitionToQuiz = false;
            } else if (phaseEndingAfterDialogue) {
                phaseEndingAfterDialogue = false;
                gp.startPhaseOneEnding();
            } else if (gp.phaseTwoState == engine.GamePanel.PhaseTwoState.GAME_ENDING) {
                gp.finalizeEnding();
            } else {
                gp.gameState = gp.playState;
            }
            gp.keyH.enterPressed = false;
            keyCooldown = 15;
        }
    }
}

    public void queuePhaseOneEnding() {
        phaseEndingAfterDialogue = true;
    }

    /** UC2 staged lecture beats before Classroom->Quiz evaluation (diagram + SD-UC2). */
    public void startClassroomLectureSequence() {
        lectureQueue = new String[] {
            "You|The chair squeaks like it also missed breakfast. You face the board and pretend this was the plan.",
            "Shehryrar Rashid|Good morning. This is CS at FAST-NU Islamabad, so yes, the quiz can smell fear.",
            "Shehryrar Rashid|Encapsulation means state changes through clear behavior. Do not leave your objects like an unlocked hostel room.",
            "Shehryrar Rashid|Inheritance is a contract. If a subclass surprises callers, your design is the one bunking the lecture.",
            "Shehryrar Rashid|Polymorphism lets callers ask for intent while concrete classes handle the details quietly.",
            "Shehryrar Rashid|GRASP is not decoration. Put responsibility where the information already lives.",
            "Shehryrar Rashid|If Java gives you conflicting default methods, override and choose clearly. Ambiguity is not a personality.",
            "Shehryrar Rashid|SOLID keeps maintenance boring. Boring code is how you graduate with fewer headaches.",
            "Shehryrar Rashid|Lecture done. Quiz time. Relax, it is only mildly personal."
        };
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = true; // Pre-lecture: should trigger quiz after
        startDialogue(lectureQueue[0], false);
    }

    /** UC2 staged lecture beats for the AI Lab / OOP Class. */
    public void startAILectureSequence() {
        lectureQueue = new String[] {
            "You|You slide into an empty seat in the AI Lab. The humming of servers fills the room.",
            "Sir Shams Farooq|Welcome to AI Lab. Today we talk agents, search, and how not to panic at branching factors.",
            "Sir Shams Farooq|A good state representation saves more time than a dozen desperate if-statements.",
            "Sir Shams Farooq|Heuristics guide the search. Bad heuristics guide you straight to regret.",
            "Sir Shams Farooq|Greedy choices can look clever until the path cost quietly ruins your day.",
            "Sir Shams Farooq|That is enough theory for now. Small quiz. Big honesty."
        };
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = true; // Pre-lecture: should trigger quiz after
        startDialogue(lectureQueue[0], false);
    }

    /** UC2 staged dialogue beats for the Cafeteria Uncle - Iran-US war backstory. */
    public void startCafeteriaUncleSequence() {
        lectureQueue = new String[] {
            "Cafeteria Uncle|You look confused, beta. Wondering why the university is so empty?",
            "Cafeteria Uncle|The Iran-US war... it changed everything. The government ordered all universities to go fully online.",
            "Cafeteria Uncle|Everyone studies from home now. Except for students like you...",
            "Cafeteria Uncle|The ones who bunk classes too much. The university called you back to campus as punishment, no?",
            "Cafeteria Uncle|And look at this cafe now... empty. No business.",
            "Cafeteria Uncle|The war took everything. The cafe is officially closed, but I stay here... waiting for better days.",
            "You|I'm sorry to hear that, Uncle. This is... a lot to take in.",
            "Cafeteria Uncle|Arre, koi baat nahi beta. Life goes on. Just don't bunk your classes, okay?"
        };
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = false; // Story sequence: no quiz after
        startDialogue(lectureQueue[0], false);
    }

    /** UC2 staged dialogue beats for the Library sleep sequence. */
    public void startLibrarySleepSequence() {
        lectureQueue = new String[] {
            "You|You sit at the library desk and open your notes. The silence is almost comforting. Time to study.",
            "You|An hour passes. Your eyes feel heavy. The words on the page start to blur together.",
            "You|Maybe some coffee would help... but the cafe is closed. You keep reading anyway.",
            "You|Your head keeps drooping. You pinch yourself to stay awake, but the notes are becoming meaningless squiggles.",
            "You|So... tired... just... five... more... minutes...",
            "You|The warm desk, the quiet air, the gentle ticking of the library clock... Your eyes close. Just for a moment...",
            "You|You fall asleep right there, drooling on your notes. The librarian will judge you in the morning."
        };
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = false; // Story sequence: no quiz after
        startDialogue(lectureQueue[0], false);
    }

    /** UC2 staged dialogue for Server Room Guard - first interaction. */
    public void startServerRoomGuardSequence() {
        lectureQueue = new String[] {
            "Security Guard|Beta, THIS IS RESTRICTED! You are not allowed here.",
            "Security Guard|Turn around and leave immediately. Only IT staff can enter."
        };
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = false; // No quiz after
        startDialogue(lectureQueue[0], false);
    }

    /** UC2 staged dialogue for Server Room Guard - touching server rack. */
    public void startServerRoomGuardRackSequence() {
        lectureQueue = new String[] {
            "Security Guard|STOP! DO NOT TOUCH THE SERVER RACK!",
            "Security Guard|Are you trying to crash the entire campus network? LEAVE NOW!"
        };
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = false; // No quiz after
        startDialogue(lectureQueue[0], false);
    }

    /** UC2 staged dialogue for Server Room Guard - touching workstation. */
    public void startServerRoomGuardDeskSequence() {
        lectureQueue = new String[] {
            "Security Guard|HEY! Those are IT department workstations!",
            "Security Guard|You are NOT authorized to use these. Get out before I report you!"
        };
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = false; // No quiz after
        startDialogue(lectureQueue[0], false);
    }

    /** Begin quiz overlay: honest path vs phone-help simulation (diagram `Quiz`, UC2/UC3). */
    public void beginQuizEncounter() {
        quizPhase = QUIZ_PHASE_PICK;
        quizAiAssistPath = false;
        aiSimTicksRemaining = 0;

        dodgeIframe = 0;
        dodgeStressHits = 0;
        quizBulletSpawnTicker = 0;
        dodgeTicksLeft = 0;
        quizBolts.clear();

        currentQuizRound = 0;
        quizCorrectRoundCount = 0;
        quizBoltRng = new Random(gp.session.hashCode() ^ 920260503);
        shuffleQuizRounds(quizBoltRng);
        loadQuizRound(0);

        quizCommandNum = 0;
        keyCooldown = 18;
    }

    private void beginDodgeSubphase() {
        quizAiAssistPath = false;
        quizPhase = QUIZ_PHASE_DODGE;
        dodgeTicksLeft = GamePanel.FPS * 9;
        dodgeIframe = 0;
        dodgeBoxX = gp.screenWidth / 2f - dodgeBoxW / 2;
        dodgeBoxY = gp.screenHeight / 2f - dodgeBoxH / 2 - 38;
        soulCentreX = dodgeBoxX + dodgeBoxW / 2;
        soulCentreY = dodgeBoxY + dodgeBoxH / 2;
        keyCooldown = 10;
    }

    private void beginAiAssistSubphase() {
        quizAiAssistPath = true;
        quizPhase = QUIZ_PHASE_AI_SIM;
        aiSimTicksRemaining = GamePanel.FPS * 5;
        quizBolts.clear();
        keyCooldown = 10;
    }

    private void beginQuestionsSubphase() {
        quizPhase = QUIZ_PHASE_QUESTIONS;
        currentQuizRound = 0;
        loadQuizRound(0);
        quizCommandNum = 0;
        keyCooldown = 14;
    }

    private void shuffleQuizRounds(Random rng) {
        int a = rng.nextInt(5);
        int b;
        do { b = rng.nextInt(5); } while (b == a);
        int c;
        do { c = rng.nextInt(5); } while (c == a || c == b);
        quizRoundIndices[0] = a;
        quizRoundIndices[1] = b;
        quizRoundIndices[2] = c;
    }

    private void loadQuizRound(int round) {
        if (round >= quizRoundIndices.length) return;
        int bank = quizRoundIndices[round];

        // Use AI quiz bank for AI Lab, CS quiz bank for Classroom
        boolean isAILab = (gp.activeLectureZone == map.ZoneType.AI_LAB);
        if (isAILab) {
            quizQuestion = AI_QUIZ_PROMPTS[bank];
            System.arraycopy(AI_QUIZ_CHOICES[bank], 0, quizOptions, 0, 4);
        } else {
            quizQuestion = HARD_QUIZ_PROMPTS[bank];
            System.arraycopy(HARD_QUIZ_CHOICES[bank], 0, quizOptions, 0, 4);
        }
        quizCommandNum = 0;
    }

    public void openPhoneMenu() {
        phoneCommandNum = 0; // Start at first selectable option
        keyCooldown = 12;
    }

    public void updatePhoneMenu(controller.GameSession session) {
        if (keyCooldown > 0) {
            keyCooldown--;
            return;
        }

        Player p = session.getPlayer();

        if (gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            p.getPhone().close();
            gp.gameState = gp.playState;
            keyCooldown = 12;
            return;
        }

        if (gp.keyH.upPressed) {
            phoneCommandNum--;
            if (phoneCommandNum < 0) phoneCommandNum = 3;
            keyCooldown = 10;
        }
        if (gp.keyH.downPressed) {
            phoneCommandNum++;
            if (phoneCommandNum > 3) phoneCommandNum = 0;
            keyCooldown = 10;
        }

        if (!gp.keyH.enterPressed) return;

        gp.keyH.enterPressed = false;
        keyCooldown = 15;

        // Option 3 is just a display (Stress Level), not selectable
        if (phoneCommandNum == 3) {
            // Do nothing - this is just a display
            return;
        }

        if (phoneCommandNum == 0) {
            p.getPhone().buildSocialMediaModifier().apply(p.getStats());
            session.getKarmaTracker().deduct(5, "Scrolled social media");
            p.getPhone().close();
            gp.gameState = gp.playState;
        } else if (phoneCommandNum == 1) {
            gp.gameState = gp.playState;
            p.getPhone().close();
        } else if (phoneCommandNum == 2) {
            startDialogue("You|Flex opens after thinking about it. Your attendance is still there, unfortunately with witnesses.");
            p.getPhone().close();
            gp.gameState = gp.dialogueState;
        }
    }

    private void drawPhoneMenu(Graphics2D g2) {
        int frameW = 430;
        int frameH = 264;
        int frameX = gp.screenWidth / 2 - frameW / 2;
        int frameY = gp.screenHeight / 2 - frameH / 2;

        g2.setColor(new Color(18, 22, 34, 236));
        g2.fillRoundRect(frameX, frameY, frameW, frameH, 12, 12);
        g2.setStroke(new BasicStroke(3));
        g2.setColor(new Color(120, 210, 185));
        g2.drawRoundRect(frameX + 4, frameY + 4, frameW - 8, frameH - 8, 10, 10);

        g2.setColor(new Color(238, 248, 242));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g2.drawString("Phone", frameX + 24, frameY + 38);
        g2.setFont(arial_14);
        g2.setColor(new Color(180, 205, 215));
        drawFittedString(g2, "FAST-NU Islamabad: socials, Flex, and a tired battery.", frameX + 24, frameY + 60, frameW - 48);
        g2.setColor(new Color(70, 95, 105));
        g2.drawLine(frameX + 22, frameY + 72, frameX + frameW - 22, frameY + 72);

        int currentStress = gp.session.getPlayer().getStats().getStress();
        String[] opts = {
            "Scroll socials (-5 stress)",
            "Close phone",
            "Open Flex"
        };
        int rowY = frameY + 88;
        int rowH = 42;
        g2.setFont(arial_14);
        for (int i = 0; i < opts.length; i++) {
            int oy = rowY + i * (rowH + 8);
            if (phoneCommandNum == i) {
                g2.setColor(new Color(46, 86, 92, 220));
                g2.fillRoundRect(frameX + 20, oy - 20, frameW - 40, rowH, 8, 8);
                g2.setColor(new Color(160, 250, 235));
            } else {
                g2.setColor(new Color(232, 238, 240));
            }
            drawFittedString(g2, (phoneCommandNum == i ? "> " : "  ") + opts[i], frameX + 34, oy + 6, frameW - 68);
        }

        // Stress Level display (non-selectable)
        int stressY = rowY + 3 * (rowH + 8);
        g2.setColor(new Color(200, 100, 100)); // Red-ish for stress
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        drawFittedString(g2, "  Stress Level: " + currentStress + "/100", frameX + 34, stressY + 6, frameW - 68);

        g2.setColor(new Color(178, 185, 190));
        drawFittedString(g2, "Enter select | Esc close", frameX + 24, frameY + frameH - 18, frameW - 48);
    }

    public void setLectureQueue(String[] queue, boolean triggersQuiz) {
        this.lectureQueue = queue;
        this.lectureQueueIndex = 0;
        this.lectureQueueTriggersQuiz = triggersQuiz;
    }

    private void endQuizEncounterReset() {
        quizBolts.clear();
        quizAiAssistPath = false;
        aiSimTicksRemaining = 0;
        lectureQueue = null;
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = false;
    }

    private void finalizeQuiz(controller.GameSession session, String closingDialogue, boolean flawlessHonestExam) {
        boolean pocketOraclePenalty = quizAiAssistPath;
        map.ZoneType completedLecture = gp.activeLectureZone;

        session.setLectureQuizActive(false);
        endQuizEncounterReset();
        gp.completeCurrentLectureQuiz();
        quizPhase = QUIZ_PHASE_PICK;

        // This takes you off the chair/desk automatically!
        gp.session.getPlayer().setSeatedInClass(false);
        // AI Lab: player was 2 tiles behind desk, move 3 tiles down to clear the area
        // Classroom: player was on desk, move 1 tile down
        int moveDistance = (completedLecture == map.ZoneType.AI_LAB) ? 3 * gp.tileSize : gp.tileSize;
        gp.session.getPlayer().setYLocation(gp.session.getPlayer().getYLocation() + moveDistance);

        if (pocketOraclePenalty) {
            session.getPlayer().getStats().updateStress(16);
            session.getPlayer().getStats().updateKarma(-26);
            session.getKarmaTracker().deduct(26, "Used phone help during quiz");
        }

        // Build post-quiz dialogue queue with player response then teacher dismissal
        // Use the already-captured completedLecture to ensure correct teacher message
        String teacherDismissal;
        if (completedLecture == map.ZoneType.CLASSROOM) {
            teacherDismissal = "Shehryrar Rashid|Alright, class dismissed. Head to Sir Shams Farooq's AI Lab next.";
        } else if (completedLecture == map.ZoneType.AI_LAB) {
            teacherDismissal = "Sir Shams Farooq|Good. Prayer time is close. Pray if you want, then meet your friends in the library.";
        } else {
            // Fallback - should not happen, but just in case
            teacherDismissal = "Teacher|Class dismissed.";
        }

        // Queue both dialogues: player's closing thought, then teacher's dismissal
        lectureQueue = new String[] { closingDialogue, teacherDismissal };
        lectureQueueIndex = 0;
        lectureQueueTriggersQuiz = false; // Post-quiz: should NOT trigger another quiz!
        startDialogue(lectureQueue[0], false);
        gp.gameState = gp.dialogueState;

        if (!pocketOraclePenalty && flawlessHonestExam && gp.session != null) {
            gp.session.getPlayer().getStats().updateKarma(4);
            gp.session.getKarmaTracker().add(4, "Clean honest exam slate");
        }
    }

private void drawDialogueScreen(Graphics2D g2) {
    int x = gp.tileSize * 2;
    int boxTop = gp.tileSize * 7;
    int width = gp.screenWidth - gp.tileSize * 4;
    int height = gp.tileSize * 9;

    g2.setColor(new Color(0, 10, 30, 230));
    g2.fillRoundRect(x, boxTop, width, height, 38, 38);
    g2.setStroke(new BasicStroke(4));
    g2.setColor(new Color(126, 255, 226));
    g2.drawRoundRect(x + 4, boxTop + 4, width - 8, height - 8, 34, 34);

    int innerX = x + gp.tileSize;
    int textY = boxTop + gp.tileSize + ((dialogueSpeakerLabel != null && !dialogueSpeakerLabel.isEmpty()) ? 14 : 0);

    if (dialogueSpeakerLabel != null && !dialogueSpeakerLabel.isEmpty()) {
        g2.setFont(dialogueSpeakerFont);
        g2.setColor(new Color(255, 115, 210));
        g2.drawString(dialogueSpeakerLabel.toUpperCase(), innerX - 8, boxTop + 36);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(innerX - 10, boxTop + 42, innerX + width - gp.tileSize * 3 + 22, boxTop + 42);
        textY = boxTop + gp.tileSize * 2 + 8;
    }

    g2.setFont(dialogueBodyFont);
    g2.setColor(new Color(240, 245, 250));
    int maxTextW = width - gp.tileSize * 3;

    List<String> bodyLines = wrapTextToLines(g2, displayedDialogueMessage, maxTextW);
    int lineH = Math.max(20, g2.getFontMetrics().getHeight());
    int maxFit = Math.max(1, (height - (textY - boxTop) - 44) / lineH - 1);
    int overflow = Math.max(0, bodyLines.size() - maxFit);
    List<String> showLine = overflow > 0 ? bodyLines.subList(0, Math.min(bodyLines.size(), maxFit)) : bodyLines;

    int cy = textY;
    for (String ln : showLine) {
        g2.drawString(ln, innerX, cy);
        cy += lineH;
    }
    if (overflow > 0) {
        g2.setFont(arial_14);
        g2.setColor(new Color(180, 184, 200));
        g2.drawString("More text after this. Press ENTER.", innerX, boxTop + height - 38);
        g2.setFont(dialogueBodyFont);
        g2.setColor(new Color(240, 245, 250));
    }

    g2.setFont(arial_14);
    if (!isTyping) {
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("ENTER - continue", innerX - 14, boxTop + height - 26);
    } else {
        g2.setColor(new Color(150, 158, 168));
        g2.drawString("ENTER - show full line", innerX - 14, boxTop + height - 26);
    }
}

    private List<String> wrapTextToLines(Graphics2D g2, String paragraph, int maxW) {
        List<String> out = new ArrayList<>();
        if (paragraph == null) return out;
        paragraph = paragraph.replace('`', '\'');
        if (paragraph.trim().isEmpty()) return out;
        FontMetrics fm = g2.getFontMetrics();
        String[] paragraphs = paragraph.split("\\n");
        for (String part : paragraphs) {
            String[] words = part.trim().split("\\s+");
            StringBuilder line = new StringBuilder();
            for (String w : words) {
                if (w.isEmpty()) continue;
                if (fm.stringWidth(w) > maxW) {
                    if (line.length() > 0) {
                        out.add(line.toString());
                        line.setLength(0);
                    }
                    String chunk = "";
                    for (int i = 0; i < w.length(); i++) {
                        String trialChunk = chunk + w.charAt(i);
                        if (!chunk.isEmpty() && fm.stringWidth(trialChunk) > maxW) {
                            out.add(chunk);
                            chunk = String.valueOf(w.charAt(i));
                        } else {
                            chunk = trialChunk;
                        }
                    }
                    if (!chunk.isEmpty()) line.append(chunk);
                    continue;
                }
                String trial = line.length() == 0 ? w : line + " " + w;
                if (fm.stringWidth(trial) > maxW) {
                    out.add(line.toString());
                    line = new StringBuilder(w);
                } else {
                    line = new StringBuilder(trial);
                }
            }
            if (line.length() > 0) out.add(line.toString());
        }
        return out;
    }

public void updateQuizScreen() {
    if (quizPhase == QUIZ_PHASE_PICK) {
        if (keyCooldown > 0) {
            keyCooldown--;
            return;
        }
        if (gp.keyH.upPressed || gp.keyH.downPressed) {
            quizCommandNum = quizCommandNum == 0 ? 1 : 0;
            gp.keyH.upPressed = false;
            gp.keyH.downPressed = false;
            keyCooldown = 10;
        }
        if (gp.keyH.enterPressed) {
            gp.keyH.enterPressed = false;
            keyCooldown = 14;
            if (quizCommandNum == 0)
                beginDodgeSubphase();
            else
                beginAiAssistSubphase();
        }
        return;
    }

    if (quizPhase == QUIZ_PHASE_AI_SIM) {
        aiSimTicksRemaining--;
        if (aiSimTicksRemaining <= 0)
            beginQuestionsSubphase();
        return;
    }

    if (quizPhase == QUIZ_PHASE_DODGE) {
        updateQuizDodge();
        return;
    }

    if (quizPhase == QUIZ_PHASE_QUESTIONS) {
        boolean phoneHelp = quizAiAssistPath;
        if (phoneHelp) {
            int b = quizRoundIndices[currentQuizRound];
            boolean isAILab = (gp.activeLectureZone == map.ZoneType.AI_LAB);
            quizCommandNum = isAILab ? AI_QUIZ_CORRECT[b] : HARD_QUIZ_CORRECT[b];
        }

        if (keyCooldown > 0) {
            keyCooldown--;
            return;
        }

        if (!phoneHelp) {
            if (gp.keyH.upPressed) {
                quizCommandNum--;
                if (quizCommandNum < 0) quizCommandNum = 3;
                keyCooldown = 8;
            }
            if (gp.keyH.downPressed) {
                quizCommandNum++;
                if (quizCommandNum > 3) quizCommandNum = 0;
                keyCooldown = 8;
            }
        }

        if (!gp.keyH.enterPressed)
            return;

        gp.keyH.enterPressed = false;
        keyCooldown = 14;

        int bankIdx = quizRoundIndices[currentQuizRound];
        boolean isAILab = (gp.activeLectureZone == map.ZoneType.AI_LAB);
        int correctIdx = isAILab ? AI_QUIZ_CORRECT[bankIdx] : HARD_QUIZ_CORRECT[bankIdx];
        boolean correct = phoneHelp || (quizCommandNum == correctIdx);

        if (correct) {
            quizCorrectRoundCount++;
            gp.session.getPlayer().getStats().updateGPA(phoneHelp ? 0.1 : 0.18);
        } else {
            gp.session.getPlayer().getStats().updateGPA(0.06);
            gp.session.getPlayer().getStats().updateStress(14);
        }

        currentQuizRound++;
        if (currentQuizRound < quizRoundIndices.length) {
            loadQuizRound(currentQuizRound);
            return;
        }

        boolean honestFlawless = quizCorrectRoundCount >= 3 && !quizAiAssistPath;
        boolean aiLecture = gp.activeLectureZone == map.ZoneType.AI_LAB;
        String teacherName = aiLecture ? "Sir Shams Farooq" : "Shehryrar Rashid";
        String scoreNote = quizCorrectRoundCount >= 3
                ? "You|Full marks. " + teacherName + " gives the smallest possible nod. It counts."
                : quizCorrectRoundCount == 2
                        ? "You|Two right. Not legendary, but not the kind of attempt people screenshot."
                        : quizCorrectRoundCount == 1
                                ? "You|One right. Your GPA accepts the tiny snack."
                                : teacherName + "|We will call that a warm-up. A very cold warm-up.";

        String sfxTrail = dodgeStressHits > 7
                ? " Also, those slide bullets were rude."
                : dodgeStressHits > 0 ? " You took a few slide hits, but survived." : "";
        finalizeQuiz(gp.session, scoreNote + sfxTrail, honestFlawless);
    }
}

    private void updateQuizDodge() {
        dodgeTicksLeft--;
        if (dodgeIframe > 0) dodgeIframe--;

        float innerPad = SOUL_SIZE * 0.45f;
        if (gp.keyH.upPressed) soulCentreY -= SOUL_MOVE;
        if (gp.keyH.downPressed) soulCentreY += SOUL_MOVE;
        if (gp.keyH.leftPressed) soulCentreX -= SOUL_MOVE;
        if (gp.keyH.rightPressed) soulCentreX += SOUL_MOVE;

        float minX = dodgeBoxX + innerPad + SOUL_SIZE / 2f;
        float maxX = dodgeBoxX + dodgeBoxW - innerPad - SOUL_SIZE / 2f;
        float minY = dodgeBoxY + innerPad + SOUL_SIZE / 2f;
        float maxY = dodgeBoxY + dodgeBoxH - innerPad - SOUL_SIZE / 2f;

        soulCentreX = clamp(soulCentreX, minX, maxX);
        soulCentreY = clamp(soulCentreY, minY, maxY);

        quizBulletSpawnTicker++;
        int spawnRate = 24 + dodgeStressHits / 2;
        if (quizBulletSpawnTicker >= spawnRate) {
            quizBulletSpawnTicker = 0;
            spawnQuizBolt();
        }

        Rectangle2D soulRect = rectangleAroundSoul();
        Iterator<QuizBolt> it = quizBolts.iterator();
        while (it.hasNext()) {
            QuizBolt b = it.next();
            if (b.dead) {
                it.remove();
                continue;
            }
            b.x += b.vx;
            b.y += b.vy;
            if (offBattleBox(b.x, b.y)) {
                it.remove();
                continue;
            }

            Rectangle2D br = new Rectangle2D.Float(b.x - 3, b.y - 2, 11, 12);
            if (dodgeIframe == 0 && soulRect.intersects(br)) {
                dodgeStressHits++;
                dodgeIframe = 42;
                it.remove();
                gp.session.getPlayer().getStats().updateStress(4);
                if (dodgeStressHits % 6 == 0)
                    gp.session.getPlayer().takeDamage(5);
            }
        }

        if (dodgeTicksLeft <= 0) {
            if (dodgeStressHits >= 6)
                gp.session.getPlayer().getStats().updateStress(14 + dodgeStressHits / 3);
            quizBolts.clear();
            beginQuestionsSubphase();
        }
    }

    private float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private Rectangle2D.Float rectangleAroundSoul() {
        return new Rectangle2D.Float(soulCentreX - SOUL_SIZE / 2f, soulCentreY - SOUL_SIZE / 2f, SOUL_SIZE, SOUL_SIZE);
    }

    private boolean offBattleBox(float x, float y) {
        return x < dodgeBoxX - 40 || x > dodgeBoxX + dodgeBoxW + 40 || y < dodgeBoxY - 40 || y > dodgeBoxY + dodgeBoxH + 40;
    }

    private void spawnQuizBolt() {
        int edge = quizBoltRng.nextInt(4);
        float speed = 2.6f + quizBoltRng.nextFloat() * 2.1f;

        switch (edge) {
            case 0:
                quizBolts.add(new QuizBolt(dodgeBoxX - 28, dodgeBoxY + quizBoltRng.nextFloat() * dodgeBoxH, speed, 0));
                break;
            case 1:
                quizBolts.add(new QuizBolt(dodgeBoxX + dodgeBoxW + 28, dodgeBoxY + quizBoltRng.nextFloat() * dodgeBoxH, -speed, 0));
                break;
            case 2:
                quizBolts.add(new QuizBolt(dodgeBoxX + quizBoltRng.nextFloat() * dodgeBoxW, dodgeBoxY - 26, 0, speed));
                break;
            default:
                quizBolts.add(new QuizBolt(dodgeBoxX + quizBoltRng.nextFloat() * dodgeBoxW, dodgeBoxY + dodgeBoxH + 26, 0, -speed));
                break;
        }
    }

public void drawQuizScreen(Graphics2D g2) {
    int frameWidth = 520;
    int frameHeight = 380;
    int frameX = gp.screenWidth / 2 - frameWidth / 2;
    int frameY = gp.screenHeight / 2 - frameHeight / 2;

    g2.setColor(new Color(16, 20, 30, 238));
    g2.fillRoundRect(frameX - 18, frameY - 34, frameWidth + 36, frameHeight + 52, 14, 14);
    g2.setColor(new Color(225, 238, 240));
    g2.setStroke(new BasicStroke(3));
    g2.drawRoundRect(frameX - 18, frameY - 34, frameWidth + 36, frameHeight + 52, 12, 12);

    g2.setFont(arial_20);
    g2.setColor(new Color(250, 225, 115));
    if (quizPhase == QUIZ_PHASE_PICK) {
        g2.drawString("Exam Choice", frameX + 16, frameY + 28);
        g2.setFont(arial_14);
        g2.setColor(new Color(210, 205, 170));
        String invigilator = gp.activeLectureZone == map.ZoneType.AI_LAB ? "Sir Shams" : "Sir Shehryrar";
        drawFittedString(g2, "Phones during quizzes cost karma. " + invigilator + " has eyes.", frameX + 16, frameY + 52, frameWidth - 32);
        String[] pick = { "Attempt honestly", "Check class group (cheating)" };
        int py = frameY + 108;
        for (int i = 0; i < 2; i++) {
            int rowY = py + i * 58;
            if (quizCommandNum == i) {
                g2.setColor(new Color(44, 86, 92, 225));
                g2.fillRoundRect(frameX + 24, rowY - 26, frameWidth - 48, 42, 8, 8);
                g2.setColor(new Color(160, 250, 235));
            } else {
                g2.setColor(Color.white);
            }
            g2.setFont(arial_20);
            drawFittedString(g2, (quizCommandNum == i ? "> " : "  ") + pick[i], frameX + 38, rowY, frameWidth - 76);
        }
        g2.setFont(arial_14);
        g2.setColor(new Color(180, 185, 190));
        drawFittedString(g2, "Up/Down choose | Enter confirm", frameX + 20, frameY + frameHeight - 20, frameWidth - 40);
        return;
    }

    if (quizPhase == QUIZ_PHASE_AI_SIM) {
        g2.drawString("Class Group Loading", frameX + 16, frameY + 30);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        g2.setColor(new Color(160, 255, 180));
        String[] feed = {
                "> opening Section D group chat...",
                "> someone sent a blurry answer at 2:13 AM",
                "> confidence level: canteen napkin",
                "> you copy it anyway. brave? no. relatable? maybe.",
                "> penalty queued. Sir Shehryrar probably knows."
        };
        int totalAiTicks = GamePanel.FPS * 5;
        int elapsed = totalAiTicks - aiSimTicksRemaining;
        int linesShown = Math.min(feed.length, Math.max(1, (elapsed * feed.length + totalAiTicks - 1) / totalAiTicks));
        for (int i = 0; i < linesShown; i++) {
            g2.drawString(feed[i], frameX + 24, frameY + 70 + i * 22);
        }
        return;
    }

    if (quizPhase == QUIZ_PHASE_DODGE) {
        int secLeft = dodgeTicksLeft / Math.max(GamePanel.FPS, 1);
        g2.drawString("Slide Dodge (" + secLeft + "s)", frameX + 16, frameY + 26);
        g2.setFont(arial_14);
        g2.setColor(new Color(210, 205, 170));
        drawFittedString(g2, "Move with WASD. The slides are somehow more aggressive than usual.", frameX + 16, frameY + 54, frameWidth - 32);

        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.white);
        g2.drawRect((int) dodgeBoxX, (int) dodgeBoxY, (int) dodgeBoxW, (int) dodgeBoxH);

        g2.setColor(new Color(0, 45, 85, 80));
        g2.fillRect((int) dodgeBoxX + 6, (int) dodgeBoxY + 6, (int) dodgeBoxW - 12, (int) dodgeBoxH - 12);

        g2.setColor(new Color(255, 200, 0));
        for (QuizBolt b : quizBolts) {
            if (!b.dead)
                g2.fillRoundRect(Math.round(b.x), Math.round(b.y), 11, 12, 2, 2);
        }

        Rectangle2D s = rectangleAroundSoul();
        g2.setColor(new Color(dodgeIframe > 0 ? 240 : 255, 36, 90));
        g2.fillRect((int) s.getX(), (int) s.getY(), (int) s.getWidth(), (int) s.getHeight());

        g2.setColor(Color.white);
        g2.setFont(arial_14);
        g2.drawString("Slide hits: " + dodgeStressHits, frameX + 16, frameY + frameHeight - 96);
        return;
    }

    g2.setFont(arial_20);
    g2.setColor(new Color(250, 225, 115));
    g2.drawString("Question", frameX + 16, frameY + 26);

    g2.setFont(arial_14);
    g2.setColor(new Color(210, 205, 170));
    drawFittedString(g2, quizAiAssistPath
            ? "Class group picked an answer. Enter submits it, with the expected karma bill."
            : "Pick the best answer yourself.", frameX + 16, frameY + 54, frameWidth - 32);

    g2.setColor(Color.white);
    int qy = frameY + 84;
    drawWrappedString(g2, quizQuestion.replace("* ", ""), frameX + 24, qy, frameWidth - 56, 22, 3);

    g2.setFont(arial_14);
    int textX = frameX + 34;
    int textY = frameY + 188;
    int rowH = 40;

    for (int i = 0; i < quizOptions.length; i++) {
        boolean isAILab = (gp.activeLectureZone == map.ZoneType.AI_LAB);
        int correctIdx = isAILab ? AI_QUIZ_CORRECT[quizRoundIndices[currentQuizRound]]
                                  : HARD_QUIZ_CORRECT[quizRoundIndices[currentQuizRound]];
        boolean aiGlow = quizAiAssistPath && i == correctIdx;
        int oy = textY + i * (rowH + 6);
        if (aiGlow || quizCommandNum == i) {
            g2.setColor(aiGlow ? new Color(190, 145, 30, 120) : new Color(44, 86, 92, 220));
            g2.fillRoundRect(textX - 10, oy - 20, frameWidth - 48, rowH, 8, 8);
        }
        g2.setColor(quizCommandNum == i ? Color.cyan : Color.white);
        String prefix = (quizCommandNum == i ? "*" : " ") + " ";
        drawWrappedString(g2, prefix + quizOptions[i], textX, oy + 5, frameWidth - 72, 18, 2);
    }

    g2.setColor(new Color(180, 185, 190));
    g2.setFont(arial_14);
    drawFittedString(g2, (quizAiAssistPath ? "Enter submit class-group pick | " : "Up/Down | Enter answer | ")
            + "Round " + (currentQuizRound + 1) + "/3", frameX + 24, frameY + frameHeight - 18, frameWidth - 48);
}

    private void drawWrappedString(Graphics2D g2, String raw, int x, int y, int maxW, int lh, int maxLines) {
        List<String> lines = wrapTextToLines(g2, raw, maxW);
        int count = Math.min(lines.size(), maxLines);
        for (int i = 0; i < count; i++) {
            String line = lines.get(i);
            if (i == count - 1 && lines.size() > maxLines) {
                line = trimLineToWidth(g2, line + "...", maxW);
            }
            g2.drawString(line, x, y + i * lh);
        }
    }

    private String trimLineToWidth(Graphics2D g2, String line, int maxW) {
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(line) <= maxW) return line;
        while (!line.isEmpty() && fm.stringWidth(line) > maxW) {
            line = line.substring(0, line.length() - 1);
        }
        return line;
    }

    private static final class QuizBolt {
        float x, y;
        float vx, vy;
        boolean dead;

        QuizBolt(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }
    }

    public void updateControlsScreen() {
        if (keyCooldown > 0) { keyCooldown--; return; }
        if (gp.keyH.escapePressed || gp.keyH.enterPressed) {
            gp.gameState = gp.pauseState;
            gp.keyH.escapePressed = false;
            gp.keyH.enterPressed = false;
            keyCooldown = 15;
        }
    }

    public void drawControlsScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(titleFont);
        g2.setColor(Color.white);
        String text = "CONTROLS";
        int x = getXForCenteredText(g2, text, 0, gp.screenWidth);
        int y = gp.tileSize * 3;
        g2.drawString(text, x, y);

        g2.setFont(arial_20);
        int startY = gp.tileSize * 5;
        String[] instructions = {
            "W / A / S / D - Move Player",
            "ENTER / E - Interact",
            "P - Open Phone",
            "M - Fast Travel Map",
            "ESC - Pause Menu / Back"
        };

        for (int i = 0; i < instructions.length; i++) {
            x = getXForCenteredText(g2, instructions[i], 0, gp.screenWidth);
            g2.drawString(instructions[i], x, startY + (i * gp.tileSize));
        }

        g2.setFont(titleOptionFont);
        text = "BACK";
        x = getXForCenteredText(g2, text, 0, gp.screenWidth);
        y = startY + (instructions.length * gp.tileSize) + gp.tileSize;
        g2.drawString(text, x, y);
        g2.drawString(">", x - gp.tileSize, y);
    }

    // ═══════════════════════════════════════════════
    //  ZOMBIE MODE — Combat UI
    // ═══════════════════════════════════════════════

    private String combatEnemyName = "";
    private int combatEnemyMaxHp = 50;
    public int combatCommandNum = 0;
    private String combatResultText = null;

    public void startCombatUI(String enemyName, int maxHp) {
        this.combatEnemyName = enemyName;
        this.combatEnemyMaxHp = maxHp;
        this.combatCommandNum = 0;
        this.combatResultText = null;
    }

    public void showCombatResult(String msg) {
        this.combatResultText = msg;
    }

    public int quizTicks = 0; // Timer in frames
    
    // Terminate Minigame Variables
    private float terminateCursorX = 0;
    private float terminateCursorSpeed = 5.0f;
    private boolean terminateDirectionRight = true;
    private int terminateTargetMin = 0;
    private int terminateTargetMax = 0;

    public void initTerminateMinigame() {
        terminateCursorX = 0;
        terminateCursorSpeed = 5.0f + (float)(Math.random() * 4.0);
        terminateDirectionRight = true;
        int center = 150 + (int)(Math.random() * 140); 
        terminateTargetMin = center - 25;
        terminateTargetMax = center + 25;
        keyCooldown = 15;
    }

    public void updateCombatScreen() {
        if (keyCooldown > 0) keyCooldown--;
        if (combatResultText != null) {
            // Show result, press Enter to continue
            if ((gp.keyH.enterPressed || gp.keyH.ePressed) && keyCooldown == 0) {
                combatResultText = null;
                gp.keyH.enterPressed = false;
                gp.keyH.ePressed = false;
                keyCooldown = 10;
            }
            return;
        }

        if (gp.phaseTwoState == engine.GamePanel.PhaseTwoState.IN_QUIZ) {
            if (gp.currentChallenge != null) {
                if (quizTicks == 0) {
                    quizTicks = gp.currentChallenge.getTimeLimit() * engine.GamePanel.FPS;
                }
                quizTicks--;
                if (quizTicks <= 0) {
                    gp.handleQuizAnswer(-1); // Auto fail
                    return;
                }
            }
            if (keyCooldown > 0) return;
            if (gp.keyH.num1Pressed) { gp.keyH.num1Pressed = false; keyCooldown = 10; quizTicks = 0; gp.handleQuizAnswer(0); }
            else if (gp.keyH.num2Pressed) { gp.keyH.num2Pressed = false; keyCooldown = 10; quizTicks = 0; gp.handleQuizAnswer(1); }
            else if (gp.keyH.num3Pressed) { gp.keyH.num3Pressed = false; keyCooldown = 10; quizTicks = 0; gp.handleQuizAnswer(2); }
            else if (gp.keyH.num4Pressed) { gp.keyH.num4Pressed = false; keyCooldown = 10; quizTicks = 0; gp.handleQuizAnswer(3); }
            return;
        }

        if (gp.phaseTwoState == engine.GamePanel.PhaseTwoState.IN_TERMINATE_MINIGAME) {
            int barWidth = 360;
            if (terminateDirectionRight) {
                terminateCursorX += terminateCursorSpeed;
                if (terminateCursorX >= barWidth) { terminateCursorX = barWidth; terminateDirectionRight = false; }
            } else {
                terminateCursorX -= terminateCursorSpeed;
                if (terminateCursorX <= 0) { terminateCursorX = 0; terminateDirectionRight = true; }
            }

            if ((gp.keyH.enterPressed || gp.keyH.ePressed) && keyCooldown == 0) {
                gp.keyH.enterPressed = false;
                gp.keyH.ePressed = false;
                boolean passed = (terminateCursorX >= terminateTargetMin && terminateCursorX <= terminateTargetMax);
                gp.handleTerminateMinigame(passed);
            }
            return;
        }

        if (keyCooldown > 0) return;
        if (gp.keyH.num1Pressed) {
            gp.keyH.num1Pressed = false;
            keyCooldown = 10;
            gp.handleCombatChoice(combat.CombatMove.DEBUG);
        } else if (gp.keyH.num2Pressed) {
            gp.keyH.num2Pressed = false;
            keyCooldown = 10;
            gp.handleCombatChoice(combat.CombatMove.TERMINATE);
        }
    }

    private void drawCombatScreen(Graphics2D g2) {
        // Dark overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int boxW = 500, boxH = 300;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = (gp.screenHeight - boxH) / 2;

        // Combat box
        g2.setColor(new Color(30, 15, 15, 240));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 15, 15);
        g2.setColor(new Color(200, 50, 50));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 15, 15);

        // Enemy name
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g2.setColor(new Color(255, 80, 80));
        String title = "⚔ COMBAT ENCOUNTER ⚔";
        g2.drawString(title, getXForCenteredText(g2, title, boxX, boxW), boxY + 35);

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g2.setColor(Color.WHITE);
        g2.drawString(combatEnemyName, getXForCenteredText(g2, combatEnemyName, boxX, boxW), boxY + 65);

        // Enemy HP bar
        int barX = boxX + 50, barY = boxY + 80, barW = boxW - 100, barH = 16;
        g2.setColor(new Color(60, 20, 20));
        g2.fillRect(barX, barY, barW, barH);
        int enemyHp = gp.currentCombatEnemy != null ? gp.currentCombatEnemy.getHp() : 0;
        float ratio = Math.max(0, (float) enemyHp / combatEnemyMaxHp);
        g2.setColor(new Color(220, 50, 50));
        g2.fillRect(barX, barY, (int)(barW * ratio), barH);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        g2.drawString("HP: " + enemyHp + "/" + combatEnemyMaxHp, barX + 5, barY + 13);

        if (combatResultText != null) {
            // Show result message
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            g2.setColor(new Color(255, 200, 100));
            String[] parts = combatResultText.split("\\|");
            String msg = parts.length > 1 ? parts[1] : combatResultText;
            drawFittedString(g2, msg, boxX + 30, boxY + 140, boxW - 60);
            g2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            g2.setColor(new Color(150, 150, 150));
            g2.drawString("[Press ENTER to continue]", getXForCenteredText(g2, "[Press ENTER to continue]", boxX, boxW), boxY + boxH - 20);
        } else if (gp.phaseTwoState == engine.GamePanel.PhaseTwoState.IN_TERMINATE_MINIGAME) {
            // Terminate Mini-Game UI
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            g2.setColor(new Color(255, 150, 150));
            String titleStr = "Press ENTER when the cursor is in the GREEN zone!";
            g2.drawString(titleStr, getXForCenteredText(g2, titleStr, boxX, boxW), boxY + 120);

            int barWidth = 360;
            int mgBarX = boxX + (boxW - barWidth) / 2;
            int mgBarY = boxY + 150;
            int mgBarH = 30;

            // Background bar
            g2.setColor(new Color(80, 80, 80));
            g2.fillRect(mgBarX, mgBarY, barWidth, mgBarH);
            g2.setColor(Color.WHITE);
            g2.drawRect(mgBarX, mgBarY, barWidth, mgBarH);

            // Target zone (Green)
            g2.setColor(new Color(50, 200, 50));
            g2.fillRect(mgBarX + terminateTargetMin, mgBarY, terminateTargetMax - terminateTargetMin, mgBarH);

            // Moving Cursor
            g2.setColor(Color.YELLOW);
            g2.fillRect(mgBarX + (int)terminateCursorX - 2, mgBarY - 5, 4, mgBarH + 10);

        } else if (gp.phaseTwoState == engine.GamePanel.PhaseTwoState.IN_QUIZ && gp.currentChallenge != null) {
            // Quiz UI
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            g2.setColor(new Color(255, 230, 150));
            String q = "Q: " + gp.currentChallenge.getQuestion();
            drawFittedString(g2, q, boxX + 20, boxY + 110, boxW - 40);

            // Options
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            g2.setColor(Color.WHITE);
            String[] opts = gp.currentChallenge.getOptions();
            for (int i = 0; i < opts.length; i++) {
                int optY = boxY + 150 + (i * 28);
                g2.drawString("[" + (i + 1) + "] " + opts[i], boxX + 30, optY);
            }

            // Timer and HP
            int sec = quizTicks / engine.GamePanel.FPS;
            g2.setColor(sec <= 5 ? Color.RED : Color.CYAN);
            g2.drawString("Time: " + sec + "s", boxX + boxW - 100, boxY + boxH - 20);

            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g2.setColor(new Color(100, 200, 100));
            int playerHp = gp.session.getPlayer().getHp();
            int playerMaxHp = gp.session.getPlayer().getMaxHp();
            g2.drawString("Your HP: " + playerHp + "/" + playerMaxHp, boxX + 20, boxY + boxH - 20);
        } else {
            // Choice buttons
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            int btnY = boxY + 140;

            // DEBUG option
            g2.setColor(new Color(50, 150, 250));
            g2.fillRoundRect(boxX + 40, btnY, boxW - 80, 40, 8, 8);
            g2.setColor(Color.WHITE);
            String dbg = "[1] DEBUG — Solve a logic challenge (Karma +10)";
            g2.drawString(dbg, getXForCenteredText(g2, dbg, boxX + 40, boxW - 80), btnY + 26);

            // TERMINATE option
            g2.setColor(new Color(200, 50, 50));
            g2.fillRoundRect(boxX + 40, btnY + 60, boxW - 80, 40, 8, 8);
            g2.setColor(Color.WHITE);
            String trm = "[2] TERMINATE — Brute force attack (Karma -5)";
            g2.drawString(trm, getXForCenteredText(g2, trm, boxX + 40, boxW - 80), btnY + 86);

            // Player HP
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g2.setColor(new Color(100, 200, 100));
            int playerHp = gp.session.getPlayer().getHp();
            int playerMaxHp = gp.session.getPlayer().getMaxHp();
            g2.drawString("Your HP: " + playerHp + "/" + playerMaxHp + "  |  Karma: " + gp.session.getPlayer().getStats().getKarma(),
                    boxX + 50, boxY + boxH - 20);
        }
    }

    // ═══════════════════════════════════════════════
    //  ENDING SCREEN
    // ═══════════════════════════════════════════════

    private String endingType = "";
    private String endingEpilogue = "";
    private int endingKarma = 0;

    public void showEndingScreen(String type, String epilogue, int karma) {
        this.endingType = type;
        this.endingEpilogue = epilogue;
        this.endingKarma = karma;
    }

    private void drawEndingScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 250));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        Color titleColor;
        if (endingType.equals("PACIFIST")) titleColor = new Color(100, 255, 150);
        else if (endingType.equals("EVIL")) titleColor = new Color(255, 60, 60);
        else titleColor = new Color(200, 200, 100);

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        g2.setColor(titleColor);
        String title = "CAMPUSFLEX";
        g2.drawString(title, getXForCenteredText(g2, title, 0, gp.screenWidth), gp.screenHeight / 2 - 80);

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        String endLabel = endingType + " ENDING";
        g2.drawString(endLabel, getXForCenteredText(g2, endLabel, 0, gp.screenWidth), gp.screenHeight / 2 - 40);

        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        g2.setColor(new Color(220, 220, 220));
        drawWrappedString(g2, endingEpilogue, 50, gp.screenHeight / 2 - 20, gp.screenWidth - 100, 22, 12);

        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g2.setColor(new Color(150, 150, 150));
        String karma = "Final Karma: " + endingKarma;
        g2.drawString(karma, getXForCenteredText(g2, karma, 0, gp.screenWidth), gp.screenHeight / 2 + 50);

        g2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        g2.setColor(new Color(120, 120, 120));
        String credits = "24I-0608 Muhammad Dyen Asif | 24I-0574 Abdullah Aamir | 24I-0669 Danial Amin";
        g2.drawString(credits, getXForCenteredText(g2, credits, 0, gp.screenWidth), gp.screenHeight / 2 + 100);
    }

    private void drawBossIntro(Graphics2D g2) {
        int ticks = gp.bossIntroTicks;
        int maxTicks = engine.GamePanel.FPS * 4;
        
        // Darken screen gradually
        int alpha = Math.min(200, ticks * 2);
        g2.setColor(new Color(50, 0, 50, alpha));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (ticks > 30) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
            g2.setColor(new Color(255, 50, 50));
            String title = "WARNING: CORRUPTED AI DETECTED";
            g2.drawString(title, getXForCenteredText(g2, title, 0, gp.screenWidth), gp.screenHeight / 2 - 40);
        }

        if (ticks > 90) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
            g2.setColor(new Color(255, 200, 200));
            String sub = "Deploying containment patch...";
            g2.drawString(sub, getXForCenteredText(g2, sub, 0, gp.screenWidth), gp.screenHeight / 2 + 10);
        }

        if (ticks > 150) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            g2.setColor(Color.WHITE);
            String ready = "PREPARE FOR COMBAT!";
            g2.drawString(ready, getXForCenteredText(g2, ready, 0, gp.screenWidth), gp.screenHeight / 2 + 60);
        }
    }
}
