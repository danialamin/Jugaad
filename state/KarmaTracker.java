package state;

import java.util.ArrayList;
import java.util.List;

public class KarmaTracker {
    private List<KarmaEvent> history = new ArrayList<>();
    private int total = 50;

    public void add(int amount, String reason) {
        history.add(new KarmaEvent(amount, reason));
        total += amount;
    }

    public void deduct(int amount, String reason) {
        history.add(new KarmaEvent(-amount, reason));
        total -= amount;
    }

    public int getTotal() {
        return total;
    }

    public List<KarmaEvent> getHistory() {
        return history;
    }
}
