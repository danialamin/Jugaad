# Graph Report - C:\Users\danial\Desktop\CampusFlex  (2026-04-24)

## Corpus Check
- 16 files · ~171,750 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 96 nodes · 144 edges · 14 communities detected
- Extraction: 69% EXTRACTED · 31% INFERRED · 0% AMBIGUOUS · INFERRED: 44 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]

## God Nodes (most connected - your core abstractions)
1. `Player` - 24 edges
2. `UI` - 10 edges
3. `GamePanel` - 8 edges
4. `TileManager` - 8 edges
5. `ObjectManager` - 7 edges
6. `DatabaseConnection` - 4 edges
7. `CollisionChecker` - 4 edges
8. `KeyHandler` - 4 edges
9. `GameStateDao` - 3 edges
10. `Furniture` - 3 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities

### Community 0 - "Community 0"
Cohesion: 0.18
Nodes (2): GameStateDao, Player

### Community 1 - "Community 1"
Cohesion: 0.2
Nodes (2): GamePanel, ObjectManager

### Community 2 - "Community 2"
Cohesion: 0.19
Nodes (0): 

### Community 3 - "Community 3"
Cohesion: 0.36
Nodes (1): TileManager

### Community 4 - "Community 4"
Cohesion: 0.36
Nodes (2): DatabaseConnection, DatabaseTest

### Community 5 - "Community 5"
Cohesion: 0.36
Nodes (1): UI

### Community 6 - "Community 6"
Cohesion: 0.4
Nodes (1): CollisionChecker

### Community 7 - "Community 7"
Cohesion: 0.4
Nodes (1): KeyHandler

### Community 8 - "Community 8"
Cohesion: 0.5
Nodes (1): GameEngine

### Community 9 - "Community 9"
Cohesion: 0.5
Nodes (1): Furniture

### Community 10 - "Community 10"
Cohesion: 1.0
Nodes (1): Tile

### Community 11 - "Community 11"
Cohesion: 1.0
Nodes (0): 

### Community 12 - "Community 12"
Cohesion: 1.0
Nodes (0): 

### Community 13 - "Community 13"
Cohesion: 1.0
Nodes (0): 

## Knowledge Gaps
- **1 isolated node(s):** `Tile`
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 10`** (2 nodes): `Tile.java`, `Tile`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 11`** (1 nodes): `setup_graphify.ps1`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 12`** (1 nodes): `push.ps1`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 13`** (1 nodes): `ZoneType.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Player` connect `Community 0` to `Community 2`, `Community 6`?**
  _High betweenness centrality (0.286) - this node is a cross-community bridge._
- **Why does `GamePanel` connect `Community 1` to `Community 8`, `Community 2`?**
  _High betweenness centrality (0.211) - this node is a cross-community bridge._
- **Why does `UI` connect `Community 5` to `Community 2`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **What connects `Tile` to the rest of the system?**
  _1 weakly-connected nodes found - possible documentation gaps or missing edges._