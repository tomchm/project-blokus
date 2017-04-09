package edu.cornell.blokus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tomasz on 4/8/2017.
 */
public class Pieces {

    private static Pieces instance;

    public Piece[] plist;

    public static final Pair[] origins = new Pair[]{new Pair(1,1), new Pair(1,1), new Pair(2,2), new Pair(1,1),
            new Pair(1,1), new Pair(2,2), new Pair(1,1), new Pair(3,3),
            new Pair(2,2), new Pair(4,4), new Pair(2,2), new Pair(3,3),
            new Pair(3,3), new Pair(2,2), new Pair(1,1), new Pair(2,2),
            new Pair(2,2), new Pair(2,2), new Pair(2,2), new Pair(2,2), new Pair(3,3)};

    private Pieces(){
        /*
        String[][][] s = new String[][][]{
                {{"CEC","ESE", "CEC"}},
                {{"CEEC","ESSE","CEEC"},{"CEC","ESE","ESE","CEC"}},
                {{".....","CEEC.","ESSE.","CESE.",".CEC."},{}}
        };
        */


        FileHandle file = Gdx.files.internal("data.txt");
        String text = file.readString();

        String wordsArray[] = text.split("\\r?\\n");
        List<String> list = Arrays.asList(wordsArray);
        Iterator<String> iter = list.iterator();

        plist = new Piece[21];

        for(int p=0; p<21; p++){
            Array<Array<String>> ps = new Array<Array<String>>();
            int r = 0;
            ps.add(new Array<String>());
            while(iter.hasNext()){
                String s = iter.next();

                if(s.charAt(0) == '/'){

                    plist[p] = new Piece(ps, origins[p]);
                    break;
                }
                else if(s.charAt(0) == '#'){
                    r++;
                    ps.add(new Array<String>());
                }
                else {

                    ps.get(r).add(s);
                }
            }
            System.out.println("YES");
        }
    }

    public static Pieces getInstance(){
        if(instance == null){
            instance = new Pieces();
        }
        return instance;
    }
}
