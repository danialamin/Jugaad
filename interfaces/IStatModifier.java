package interfaces;

import entity.PlayerStats;

public interface IStatModifier {
    void apply(PlayerStats stats);
    String getDescription();
}
