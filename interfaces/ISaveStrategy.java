package interfaces;

import state.GameState;

public interface ISaveStrategy {
    boolean save(GameState state);
    GameState load(int saveId);
}
