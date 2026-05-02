# CampusFlex - Domain Model & Sequence Diagrams Summary

## Project Info
- **Team**: Muhammad Dyen Asif (24I-0608), Abdullah Aamir (24I-0574), Danial Amin (24I-0669)
- **Section**: D | **Institution**: FAST NUCES

---

## Domain Model Overview

### Core Entities

**Player**
- Attributes: name, currentHP, maxHP, level, energy, stress, karma, currentMode, isUsedQuiz
- Relationships: carries Inventory, navigates CampusMap, receives/defeats Zombies, engages DebugChallenge

**CombatEncounter**
- Attributes: isResolved, combatType
- Relationships: may contain Zombies or DebugChallenges
- Initiates when Player contacts enemies

**Inventory**
- Manages Player items
- Relationships: contains Items, interacts with CampusMap

**Location** (Base class for all campus areas)
- Attributes: longitude, latitude, floorLevel, buildingName, isAccessible
- Relationships: contains Floors, accessed from CampusMap, has associated NPCs/Quizzes

**Player Stats**
- GPA (Grade Point Average)
- Energy (depleted by activities, restored at Canteen)
- Stress (increased by exams/challenges, reduced by Prayer/Phone)
- Karma (affected by NPC interactions, impacts Zombie Mode difficulty)

### Key Classes

**Floor** - Subdivisions within buildings; contains Classrooms, Labs, PrayerAreas, Canteens, Fooditems

**Classroom** - Holds Lectures and Quizzes; tracks attendance

**Lecture** - Educational content with knowledgeTag and hasQuiz flag

**Quiz** - Assessment mechanism; question + type + correctAnswer + qValue (difficulty)

**CampusMap** - Central navigation hub; defines all explorable locations and zone boundaries

**Zombie** - Enemy entity with name, hp, isPacified status

**NPC** - Interactive characters with role and dialogue; can be hostile/pacified based on Player karma

**Route** - Connections between Floors; isBlocked flag determines accessibility

**Item** - Collectible objects in inventory; type, name, description

**GameEnding** - Terminal state; type, description, karmaThreshold; can trigger positive or negative outcomes

**FinalBoss** - Special enemy in ResearchLab; phase-based combat, isDefeated flag

**DebugChallenge** - Academic combat; type, position, correctAnswer, timeLimit, karmaReward

**DialogueOption** - NPC conversation choices; text, karmaEffect

**Fooditem** - Canteen mechanics; name, price, nutritionValue, stressReduction

**ResearchLab** - Location where final boss is encountered

---

## Sequence Diagram Summaries

### Use Case 1: Start New Game (SSD)
1. Player calls `openGame()`
2. System displays Main Menu
3. Player selects `selectNewGame()`
4. Intro cutscene plays
5. Stats initialized (GPA, Energy, Stress, Karma = defaults)
6. FAST-NU Campus map loaded
7. Player spawned at C-Block entrance; Normal Mode active

### Use Case 2: Attend Class & Solve Quiz (SSD)
1. Player calls `enterClassroom()`
2. Attendance cutscene plays
3. Lecture content displayed
4. Quiz presented (MCQ or short problem)
5. Player submits `submitAnswer(playerChoice)`
6. System updates GPA (increased) and sets knowledge flag
7. Player returned to campus map

### Use Case 3: Use Phone (SSD)
1. Player calls `openPhone()`
2. Phone menu displayed
3. Player selects `selectScrollSocialMedia()`
4. Phone animation plays
5. Stress –5, Karma –5 applied
6. Phone closes, gameplay resumes

### Use Case 4: Visit Canteen to Restore Energy (SSD)
1. Player calls `enterCanteen()`
2. Canteen menu displayed
3. Player selects `selectFood()`
4. Eating animation plays
5. Energy restored, Stress reduced slightly
6. Player returned to campus map

### Use Case 5: Pray at Prayer Area (SSD)
1. Player calls `enterPrayerArea()`
2. Interaction prompt displayed
3. Player executes `chooseToPlay()`
4. Prayer animation plays
5. Karma increased, prayer flag set
6. Player returned to campus map

### Use Case 6: Interact with NPC (SSD)
1. Player calls `initiateConversation()`
2. NPC dialogue displayed with response options
3. Player selects `selectResponse(playerChoice)`
4. NPC reacts positively (Karma increased)
5. Interaction logged, dialogue ends
6. Player returned to campus map

### Use Case 7: Save Game at Checkpoint (SSD)
1. Player calls `approachSavePoint()`
2. Save prompt displayed
3. Player confirms `confirmSave()`
4. Game state packaged (stats, flags, location, mode)
5. State written to database
6. "Game Saved" confirmation displayed

### Use Case 8: Explore Campus Map (SSD)
1. Player calls `exploreCampus()`
2. Adjacent C and D Block zones rendered
3. Player reaches `reachZoneBoundary()`
4. Transition trigger detected
5. New zone loaded and displayed

### Use Case 9: Engage in Combat (SSD)
1. Player calls `contactZombie()`
2. Combat screen displayed
3. Enemy HP and Player HP shown
4. Debug/Terminate options presented
5. Player executes `makeChoice(Debug)`
6. Enemy HP drops
7. Fight ends, path cleared, Karma updated
8. Player returned to campus map

### Use Case 10: Debug Enemy (Pacifist Combat)
1. Player selects `selectDebug()`
2. Programming/logic challenge presented
3. Player submits `submitSolution(answer)`
4. System checks `checkInput()`
5. Success animation plays
6. Karma stats updated
7. Campus map displayed

### Use Case 11: Navigate Campus Obstacles (SSD)
1. Player calls `attemptBlockedRoute()`
2. Barrier message: "Stairs are sealed. Find another way."
3. Player executes `takeAlternateRoute()`
4. Alternate path opens to D-Block
5. Player calls `collectKey()`
6. Key added to inventory, door unlocked
7. Route cleared, player can proceed

### Use Case 12: Deploy System Patch – Final Boss (SSD)
1. Player enters `enterResearchLab()`
2. Final boss cutscene plays
3. Boss fight initiates
4. Player solves `solveChallenge()`
5. Boss defeated, patch deployment interface shown
6. Player confirms `confirmPatch()`
7. Total Karma calculated
8. Pacifist Ending triggered (campus saved, semester passed)
9. Credits roll, return to Main Menu

---

## Stat System

| Stat | Purpose | Affected By | Impact |
|------|---------|-------------|--------|
| **GPA** | Academic performance | Attending classes, passing quizzes | Affects Normal Mode progression |
| **Energy** | Daily capacity | Classes, sleep, eating | Low = reduced effectiveness |
| **Stress** | Mental load | Exams, combat, phone use | High = stat decay, zombie difficulty |
| **Karma** | Moral standing | NPC interactions, choices | Determines Zombie Mode difficulty/ending |
| **Sleep** | Recovery | Prayer, rest | Critical for stat regeneration |

---

## Game Flow

```
Normal Mode (Regular Semester)
├─ Attend Classes → Quiz → GPA increase
├─ Use Phone → Stress/Karma decrease
├─ Visit Canteen → Energy increase
├─ Pray → Karma increase
├─ Interact NPCs → Karma/relationship change
└─ Save at Checkpoints

Dead Week Trigger ↓

Zombie Mode (Survival)
├─ Explore campus as zombie apocalypse
├─ Combat encounters (Debug or Terminate)
├─ Collect keys/items for locked routes
├─ Reach Research Lab
├─ Final Boss fight (DebugChallenge-based)
└─ Deploy patch → Ending (based on Karma)
```

---

## Key Design Patterns Evident

- **State Pattern**: Normal Mode vs. Zombie Mode
- **Strategy Pattern**: Debug vs. Terminate combat options
- **Observer Pattern**: Stat system tracking player actions
- **Command Pattern**: Player actions (enterClassroom, openPhone, etc.)
- **Factory Pattern**: Location/NPC instantiation

---

## Gameplay Philosophy

**"Actions have consequences"** — Decisions in Normal Mode directly affect Zombie Mode difficulty, NPC behavior, and endings. High karma = pacifist path; Low karma = enemies more hostile.
