package engine;

public class Player {
    private int id;
    private double gpa;
    private int energy;
    private int stress;
    private int karma;
    private int xLocation;
    private int yLocation;

    public Player() {}

    public Player(int id, double gpa, int energy, int stress, int karma, int xLocation, int yLocation) {
        this.id = id;
        this.gpa = gpa;
        this.energy = energy;
        this.stress = stress;
        this.karma = karma;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public int getStress() { return stress; }
    public void setStress(int stress) { this.stress = stress; }

    public int getKarma() { return karma; }
    public void setKarma(int karma) { this.karma = karma; }

    public int getXLocation() { return xLocation; }
    public void setXLocation(int xLocation) { this.xLocation = xLocation; }

    public int getYLocation() { return yLocation; }
    public void setYLocation(int yLocation) { this.yLocation = yLocation; }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", gpa=" + gpa +
                ", energy=" + energy +
                ", stress=" + stress +
                ", karma=" + karma +
                ", xLocation=" + xLocation +
                ", yLocation=" + yLocation +
                '}';
    }
}
