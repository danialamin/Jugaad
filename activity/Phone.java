package activity;

// Phone behavior follows UC3 in SummarizedData/CampusFlex_Use_Cases_Summary.md
// and activity.Phone in SummarizedData/ClassDiagramPUML.txt.

import controller.GameSession;
import interfaces.IStatModifier;
import entity.StatModifierImpl;

public class Phone {
    private boolean isOpen;

    public void open() { isOpen = true; }
    public void close() { isOpen = false; }

    /** UC3 outside-quiz use: stress drops, but wasting time still costs Karma. */
    public IStatModifier buildSocialMediaModifier() {
        return new StatModifierImpl(0.0, 0, -5, -5);
    }

    /** UC3 quiz extension: phone use during a quiz is cheating. */
    public IStatModifier buildCheatModifier() {
        return new StatModifierImpl(0.0, 0, 25, -15);
    }

    /** True while the Classroom/Quiz flow is active per SD-UC3. */
    public boolean isDuringQuiz(GameSession session) {
        return session != null && session.isLectureQuizActive();
    }
}
