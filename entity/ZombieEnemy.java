package entity;

public class ZombieEnemy extends Enemy {
    private String type;
    private int patrolZoneId;

    public ZombieEnemy(String type, int patrolZoneId) {
        this(type, patrolZoneId, 50);
    }

    public ZombieEnemy(String type, int patrolZoneId, int customHp) {
        this.type = type;
        this.patrolZoneId = patrolZoneId;
        this.name = "Zombie " + type;
        this.hp = customHp;
        this.maxHp = customHp;
        this.rewardKarma = 10;
    }

    public String getType() { return type; }
    public int getPatrolZoneId() { return patrolZoneId; }
}
