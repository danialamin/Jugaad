package entity;

public class ZombieEnemy extends Enemy {
    private String type;
    private int patrolZoneId;

    public ZombieEnemy(String type, int patrolZoneId) {
        this.type = type;
        this.patrolZoneId = patrolZoneId;
        this.name = "Zombie " + type;
        this.hp = 50;
        this.maxHp = 50;
        this.rewardKarma = 10;
    }

    public String getType() { return type; }
    public int getPatrolZoneId() { return patrolZoneId; }
}
