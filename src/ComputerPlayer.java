import java.util.ArrayList;

/**
 * Created by chris on 5/2/16.
 */
public class ComputerPlayer {
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
        System.out.println("poss: " + possiblePlaces.size());
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
        Tile[] bestTiles = words.get(0);
        int bestScore = scores.get(0);
        for (int i = 1; i < words.size(); i++) {
            if (scores.get(i) > bestScore) {
                bestScore = scores.get(i);
                bestTiles = words.get(i);
            }
        }
        return bestTiles;
    }

    public static boolean acceptableForComputer(Board b, Tile t) {
        int nonBlanks = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                try {
                    if (b.board[t.coords[0] + i][t.coords[1] + j].state == Tile.PLACED_TILE) {
                        nonBlanks++;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        }
        return nonBlanks <= 3;
    }
}
