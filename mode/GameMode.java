package mode;

import controller.GameSession;

public abstract class GameMode {
    protected String modeName;

    public void activate() {
        System.out.println("Mode activated: " + modeName);
    }

    public void deactivate() {
        System.out.println("Mode deactivated: " + modeName);
    }

    public void onPlayerMove(float x, float y, GameSession session) {
        session.getPlayer().moveTo(x, y);
    }

    public void onInteract(GameSession session) {
        // Trigger interaction with the nearest location
        map.Zone currentZone = session.getCampusMap().getZone(map.ZoneType.values()[session.getPlayer().getCurrentZoneId()]);
        if (currentZone != null) {
            map.Location loc = currentZone.getNearbyLocation(session.getPlayer().getPosition());
            if (loc != null) {
                loc.interact(session.getPlayer(), session);
            }
        }
    }

    public String getModeName() { return modeName; }
}
