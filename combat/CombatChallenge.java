package combat;

import interfaces.IStatModifier;
import entity.StatModifierImpl;
import java.util.Random;

/**
 * Quiz challenge system for DEBUG combat strategy.
 * Difficulty scales: EASY (1), MEDIUM (2), HARD (3).
 * Per SD-UC10: CombatChallenge.present() → player answers → evaluate(answer).
 */
public class CombatChallenge {
    private int challengeId;
    private String question;
    private String[] options; // 4 options
    private int correctIndex; // 0-3
    private int timeLimit;
    private int difficulty; // 1=easy, 2=medium, 3=hard

    // ─── Question Banks ───
    private static final String[][] EASY_QUESTIONS = {
        {"What does OOP stand for?", "Object Oriented Programming", "Only One Process", "Open Operation Protocol", "Ordered Object Parsing", "0"},
        {"Which keyword creates an object in Java?", "new", "create", "make", "object", "0"},
        {"What is the base class of all Java classes?", "Object", "Base", "Root", "Main", "0"},
        {"Which of these is NOT a primitive type?", "String", "int", "boolean", "char", "0"},
        {"What does 'void' mean in a method?", "Returns nothing", "Returns int", "Takes no params", "Is abstract", "0"},
        {"What symbol starts a comment in Java?", "//", "##", "**", "@@", "0"},
        {"Which loop checks condition AFTER execution?", "do-while", "for", "while", "foreach", "0"},
        {"What is an array index starting point?", "0", "1", "-1", "It varies", "0"},
    };

    private static final String[][] MEDIUM_QUESTIONS = {
        {"What is polymorphism?", "Many forms of a method", "Multiple inheritance", "Type casting only", "Variable shadowing", "0"},
        {"Which design pattern ensures one instance?", "Singleton", "Factory", "Observer", "Adapter", "0"},
        {"What does the 'final' keyword prevent?", "Overriding/reassignment", "Compilation", "Garbage collection", "Inheritance only", "0"},
        {"What is encapsulation?", "Hiding internal state", "Making everything public", "Using only static methods", "Removing getters", "0"},
        {"What does SOLID's 'S' stand for?", "Single Responsibility", "Static Reference", "Simple Execution", "Sealed Class", "0"},
        {"Which collection allows duplicate elements?", "List", "Set", "Map keys", "HashSet", "0"},
        {"What is a deadlock?", "Threads waiting on each other", "A crashed program", "Memory overflow", "Stack underflow", "0"},
        {"What is the time complexity of binary search?", "O(log n)", "O(n)", "O(n²)", "O(1)", "0"},
    };

    private static final String[][] HARD_QUESTIONS = {
        {"What makes A* search optimal?", "Admissible heuristic", "Random exploration", "Depth-first order", "No heuristic needed", "0"},
        {"Liskov Substitution Principle means?", "Subtypes must be substitutable", "Use only abstract classes", "Avoid inheritance", "Override everything", "0"},
        {"What is the Gang of Four?", "Design Patterns authors", "A Java framework", "An OS kernel team", "A testing methodology", "0"},
        {"Which pattern decouples strategy at runtime?", "Strategy Pattern", "Singleton Pattern", "Builder Pattern", "Proxy Pattern", "0"},
        {"What is GRASP in software design?", "General assignment of responsibility", "Graphics Rendering And Signal Processing", "Grouped Resource Allocation", "Generic Runtime Abstraction", "0"},
        {"What is the Observer pattern used for?", "Event notification", "Object creation", "Memory management", "Thread synchronization", "0"},
        {"What is memoization?", "Caching computed results", "Memorizing code syntax", "Logging all function calls", "Precompiling bytecode", "0"},
        {"What is the halting problem?", "Undecidable if program halts", "When CPU overheats", "Stopping a deadlock", "Pausing a thread", "0"},
    };

    // Boss-specific questions
    private static final String[][] BOSS_QUESTIONS = {
        {"In the Adapter pattern, who translates?", "The Adapter class", "The Client", "The Adaptee", "The Interface", "0"},
        {"What does the Factory pattern create?", "Objects without specifying class", "Threads", "Database connections only", "GUI components only", "0"},
        {"State pattern lets an object alter behavior when?", "Internal state changes", "User clicks a button", "Memory is full", "Compilation completes", "0"},
    };

    private static final Random RNG = new Random();

    public CombatChallenge() {
        this(1); // default easy
    }

    public CombatChallenge(int difficulty) {
        this.difficulty = Math.max(1, Math.min(3, difficulty));
        this.timeLimit = difficulty == 1 ? 20 : difficulty == 2 ? 15 : 12;
        generateQuestion();
    }

    /** Special constructor for boss fights. */
    public static CombatChallenge forBossPhase(int phase) {
        CombatChallenge c = new CombatChallenge(3);
        if (phase >= 1 && phase <= BOSS_QUESTIONS.length) {
            String[] q = BOSS_QUESTIONS[phase - 1];
            c.question = q[0];
            c.options = new String[]{q[1], q[2], q[3], q[4]};
            c.correctIndex = Integer.parseInt(q[5]);
        }
        c.timeLimit = 15;
        return c;
    }

    private void generateQuestion() {
        String[][] bank;
        if (difficulty == 1) bank = EASY_QUESTIONS;
        else if (difficulty == 2) bank = MEDIUM_QUESTIONS;
        else bank = HARD_QUESTIONS;

        String[] q = bank[RNG.nextInt(bank.length)];
        this.question = q[0];
        this.options = new String[]{q[1], q[2], q[3], q[4]};
        this.correctIndex = Integer.parseInt(q[5]);

        // Shuffle options to make it non-trivial (correct answer isn't always 1)
        shuffleOptions();
    }

    private void shuffleOptions() {
        String correctAnswer = options[correctIndex];
        for (int i = options.length - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            String tmp = options[i];
            options[i] = options[j];
            options[j] = tmp;
        }
        // Find new correct index after shuffle
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(correctAnswer)) {
                correctIndex = i;
                break;
            }
        }
    }

    public void present() {
        System.out.println("Combat Challenge: " + question);
    }

    /** Evaluate answer by index (0-3). */
    public boolean evaluate(int answerIndex) {
        return answerIndex == correctIndex;
    }

    /** Legacy string-based evaluate. */
    public boolean evaluate(String answer) {
        try {
            return evaluate(Integer.parseInt(answer));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public IStatModifier buildPenaltyModifier() {
        return new StatModifierImpl(0, 5, 0);
    }

    // Getters
    public String getQuestion() { return question; }
    public String[] getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public int getTimeLimit() { return timeLimit; }
    public int getDifficulty() { return difficulty; }
}
