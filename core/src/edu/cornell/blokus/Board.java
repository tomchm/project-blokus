package edu.cornell.blokus;

/**
 * Created by vanyaivan on 5/13/2017.
 */
public class Board {
    public static GameMode.Tile[][] grid;
    public static int[][] statusGrid;
    public int width, height;
    public int gx, gy;
    public int tileSize;

    public Board(int x, int y, int width, int height, int tileSize){
        this.gx = x;
        this.gy = y;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        grid = new GameMode.Tile[height][width];
        statusGrid = new int[height][width];


        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                grid[i][j] = GameMode.Tile.BLANK;
                statusGrid[i][j] = -1;
            }
        }
    }

    public boolean inGrid(float x, float y) {
        boolean tempA = x > gx && x < gx + width * tileSize;
        boolean tempB = y > gy && y < gy + height * tileSize;
        return tempA  && tempB;
    }


    public void putPieceOnGrid(GamePiece gp) {
        for (int i = 0; i < height; i ++) {
            for (int j = 0; j < width; j++) {
                int setTile = gp.isContained(j * tileSize + gx + tileSize/2, i * tileSize + gy + tileSize/2, tileSize);
                if (setTile != -1 && (grid[i][j] == GameMode.Tile.GREEN || grid[i][j] == GameMode.Tile.BLANK )){
                    if (setTile == 0) {
                        grid[i][j] = gp.tile;
                    }
                    statusGrid[i][j] = setTile;
                }
            }
        }
    }



    public boolean checkValidPlacement(GamePiece gp) {
        int greenCount = 0;
        int blueCount = 0;
        for (int i = 0; i < height; i ++) {
            for (int j = 0; j < width; j++) {
                int setTile = gp.isContained(j * tileSize + gx + tileSize/2, i * tileSize + gy + tileSize/2, tileSize);
                if (setTile == 0){
                    blueCount ++;

                    if (statusGrid[i][j] == 1) return false;
                    else if (statusGrid[i][j] == 0) return false;

                    if (j == 0 && i == 0) greenCount++;
                    else if (j == 0 && i == height- 1) greenCount++;
                    else if (j == width - 1 && i == 0) greenCount++;
                    else if (j == width - 1 && i == height - 1) greenCount++;
                }
                else if (setTile == 2){
                    if (grid[i][j] == gp.tile) greenCount++;
                }
            }
        }
        return greenCount > 0 && gp.template.solids[0].length == blueCount;
    }
}
