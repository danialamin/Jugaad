package combat;

import interfaces.ICombatStrategy;

public class CombatStrategyFactory {
    public ICombatStrategy createStrategy(String strategyType) {
        return createStrategy(strategyType, false);
    }

    /** @param isBossFight when true, Terminate uses SD-UC12 karma penalty (-15) instead of SD-UC9 (-10). */
    public ICombatStrategy createStrategy(String strategyType, boolean isBossFight) {
        if ("terminate".equalsIgnoreCase(strategyType)) {
            // SD-UC9 line 74: normal=-10; SD-UC12 line 102: boss=-15
            return new TerminateStrategy(isBossFight ? -15 : -10);
        } else if ("debug".equalsIgnoreCase(strategyType)) {
            return new DebugStrategy(new CombatChallenge());
        }
        return new TerminateStrategy(); // default
    }
}
