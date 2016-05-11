import java.util.ArrayList;

/**
 * Created by chris and seth on 5/2/16.
 * Handles a static AI. There's no need to make the AI
 * own anything because it just makes actions so everything
 * is static.
 */
public class ComputerPlayer {

    /**
     * Given the current board and hand, find the best move.
     *
     * @param board - The current board
     * @param hand  - the players hand to consider
     * @return - The tile placement of the best move.
     */
    public static Tile[] getNextMove(Board board, Tile[] hand) {
        ArrayList<Tile> possiblePlaces = new ArrayList<>();
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[0].length; j++) {
                if (board.board[i][j].state == Tile.PLACED_TILE) {
                    if (acceptableForComputer(board, board.board[i][j])) {
                        possiblePlaces.add(board.board[i][j]);
                    }
                }
            }
        }

        //Find best place to play tiles by score.
        ArrayList<Tile[]> words = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();

        for (Tile t : possiblePlaces) {
            String str = board.getBestWordPossible(hand, t);
            if (!(str == null)) {
                Tile[] temp = board.getTilePlacement(str, t);
                int score = board.getScore(temp);
                words.add(temp);
                scores.add(score);
            }
        }
        if (words.size() >= 1) {
            Tile[] bestTiles = words.get(0);
            int bestScore = scores.get(0);
            for (int i = 1; i < words.size(); i++) {
                if (scores.get(i) > bestScore) {
                    bestScore = scores.get(i);
                    bestTiles = words.get(i);
                }
            }
            return bestTiles;
        } else {
            return null;
        }
    }

    /**
     * Redundant method. Finds the fixed tiles around a tile t.
     *
     * @param b - board
     * @param t - tile
     * @return - Will return true while its surrounding <= 3 tiles.
     */
    public static boolean acceptableForComputer(Board b, Tile t) {
        int nonBlanks = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                try {
                    if (b.board[t.coords[0] + i][t.coords[1] + j].state == Tile.PLACED_TILE) {
                        nonBlanks++;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    //Does nothing because it doesn't matter.
                }
            }
        }
        return nonBlanks <= 3;
    }
}
