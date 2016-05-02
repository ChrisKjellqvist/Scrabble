/**
 * Created by chris on 4/23/16.
 */
public class Tile {
    /**
     * State determines what is showing on the board.
     *
     * 0 blank
     * 1 star
     * 2 double letter
     * 3 triple letter
     * 4 double word
     * 5 triple word
     * 6 placed tile
     *
     */

    static final int BLANK = 0;
    static final int STAR = 1;
    static final int DL = 2;
    static final int TL = 3;
    static final int DW = 4;
    static final int TW = 5;
    static final int PLACED_TILE = 6;
    public int state;

    /**
     * Only relevant of value of tile is 6.
     */
    public char letter;

    /**
     * Only applies if letter is in hand. Used for graphics purposes.
     */
    public boolean placed;

    public boolean isFixed = false;
    public int[] coords = new int[2];
    public boolean spelledHorizontally = false;


    public Tile(int state){
        this.state = state;
    }

    public Tile(char letter) {
        this.letter = letter;
        state = PLACED_TILE;
    }

    @Override
    public String toString() {
        return coords[0] + " " + coords[1];
    }

    @Override
    public boolean equals(Object o) {
        Tile t = (Tile) o;
        return t.coords[0] == this.coords[0] && t.coords[1] == this.coords[1];
    }

}
