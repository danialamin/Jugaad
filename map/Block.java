package map;
import java.util.ArrayList;
import java.util.List;

public class Block {
    private int blockId;
    private String name;
    private List<Zone> zones;

    public Block(int blockId, String name) {
        this.blockId = blockId;
        this.name = name;
        this.zones = new ArrayList<>();
    }

    public void addZone(Zone zone) {
        zones.add(zone);
    }

    public void createZone(int id, int floorNumber) {
        // Gray-structure placeholder: map floor/id to a concrete ZoneType when implementing.
    }

    public Zone getZone(int floorNumber) {
        // Gray-structure placeholder: resolve floor-to-zone mapping.
        return null;
    }

    public List<Zone> getAllZones() {
        return zones;
    }

    public List<Zone> getZones() { return zones; }
    public int getBlockId() { return blockId; }
    public String getName() { return name; }
}
