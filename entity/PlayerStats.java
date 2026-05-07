package entity;

import interfaces.IStatModifier;

public class PlayerStats {
    private double gpa;
    private int energy;
    private int stress;
    private int karma;
    private int maxEnergy = 100;

    public PlayerStats() {
        this.gpa = 4.0;
        this.energy = 100;
        this.stress = 0;
        this.karma = 50;
    }

    public void applyModifier(IStatModifier mod) {
        mod.apply(this);
    }

    public void updateGPA(double delta) {
        this.gpa += delta;
        if (this.gpa > 4.0) this.gpa = 4.0;
        if (this.gpa < 0.0) this.gpa = 0.0;
    }

    public void updateEnergy(int delta) {
        this.energy += delta;
        if (this.energy > maxEnergy) this.energy = maxEnergy;
        if (this.energy < 0) this.energy = 0;
    }

    public void updateStress(int delta) {
        this.stress += delta;
        if (this.stress < 0) this.stress = 0;
        if (this.stress > 100) this.stress = 100;
    }

    public void updateKarma(int delta) {
        this.karma += delta;
    }

    public boolean isEnergyFull() {
        return this.energy >= this.maxEnergy;
    }

    public double getGPA() { return gpa; }
    public int getEnergy() { return energy; }
    public int getStress() { return stress; }
    public int getKarma() { return karma; }

    public void setGPA(double gpa) { this.gpa = gpa; }
    public void setEnergy(int energy) { this.energy = energy; }
    public void setStress(int stress) { this.stress = stress; }
    public void setKarma(int karma) { this.karma = karma; }
}
