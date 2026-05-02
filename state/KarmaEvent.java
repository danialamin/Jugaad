package state;

public class KarmaEvent {
    private int amount;
    private String reason;
    private String timestamp;

    public KarmaEvent(int amount, String reason) {
        this.amount = amount;
        this.reason = reason;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public int getAmount() { return amount; }
    public String getReason() { return reason; }
}
