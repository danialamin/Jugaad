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
            case PACIFIST: 
                return "The Corrupted AI's firewall shatters, but rather than destroying it, you deployed a logical patch. The infected systems slowly reboot. The red emergency lights fade back to a calm fluorescent white. You saved the campus through intellect and patience, preserving both the hardware and the students' sanity. The University honors you as a true engineer.";
            case EVIL: 
                return "You brute-forced your way through every problem, tearing down the AI's core. Sparks shower the server room as the mainframes violently crash. The zombies collapse, but the entire FAST-NU infrastructure is destroyed. Student records, grades, and the campus network are gone forever. You survived, but at what cost?";
            default: 
                return "You managed to defeat the Corrupted AI using a mix of force and logic. The campus is safe, but several servers require heavy maintenance. The administration is relieved, though questions remain about the long-term stability of the network. A solid, if imperfect, victory.";
        }
    }
}
