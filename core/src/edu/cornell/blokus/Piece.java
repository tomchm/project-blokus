package edu.cornell.blokus;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Tomasz on 4/8/2017.
 */
public class Piece {
    public int rotations;
    public Pair[][] solids, edges, corners;

    public Piece(Array<Array<String>> data, Pair origin){
        int ox = origin.x;
        int oy = origin.y;

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

}
