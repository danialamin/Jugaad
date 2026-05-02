package map;
import entity.Position;

public class Location {
    private String name;
    private Position position;
    private ZoneType targetZone;
    private Position targetSpawnPosition;
    private String requiredDirection; // "up", "down", "left", "right"

    public Location(String name, Position position, ZoneType targetZone, Position targetSpawnPosition, String requiredDirection) {
        this.name = name;
        this.position = position;
        this.targetZone = targetZone;
        this.targetSpawnPosition = targetSpawnPosition;
        this.requiredDirection = requiredDirection;
    }

    public String getName() { return name; }
    public Position getPosition() { return position; }
    public ZoneType getTargetZone() { return targetZone; }
    public Position getTargetSpawnPosition() { return targetSpawnPosition; }
    public String getRequiredDirection() { return requiredDirection; }
}
