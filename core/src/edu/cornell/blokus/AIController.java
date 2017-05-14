package edu.cornell.blokus;

import com.badlogic.gdx.math.Vector2;
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
        getValidMoves();
        float maxScore = -Float.MAX_VALUE;
        float score;
        GamePiece returnPiece = null;
        GamePiece gp;
        selected = null;

        for(int i = 0; i < moveList.size; i++) {
            gp = moveList.get(i);
            score = w[0] * getSize(gp) + w[1]*getDistToMiddle(gp);
            if (score > maxScore) {
                returnPiece = gp;
                selected = pa.gamePieces[selectedList.get(i)];
                maxScore = score;
            }
        }

        clearValidMoves();
        return returnPiece;
    }


    public float getSize(GamePiece gp) {
        return gp.template.solids[0].length / 5.0f;
    }

    public float getDistToMiddle(GamePiece gp) {
        float midX = board.gx + board.width * board.tileSize/2.0f;
        float midY = board.gy + board.height * board.tileSize/2.0f;
        return 1 - (float)(Math.sqrt(Math.pow(gp.x - midX, 2) + Math.pow(gp.y - midY, 2))/ Math.sqrt(Math.pow(board.gx - midX, 2) + Math.pow(board.gy - midY, 2)));
    }


    public void clearValidMoves() {
        selectedList.clear();
        moveList.clear();
    }

    public void getValidMoves(){
        shuffleArray(indexArray);
        shuffleArray(widthIndex);
        shuffleArray(heightIndex);

        for(int index: indexArray) {
            GamePiece gp = pa.gamePieces[index];
            if (gp == null) continue;
            for (int i : widthIndex) {
                for (int j: heightIndex) {
                    for (int rot = 0; rot < gp.template.rotations; rot ++){
                        if (board.checkValidPlacement(gp.template, rot, j, i, gp.tile)){
                            Pair coords = board.boardToScreen(j, i);
                            selectedList.add(index);
                            moveList.add(new GamePiece(coords.x-1, coords.y-1, rot, gp.template, gp.tile));
                        }
                    }
                }
            }
        }
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
