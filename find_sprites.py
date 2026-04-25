from PIL import Image

def find_sprites(image_path):
    img = Image.open(image_path).convert("RGBA")
    data = img.load()
    width, height = img.size
    
    visited = set()
    sprites = []
    
    for y in range(height):
        for x in range(width):
            if data[x, y][3] > 0 and (x, y) not in visited:
                queue = [(x, y)]
                visited.add((x, y))
                min_x, max_x = x, x
                min_y, max_y = y, y
                
                while queue:
                    cx, cy = queue.pop(0)
                    for dx in [-1, 0, 1]:
                        for dy in [-1, 0, 1]:
                            nx, ny = cx + dx, cy + dy
                            if 0 <= nx < width and 0 <= ny < height:
                                if (nx, ny) not in visited and data[nx, ny][3] > 0:
                                    visited.add((nx, ny))
                                    queue.append((nx, ny))
                                    min_x = min(min_x, nx)
                                    max_x = max(max_x, nx)
                                    min_y = min(min_y, ny)
                                    max_y = max(max_y, ny)
                
                sprites.append((min_x, min_y, max_x - min_x + 1, max_y - min_y + 1))
                
    # Filter out tiny artifacts (noise)
    sprites = [s for s in sprites if s[2] > 5 and s[3] > 5]
    sprites.sort(key=lambda s: (s[1], s[0]))
    
    print(f"Total sprites found: {len(sprites)}")
    for i, s in enumerate(sprites):
        print(f"Sprite {i}: x={s[0]}, y={s[1]}, w={s[2]}, h={s[3]}")

if __name__ == "__main__":
    find_sprites("assets/LibraryCompeteSet.png")
