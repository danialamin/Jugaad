package activity;

public class EasterEgg {
    private int eggId;
    private int triggerNpcId;
    private String eggText;

    public EasterEgg(int eggId, int triggerNpcId, String eggText) {
        this.eggId = eggId;
        this.triggerNpcId = triggerNpcId;
        this.eggText = eggText;
    }

    public void trigger() {
        System.out.println("Easter Egg Triggered: " + eggText);
    }
}
