# CampusFlex - Use Cases Summary

**Project:** CampusFlex Game System  
**Team Members:**
- Muhammad Dyen Asif (24I-0608)
- Abdullah Aamir (24I-0574)
- Danial Amin (24I-0669)
- Section D

---

## Overview

CampusFlex is a campus-based game system featuring two main gameplay modes: **Normal Mode** and **Zombie Mode**. The game is set at FAST-NU campus (C & D Block) and focuses on player choices that affect four core statistics: GPA, Energy, Stress, and Karma. The game progresses through 12 interconnected use cases that span exploration, academics, combat, and social interaction.

---

## Game Structure

### Three Main Areas

1. **Normal Mode** (Light green zone)
   - Academic activities and campus exploration
   - Focus on maintaining stats and building knowledge
   - 6 primary use cases

2. **Zombie Mode** (Light pink zone)
   - Survival and combat-focused gameplay
   - Blocked paths and obstacles create puzzle elements
   - 4 primary use cases

3. **System/UI** (Light blue zone)
   - Universal features accessible across both modes
   - 2 use cases for navigation and saving

---

## The 12 Use Cases

### **Use Case 1: Start New Game**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player launches the game and initiates a new session.

**Main Flow:**
1. Player opens the game and reaches Main Menu
2. Selects 'New Game'
3. System plays intro cutscene
4. All player stats initialize to default values
5. Campus map (C & D Block) loads
6. Player spawns at C-Block entrance
7. Normal Mode begins

**Key Extensions:**
- Player can select 'Load Game' to resume from last checkpoint
- Player can skip the cutscene

**Pre-conditions:** Game is open on Main Menu

**Post-conditions:** New game session running with default stats; Normal Mode active

---

### **Use Case 2: Attend Class & Solve Quiz**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player attends lectures and completes quizzes to improve GPA and unlock knowledge for Zombie Mode.

**Main Flow:**
1. Player enters a classroom during scheduled hours
2. System plays attendance cutscene
3. Lecture content displays as in-game dialogue
4. Quiz appears (multiple choice or problem)
5. Player answers
6. System evaluates answer
7. Correct answer → GPA increases + knowledge flag set
8. Stats update; player returns to campus map

**Key Extensions:**
- Using phone during quiz counts as cheating (Karma -15)
- Wrong answers still give minor GPA boost without knowledge flag
- Skipping class reduces both GPA and Karma

**Pre-conditions:** Normal Mode active; player outside classroom during scheduled hours

**Post-conditions:** GPA increased; knowledge flag set if correct; Karma penalized if cheated

---

### **Use Case 3: Use Phone**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player can use phone to scroll social media or cheat on quizzes with different stat consequences.

**Main Flow:**
1. Player presses phone button
2. Phone Menu appears
3. Player selects 'Scroll Social Media'
4. Animation plays
5. Stress -5, Karma -5
6. Phone closes; gameplay resumes

**Key Extensions:**
- Using phone during quiz: Cheating flag activated, Karma -15, quiz attempt cancelled
- Player can close phone with no stat changes

**Pre-conditions:** Normal Mode active; phone menu accessible at any time

**Post-conditions:** 
- Outside quiz: Stress reduced but Karma reduced
- During quiz: Karma significantly reduced; quiz answer wiped

---

### **Use Case 4: Visit Canteen to Restore Energy**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player visits the canteen on B1 of C-Block to restore Energy.

**Main Flow:**
1. Player enters Canteen (C-Block B1)
2. Canteen menu prompt appears
3. Player selects food
4. Eating animation plays
5. Energy increases
6. Player returns to campus map

**Key Extensions:**
- If Energy already full: System responds "You're already full, bhai" with no changes
- Player can leave without ordering

**Pre-conditions:** Normal Mode running; player at Canteen; Energy below max

**Post-conditions:** Energy restored; Stress may slightly decrease

---

### **Use Case 5: Pray at Prayer Area**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player visits the Prayer Area to build Karma (limited to once per cycle).

**Main Flow:**
1. Player reaches Prayer Area (C-Block 2nd Floor)
2. Interaction prompt appears
3. Player chooses to pray
4. Prayer animation plays
5. Karma increases
6. System marks 'prayed this cycle' flag
7. Player returns to map

**Key Extensions:**
- Player can choose not to pray
- Already prayed this cycle: Prompt grayed out; message says "You've already prayed today"

**Pre-conditions:** Normal Mode active; player at Prayer Area; hasn't prayed this cycle

**Post-conditions:** Karma increased; one-time cycle flag prevents repeated use

---

### **Use Case 6: Interact with NPC**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player engages in conversations with classmates, professors, and staff. Choices affect Karma and future interactions.

**Main Flow:**
1. Player walks near NPC; dialogue prompt appears
2. Player initiates conversation
3. System displays dialogue based on mode and past interactions
4. Player selects a response
5. System checks if choice affects Karma
6. Stat changes applied in background
7. Dialogue ends; player returns to map

**Key Extensions:**
- Rude/dismissive responses lower Karma; affect NPC behavior in Zombie Mode
- Hidden Easter Egg lines exist with no stat effect
- Player can walk away without talking

**Pre-conditions:** Player close to NPC; NPC available for interaction

**Post-conditions:** 
- Kind responses raise Karma
- Dismissive responses lower Karma
- In Zombie Mode, past choices influence NPC behavior

---

### **Use Case 7: Save Game at Checkpoint**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player saves progress at glowing Save Points on the map.

**Main Flow:**
1. Player reaches a Save Point
2. System asks "Save your progress?"
3. Player confirms
4. System packages current state: stats, location, flags, quiz results, mode
5. Data written to Oracle database
6. System confirms "Game Saved"
7. Player continues playing

**Key Extensions:**
- Player can cancel; nothing is written
- Database failure: Error message displayed; no partial data saved

**Pre-conditions:** Player at or next to a Save Point

**Post-conditions:** Current game state stored in database; can resume from this exact point

---

### **Use Case 8: Explore Campus Map**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player freely roams the C and D Block campus zones.

**Main Flow:**
1. Player uses directional controls to move
2. System renders surrounding zones
3. Player reaches zone boundary (e.g., bridge from C to D Block)
4. System detects transition trigger
5. Next zone loads
6. Player continues exploring in new area

**Key Extensions:**
- In Zombie Mode, sealed routes show barriers with hints toward alternate paths
- Zone load failure: Error displayed; player stays in current location

**Pre-conditions:** Game session active (Normal or Zombie Mode); player on campus map

**Post-conditions:** Player moved to new location or loaded new zone; blocked routes enforced in Zombie Mode

---

### **Use Case 9: Engage in Combat**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player encounters zombies and chooses between pacifist (Debug) or brute force (Terminate) approaches.

**Main Flow:**
1. Player makes contact with zombie
2. Combat screen appears
3. System displays enemy and player's current HP
4. System presents two options: Debug (Pacifist) or Terminate (Brute Force)
5. Player chooses approach
6. System executes chosen path
7. Enemy HP drops
8. If enemy HP reaches zero: Fight ends, path clears, Karma updates
9. Player returns to map

**Key Extensions:**
- Player's HP hits zero: Game over; player respawns at last checkpoint
- Repeated Terminate choices: Karma continuously drops, affecting final ending

**Pre-conditions:** Zombie Mode active; player encountered zombie; Player HP > 0

**Post-conditions:** Fight over; path clear if enemy defeated or player back at checkpoint; Karma adjusted

---

### **Use Case 10: Debug Enemy (Pacifist Combat)**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player solves programming/logic challenges to neutralize zombies non-violently.

**Main Flow:**
1. Player selects 'Debug' during combat
2. System presents challenge (fix code snippet, CS question, etc.)
3. Player types solution or picks answer
4. System evaluates input
5. Correct: Success animation; zombie pacified; Karma increases
6. Fight ends; player returns to map

**Key Extensions:**
- Wrong answer: Zombie bites back (minor HP loss); player can retry or switch to Terminate
- Time penalty: If player takes too long, HP loss triggered and challenge resets or forces Terminate

**Pre-conditions:** Combat encounter in progress; player selected 'Debug' option

**Post-conditions:** 
- If correct: Zombie neutralized; Karma increased
- If wrong: Player took damage; can retry or switch tactics

---

### **Use Case 11: Navigate Campus Obstacles**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player finds alternate routes and collects keys to unlock blocked paths in Zombie Mode.

**Main Flow:**
1. Player attempts blocked route (e.g., C-Block stairs)
2. System displays barrier with message ("Stairs are sealed. Find another way.")
3. Player takes alternate route (e.g., D-Block bridge)
4. Player finds locked door requiring key
5. Player navigates to key location (e.g., D-Block B2/B3 car park)
6. Player collects key
7. System adds key to inventory; door unlocks
8. Player proceeds through opened route

**Key Extensions:**
- Attempting locked door without key: Message "It's locked. You need to find the key."
- Key location guarded by zombie: Combat initiates before key pickup

**Pre-conditions:** Zombie Mode active; player encountered blocked path or locked door

**Post-conditions:** Player has key or found alternate path; blocked route now open; can continue climbing toward labs

---

### **Use Case 12: Deploy System Patch (Final Boss)**

**Scope:** CampusFlex Game System | **Level:** User-Goal

**Summary:** Player reaches the Research Lab and defeats the final boss to end the outbreak and determine the game's ending.

**Main Flow:**
1. Player enters Research Lab (top of D-Block)
2. System plays final boss cutscene (corrupted AI appears)
3. Final boss fight initiates (escalated version of normal combat)
4. Player solves final challenge or uses brute force
5. Boss defeated
6. System shows patch deployment interface
7. Player confirms patch deployment
8. System totals Karma from Normal and Zombie Mode
9. Ending determined by total Karma:
   - **High Karma (Pacifist Ending):** Campus saved; player passes semester; celebration
   - **Low Karma (Evil Ending):** Campus saved; player expelled; dark epilogue
   - **Mixed Karma (Normal Ending):** Campus saved; bittersweet outcome
10. Credits roll; game returns to Main Menu

**Key Extensions:**
- Final challenge failed: HP drops; player can retry or use Terminate at Karma cost
- No checkpoint before boss: System forces retry from lab entrance

**Pre-conditions:** Zombie Mode active; player reached Research Lab; all obstacles cleared

**Post-conditions:** Final boss defeated; Karma totaled; ending determined; game session complete

---

## Key Game Mechanics

### Core Statistics
- **GPA:** Increased through attending classes and solving quizzes correctly
- **Energy:** Consumed by movement; restored at the Canteen
- **Stress:** Reduced through phone use (with Karma cost) or eating
- **Karma:** Central mechanic affecting ending; increased through praying, kind NPC interactions, and pacifist combat; decreased through cheating, rudeness, and violent choices

### Game Flow
- **Normal Mode → Zombie Mode:** Triggered by specific story progression
- **Checkpoint System:** Players save progress via Save Points; determines respawn location
- **Knowledge Carryover:** Correct quiz answers unlock knowledge flags useful in Zombie Mode
- **NPC Memory:** NPCs remember player choices and behave accordingly

### Multiple Endings
Game ending (Pacifist, Evil, Mixed) determined by cumulative Karma across both modes

---

## System Architecture Notes

- **Database:** Oracle database stores all game state (stats, location, flags, quiz results, mode)
- **Campus Setting:** FAST-NU campus (C & D Block) serves as primary game world
- **Zone System:** Automatic zone loading at boundaries; dynamic path blocking in Zombie Mode
- **Dialogue System:** Context-aware NPC responses based on current mode and interaction history

---

## Design Philosophy

CampusFlex blends academic progression (GPA, classes) with moral choice mechanics (Karma) to create a campus-themed game where player decisions meaningfully impact both gameplay and narrative outcome. The inclusion of pacifist combat options and knowledge-based challenges emphasizes problem-solving over violence.
