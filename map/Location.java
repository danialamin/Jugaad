package map;

import entity.Player;
import entity.Position;
import interfaces.IInteractable;
import controller.GameSession;

public class Location {
    private int locationId;
    private String name;
    private boolean isInteractable;
    private float triggerX;
    private float triggerY;
    private float triggerRadius;
    private IInteractable interactable;

    // Original fields for Door logic
    private Position position;
    private ZoneType targetZone;
    private Position targetSpawnPosition;
    private String requiredDirection;

    public Location(int id, String name, boolean isInteractable, IInteractable i, float triggerX, float triggerY, float triggerRadius) {
        this.locationId = id;
        this.name = name;
        this.isInteractable = isInteractable;
        this.interactable = i;
        this.triggerX = triggerX;
        this.triggerY = triggerY;
        this.triggerRadius = triggerRadius;
    }

    // Door constructor for GamePanel compatibility
    public Location(String name, Position position, ZoneType targetZone, Position targetSpawnPosition, String requiredDirection) {
        this.name = name;
        this.position = position;
        this.targetZone = targetZone;
        this.targetSpawnPosition = targetSpawnPosition;
        this.requiredDirection = requiredDirection;
        this.triggerX = position.getX();
        this.triggerY = position.getY();
        this.triggerRadius = 32; // Default Tile Size radius
    }

    public void setInteractable(IInteractable i) {
        this.interactable = i;
    }

    public void interact(Player player, GameSession session) {
        if (isInteractable && interactable != null) {
            interactable.onInteract(player, session);
        }
    }

    public boolean isPlayerInRange(Position pos) {
        float dx = pos.getX() - triggerX;
        float dy = pos.getY() - triggerY;
        return Math.sqrt(dx * dx + dy * dy) <= triggerRadius;
    }

    public boolean isInteractable() { return isInteractable; }
    public String getName() { return name; }
    
    public Position getPosition() { return position; }
    public ZoneType getTargetZone() { return targetZone; }
    public Position getTargetSpawnPosition() { return targetSpawnPosition; }
    public String getRequiredDirection() { return requiredDirection; }
}
