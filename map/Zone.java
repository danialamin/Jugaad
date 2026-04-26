package map;
import java.util.ArrayList;
import java.util.List;

public class Zone {
    private ZoneType type;
    private String name;
    private boolean isLocked;
    private List<ZoneType> connectedZones;
    private List<Location> locations;

    public Zone(ZoneType type, String name) {
        this.type = type;
        this.name = name;
        this.isLocked = false;
        this.connectedZones = new ArrayList<>();
        this.locations = new ArrayList<>();
    }

    public void addLocation(Location loc) {
        locations.add(loc);
        if (!connectedZones.contains(loc.getTargetZone())) {
            connectedZones.add(loc.getTargetZone());
        }
    }

    public void unlock() { isLocked = false; }
    public void lock() { isLocked = true; }
    public boolean isLocked() { return isLocked; }

    public ZoneType getType() { return type; }
    public String getName() { return name; }
    public List<Location> getLocations() { return locations; }
}
