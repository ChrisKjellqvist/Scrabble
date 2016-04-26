import java.util.ArrayList;

/**
 * Created by chris on 4/25/16.
 */
public class Board {
    public int squareSize = 1;
    Tile[][] board = new Tile[15][15];

    public Board() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = new Tile(0);
            }
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (i == j || i == 14 - j) {
                    if (i == 0 || i == 14) {
                        board[i][j] = new Tile(5);
                    } else if ((i > 0 && i < 5) || (i > 9 && i < 14)) {
                        board[i][j] = new Tile(4);
                    } else if (i == 5 || i == 9) {
                        board[i][j] = new Tile(3);
                    } else if (i == 6 || i == 8) {
                        board[i][j] = new Tile(2);
                    } else if (i == 7) {
                        board[i][j] = new Tile(1);
                    }
                }
            }
        }
        board[0][7] = new Tile(5);
        board[14][7] = new Tile(5);
        board[7][0] = new Tile(5);
        board[7][14] = new Tile(5);

        board[3][0] = new Tile(2);
        board[11][0] = new Tile(2);
        board[0][3] = new Tile(2);
        board[0][11] = new Tile(2);
        board[14][3] = new Tile(2);
        board[14][11] = new Tile(2);
        board[3][14] = new Tile(2);
        board[11][14] = new Tile(2);

        board[5][1] = new Tile(3);
        board[9][1] = new Tile(3);
        board[1][5] = new Tile(3);
        board[13][5] = new Tile(3);
        board[1][9] = new Tile(3);
        board[13][9] = new Tile(3);
        board[5][13] = new Tile(3);
        board[9][13] = new Tile(3);

        board[6][2] = new Tile(2);
        board[8][2] = new Tile(2);
        board[2][6] = new Tile(2);
        board[12][6] = new Tile(2);
        board[2][8] = new Tile(2);
        board[12][8] = new Tile(2);
        board[6][12] = new Tile(2);
        board[8][12] = new Tile(2);

        board[7][3] = new Tile(2);
        board[3][7] = new Tile(2);
        board[11][7] = new Tile(2);
        board[7][11] = new Tile(2);
    }

    boolean isValidMove(ArrayList<Tile> list) {
        if (list.size() > 2) {
            if (list.get(0).coords[0] == list.get(1).coords[0]) {
                int x = list.get(0).coords[0];
                int y = list.get(0).coords[1];
                for (int i = 1; i < list.size(); i++) {
                    Tile c = list.get(i);
                    if (x != c.coords[0] || y != c.coords[1] - i * squareSize) {
                        return false;
                    }
                }
            } else {
                int x = list.get(0).coords[0];
                int y = list.get(0).coords[1];
                for (int i = 1; i < list.size(); i++) {
                    Tile c = list.get(i);
                    if (y != c.coords[1] || x != c.coords[0] - i * squareSize) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
