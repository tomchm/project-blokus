package edu.cornell.blokus;

import com.badlogic.gdx.utils.Array;

import java.util.Random;


/**
 * Created by vanyaivan on 5/13/2017.
 */
public class AIController {
    public PlayerArea pa;
    public Board board;
    public GamePiece selected;
    public int[] indexArray;
    public int[] widthIndex;
    public int[] heightIndex;
    public Array<GamePiece> moveList = new Array <GamePiece>();
    public Array<Integer> selectedList = new Array <Integer>();
    public float[] w;
    public int turn = 0;

    public AIController(PlayerArea pa, Board board, float[] weights) {
        this.pa = pa;
        this.board = board;
        this.w = weights;

        selected = null;
        indexArray = new int[pa.gamePieces.length];
        for (int i = 0; i < indexArray.length; i++){
            indexArray[i] = i;
        }
        widthIndex = new int[board.width];
        for (int i = 0; i < board.width; i++){
            widthIndex[i] = i;
        }
        heightIndex = new int[board.height];
        for (int i = 0; i < board.height; i++){
            heightIndex[i] = i;
        }
    }


    public GamePiece choosePiece() {
        moveList = getValidMoves();

//        if(pa.tile == GameMode.Tile.BLUE){
//            System.out.println(moveList.size);
//        }
        float maxScore = -Float.MAX_VALUE;
        float score;
        GamePiece returnPiece = null;
        GamePiece gp;
        selected = null;

        for(int i = 0; i < moveList.size; i++) {
            gp = moveList.get(i);
            score = w[0] * getSize(gp) + w[1]*getDistToMiddle(gp) + w[2] * getCornerAdd(gp) + w[3] * getCornerBlock(gp) + w[4] * getPossibleMoves(gp);
            if (score > maxScore) {
                returnPiece = gp;
                maxScore = score;
            }
        }

        selected = pa.getSelectedPiece(returnPiece);
        convertPieceBack(returnPiece);
        clearValidMoves();
        turn ++;
        return returnPiece;
    }

    public void convertPieceBack(GamePiece gp) {
        Pair coords = board.boardToScreen(gp.x, gp.y);
        gp.x = coords.x-1;
        gp.y = coords.y-1;
    }


    public float getSize(GamePiece gp) {
        return gp.template.solids[0].length / 5.0f;
    }

    public float getDistToMiddle(GamePiece gp) {
        float midX = 10;
        float midY = 10;
        return 1 - (float)(Math.sqrt(Math.pow(gp.x - midX, 2) + Math.pow(gp.y - midY, 2))/ Math.sqrt(Math.pow(10, 2) + Math.pow(10, 2)));
    }

    public float getCornerAdd(GamePiece gp) {
        GamePiece gpTemp = new GamePiece(gp);
        int[][][] statusGridsTemp = board.copyStatusGrid();
        GameMode.Tile[][] gridTemp = board.copyGrid();

        int cornerCount = decrementCorner(board.tileToID(gp.tile));

        convertPieceBack(gpTemp);
        board.putPieceOnGrid(gpTemp);

        cornerCount = cornerCount + incrementCorner(board.tileToID(gp.tile));

        board.statusGrids = statusGridsTemp;
        board.grid = gridTemp;
        return Math.max(cornerCount,0)/8.0f;
    }

    public float getCornerBlock(GamePiece gp) {
        GamePiece gpTemp = new GamePiece(gp);
        int[][][] statusGridsTemp = board.copyStatusGrid();
        GameMode.Tile[][] gridTemp = board.copyGrid();

        int cornerCount = 0;
        for(int i = 0; i < 4; i++) {
            if(board.tileToID(gp.tile) != i){
                cornerCount = cornerCount + decrementCorner(i);
            }
        }

        convertPieceBack(gpTemp);
        board.putPieceOnGrid(gpTemp);

        for(int i = 0; i < 4; i++) {
            if(board.tileToID(gp.tile) != i){
                cornerCount = cornerCount + incrementCorner(i);
            }
        }

        board.statusGrids = statusGridsTemp;
        board.grid = gridTemp;
        return Math.max(-cornerCount, 0)/5.0f;
    }

    public int decrementCorner(int id){
        int[][] statusGrid = board.statusGrids[id];
        int cornerCount = 0;
        for (int i = 0; i < board.height; i ++) {
            for (int j = 0; j < board.width; j++) {
                if (statusGrid[i][j] == 2){
                    cornerCount --;
                }
            }
        }
        return cornerCount;
    }

    public int incrementCorner(int id) {
        int[][] statusGrid = board.statusGrids[id];
        int cornerCount = 0;
        for (int i = 0; i < board.height; i ++) {
            for (int j = 0; j < board.width; j++) {
                if (statusGrid[i][j] == 2){
                    cornerCount ++;
                }
            }
        }
        return cornerCount;
    }

    public float getPossibleMoves(GamePiece gp){
        if (turn < 12){
            return 0;
        }
        GamePiece gpTemp = new GamePiece(gp);
        int[][][] statusGridsTemp = board.copyStatusGrid();
        GameMode.Tile[][] gridTemp = board.copyGrid();

        int numMoves1 = moveList.size;

        convertPieceBack(gpTemp);
        board.putPieceOnGrid(gpTemp);

        int numMoves2 = getValidMoves().size;

        board.statusGrids = statusGridsTemp;
        board.grid = gridTemp;
        System.out.println(numMoves2/(float)numMoves1 * 0.5f);
        return numMoves2/(float)numMoves1 * 0.5f;
    }








    public void clearValidMoves() {
        moveList.clear();
    }

    public Array<GamePiece> getValidMoves(){
        shuffleArray(indexArray);
        shuffleArray(widthIndex);
        shuffleArray(heightIndex);

        Array<GamePiece> tempList = new Array <GamePiece>();
        for(int index: indexArray) {
            GamePiece gp = pa.gamePieces[index];
            if (gp == null) continue;
            for (int i : widthIndex) {
                for (int j: heightIndex) {
                    if (turn > 0 && !board.inRange(board.tileToID(gp.tile), gp.template.getRadius(), j, i)) {
                        continue;
                    }
                    for (int rot = 0; rot < gp.template.rotations; rot ++){
                        if (board.checkValidPlacement(gp.template, rot, j, i, gp.tile)){
                            tempList.add(new GamePiece(j, i, rot, gp.template, gp.tile));
                        }
                    }
                }
            }
        }
        return tempList;
    }



    // Implementing Fisher–Yates shuffle
    // from http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
    static void shuffleArray(int[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
