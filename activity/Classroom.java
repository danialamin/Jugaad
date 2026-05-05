package activity;

import controller.GameSession;
import entity.Player;
import interfaces.IInteractable;

public class Classroom implements IInteractable {
    private int roomId;
    private String subject;
    private int scheduledHour;
    private boolean isAttended;

    public Classroom(int roomId, String subject, int scheduledHour) {
        this.roomId = roomId;
        this.subject = subject;
        this.scheduledHour = scheduledHour;
    }

    @Override
    public void onInteract(Player player, GameSession session) {
        if (!isAttended) {
            startLecture(player);
        } else {
            System.out.println("You have already attended " + subject + " today.");
        }
    }

    public boolean isAvailable(int currentHour) {
        return currentHour == scheduledHour && !isAttended;
    }

    public Quiz startLecture(Player player) {
        // SD-UC2 line 32-34: classroom.startLecture(player) → generateQuiz() → return quiz to caller
        System.out.println("Attending lecture: " + subject);
        Quiz quiz = generateQuiz();
        System.out.println("Quiz generated for: " + subject);
        return quiz;
    }

    public Quiz generateQuiz() {
        return new Quiz();
    }

    public void markAttended() {
        this.isAttended = true;
    }
}
