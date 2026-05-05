package entity;

import interfaces.IStatModifier;

public class StatModifierImpl implements IStatModifier {
    private double gpaDelta;
    private int stressDelta;
    private int karmaDelta;

    public StatModifierImpl(double gpaDelta, int stressDelta, int karmaDelta) {
        this.gpaDelta = gpaDelta;
        this.stressDelta = stressDelta;
        this.karmaDelta = karmaDelta;
    }

    @Override
    public void apply(PlayerStats stats) {
        stats.updateGPA(gpaDelta);
        stats.updateStress(stressDelta);
        stats.updateKarma(karmaDelta);
    }

    @Override
    public String getDescription() {
        return "Stats Modifier";
    }
}
