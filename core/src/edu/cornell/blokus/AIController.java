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
    public Array<GamePiece> moveList = new Array <GamePiece>();
    public Array<Integer> selectedList = new Array <Integer>();

    public AIController(PlayerArea pa, Board board) {
        this.pa = pa;
        this.board = board;
        selected = null;
        indexArray = new int[pa.gamePieces.length];
        for (int i = 0; i < indexArray.length; i++){
            indexArray[i] = i;
        }

    }

    public GamePiece selectPiece(){
        shuffleArray(indexArray);
        for(int index: indexArray) {
            GamePiece gp = pa.gamePieces[index];
            if (gp == null) continue;
            for (int i = 0; i < board.height; i ++) {
                for (int j = 0; j < board.width; j++) {
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

        if (selectedList.size == 0) {
            selected = null;
            return null;
        }
        selected = pa.gamePieces[selectedList.get(0)];
        GamePiece placedPiece = moveList.get(0);

        selectedList.clear();
        moveList.clear();

        return  placedPiece;
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
