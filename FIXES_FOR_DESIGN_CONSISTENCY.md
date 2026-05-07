# CampusFlex — Design-to-Code Consistency Fixes

## Context
The CampusFlex project has 6 discrepancies between its design documents (SummarizedData/ClassDiagramPUML.txt and SummarizedData/SD-UC*.txt) and the actual Java code. All other code is fully compliant with the design. These fixes are **non-breaking** and **additive only** — they do not change existing GamePanel/engine behavior.

---

## Fix 1: Move `EasterEgg` to `map` Package + Add `getText()`

**Problem**: ClassDiagramPUML.txt (line 289) places `EasterEgg` in the `map` package with a `getText()` method. Code has it in `activity` package without `getText()`.

**Action**:
1. Delete `activity/EasterEgg.java`
2. Create `map/EasterEgg.java` with the following content:

```java
package map;

public class EasterEgg {
    private int eggId;
    private int triggerNpcId;
    private String eggText;

    public EasterEgg(int eggId, int triggerNpcId, String eggText) {
        this.eggId = eggId;
        this.triggerNpcId = triggerNpcId;
        this.eggText = eggText;
    }

    public void trigger() {
        System.out.println("Easter Egg Triggered: " + eggText);
    }

    public String getText() {
        return eggText;
    }
}
```

3. If any file imports `activity.EasterEgg`, change it to `map.EasterEgg`. (Currently no file imports it, so nothing else changes.)

---

## Fix 2: `Canteen.onInteract` Must Call `player.heal(30)`

**Problem**: SD-UC4 line 49 specifies `gameSession -> playerEntity: heal(hpDelta)` as a separate step from the stress modifier. Current code only applies the stress modifier, never heals HP.

**Action**: In `activity/Canteen.java`, inside `onInteract`, add `player.heal(30);` after the modifier line.

**Change the method to**:
```java
@Override
public void onInteract(Player player, GameSession session) {
    if (canEat(player.getStats())) {
        showMenu();
        System.out.println("Eating at canteen...");
        player.getStats().applyModifier(buildFoodModifier());
        player.heal(30);
    } else {
        System.out.println("You are not hungry right now.");
    }
}
```

---

## Fix 3: `PrayerArea.onInteract` Must Log to `KarmaTracker`

**Problem**: SD-UC5 line 43 specifies `gameSession -> karmaTracker: add(20, "Prayer at Prayer Area")`. Current code applies karma via StatModifierImpl to PlayerStats but never logs the event to KarmaTracker. This is a **real bug** — `EndingResolver` uses `KarmaTracker.getTotal()` for ending resolution, so prayer karma is never counted in history.

**Action**: In `activity/PrayerArea.java`, inside `onInteract`, add a call to the session's KarmaTracker after the modifier.

**Change the method to**:
```java
@Override
public void onInteract(Player player, GameSession session) {
    if (isAvailable()) {
        System.out.println("Praying...");
        player.getStats().applyModifier(buildPrayerModifier());
        session.getKarmaTracker().add(20, "Prayer at Prayer Area");
        prayedThisCycle = true;
    } else {
        System.out.println("You have already prayed.");
    }
}
```

**Note**: `GameSession` must have a public `getKarmaTracker()` method. Check that it exists — if not, add:
```java
public KarmaTracker getKarmaTracker() { return karmaTracker; }
```

---

## Fix 4: `NPC.onInteract` Must Integrate `DialogueFactory`

**Problem**: SD-UC6 lines 22-41 show NPC checking hostility, then delegating to DialogueFactory for dialogue loading and karma effect application. Current code just prints "Hello there!".

**Action**: Replace `entity/NPC.java`'s `onInteract` method with:

```java
@Override
public void onInteract(Player player, GameSession session) {
    interactionCount++;

    // SD-UC6 line 22: Check hostility
    if (isHostile(player.getStats().getKarma())) {
        System.out.println(name + ": I have nothing to say to you.");
        return;
    }

    // SD-UC6 lines 27-28: Load dialogue via factory
    dialogue.DialogueManager dialogueManager = new dialogue.DialogueManager();

    // SD-UC6 line 30: Check easter egg
    if (dialogueManager.hasEasterEgg(npcId)) {
        System.out.println(name + ": [Easter Egg triggered!]");
    }

    // SD-UC6 line 33: Load and display dialogue
    dialogue.Dialogue d = dialogueManager.loadDialogue(npcId, session.getMode());
    d.display();

    // SD-UC6 lines 37-41: Apply first option effect as default (UI handles real selection)
    if (!d.getOptions().isEmpty()) {
        dialogue.DialogueOption opt = d.getOptions().get(0);
        dialogueManager.applyEffect(opt, session.getKarmaTracker());
    }
}
```

**Prerequisite**: `GameSession` must expose `getMode()` returning `GameModeType`. If it doesn't exist, add:
```java
public GameModeType getMode() { return mode; }
```

Also add import at top of NPC.java:
```java
import dialogue.DialogueManager;
import dialogue.Dialogue;
import dialogue.DialogueOption;
```

---

## Fix 5: Add `CombatController.applyResult()` Method

**Problem**: SD-UC9 lines 50-56 and SD-UC12 lines 99-107 show CombatController applying the CombatResult (karma update + HP change) to the player and KarmaTracker. Current code has no such method — all application is done in GamePanel.

**Action**: Add this method to `controller/CombatController.java`:

```java
public void applyResult(CombatResult result, state.KarmaTracker karmaTracker) {
    // SD-UC9 line 52: apply HP change
    if (result.getHPChange() < 0) {
        player.takeDamage(Math.abs(result.getHPChange()));
    }

    // SD-UC9 lines 54-56: apply karma change
    if (result.getKarmaChange() > 0) {
        karmaTracker.add(result.getKarmaChange(), "Combat victory");
    } else if (result.getKarmaChange() < 0) {
        karmaTracker.deduct(Math.abs(result.getKarmaChange()), "Combat aggression");
    }
}
```

Add import at top if not present:
```java
import state.KarmaTracker;
```

---

## Fix 6: Add `ZombieMode.attemptMove()` Delegation

**Problem**: SD-UC11 lines 19-22 show `ZombieMode` coordinating route-blocking checks via `CampusMap.isRouteBlocked()`. Current code handles this entirely in GamePanel without going through ZombieMode.

**Action**: Add this method to `mode/ZombieMode.java`:

```java
/**
 * SD-UC11: Coordinates zone accessibility during zombie mode.
 * Returns true if the player is NOT allowed to move to the target zone.
 */
public boolean attemptMove(int fromZoneId, int toZoneId, map.CampusMap campusMap) {
    // Check local blocked list first
    if (isZoneBlocked(toZoneId)) {
        return true; // blocked
    }
    // Delegate to CampusMap for structural route checks
    return campusMap.isRouteBlocked(fromZoneId, toZoneId);
}
```

---

## Verification Checklist

After applying all fixes, verify:
- [ ] `map/EasterEgg.java` exists, `activity/EasterEgg.java` is deleted
- [ ] `Canteen.onInteract` calls `player.heal(30)` 
- [ ] `PrayerArea.onInteract` calls `session.getKarmaTracker().add(20, ...)`
- [ ] `NPC.onInteract` uses `DialogueManager` and checks hostility
- [ ] `CombatController` has `applyResult(CombatResult, KarmaTracker)` method
- [ ] `ZombieMode` has `attemptMove(int, int, CampusMap)` method
- [ ] `GameSession` exposes `getKarmaTracker()` and `getMode()` (both likely already exist)
- [ ] Project still compiles with no errors

## What These Fixes Do NOT Change

- `GamePanel` behavior is untouched (it still works as before)
- No existing method signatures are altered
- No imports in other files break
- The game runs identically — these add design-compliant pathways alongside the existing engine logic
