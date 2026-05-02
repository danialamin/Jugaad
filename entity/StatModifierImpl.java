package entity;

import interfaces.IStatModifier;

public class StatModifierImpl implements IStatModifier {
    private double gpaDelta;
    private int energyDelta;
    private int stressDelta;
    private int karmaDelta;

    public StatModifierImpl(double gpaDelta, int energyDelta, int stressDelta, int karmaDelta) {
        this.gpaDelta = gpaDelta;
        this.energyDelta = energyDelta;
        this.stressDelta = stressDelta;
        this.karmaDelta = karmaDelta;
    }

    @Override
    public void apply(PlayerStats stats) {
        stats.updateGPA(gpaDelta);
        stats.updateEnergy(energyDelta);
        stats.updateStress(stressDelta);
        stats.updateKarma(karmaDelta);
    }

    @Override
    public String getDescription() {
        return "Stats Modifier";
    }
}
