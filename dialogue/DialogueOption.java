package dialogue;

public class DialogueOption {
    private String optionText;
    private int karmaEffect;
    private boolean isKind;

    public DialogueOption(String text, int karma, boolean kind) {
        this.optionText = text;
        this.karmaEffect = karma;
        this.isKind = kind;
    }

    public String getText() { return optionText; }
    public int getKarmaEffect() { return karmaEffect; }
    public boolean isKindChoice() { return isKind; }
}
