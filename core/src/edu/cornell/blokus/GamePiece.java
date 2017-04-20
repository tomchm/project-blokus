package edu.cornell.blokus;

import com.badlogic.gdx.graphics.Texture;

import java.awt.*;

/**
 * Created by vanyaivan on 4/20/2017.
 */
public class GamePiece {
    public Piece template;
    public int rotation;
    public int x;
    public int y;

    public GamePiece(int x, int y, int rotation, Piece piece){
        template = piece;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }
}
