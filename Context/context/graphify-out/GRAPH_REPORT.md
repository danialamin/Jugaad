# Graph Report - C:\Users\dyssa\Desktop\University\CS3004\Final_Project  (2026-04-26)

## Corpus Check
- 25 files · ~883,174 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 173 nodes · 293 edges · 19 communities detected
- Extraction: 66% EXTRACTED · 34% INFERRED · 0% AMBIGUOUS · INFERRED: 101 edges (avg confidence: 0.8)
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
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]

## God Nodes (most connected - your core abstractions)
1. `Player` - 24 edges
2. `UI` - 18 edges
3. `TileManager` - 15 edges
4. `ObjectManager` - 13 edges
5. `GamePanel` - 12 edges
6. `Zone` - 9 edges
7. `Position` - 7 edges
8. `Location` - 7 edges
9. `Classroom` - 6 edges
10. `Block` - 6 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities

### Community 0 - "Community 0"
Cohesion: 0.14
Nodes (2): GameStateDao, Player

### Community 1 - "Community 1"
Cohesion: 0.15
Nodes (2): GamePanel, ObjectManager

### Community 2 - "Community 2"
Cohesion: 0.09
Nodes (3): Location, Position, Zone

### Community 3 - "Community 3"
Cohesion: 0.23
Nodes (1): TileManager

### Community 4 - "Community 4"
Cohesion: 0.18
Nodes (2): Block, CampusMap

### Community 5 - "Community 5"
Cohesion: 0.3
Nodes (1): UI

### Community 6 - "Community 6"
Cohesion: 0.36
Nodes (2): DatabaseConnection, DatabaseTest

### Community 7 - "Community 7"
Cohesion: 0.29
Nodes (1): Classroom

### Community 8 - "Community 8"
Cohesion: 0.4
Nodes (1): CollisionChecker

### Community 9 - "Community 9"
Cohesion: 0.4
Nodes (1): KeyHandler

### Community 10 - "Community 10"
Cohesion: 0.5
Nodes (1): GameEngine

### Community 11 - "Community 11"
Cohesion: 0.5
Nodes (1): Furniture

### Community 12 - "Community 12"
Cohesion: 1.0
Nodes (0): 

### Community 13 - "Community 13"
Cohesion: 1.0
Nodes (0): 

### Community 14 - "Community 14"
Cohesion: 1.0
Nodes (0): 

### Community 15 - "Community 15"
Cohesion: 1.0
Nodes (1): Tile

### Community 16 - "Community 16"
Cohesion: 1.0
Nodes (0): 

### Community 17 - "Community 17"
Cohesion: 1.0
Nodes (0): 

### Community 18 - "Community 18"
Cohesion: 1.0
Nodes (0): 

## Knowledge Gaps
- **1 isolated node(s):** `Tile`
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 12`** (2 nodes): `print_bbox()`, `bbox.py`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 13`** (2 nodes): `find_sprites.py`, `find_sprites()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 14`** (2 nodes): `read_pdf.py`, `extract_pdf()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 15`** (2 nodes): `Tile.java`, `Tile`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 16`** (1 nodes): `setup_graphify.ps1`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 17`** (1 nodes): `push.ps1`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 18`** (1 nodes): `ZoneType.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Player` connect `Community 0` to `Community 8`?**
  _High betweenness centrality (0.105) - this node is a cross-community bridge._
- **Why does `UI` connect `Community 5` to `Community 0`, `Community 1`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Why does `GamePanel` connect `Community 1` to `Community 10`, `Community 2`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **What connects `Tile` to the rest of the system?**
  _1 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.14 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.09 - nodes in this community are weakly interconnected._