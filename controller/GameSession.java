package controller;

// lectureQuizActive models UC2 quiz phase + UC3 cheat detection branching (SD-UC2 / SD-UC3 summaries).

import entity.Player;
import entity.Enemy;
import map.CampusMap;
import mode.GameModeType;
import state.GameState;
import state.GameStateRepository;
import state.KarmaTracker;
import interfaces.ISaveStrategy;
import mode.GameModeFactory;
import mode.GameMode;

public class GameSession {
    private int sessionId;
    private GameModeType mode;
    private boolean isActive;
    /** UC2 / SD-UC2: lecture quiz in progress — used by {@link activity.Phone#isDuringQuiz(GameSession)}. */
    private boolean lectureQuizActive;
    
    private Player player;
    private CampusMap campusMap;
    private KarmaTracker karmaTracker;
    private ISaveStrategy saveStrategy;
    private GameMode activeGameMode;
    private CombatController combatController;

    public void startNewGame() {
        this.player = new Player("Player 1", 1); // Default spawn zone
        this.karmaTracker = new KarmaTracker();
        this.campusMap = new CampusMap();
        this.saveStrategy = new GameStateRepository();
        this.combatController = new CombatController(this.player);
        
        GameModeFactory factory = new GameModeFactory();
        this.activeGameMode = factory.createNormalMode();
        this.mode = GameModeType.NORMAL;
        
        this.activeGameMode.activate();
        this.isActive = true;
    }

    public void loadFromSave(int saveId) {
        this.saveStrategy = new GameStateRepository();
        GameState state = saveStrategy.load(saveId);
        
        this.player = new Player();
        this.player.moveTo(state.getPosX(), state.getPosY());
        this.player.setCurrentZoneId(state.getZoneId());
        
        this.karmaTracker = new KarmaTracker(); // Should load from state
        this.campusMap = new CampusMap();
        this.combatController = new CombatController(this.player);
        
        GameModeFactory factory = new GameModeFactory();
        if (state.getModeSnapshot() == GameModeType.ZOMBIE) {
            this.activeGameMode = factory.createZombieMode();
            this.mode = GameModeType.ZOMBIE;
        } else {
            this.activeGameMode = factory.createNormalMode();
            this.mode = GameModeType.NORMAL;
        }
        
        this.activeGameMode.activate();
        this.isActive = true;
    }

    public void handleSystemEvent(String event) {
        System.out.println("System event: " + event);
    }

    public void switchToZombieMode() {
        this.mode = GameModeType.ZOMBIE;
        GameModeFactory factory = new GameModeFactory();
        this.activeGameMode = factory.swapMode(activeGameMode);
    }
    
    public void onPlayerMove(float x, float y) {
        activeGameMode.onPlayerMove(x, y, this);
    }
    
    public void onInteract() {
        activeGameMode.onInteract(this);
    }

    public GameModeType getCurrentMode() { return mode; }
    public boolean isLectureQuizActive() { return lectureQuizActive; }
    public void setLectureQuizActive(boolean active) { this.lectureQuizActive = active; }
    public Player getPlayer() { return player; }
    public CampusMap getCampusMap() { return campusMap; }
    public KarmaTracker getKarmaTracker() { return karmaTracker; }
    public ISaveStrategy getSaveStrategy() { return saveStrategy; }
    public CombatController getCombatController() { return combatController; }

    public GameState buildCurrentGameState() {
        GameState state = new GameState();
        state.setPosX(player.getPosition().getX());
        state.setPosY(player.getPosition().getY());
        state.setZoneId(player.getCurrentZoneId());
        state.setModeSnapshot(mode);
        state.setFlag("saved", true);
        return state;
    }

    public void showGameOver() {
        this.isActive = false;
        System.out.println("Game Over.");
    }

    public void updateZone(map.Zone zone) {
        if (zone != null) {
            this.player.setCurrentZoneId(zone.getType().ordinal());
        }
    }

    public void endSession() {
        this.isActive = false;
        System.out.println("Session ended.");
    }
}
