import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class FourteenPuzzle {

    private class TilePos {
        public int x;
        public int y;
        public int blankSelector;  // 0 for first blank, -1 for second blank

        public TilePos(int x, int y, int blankSelector) {
            this.x=x;
            this.y=y;
            this.blankSelector = blankSelector;
        }

    }

    public final static int DIMS=4;
    private int[][] tiles;
    private int display_width;
    private TilePos blank, secondBlank;

    public FourteenPuzzle() {
        tiles = new int[DIMS][DIMS];
        int cnt=1;
        for(int i=0; i<DIMS; i++) {
            for(int j=0; j<DIMS; j++) {
                tiles[i][j]=cnt;
                cnt++;
            }
        }

        display_width=Integer.toString(cnt).length();

        // init blank
        blank = new TilePos(DIMS-1,DIMS-1, 0);
        tiles[blank.x][blank.y]=0;
        secondBlank = new TilePos(DIMS-1,DIMS-2, -1);
        tiles[secondBlank.x][secondBlank.y]=-1;
    }

    public final static FourteenPuzzle SOLVED=new FourteenPuzzle();


    public FourteenPuzzle(FourteenPuzzle toClone) {
        this();  // chain to basic init
        for(TilePos p: allTilePos()) {
            tiles[p.x][p.y] = toClone.tile(p);
        }
        blank = toClone.getBlank();
        secondBlank = toClone.getSecondBlank();
    }

    public List<TilePos> allTilePos() {
        ArrayList<TilePos> out = new ArrayList<TilePos>();
        for(int i=0; i<DIMS; i++) {
            for(int j=0; j<DIMS; j++) {
                out.add(new TilePos(i,j,0));
            }
        }
        return out;
    }


    public int tile(TilePos p) {
        return tiles[p.x][p.y];
    }


    public TilePos getBlank() {
        return blank;
    }

    public TilePos getSecondBlank() {
        return secondBlank;
    }

    public TilePos whereIs(int x) {
        for(TilePos p: allTilePos()) {
            if( tile(p) == x ) {
                return p;
            }
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof FourteenPuzzle) {
            for(TilePos p: allTilePos()) {
                if( this.tile(p) != ((FourteenPuzzle) o).tile(p)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public int hashCode() {
        int out=0;
        for(TilePos p: allTilePos()) {
            out= (out*DIMS*DIMS) + this.tile(p);
        }
        return out;
    }


    public void show() {
        System.out.println("-----------------");
        for(int i=0; i<DIMS; i++) {
            System.out.print("| ");
            for(int j=0; j<DIMS; j++) {
                int n = tiles[i][j];
                String s;
                if( n>0) {
                    s = Integer.toString(n);
                } else {
                    s = "";
                }
                while( s.length() < display_width ) {
                    s += " ";
                }
                System.out.print(s + "| ");
            }
            System.out.print("\n");
        }
        System.out.print("-----------------\n\n");
    }


    public List<TilePos> allValidMoves() {
        ArrayList<TilePos> out = new ArrayList<TilePos>();
        for(int dx=-1; dx<2; dx++) {
            for(int dy=-1; dy<2; dy++) {
                TilePos tp = new TilePos(blank.x + dx, blank.y + dy, 0);
                TilePos tp2 = new TilePos(secondBlank.x + dx, secondBlank.y + dy, -1);
                if( isValidMove(tp, blank) ) {
                    //System.out.println("blank1: " + dx + " " + dy);
                    out.add(tp);
                }
                if( isValidMove(tp2, secondBlank) ) {
                    //System.out.println("blank2: " + dx + " " + dy);
                    out.add(tp2);
                }
            }
        }

        return out;
    }


    public boolean isValidMove(TilePos p, TilePos b) {
        if( ( p.x < 0) || (p.x >= DIMS) ) {
            return false;
        }
        if( ( p.y < 0) || (p.y >= DIMS) ) {
            return false;
        }
        int dx = b.x - p.x;
        int dy = b.y - p.y;

        if( (Math.abs(dx) + Math.abs(dy) != 1 ) || (dx*dy != 0) || (tile(p) == -1) || (tile(p) == 0)) {
            return false;
        }
        //System.out.println("tile" + p.x + " " + p.y + " " + tile(p));
        return true;
    }

    @SuppressWarnings("Duplicates")
    public void move(TilePos p) {
        if (p.blankSelector == 0) {
            if (!isValidMove(p, blank)) {
                throw new RuntimeException("Invalid move");
            }
            assert tiles[blank.x][blank.y] == 0;
            tiles[blank.x][blank.y] = tiles[p.x][p.y];
            tiles[p.x][p.y] = 0;
            blank = p;
        }
        else {
            if (!isValidMove(p, secondBlank)) {
                throw new RuntimeException("Invalid move");
            }
            assert tiles[secondBlank.x][secondBlank.y] == -1;
            tiles[secondBlank.x][secondBlank.y] = tiles[p.x][p.y];
            tiles[p.x][p.y] = -1;
            secondBlank = p;
        }
    }


    /**
     * returns a new puzzle with the move applied
     * @param p
     * @return
     */
    public FourteenPuzzle moveClone(TilePos p) {
        FourteenPuzzle out = new FourteenPuzzle(this);
        out.move(p);
        return out;
    }


    public void shuffle(int howmany) {
        for(int i=0; i<howmany; i++) {
            List<TilePos> possible = allValidMoves();
            //show();
            /*for(int ii = 0; ii < possible.size(); ii++) {
                System.out.println(tiles[possible.get(ii).x][possible.get(ii).y]);
            }
            System.out.println();*/
            int which =  (int) (Math.random() * possible.size());
            TilePos move = possible.get(which);
            this.move(move);
        }
    }


    public void shuffle() {
        shuffle(DIMS*DIMS*DIMS*DIMS*DIMS);
    }


    public int numberMisplacedTiles() {
        int wrong=0;
        for(int i=0; i<DIMS; i++) {
            for(int j=0; j<DIMS; j++) {
                if( (tiles[i][j] >0) && ( tiles[i][j] != SOLVED.tiles[i][j] ) ){
                    wrong++;
                }
            }
        }
        return wrong;
    }


    public boolean isSolved() {
        return numberMisplacedTiles() == 0;
    }


    /**
     * another A* heuristic.
     * Total manhattan distance (L1 norm) from each non-blank tile to its correct position
     * @return
     */
    public int manhattanDistance() {
        int sum=0;
        for(TilePos p: allTilePos()) {
            int val = tile(p);
            if( val > 0 ) {
                TilePos correct = SOLVED.whereIs(val);
                sum += Math.abs( correct.x - p.x );
                sum += Math.abs( correct.y - p.y );
            }
        }

        return sum;
    }

    /**
     * distance heuristic for A*
     * @return
     */
    public int estimateError() {
        //return this.numberMisplacedTiles();
        //return 5*this.numberMisplacedTiles(); // finds a non-optimal solution faster
        return this.manhattanDistance();
    }


    public List<FourteenPuzzle> allAdjacentPuzzles() {
        ArrayList<FourteenPuzzle> out = new ArrayList<FourteenPuzzle>();
        for( TilePos move: allValidMoves() ) {
            out.add( moveClone(move) );
        }
        return out;
    }

    /**
     * returns a list of boards if it was able to solve it, or else null
     * @return
     */
    public List<FourteenPuzzle> aStarSolve() {
        HashMap<FourteenPuzzle,FourteenPuzzle> predecessor = new HashMap<FourteenPuzzle,FourteenPuzzle>();
        HashMap<FourteenPuzzle,Integer> depth = new HashMap<FourteenPuzzle,Integer>();
        final HashMap<FourteenPuzzle,Integer> score = new HashMap<FourteenPuzzle,Integer>();
        Comparator<FourteenPuzzle> comparator = new Comparator<FourteenPuzzle>() {
            @Override
            public int compare(FourteenPuzzle a, FourteenPuzzle b) {
                return score.get(a) - score.get(b);
            }
        };
        PriorityQueue<FourteenPuzzle> toVisit = new PriorityQueue<FourteenPuzzle>(10000,comparator);

        predecessor.put(this, null);
        depth.put(this,0);
        score.put(this, this.estimateError());
        toVisit.add(this);
        int cnt=0;
        while( toVisit.size() > 0) {
            FourteenPuzzle candidate = toVisit.remove();
            cnt++;
            if( cnt % 10000 == 0) {
                System.out.printf("Considered %,d positions. Queue = %,d\n", cnt, toVisit.size());
            }
            if( candidate.isSolved() ) {
                System.out.printf("Solution considered %d boards\n", cnt);
                LinkedList<FourteenPuzzle> solution = new LinkedList<FourteenPuzzle>();
                FourteenPuzzle backtrace=candidate;
                while( backtrace != null ) {
                    solution.addFirst(backtrace);
                    backtrace = predecessor.get(backtrace);
                }
                return solution;
            }
            for(FourteenPuzzle fp: candidate.allAdjacentPuzzles()) {
                if( !predecessor.containsKey(fp) ) {
                    predecessor.put(fp,candidate);
                    depth.put(fp, depth.get(candidate)+1);
                    int estimate = fp.estimateError();
                    score.put(fp, depth.get(candidate)+1 + estimate);
                    // dont' add to p-queue until the metadata is in place that the comparator needs
                    toVisit.add(fp);
                }
            }
        }
        return null;
    }

    private static void showSolution(List<FourteenPuzzle> solution) {
        if (solution != null ) {
            System.out.printf("Success!  Solution with %d moves:\n", solution.size());
            for( FourteenPuzzle sp: solution) {
                sp.show();
            }
        } else {
            System.out.println("Did not solve. :(");
        }
    }


    public static void main(String[] args) {
        FourteenPuzzle p = new FourteenPuzzle();
        //p.shuffle(50);  // Number of shuffles is critical -- large numbers (100+) and 4x4 puzzle is hard even for A*.
        p.tiles  = new int[][]{
                {5, 1, 8, 3},
                {7, 9, 2, 4},
                {0, 6, 10, 12},
                {13, 11, -1, 14}
        };

        // init blank
        p.blank.x = 2;
        p.blank.y = 0;
        p.secondBlank.x = 3;
        p.secondBlank.y = 2;

        System.out.println("Shuffled board:");
        p.show();

        List<FourteenPuzzle> solution;

        System.out.println("Solving with A*");
        solution = p.aStarSolve();
        showSolution(solution);

		/*System.out.println("Solving with Dijkstra");
		solution = p.dijkstraSolve();
		showSolution(solution);*/
    }

}