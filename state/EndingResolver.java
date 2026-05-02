package state;

public class EndingResolver {
    private static final int PACIFIST_MIN = 70;
    private static final int EVIL_MAX = 30;

    public enum EndingType {
        PACIFIST,
        NEUTRAL,
        EVIL
    }

    public EndingType resolve(KarmaTracker tracker) {
        int t = tracker.getTotal();
        if (t >= PACIFIST_MIN) return EndingType.PACIFIST;
        if (t <= EVIL_MAX) return EndingType.EVIL;
        return EndingType.NEUTRAL;
    }

    public String buildEpilogueText(EndingType type) {
        switch (type) {
            case PACIFIST: return "You saved the campus!";
            case EVIL: return "You destroyed everything!";
            default: return "You survived.";
        }
    }
}
