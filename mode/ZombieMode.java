package mode;

import map.Zone;
import java.util.ArrayList;
import java.util.List;

public class ZombieMode extends GameMode {
    private int currentFloor;
    private List<Integer> blockedZoneIds = new ArrayList<>();

    public ZombieMode() {
        this.modeName = "Zombie Mode";
    }

    public void moveUp() {
        currentFloor++;
    }

    public boolean isZoneBlocked(int zoneId) {
        return blockedZoneIds.contains(zoneId);
    }

    public void revealAlternate(Zone zone) {
        if (zone.getAlternateZone() != null) {
            System.out.println("Revealed alternate route: " + zone.getAlternateZone().getName());
        }
    }
}
