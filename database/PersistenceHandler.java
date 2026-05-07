package database;

import state.GameState;

public interface PersistenceHandler {
    boolean save(GameState state);
    GameState load(int saveId);
}
