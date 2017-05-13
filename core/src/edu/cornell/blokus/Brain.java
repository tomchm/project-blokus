package edu.cornell.blokus;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Tomasz on 5/12/2017.
 */
public class Brain {
    /*
     * Assume BLUE starts in (0,0) corner. AI RED, GREEN, YELLOW will start in corners (39,0), (39,39), and (0,39).

     */

    public static Array<GamePiece> getAllFirstMoves(PlayerArea pa, GameMode.Tile[][] grid){
        int ox = 0, oy = 0;

        switch(pa.playerColor){
            case BLUE:
                ox = -1;
                oy = -1;
                break;
            case RED:
                ox = 20;
                oy = -1;
                break;
            case GREEN:
                ox = 20;
                oy = 20;
                break;
            case YELLOW:
                ox = -1;
                oy = 20;
                break;
        }

        Array<GamePiece> possibleMoves  = new Array<GamePiece>();
        for(GamePiece gp : pa.gamePieces){
            if(gp != null) {
                Piece p = gp.template;
                for (int i = 0; i < p.rotations; i++) {
                    for (int j = 0; j < p.corners[i].length; j++) {
                        Pair pair = p.corners[i][j];
                        int x = ox - pair.x;
                        int y = oy - pair.y;
                        if (validPartial(p, i, x, y, grid, pa.playerColor)) {
                            possibleMoves.add(new GamePiece(x, y, i, p, pa.playerColor));
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    public static boolean validPartial(Piece p, int rot, int x, int y, GameMode.Tile[][] grid, GameMode.Tile color){
        for(int i=0; i<p.solids[rot].length; i++){
            Pair pair = p.solids[rot][i];
            int nx = pair.x + x;
            int ny = pair.y + y;
            if(nx < 0 || nx >= 20 || ny < 0 || ny >= 20){
                return false;
            }
            else if(grid[nx][ny] != GameMode.Tile.BLANK){
                return false;
            }
        }

        for(int i=0; i<p.edges[rot].length; i++){
            Pair pair = p.edges[rot][i];
            int nx = pair.x + x;
            int ny = pair.y + y;
            if(nx >= 0 && nx < 20 && ny >= 0 && ny < 20){
                if(grid[nx][ny] == color){
                    return false;
                }
            }
        }

        return true;
    }

    public static Array<GamePiece> getAllMoves(PlayerArea pa, GameMode.Tile[][] grid){
        Array<Pair> corners = getAllCorners(grid, pa.playerColor);


        Array<GamePiece> possibleMoves  = new Array<GamePiece>();
        for(Pair co : corners){
            for(GamePiece gp : pa.gamePieces){
                if(gp != null){
                    Piece p = gp.template;
                    for(int i=0; i<p.rotations; i++){
                        for(int j=0; j<p.solids[i].length; j++){
                            Pair pair = p.solids[i][j];
                            int x = co.x - pair.x;
                            int y = co.y - pair.y;
                            if(validAll(p, i, x, y, grid, pa.playerColor)){
                                possibleMoves.add(new GamePiece(x, y, i, p, pa.playerColor));
                            }
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    public static boolean validAll(Piece p, int rot, int x, int y, GameMode.Tile[][] grid, GameMode.Tile color){
        for(int i=0; i<p.solids[rot].length; i++){
            Pair pair = p.solids[rot][i];
            int nx = pair.x + x;
            int ny = pair.y + y;
            if(nx < 0 || nx >= 20 || ny < 0 || ny >= 20){
                return false;
            }
            else if(grid[nx][ny] != GameMode.Tile.BLANK){
                return false;
            }
        }

        for(int i=0; i<p.edges[rot].length; i++){
            Pair pair = p.edges[rot][i];
            int nx = pair.x + x;
            int ny = pair.y + y;
            if(nx >= 0 && nx < 20 && ny >= 0 && ny < 20){
                if(grid[nx][ny] == color){
                    return false;
                }
            }
        }

        for(int i=0; i<p.corners[rot].length; i++){
            Pair pair = p.corners[rot][i];
            int nx = pair.x + x;
            int ny = pair.y + y;
            if(nx >= 0 && nx < 20 && ny >= 0 && ny < 20){
                if(grid[nx][ny] == color) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Array<Pair> getAllCorners(GameMode.Tile[][] grid, GameMode.Tile color){
        Array<Pair> pairs = new Array<Pair>();
        for(int x=0; x<20; x++){
            for(int y=0; y<20; y++){
                if(grid[x][y] == GameMode.Tile.BLANK){
                    if(x>0 && grid[x-1][y] == color) continue;
                    if(x<19 && grid[x+1][y] == color) continue;
                    if(y>0 && grid[x][y-1] == color) continue;
                    if(y<19 && grid[x][y+1] == color) continue;

                    if(x>0 && y>0 && grid[x-1][y-1] == color){
                        pairs.add(new Pair(x,y));
                        continue;
                    }
                    if(x<19 && y>0 && grid[x+1][y-1] == color){
                        pairs.add(new Pair(x,y));
                        continue;
                    }
                    if(x>0 && y<19 && grid[x-1][y+1] == color){
                        pairs.add(new Pair(x,y));
                        continue;
                    }
                    if(x<19 && y<19 && grid[x+1][y+1] == color){
                        pairs.add(new Pair(x,y));
                        continue;
                    }
                }
            }
        }
        return pairs;
    }
}
