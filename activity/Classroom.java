package activity;

import entity.Player;
// import controller.GameSession;

public class Classroom {
    private int roomId;
    private String subject;
    private int scheduledHour;
    private boolean isAttended;

    public Classroom(int roomId) {
        this.roomId = roomId;
        this.subject = "General Studies";
        this.scheduledHour = 9;
        this.isAttended = false;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean isAvailable(int currentHour) {
        return currentHour >= scheduledHour;
    }

    public void startLecture(Player player) {
        // Lecture logic
    }

    public void markAttended() {
        this.isAttended = true;
    }
}
