package entity;

import combat.CombatChallenge;

public abstract class Enemy {
    protected int enemyId;
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int rewardKarma;

    public void takeDamage(int amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public void resetHp() {
        hp = maxHp;
    }

    public boolean isDefeated() {
        return hp <= 0;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public String getName() {
        return name;
    }

    public void attack(Player player) {
        player.takeDamage(10);
    }

    public CombatChallenge getCombatChallenge() {
        return new CombatChallenge();
    }
}
