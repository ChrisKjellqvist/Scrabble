import java.util.ArrayList;

/**
 * Created by chris on 5/2/16.
 */
public class ComputerPlayer {
    public static ArrayList<Tile> getNextMove(Board board, Tile[] hand) {
        ArrayList<Tile> possiblePlaces = new ArrayList<>();
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[0].length; j++) {
                if (acceptableForComputer(board, board.board[i][j])) {
                    possiblePlaces.add(board.board[i][j]);
                }
            }
        }

        ArrayList<Tile[]> words = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
        for (Tile t : possiblePlaces) {

        }
        return new ArrayList<>();
    }

    public static boolean acceptableForComputer(Board b, Tile t) {
        int nonBlanks = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (b.board[t.coords[0] + i][t.coords[1] + j].state == Tile.PLACED_TILE) {
                    nonBlanks++;
                }
            }
        }
        return nonBlanks <= 3;
    }
}
