package controller;

public class Game {
    private GameSession session;

    public void start() {
        session = new GameSession();
        session.startNewGame();
    }

    public void loadGame(int saveId) {
        session = new GameSession();
        session.loadFromSave(saveId);
    }

    public void quit() {
        if (session != null) {
            session.endSession();
        }
        System.exit(0);
    }
}
