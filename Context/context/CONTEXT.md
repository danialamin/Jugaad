# CampusFlex Context Map
This file serves as the strict context and source of truth for the CampusFlex 2D Game project. Any AI Assistant or IDE modifying this project MUST adhere to these rules.

## Overview
CampusFlex is a top-down 2D Java game set in a university campus.

## 🛑 STRICT RULE: The PUML Class Diagram
All architectural and code changes **MUST** strictly trace back to `CampusFlex_GRASP.puml`. Do not introduce new entities, interfaces, or logic flow without matching the provided PlantUML Class Diagram.

## Architecture Breakdown (Based on GRASP Patterns)

### 1. Controllers & State (Band 1)
- **`controller` package:** Manages game flow. `GameSession` is the core loop state holder. `CombatController` handles enemy encounters.
- **`state` package:** `GameState` manages save data (zone, position, flags). `KarmaTracker` stores morality history. `EndingResolver` determines final game state.
- **`interfaces`:** Core decoupled logic. `IInteractable`, `ICombatStrategy`, `ISaveStrategy`, `IStatModifier`.

### 2. Domain Entities, Map, Inventory (Band 2)
- **`entity` package:** 
  - `Player` holds `PlayerStats`, `Inventory`, and `Phone`. 
  - `Enemy` is an abstract base class extended by `ZombieEnemy` and `FinalBoss`. 
  - `NPC` interacts with the dialogue system.
- **`map` package:** Hierarchical generation. `CampusMap` contains `Block`s. `Block` contains `Zone`s (floors). `Zone` contains `Location`s, `NPC`s, and `Enemy`s. 
- **`inventory` package:** Manages `KeyItem` capacity.

### 3. Behaviors, Combat, Dialogue (Band 3)
- **`mode` package:** Defines gameplay modes (`NormalMode`, `ZombieMode`).
- **`activity` package:** University tasks like `Classroom` (Quiz generation), `Canteen` (Food), `PrayerArea`, and `Phone` usage.
- **`combat` package:** Uses `CombatChallenge` (Quiz-based combat) and Strategy Pattern (`DebugStrategy`, `TerminateStrategy`).
- **`dialogue` package:** Handles NPC branches, Karma effects, and choices.

## Existing Base Assets
- `assets/player_walk.png`: Sprite sheet for character movement.
- `assets/floor.png`: Standard tile graphics map.

