package edu.cornell.blokus;

import com.badlogic.gdx.Game;

/**
 * Created by vanyaivan on 5/13/2017.
 */
public class Board {
    public GameMode.Tile[][] grid;
    public int[][][] statusGrids;
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
        statusGrids = new int[4][height][width];


        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                grid[i][j] = GameMode.Tile.BLANK;
                for (int k = 0; k < 4; k ++){
                    statusGrids[k][i][j] = -1;
                }
            }
        }
    }

    public int tileToID(GameMode.Tile tile) {
        if (tile == GameMode.Tile.BLUE) return 0;
        else if (tile == GameMode.Tile.GREEN) return 1;
        else if (tile == GameMode.Tile.RED) return 2;
        else if (tile == GameMode.Tile.YELLOW) return 3;
        else return -1;
    }

    public boolean inGrid(float x, float y) {
        boolean tempA = x > gx && x < gx + width * tileSize;
        boolean tempB = y > gy && y < gy + height * tileSize;
        return tempA  && tempB;
    }


    public void putPieceOnGrid(GamePiece gp) {
        int[][] statusGrid = statusGrids[tileToID(gp.tile)];
        for (int i = 0; i < height; i ++) {
            for (int j = 0; j < width; j++) {
                Pair coords = boardToScreen(j, i);
                int setTile = gp.isContained(coords.x, coords.y , tileSize);
                if (setTile != -1 && (statusGrid[i][j] == 2 || statusGrid[i][j] == -1)){
                    if (setTile == 0) {
                        grid[i][j] = gp.tile;
                        statusGrids[0][i][j] = setTile;
                        statusGrids[1][i][j] = setTile;
                        statusGrids[2][i][j] = setTile;
                        statusGrids[3][i][j] = setTile;
                    }
                    statusGrid[i][j] = setTile;
                }
            }
        }
    }

    public Pair boardToScreen(int j, int i) {
        return new Pair(j * tileSize + gx + tileSize/2, i * tileSize + gy + tileSize/2);
    }

    public Pair screenToBoard(int x, int y) {
        return new Pair((int)((x - gx + tileSize/2.0) / tileSize), (int)((y - gy + tileSize/2.0) / tileSize));
    }



    public boolean checkValidPlacement(GamePiece gp) {
        Pair coords = screenToBoard(gp.x, gp.y);
        return checkValidPlacement(gp.template, gp.rotation, coords.x, coords.y, gp.tile);
    }

    public boolean checkValidPlacement(Piece p, int rot, int x, int y, GameMode.Tile tile) {
        int greenCount = 0;
        int blueCount = 0;
        int[][] statusGrid = statusGrids[tileToID(tile)];
        for (int i = 0; i < height; i ++) {
            for (int j = 0; j < width; j++) {
                int setTile = p.isContained(rot, j, i, x, y);
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
                    if (grid[i][j] == tile) greenCount++;
                }
            }
        }
        return greenCount > 0 && p.solids[0].length == blueCount;
    }
}
