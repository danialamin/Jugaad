package dialogue;

import java.util.ArrayList;
import java.util.List;

public class Dialogue {
    private int dialogueId;
    private String text;
    private List<DialogueOption> options = new ArrayList<>();

    public Dialogue(int id, String text) {
        this.dialogueId = id;
        this.text = text;
    }

    public void addOption(DialogueOption opt) {
        options.add(opt);
    }

    public void display() {
        System.out.println(text);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i).getText());
        }
    }

    public List<DialogueOption> getOptions() { return options; }
    public DialogueOption getOption(int index) { return options.get(index); }
}
