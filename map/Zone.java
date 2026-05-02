package map;
import java.util.ArrayList;
import java.util.List;

public class Zone {
    private ZoneType type;
    private String name;
    private boolean isLocked;
    private Zone alternateZone;
    private List<ZoneType> connectedZones;
    private List<Location> locations;
    private List<entity.NPC> npcs;
    private List<entity.Enemy> enemies;

    public Zone(ZoneType type, String name) {
        this.type = type;
        this.name = name;
        this.isLocked = false;
        this.connectedZones = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.enemies = new ArrayList<>();
    }

    public void createLocation(int id, String name, boolean isInteractable, interfaces.IInteractable i, float triggerX, float triggerY, float triggerRadius) {
        Location loc = new Location(id, name, isInteractable, i, triggerX, triggerY, triggerRadius);
        locations.add(loc);
    }

    public void addNPC(entity.NPC npc) { npcs.add(npc); }
    public void addEnemy(entity.Enemy enemy) { enemies.add(enemy); }

    public Location getLocation(int locationId) {
        // Placeholder simple linear search
        return locations.isEmpty() ? null : locations.get(0);
    }

    public Location getNearbyLocation(entity.Position pos) {
        for (Location loc : locations) {
            if (loc.isPlayerInRange(pos)) return loc;
        }
        return null;
    }

    public Zone getAlternateZone() { return alternateZone; }
    public void setAlternateZone(Zone zone) { this.alternateZone = zone; }

    public void addLocation(Location loc) {
        locations.add(loc);
        if (!connectedZones.contains(loc.getTargetZone())) {
            connectedZones.add(loc.getTargetZone());
        }
    }

    public void unlock() { isLocked = false; }
    public void lock() { isLocked = true; }
    public boolean isLocked() { return isLocked; }
    public boolean isBlocked() { return isLocked; }

    public ZoneType getType() { return type; }
    public String getName() { return name; }
    public List<Location> getLocations() { return locations; }
}
