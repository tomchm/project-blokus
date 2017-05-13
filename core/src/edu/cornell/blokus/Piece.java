package edu.cornell.blokus;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Tomasz on 4/8/2017.
 */
public class Piece {
    public int rotations;
    public Pair origin;
    public Pair[][] solids, edges, corners;

    public Piece(Array<Array<String>> data, Pair origin){
        int ox = origin.x;
        int oy = origin.y;
        this.origin = new Pair(ox, oy);

        rotations = data.size;

        solids = new Pair[rotations][];
        edges = new Pair[rotations][];
        corners = new Pair[rotations][];

        for(int r=0; r<rotations; r++){
            Array<Pair> solidArray = new Array<Pair>();
            Array<Pair> edgeArray = new Array<Pair>();
            Array<Pair> cornerArray = new Array<Pair>();

            for(int y=0; y<data.get(r).size; y++){
                for(int x=0; x<data.get(r).get(y).length(); x++){
                    char c = data.get(r).get(y).charAt(x);
                    if(c == 'S'){
                        solidArray.add(new Pair(x-ox, y-oy));
                    }
                    else if(c == 'E'){
                        edgeArray.add(new Pair(x-ox, y-oy));
                    }
                    else if(c == 'C'){
                        cornerArray.add(new Pair(x-ox, y-oy));
                    }
                }
            }

            solids[r] = makeArray(solidArray);
            edges[r] = makeArray(edgeArray);
            corners[r] = makeArray(cornerArray);
        }
    }

    public Pair[] makeArray(Array<Pair> a){
        Pair[] pairs = new Pair[a.size];
        for(int i=0; i<a.size; i++){
            pairs[i] = a.get(i);
        }
        return pairs;
    }

    public Pair getDimensions(int rotation) {
        int xmax = 0;
        int ymax = 0;
        int xmin = 1000;
        int ymin = 1000;
        for (int i = 0; i < edges[rotation].length; i ++) {
            xmax = Math.max(xmax, edges[rotation][i].x);
            ymax = Math.max(ymax, edges[rotation][i].y);
            xmin = Math.min(xmin, edges[rotation][i].x);
            ymin = Math.min(ymin, edges[rotation][i].y);
        }
        return new Pair(xmax - xmin, ymax - ymin);
    }

    public Pair getOrigin() {
        return origin;
    }

    public Pair getMin(int rotation) {
        int xmin = 1000;
        int ymin = 1000;
        for (int i = 0; i < edges[rotation].length; i ++) {
            xmin = Math.min(xmin, edges[rotation][i].x);
            ymin = Math.min(ymin, edges[rotation][i].y);
        }
        return new Pair(xmin, ymin);
    }

    public int isContained(int rot, int x, int y, int ox, int oy) {
        int tempx = x - ox;
        int tempy = y - oy;
        Pair tile;
        for (int i = 0; i < solids[rot].length; i++) {
            tile = solids[rot][i];
            if (tempx >= tile.x && tempx < tile.x + 1 && tempy >= tile.y && tempy < tile.y + 1) {
                return 0;
            }
        }
        for (int i = 0; i < edges[rot].length; i++) {
            tile = edges[rot][i];
            if (tempx >= tile.x && tempx < tile.x + 1 && tempy >= tile.y && tempy < tile.y + 1) {
                return 1;
            }
        }
        for (int i = 0; i < corners[rot].length; i++) {
            tile = corners[rot][i];
            if (tempx >= tile.x && tempx < tile.x+1 && tempy >= tile.y && tempy < tile.y+1) {
                return 2;
            }
        }
        return -1;
    }

}
