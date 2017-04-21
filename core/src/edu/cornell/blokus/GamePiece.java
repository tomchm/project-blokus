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

    public  GamePiece(GamePiece gp) {
        template = gp.template;
        rotation = gp.rotation;
        x = gp.x;
        y = gp.y;
    }

    public void setXY(float x1, float y1) {
        x = (int)x1;
        y = (int)y1;
    }

    public boolean isContained(float x1, float y1, int tileSize) {
        float tempx = (x1 - x) / (float)tileSize;
        float tempy = (y1 - y) / (float)tileSize;
        for (int i = 0; i < template.solids[rotation].length; i++){
            Pair tile = template.solids[rotation][i];
            if (tempx > tile.x && tempx < tile.x+1 && tempy > tile.y && tempy < tile.y+1) {
                return true;
            }
        }
        return false;
    }
}
