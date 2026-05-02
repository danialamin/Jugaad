# CampusFlex - Project Overview & Proposal Summary

## Project Header
- **Title**: CampusFlex — "A Campus which you have to survive."
- **Type**: 2D Role-Playing Game (RPG) in Java
- **Team**: 
  - Muhammad Dyen Asif (24I-0608)
  - Abdullah Aamir (24I-0574)
  - Danial Amin (24I-0669)
- **Section**: D
- **Institution**: FAST-NUCES Islamabad, Pakistan
- **Document Date**: February 2026

---

## 1. Project Overview

### What is CampusFlex?

CampusFlex is a **2D RPG that simulates student life at FAST-NU Islamabad** with two distinct gameplay modes:

#### **Normal Mode (Regular Semester)**
Players experience a typical semester including:
- Attending classes
- Studying in the library
- Participating in events
- Socializing
- Visiting the mosque
- Managing sleep schedules
- Interacting with mobile phones

**Core Mechanics**: Every activity impacts player stats:
- **GPA** (Grade Point Average)
- **Energy**
- **Stress**
- **Sleep**
- **Karma**

Trade-offs are built in: balancing academic performance with personal well-being.

#### **Zombie Mode (Dead Week/Apocalypse)**
During "dead week", an AI lab malfunction traps the campus in an infinite loop, converting students and staff into zombies.

**Objective**: Navigate familiar campus locations to reach the server room and deploy a system patch.

### Gameplay Philosophy: "Actions Have Consequences"

- **Normal Mode choices directly impact Zombie Mode**:
  - Helping classmates → easier Zombie encounters
  - Skipping lectures → tougher opposition
  - Cheating on quizzes → reduced NPC support
  
- **Endings are consequence-driven**:
  - High karma + successful patch = Pacifist ending (save campus, pass semester without finals)
  - Low karma = harsh Zombie Mode with combat focus

---

## 2. Motivation

### Problem Statement
FAST-NU's demanding academic environment often:
- Overwhelms new students
- Causes stress and burnout
- Creates gaps in fundamental concepts (esp. Programming)
- Rarely addresses life balance or emotional fatigue

### Solution
**Gamification** bridges the gap by:
- Turning daily routines into interactive gameplay
- Providing psychological escape
- Offering learning experiences simultaneously
- Teaching logical thinking through puzzles
- Reinforcing time/stress/energy management

### Localization Strategy
CampusFlex targets **FAST students specifically**:
- Uses familiar campus locations (C-Block, D-Block, Library, Mosque, Cafeteria, etc.)
- Includes inside jokes and recognizable student behaviors
- Makes game relatable and engaging
- Creates opportunity for real-world testing and scalability assessment
- Potential for deployment beyond semester as student project

---

## 3. Objectives

The project aims to:

1. **Architecture & Design**
   - Design scalable RPG architecture in Java
   - Apply clean coding practices
   - Implement standard Software Design Patterns

2. **Gameplay Mechanics**
   - Simulate authentic student life through meaningful mechanics
   - Implement working stat system (GPA, Energy, Stress, Sleep, Karma)
   - Create meaningful consequences for player actions

3. **Core Features**
   - Develop exploration mechanics
   - Implement stat management systems
   - Create academic-themed combat
   - Enable progression tracking

4. **Educational Value**
   - Reinforce foundational programming concepts
   - Develop logical thinking through interactive challenges
   - Promote healthier academic habits
   - Encourage life balance awareness

5. **Deliverable**
   - Create deployable prototype playable on Java-enabled systems

---

## 4. Expected Outcomes

Upon project completion, deliverables include:

- ✅ **Playable 2D RPG Prototype**
  - Both Normal Mode and Zombie Mode fully functional
  - Two distinct gameplay experiences

- ✅ **Standalone Executable**
  - `.jar` file that runs on any Java-enabled desktop
  - No external dependencies required

- ✅ **Complete System Design**
  - UML diagrams
  - Gameplay flow structures
  - Architecture documentation

- ✅ **Working Stat System**
  - Player choices meaningfully impact progression
  - Consequences system fully implemented

- ✅ **Explorable Campus Zone**
  - At least one fully explorable area
  - NPC interactions functional
  - Academic combat operational

- ✅ **Stable Gameplay Experience**
  - Successful blend of entertainment and education
  - Bug-free core gameplay loops

---

## 5. Project Scope

### In-Scope Features

**World & Exploration**
- Open-world campus simulation
- Core student activities (classes, prayer, dining, socializing)
- Multiple explorable zones (C-Block, D-Block, Library, Mosque, Cafeteria, etc.)

**Player Systems**
- Player stat management (GPA, Energy, Stress, Sleep, Karma)
- Inventory system for collectibles

**Gameplay Mechanics**
- Academic combat with logic and programming challenges
- Consequence-based gameplay linking Normal Mode → Zombie Mode
- NPC interaction and dialogue system
- Quiz/assessment mechanics

**Thematic Elements**
- FAST-themed inside references and humor
- Realistic campus activities and schedules
- Relatable character interactions

**Technical**
- Java-based game architecture
- JavaFX-based GUI
- Database persistence
- Save/load functionality

### Out of Scope (Assumed)
- Multiplayer functionality
- Mobile platform support (Desktop Java only)
- Advanced graphics or 3D rendering
- Voice acting or professional audio

---

## 6. Tools & Technologies

### Frontend
- **Java** — Core game logic and engine
- **JavaFX** — Graphical user interface, animations, player interactions

### Backend
- **Oracle Database** — Persistent storage for:
  - Player data
  - Game statistics
  - Progress tracking
  - Game state snapshots

### Development & Collaboration
- **GitHub** — Version control, code tracking, collaborative feature integration
- **ClickUp** — Project management, task assignment, progress tracking, workflow organization

### Architecture Approach
- Clean code principles
- Design pattern implementation
- Modular system design
- Scalable architecture for future expansion

---

## 7. Key Game Systems

### Stat System

| Stat | Description | How It Changes |
|------|-------------|-----------------|
| **GPA** | Academic performance metric | ↑ Pass quizzes/attend class; ↓ Skip lectures/fail assessments |
| **Energy** | Daily action capacity | ↑ Sleep/eat; ↓ Classes/combat/running |
| **Stress** | Mental fatigue level | ↑ Exams/conflict; ↓ Prayer/phone/canteen |
| **Sleep** | Recovery & well-being | ↑ Rest/prayer; ↓ Late-night gaming |
| **Karma** | Moral standing | ↑ Help NPCs/pray; ↓ Cheat/betray/phone |

### Two-Mode Structure

**Normal Mode → Zombie Mode Connection**
- All Normal Mode choices are logged
- Zombie Mode difficulty/NPC behavior is dynamically adjusted
- High-karma players get pacifist options
- Low-karma players face harder combat and fewer allies

---

## 8. Gameplay Experience

### Player Journey Example

**Normal Semester (Normal Mode)**
1. Start at C-Block entrance
2. Attend morning class → GPA +5
3. Study in library → Learn new concepts
4. Pray → Stress –10, Karma +10
5. Eat at canteen → Energy +20
6. Interact with friend → Karma +5
7. Late-night phone → Stress –5, Karma –5
8. Sleep → Energy & mental recovery
9. **Progress through semester, build karma, face consequences**

**Dead Week (Zombie Mode)**
10. AI lab fails → Campus converted to zombies
11. Navigate with established relationships
12. Combat encounters based on Normal Mode choices
13. Solve logic puzzles to "Debug" enemies (pacifist path for high-karma players)
14. Reach Research Lab
15. Final boss: Deploy patch
16. **Ending determined by Karma + Choices**

### Ending Variations
- **Pacifist Ending** (High Karma): Campus saved peacefully, semester passed without finals
- **Combat Ending** (Low Karma): Harsh zombie encounters, must fight through
- **Hidden Endings**: Based on specific achievement combinations

---

## 9. Design Principles

### 1. Consequence System
Every action has trade-offs; no "optimal" playstyle exists.

### 2. Player Agency
Meaningful choices that affect story, difficulty, and endings.

### 3. Relatable Authenticity
FAST-specific details make game personally resonant.

### 4. Educational Integration
Learning naturally embedded in gameplay, not forced.

### 5. Scalability
Clean architecture allows for future expansion (more zones, features, characters).

### 6. Accessibility
Standalone `.jar` enables easy distribution to FAST student community.

---

## 10. Target Audience

**Primary**: FAST-NU students (current & prospective)
- Relatable campus setting
- Inside jokes and humor
- Relevant academic and emotional themes

**Secondary**: Students interested in:
- Educational games
- Game design and implementation
- Java programming practices
- Campus simulations

---

## 11. Success Metrics

Project considered successful if:
- ✅ Both Normal Mode and Zombie Mode are fully playable
- ✅ Stat system meaningfully affects gameplay
- ✅ Player choices visibly impact story and difficulty
- ✅ Executable runs stably on Windows/Mac/Linux (Java-capable systems)
- ✅ FAST community finds it engaging and relatable
- ✅ Code demonstrates solid design patterns and clean architecture
- ✅ Documentation is complete and comprehensive

---

## 12. Development Context

### Why This Project?

**Educational Value**
- Teaches software design patterns in practical context
- Reinforces Java fundamentals
- Demonstrates full software lifecycle

**Community Impact**
- Provides tool for FAST students to reflect on campus life
- Raises awareness about mental health and balance
- Creates shareable/memorable artifact

**Technical Challenge**
- Requires integration of multiple systems (UI, database, game logic)
- Demands careful architecture for scalability
- Balances entertainment with education

### Next Steps (Post-Proposal)
1. Detailed design phase (architecture specification)
2. Core system implementation (player, stats, map)
3. Feature development (UI, NPCs, combat, quizzes)
4. Testing and debugging
5. Optimization and polish
6. Deployment and community feedback

---

## Summary Statement

**CampusFlex** is an ambitious educational game that transforms FAST-NU campus life into an interactive RPG experience. By blending entertaining gameplay with meaningful consequences and academic challenges, it offers students a fresh perspective on their university journey while demonstrating professional software engineering practices. The project bridges the gap between casual gaming and educational value, creating a relatable, deployable product for the FAST community.
