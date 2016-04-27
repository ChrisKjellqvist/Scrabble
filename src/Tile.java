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

    /**
     * Only applies if letter is in hand. Used for graphics purposes.
     */
    public boolean placed;
    public int[] coords = new int[2];
    public boolean spelledHorizontally = false;


    public Tile(int state){
        this.state = state;
    }

    public Tile(char letter) {
        this.letter = letter;
        state = 6;
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
