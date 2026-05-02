package engine;

import entity.Player;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Player player) {
        // Calculate player's hitbox corners in tile coordinates
        // Using a slightly smaller hitbox for better gameplay feel
        int playerLeftWorldX = player.xLocation + 8;
        int playerRightWorldX = player.xLocation + gp.tileSize - 8;
        int playerTopWorldY = player.yLocation + 16;
        int playerBottomWorldY = player.yLocation + gp.tileSize;

        int playerLeftCol = playerLeftWorldX / gp.tileSize;
        int playerRightCol = playerRightWorldX / gp.tileSize;
        int playerTopRow = playerTopWorldY / gp.tileSize;
        int playerBottomRow = playerBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        switch(player.direction) {
            case "up":
                playerTopRow = (playerTopWorldY - player.speed) / gp.tileSize;
                if (playerTopRow < 0) {
                    player.collisionOn = true;
                } else {
                    tileNum1 = gp.tileM.mapTileNum[playerLeftCol][playerTopRow];
                    tileNum2 = gp.tileM.mapTileNum[playerRightCol][playerTopRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        player.collisionOn = true;
                    }
                }
                break;
            case "down":
                playerBottomRow = (playerBottomWorldY + player.speed) / gp.tileSize;
                if (playerBottomRow >= gp.maxScreenRow) {
                    player.collisionOn = true;
                } else {
                    tileNum1 = gp.tileM.mapTileNum[playerLeftCol][playerBottomRow];
                    tileNum2 = gp.tileM.mapTileNum[playerRightCol][playerBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        player.collisionOn = true;
                    }
                }
                break;
            case "left":
                playerLeftCol = (playerLeftWorldX - player.speed) / gp.tileSize;
                if (playerLeftCol < 0) {
                    player.collisionOn = true;
                } else {
                    tileNum1 = gp.tileM.mapTileNum[playerLeftCol][playerTopRow];
                    tileNum2 = gp.tileM.mapTileNum[playerLeftCol][playerBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        player.collisionOn = true;
                    }
                }
                break;
            case "right":
                playerRightCol = (playerRightWorldX + player.speed) / gp.tileSize;
                if (playerRightCol >= gp.maxScreenCol) {
                    player.collisionOn = true;
                } else {
                    tileNum1 = gp.tileM.mapTileNum[playerRightCol][playerTopRow];
                    tileNum2 = gp.tileM.mapTileNum[playerRightCol][playerBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        player.collisionOn = true;
                    }
                }
                break;
        }
    }

    public void checkObjectCollision(Player player) {
        if (gp.objM == null) return;

        // Player's predicted solid area
        int nextX = player.xLocation;
        int nextY = player.yLocation;

        switch (player.direction) {
            case "up": nextY -= player.speed; break;
            case "down": nextY += player.speed; break;
            case "left": nextX -= player.speed; break;
            case "right": nextX += player.speed; break;
        }

        // Using +8 and +16 for a slightly smaller, more forgiving hitbox
        java.awt.Rectangle playerBox = new java.awt.Rectangle(nextX + 8, nextY + 16, gp.tileSize - 16, gp.tileSize - 16);

        for (entity.Furniture f : gp.objM.furnitureList) {
            if (playerBox.intersects(f.solidArea)) {
                player.collisionOn = true;
                break;
            }
        }
    }
}
