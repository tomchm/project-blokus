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
        grid[10][10] = GameMode.Tile.BLUE;
        grid[10][11] = GameMode.Tile.BLUE;
        grid[10][12] = GameMode.Tile.BLUE;
    }

    public static void getNextPiece(){
        if(possibleMoves.size == 0){
            System.out.println("No possible moves.");
            return;
        }
        gpi++;
        if(gpi >= possibleMoves.size){
            gpi = 0;
        }
        gamePiece = possibleMoves.get(gpi);
    }

    public static void getPrevPiece(){
        if(possibleMoves.size == 0){
            System.out.println("No possible moves.");
            return;
        }
        gpi--;
        if(gpi < 0){
            gpi = possibleMoves.size-1;
        }
        gamePiece = possibleMoves.get(gpi);
    }
}
