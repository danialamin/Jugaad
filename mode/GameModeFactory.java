package mode;

public class GameModeFactory {
    public NormalMode createNormalMode() {
        return new NormalMode();
    }

    public ZombieMode createZombieMode() {
        return new ZombieMode();
    }

    public GameMode swapMode(GameMode currentMode) {
        currentMode.deactivate();
        if (currentMode instanceof NormalMode) {
            ZombieMode zm = createZombieMode();
            zm.activate();
            return zm;
        } else {
            NormalMode nm = createNormalMode();
            nm.activate();
            return nm;
        }
    }
}
