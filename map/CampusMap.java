package map;
import entity.Position;
import java.util.ArrayList;
import java.util.List;

public class CampusMap {
    private int mapId;
    private List<Block> blocks;

    public CampusMap() {
        this.mapId = 1;
        this.blocks = new ArrayList<>();
        initializeMap();
    }

    private void initializeMap() {
        // Placeholder for initial setup
        createBlock(1, "C-Block (Ground)");
        createBlock(2, "C-Block (First Floor)");
        createBlock(3, "D-Block");
        createBlock(4, "Shared Area");
        // Block 1: C-Block (Ground Floor)
        Block cBlockGround = new Block(1, "C-Block (Ground)");
        Zone ground = new Zone(ZoneType.GROUND, "Campus Ground");
        Zone cafe = new Zone(ZoneType.CAFETERIA, "Cafeteria");
        Zone prayer = new Zone(ZoneType.PRAYER_AREA, "Prayer Area");
        cBlockGround.addZone(ground);
        cBlockGround.addZone(cafe);
        cBlockGround.addZone(prayer);

        // Block 2: C-Block (First Floor)
        Block cBlockFirst = new Block(2, "C-Block (First Floor)");
        Zone corridor = new Zone(ZoneType.CORRIDOR, "Academic Corridor");
        Zone classroom = new Zone(ZoneType.CLASSROOM, "Classroom"); // Handled dynamically
        Zone aiLab = new Zone(ZoneType.AI_LAB, "AI Lab"); // Handled dynamically
        cBlockFirst.addZone(corridor);
        cBlockFirst.addZone(classroom);
        cBlockFirst.addZone(aiLab);

        // Block 3: D-Block
        Block dBlock = new Block(3, "D-Block");
        Zone library = new Zone(ZoneType.LIBRARY, "Library");
        Zone serverRoom = new Zone(ZoneType.SERVER_ROOM, "Main Server Room");
        dBlock.addZone(library);
        dBlock.addZone(serverRoom);

        // Block 4: Common Walkway Hub
        Block hubBlock = new Block(4, "Shared Area");
        Zone walkway = new Zone(ZoneType.WALKWAY, "Campus Walkway");
        hubBlock.addZone(walkway);

        blocks.add(cBlockGround);
        blocks.add(cBlockFirst);
        blocks.add(dBlock);
        blocks.add(hubBlock);

        // --- Define Connections (Doors) ---
        int tileSize = 32;
        int maxCol = 24;
        int maxRow = 18;

        // C-Block (Ground): Ground <-> Cafeteria
        ground.addLocation(new Location("Cafe Door", new Position((maxCol / 2), 0), ZoneType.CAFETERIA, new Position((maxCol / 2) * tileSize, (maxRow - 3) * tileSize), "up"));
        cafe.addLocation(new Location("Ground Door", new Position((maxCol / 2), maxRow - 1), ZoneType.GROUND, new Position((maxCol / 2) * tileSize, 2 * tileSize), "down"));

        // C-Block (Ground): Ground <-> Prayer Area
        ground.addLocation(new Location("Prayer Door", new Position(maxCol / 2 + 5, 0), ZoneType.PRAYER_AREA, new Position((maxCol / 2) * tileSize, (maxRow - 3) * tileSize), "up"));
        prayer.addLocation(new Location("Ground Door", new Position((maxCol / 2), maxRow - 1), ZoneType.GROUND, new Position((maxCol / 2 + 5) * tileSize, 2 * tileSize), "down"));

        // D-Block: Library <-> Server Room (Left wall of Library, row = maxRow/2)
        library.addLocation(new Location("Server Room", new Position(0, maxRow / 2), ZoneType.SERVER_ROOM, new Position((maxCol - 3) * tileSize, (maxRow / 2) * tileSize), "left"));
        serverRoom.addLocation(new Location("Library", new Position(maxCol - 1, maxRow / 2), ZoneType.LIBRARY, new Position(2 * tileSize, (maxRow / 2) * tileSize), "right"));

        // Hub Connections: Walkway <-> Ground, Corridor, Library
        // Walkway (Right, row=maxRow/2) <-> Ground (Left, row=maxRow/2)
        walkway.addLocation(new Location("C-Block (Ground)", new Position(maxCol - 1, maxRow / 2), ZoneType.GROUND, new Position(2 * tileSize, (maxRow / 2) * tileSize), "right"));
        ground.addLocation(new Location("Campus Walkway", new Position(0, maxRow / 2), ZoneType.WALKWAY, new Position((maxCol - 3) * tileSize, (maxRow / 2) * tileSize), "left"));

        // Walkway (Top, col=maxCol/2) <-> Corridor (Left, row=maxRow/2)
        walkway.addLocation(new Location("C-Block (First Floor)", new Position(maxCol / 2, 0), ZoneType.CORRIDOR, new Position(2 * tileSize, (maxRow / 2) * tileSize), "up"));
        corridor.addLocation(new Location("Stairs to Walkway", new Position(0, maxRow / 2), ZoneType.WALKWAY, new Position((maxCol / 2) * tileSize, 2 * tileSize), "left"));

        // Walkway (Bottom, col=maxCol/2) <-> Library (Bottom, col=maxCol/2)
        walkway.addLocation(new Location("D-Block (Library)", new Position(maxCol / 2, maxRow - 1), ZoneType.LIBRARY, new Position((maxCol / 2) * tileSize, 5 * tileSize), "down"));
        library.addLocation(new Location("Campus Walkway", new Position(maxCol / 2, maxRow - 1), ZoneType.WALKWAY, new Position((maxCol / 2) * tileSize, (maxRow - 3) * tileSize), "down"));
    }

    public void createBlock(int id, String name) {
        // Only add if not exists
        if (getBlock(id) == null) {
            blocks.add(new Block(id, name));
        }
    }

    public Zone getZone(ZoneType type) {
        for (Block b : blocks) {
            for (Zone z : b.getZones()) {
                if (z.getType() == type) {
                    return z;
                }
            }
        }
        return null;
    }

    public Zone getZone(int zoneId) {
        // Placeholder implementation to match PUML signature exactly
        for (Block b : blocks) {
            for (Zone z : b.getZones()) {
                if (z.getType().ordinal() == zoneId) return z;
            }
        }
        return null;
    }

    public Block getBlock(int blockId) {
        for (Block b : blocks) {
            if (b.getBlockId() == blockId) return b;
        }
        return null;
    }

    public boolean isRouteBlocked(int fromZoneId, int toZoneId) {
        // Placeholder implementation to match PUML signature exactly
        return false;
    }

    public boolean isRouteBlocked(ZoneType fromZone, ZoneType toZone) {
        Zone from = getZone(fromZone);
        if (from != null) {
            for (Location loc : from.getLocations()) {
                if (loc.getTargetZone() == toZone) {
                    Zone to = getZone(toZone);
                    return to != null && to.isLocked();
                }
            }
        }
        return true; // No route exists
    }

    public Zone getSpawnZone() {
        return getZone(ZoneType.GROUND); // Default
    }
}
