/**
 * Created by chris and seth on 4/23/16.
 * A Tile is the functional unit of the game, storing the letter, coordinates, and state of the game piece.
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

    //A tile is fixed when it has been played on the board.
    public boolean isFixed = false;
    //This will store the coordinates of the tile when it has been placed on the board
    public int[] coords = new int[2];
    
    //The alignment is assigned after it is played, it is used to determine validity of the AI's possible moves.
    public int alignment = Board.UNALIGNED;
    
    //Constructors compatable with state and letter
    public Tile(int state){
        this.state = state;
    }

    public Tile(char letter) {
        this.letter = letter;
        state = PLACED_TILE;
    }

    //Used for debugging purposes
    @Override
    public String toString() {
        return letter + " ";
    }

    //Defines what it means for a tile to be equal to another tile: when they share the same coordinates
    @Override
    public boolean equals(Object o) {
        Tile t = (Tile) o;
        return t.coords[0] == this.coords[0] && t.coords[1] == this.coords[1];
    }

}
