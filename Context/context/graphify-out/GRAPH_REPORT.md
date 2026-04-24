# Graph Report - C:\Users\danial\Desktop\CampusFlex  (2026-04-24)

## Corpus Check
- 12 files · ~14,100 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 69 nodes · 97 edges · 12 communities detected
- Extraction: 69% EXTRACTED · 31% INFERRED · 0% AMBIGUOUS · INFERRED: 30 edges (avg confidence: 0.8)
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

## God Nodes (most connected - your core abstractions)
1. `Player` - 24 edges
2. `GamePanel` - 6 edges
3. `UI` - 6 edges
4. `TileManager` - 5 edges
5. `DatabaseConnection` - 4 edges
6. `KeyHandler` - 4 edges
7. `GameStateDao` - 3 edges
8. `DatabaseTest` - 2 edges
9. `GameEngine` - 2 edges
10. `Tile` - 1 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities

### Community 0 - "Community 0"
Cohesion: 0.2
Nodes (1): GameStateDao

### Community 1 - "Community 1"
Cohesion: 0.22
Nodes (2): GameEngine, GamePanel

### Community 2 - "Community 2"
Cohesion: 0.28
Nodes (1): Player

### Community 3 - "Community 3"
Cohesion: 0.36
Nodes (2): DatabaseConnection, DatabaseTest

### Community 4 - "Community 4"
Cohesion: 0.33
Nodes (0): 

### Community 5 - "Community 5"
Cohesion: 0.47
Nodes (1): TileManager

### Community 6 - "Community 6"
Cohesion: 0.47
Nodes (1): UI

### Community 7 - "Community 7"
Cohesion: 0.4
Nodes (0): 

### Community 8 - "Community 8"
Cohesion: 0.4
Nodes (1): KeyHandler

### Community 9 - "Community 9"
Cohesion: 1.0
Nodes (1): Tile

### Community 10 - "Community 10"
Cohesion: 1.0
Nodes (0): 

### Community 11 - "Community 11"
Cohesion: 1.0
Nodes (0): 

## Knowledge Gaps
- **1 isolated node(s):** `Tile`
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 9`** (2 nodes): `Tile.java`, `Tile`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 10`** (1 nodes): `setup_graphify.ps1`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 11`** (1 nodes): `push.ps1`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Player` connect `Community 2` to `Community 0`, `Community 4`, `Community 7`?**
  _High betweenness centrality (0.294) - this node is a cross-community bridge._
- **Why does `UI` connect `Community 6` to `Community 7`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **What connects `Tile` to the rest of the system?**
  _1 weakly-connected nodes found - possible documentation gaps or missing edges._