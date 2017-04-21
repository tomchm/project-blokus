package edu.cornell.blokus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by vanyaivan on 4/20/2017.
 */
public class PlayerArea {
    public int x;
    public int y;
    public int width;
    public int height;
    public int tileSize;
    public Texture texture;
    public GamePiece[] gamePieces;

    public PlayerArea(int x, int y, int width, int height, int tileSize) {
       this.x = x;
       this.y = y;
       this.width = width;
       this.height = height;
       this.tileSize = tileSize;
       initializeGamePieces();
    }

    public void initializeGamePieces() {
        gamePieces = new GamePiece[Pieces.getInstance().plist.length];

        int tempx = x - width/2;
        int tempy = y - height/2;

        int xoffset = 0;
        int yoffset = 0;
        int yoffsetmax = yoffset;

        for (int i = 0; i < gamePieces.length; i++){
            Piece piece = Pieces.getInstance().plist[i];
            gamePieces[i] = new GamePiece(tempx + xoffset - (piece.getMin(0).x) * tileSize, tempy + yoffset - (piece.getMin(0).y) * tileSize, 0, piece);

            xoffset = xoffset + piece.getDimensions(0).x  * tileSize + tileSize/4;
            yoffsetmax = Math.max(yoffsetmax, yoffset + piece.getDimensions(0).y * tileSize);

            Piece nextPiece = null;
            if(i < Pieces.getInstance().plist.length -1) {
                nextPiece = Pieces.getInstance().plist[i+1];
            }

            if (nextPiece != null && (nextPiece.getDimensions(0).x + 1) * tileSize + xoffset > width) {
                xoffset = 0;
                yoffset = yoffsetmax + tileSize/4;
            }
        }
    }

    public GamePiece getPieceAt(float x1, float y1) {
        for (GamePiece gp: gamePieces) {
            if (gp.isContained(x1, y1, tileSize)) {
                return gp;
            }
        }
        return null;
    }

    public void setTexture(Texture tex) {
        texture = tex;
    }

    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE, texture.getWidth()/2, texture.getHeight()/2, x,y, 0, ((float)width)/texture.getWidth(), ((float)height)/texture.getHeight());
    }

    public void drawFadeOut(GameCanvas canvas) {
        canvas.draw(texture, new Color(1,1,1,0.5f), texture.getWidth()/2, texture.getHeight()/2, x,y, 0, ((float)width)/texture.getWidth(), ((float)height)/texture.getHeight());

    }
}