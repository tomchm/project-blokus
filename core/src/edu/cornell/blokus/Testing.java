package edu.cornell.blokus;


import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Created by Tomasz on 5/12/2017.
 */
public class Testing {
    public static Array<GamePiece> possibleMoves;
    public static Random random = new Random();
    public static GamePiece gamePiece;
    public static int gpi = 0;

    public static void init(PlayerArea pa, GameMode.Tile[][] grid){
        possibleMoves = Brain.getAllFirstMoves(pa, grid);
    }

    public static void getNextPiece(){
        gpi++;
        if(gpi >= possibleMoves.size){
            gpi = 0;
        }
        gamePiece = possibleMoves.get(gpi);
    }
}
