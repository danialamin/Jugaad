package mode;

import activity.Classroom;
import java.util.ArrayList;
import java.util.List;

public class NormalMode extends GameMode {
    private List<Classroom> activeClassrooms = new ArrayList<>();
    private int cycleCount;

    public NormalMode() {
        this.modeName = "Normal Mode";
    }

    public void triggerZombieMode() {
        System.out.println("Zombie Mode triggered!");
    }

    public int getCycleCount() { return cycleCount; }
}
