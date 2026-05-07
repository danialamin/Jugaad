package controller;

// lectureQuizActive models UC2 quiz phase + UC3 cheat detection branching (SD-UC2 / SD-UC3 summaries).

import entity.Player;
import entity.Enemy;
import map.CampusMap;
import mode.GameModeType;
import state.GameState;
import state.KarmaTracker;
import mode.GameModeFactory;
import mode.GameMode;
import database.PersistenceFactory;
import database.PersistenceHandler;
import javax.swing.JOptionPane;

public class GameSession {
    private int sessionId;
    private GameModeType mode;
    private boolean isActive;
    /** UC2 / SD-UC2: lecture quiz in progress — used by {@link activity.Phone#isDuringQuiz(GameSession)}. */
    private boolean lectureQuizActive;
    
    private Player player;
    private CampusMap campusMap;
    private KarmaTracker karmaTracker;
    private GameMode activeGameMode;
    private CombatController combatController;

    public GameSession() {
        this.player = new Player("Player 1", 1); // Default spawn zone
        this.karmaTracker = new KarmaTracker();
        this.campusMap = new CampusMap();
        this.combatController = new CombatController(this.player);
        this.mode = GameModeType.NORMAL; // Default mode
        
        GameModeFactory factory = new GameModeFactory();
        this.activeGameMode = factory.createNormalMode();
    }

    public void startNewGame() {
        this.player = new Player("Player 1", 1); // Default spawn zone
        this.karmaTracker = new KarmaTracker();
        this.campusMap = new CampusMap();
        this.combatController = new CombatController(this.player);
        
        GameModeFactory factory = new GameModeFactory();
        this.activeGameMode = factory.createNormalMode();
        this.mode = GameModeType.NORMAL;
        
        this.activeGameMode.activate();
        this.isActive = true;
    }

    public void loadFromSave(int saveId) {
        String[] options = {"SQL Server", "File System (XML)"};
        int choice = JOptionPane.showOptionDialog(null, 
            "Where do you want to load the game from?", 
            "Load Game", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);
            
        if (choice == JOptionPane.CLOSED_OPTION) {
            return; // Player canceled loading
        }
            
        PersistenceHandler handler;
        if (choice == 0) {
            handler = PersistenceFactory.getPersistenceHandler(PersistenceFactory.PersistenceType.SQL_SERVER);
        } else {
            handler = PersistenceFactory.getPersistenceHandler(PersistenceFactory.PersistenceType.XML);
        }

        GameState state = handler.load(saveId);
        
        if (state == null) {
            JOptionPane.showMessageDialog(null, "Failed to load game or no save found!");
            return;
        }
        
        this.player = new Player();
        this.player.moveTo(state.getPosX(), state.getPosY());
        this.player.setCurrentZoneId(state.getZoneId());
        
        this.player.setHp(state.getHp());
        this.player.setMaxHp(state.getMaxHp());
        if (this.player.getStats() != null) {
            this.player.getStats().setGPA(state.getGpa());
            this.player.getStats().setEnergy(state.getEnergy());
            this.player.getStats().setStress(state.getStress());
            this.player.getStats().setKarma(state.getKarma());
        }
        
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
    public CombatController getCombatController() { return combatController; }

    public void saveGame() {
        String[] options = {"SQL Server", "File System (XML)"};
        int choice = JOptionPane.showOptionDialog(null, 
            "Where do you want to save the game?", 
            "Save Game", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);
            
        if (choice == JOptionPane.CLOSED_OPTION) {
            handleSystemEvent("cancelSave");
            return; // Player canceled saving
        }
            
        handleSystemEvent("confirmSave");
        PersistenceHandler handler;
        if (choice == 0) {
            handler = PersistenceFactory.getPersistenceHandler(PersistenceFactory.PersistenceType.SQL_SERVER);
        } else {
            handler = PersistenceFactory.getPersistenceHandler(PersistenceFactory.PersistenceType.XML);
        }
        
        boolean success = handler.save(buildCurrentGameState());
        if (success) {
            JOptionPane.showMessageDialog(null, "Game saved successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Failed to save game.");
        }
    }

    public GameState buildCurrentGameState() {
        GameState state = new GameState();
        state.setSaveId(1);
        state.setPosX(player.getPosition().getX());
        state.setPosY(player.getPosition().getY());
        state.setZoneId(player.getCurrentZoneId());
        state.setModeSnapshot(mode);
        state.setFlag("saved", true);
        
        state.setHp(player.getHp());
        state.setMaxHp(player.getMaxHp());
        if (player.getStats() != null) {
            state.setGpa(player.getStats().getGPA());
            state.setEnergy(player.getStats().getEnergy());
            state.setStress(player.getStats().getStress());
            state.setKarma(player.getStats().getKarma());
        }
        
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
