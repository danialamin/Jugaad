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

    public void startLecture(Player player) {
        System.out.println("Attending lecture: " + subject);
        Quiz quiz = generateQuiz();
        System.out.println("A quiz has started!");
        player.getStats().applyModifier(quiz.buildResultModifier(true)); // Placeholder auto-win
        markAttended();
    }

    public Quiz generateQuiz() {
        return new Quiz();
    }

    public void markAttended() {
        this.isAttended = true;
    }
}
