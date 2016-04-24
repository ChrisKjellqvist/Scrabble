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
    public int state;

    /**
     * Only relevant of value of tile is 6.
     */
    public char letter;

    public Tile(int state){
        this.state = state;
    }

    public Tile(char letter) {
        this.letter = letter;
        state = 6;
    }


}
