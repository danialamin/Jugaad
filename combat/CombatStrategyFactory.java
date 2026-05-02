package combat;

import interfaces.ICombatStrategy;

public class CombatStrategyFactory {
    public ICombatStrategy createStrategy(String strategyType) {
        if ("terminate".equalsIgnoreCase(strategyType)) {
            return new TerminateStrategy();
        } else if ("debug".equalsIgnoreCase(strategyType)) {
            return new DebugStrategy(new CombatChallenge());
        }
        return new TerminateStrategy(); // default
    }
}
