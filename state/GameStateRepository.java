package state;

import interfaces.ISaveStrategy;

public class GameStateRepository implements ISaveStrategy {
    private String connectionString;

    @Override
    public boolean save(GameState state) {
        System.out.println("Mock saving game state...");
        return true;
    }

    @Override
    public GameState load(int saveId) {
        System.out.println("Mock loading game state...");
        return new GameState();
    }

    public boolean persist(GameState state) {
        return save(state);
    }

    public GameState fetch(int saveId) {
        return load(saveId);
    }

    public void deleteExpired() {
        // Placeholder
    }
}
