import java.util.ArrayList;

/**
 * Created by Chris Kjellqvist and Seth Dalton on 4/25/16.
 * This is the board object. It stores the tiles, gets the validity of moves,
 * and runs general game functions.
 */
public class Board {
    /**
     * Orientation constants
     * Indicate whether a tile is spelling a word vertically, horizontally, or with no orientation
     */
    public static final int UNALIGNED = -1;
    public static final int HORIZONTAL_ALIGNMENT = 0;
    public static final int VERTICAL_ALIGNMENT = 1;

    /**
     * Used so that special value tiles can be seen after they have had tiles placed on them.
     */
    static Tile[][] referenceBoard;

    /**
     * 1 is just a default value so it compiles. Refer to Display for its usage
     */
    public int squareSize = 1;

    /**
     * The actual playing board.
     */
    Tile[][] board = new Tile[15][15];


    public Board() {
        /**
         * This initializes the board to be the official scrabble board. A lot had to
         * be hard coded but that's just the way it had to be.
         */
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

        /**
         * Giving values to reference board.
         */
        referenceBoard = new Tile[15][15];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                referenceBoard[i][j] = new Tile(0);
                referenceBoard[i][j].state = board[i][j].state;
            }
        }
    }

    /**
     * Gets orientation of two tiles in relation to each other by seeing if their x and y
     * coordinates are the same or neither.
     *
     * @param a arbitrary tile - needs to be the top/leftmost tile
     * @param b arbitrary tile - needs to be the bottom/rightmost tile
     * @return returns alignement based off of constants given above
     */
    static int findAlignment(Tile a, Tile b) {
        if (a.coords[0] == b.coords[0]) {
            if (a.coords[1] + 1 == b.coords[1]) {
                return VERTICAL_ALIGNMENT;
            } else {
                return UNALIGNED;
            }
        } else if (a.coords[1] == b.coords[1]) {
            if (a.coords[0] + 1 == b.coords[0]) {
                return HORIZONTAL_ALIGNMENT;
            } else {
                return UNALIGNED;
            }
        } else {
            return UNALIGNED;
        }
    }

    /**
     * @param t - Arbitrary tile that is part of the game board
     * @return - state of the board. Refer to Tile class for values
     */
    private static int getReferenceState(Tile t) {
        return referenceBoard[t.coords[0]][t.coords[1]].state;
    }

    /**
     * Functions by checking that every tile pair is adjacently aligned in
     * the same way as every other tile pair.
     *
     * @param list - a list of placed tiles for the current move
     * @return Whether or not the tiles are placed in a line
     */
    boolean isValidMove(ArrayList<Tile> list) {
        if (list.size() > 1) {
            int alignment = findAlignment(list.get(0), list.get(1));
            if (alignment == UNALIGNED) {
                return false;
            }
            for (int i = 1; i < list.size() - 1; i++) {
                int newAlignment = findAlignment(list.get(i), list.get(i + 1));
                if (newAlignment != alignment) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Finds the best value word given a certain hand and a fixed tile that fits at the fixed tile.
     *
     * @param hand - Hand that is currently being considered.
     * @param fixed - Fixed tile that must be included in the word.
     * @return - Best word possible with the given hand and fixed tile
     */
    public String getBestWordPossible(Tile[] hand, Tile fixed) {
        ArrayList<String> blacklist = new ArrayList<>();
        byte[] dist = Dictionary.getLetterDistribution(hand);
        dist[fixed.letter - 97]++;
        String bestWord = Dictionary.findBestWordContaining(dist, fixed.letter, blacklist);
        while (!fitsAtLetter(getTilePlacement(bestWord, fixed))) {
            blacklist.add(bestWord);
            bestWord = Dictionary.findBestWordContaining(dist, fixed.letter, blacklist);
            if (bestWord == null) {
                return null;
            }
        }
        return bestWord;
    }

    /**
     * Places letter tiles around a fixed tile, putting them in an ordered array.
     * @param word - word that is being spelled
     * @param fixed - fixed letter
     * @return - Tile placement of the word
     */
    public Tile[] getTilePlacement(String word, Tile fixed) {
        Tile[] tilesForWord = new Tile[word.length()];
        boolean q = true;
        int prefixLength = 0;
        int suffixLength = 1;
        for (int i = 0; i < word.length(); i++) {
            if (fixed.letter == word.charAt(i) && q) {
                q = false;
            } else if (q) {
                prefixLength++;
            } else {
                suffixLength++;
            }
        }

        int index = 0;
        boolean makeHorizontal;
        int pivot;
        int constant;

        if (fixed.alignment == Board.HORIZONTAL_ALIGNMENT) {
            makeHorizontal = false;
            pivot = fixed.coords[1];
            constant = fixed.coords[0];
        } else {
            makeHorizontal = true;
            pivot = fixed.coords[0];
            constant = fixed.coords[1];
        }

        for (int i = pivot - prefixLength; i < pivot; i++) {
            Tile temp = new Tile(word.charAt(index));
            if (makeHorizontal) {
                temp.coords[0] = i;
                temp.coords[1] = constant;
            } else {
                temp.coords[0] = constant;
                temp.coords[1] = i;
            }
            tilesForWord[index] = temp;
            index++;
        }
        tilesForWord[index] = fixed;
        index++;
        for (int i = pivot + 1; i < pivot + suffixLength; i++) {
            Tile temp = new Tile(word.charAt(index));
            if (makeHorizontal) {
                temp.coords[0] = i;
                temp.coords[1] = constant;
            } else {
                temp.coords[0] = constant;
                temp.coords[1] = i;
            }
            tilesForWord[index] = temp;
            index++;
        }
        return tilesForWord;
    }

    public boolean fitsAtLetter(Tile[] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            try {
                if (board[tiles[i].coords[0]][tiles[i].coords[1]].state == Tile.PLACED_TILE && !tiles[i].isFixed) {
                    //Will be true when placed tiles will be on top of already placed letters.
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException exc) {
                //Will only be called if placed tiles are going to be outside of board.
                return false;
            }
        }

        //Used to tell whether or not you are incrementing the suffix or prefix lengths.
        boolean temp = true;
        //Letters before the fixed letter.
        int prefixLength = 0;
        //Letters after the fixed letter.
        int suffixLength = 0;
        Tile t = new Tile(0);
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].isFixed && temp) {
                t = tiles[i];
                temp = false;
            } else if (temp) {
                prefixLength++;
            } else if (!temp) {
                suffixLength++;
            }
        }
        int alignment = Board.findAlignment(tiles[0], tiles[1]);

        /**
         * With a given word, you will either be spelling it horizontally or vertically.
         * If the fixed tile is already part of a vertically spelled word, you have to
         * be spelling it horizontally. Thus, depending on the alignment, you will be
         * keeping the x or y value for the tiles constant and have one axis that you are
         * going across.
         *
         * This particular piece of code checks that it doesn't have any connecting tiles
         * before or after the word, or to the sides of the word.
         */
        int constant;
        int pivot;
        if (alignment == Board.HORIZONTAL_ALIGNMENT) {
            constant = t.coords[1];
            pivot = t.coords[0];
        } else {
            constant = t.coords[0];
            pivot = t.coords[1];
        }

        switch (alignment) {
            //Are there tiles before or after the word being spelled?
            case Board.HORIZONTAL_ALIGNMENT:
                try {
                    if (board[constant][pivot - prefixLength - 1].state == Tile.PLACED_TILE) {
                        return false;
                    }
                    if (board[constant][pivot + suffixLength + 1].state == Tile.PLACED_TILE) {
                        return false;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    //Should have already been accounted for but JUST IN CASE
                    if (pivot - prefixLength < 0 || pivot + suffixLength >= 15) {
                        return false;
                    }
                }
                break;
            case Board.VERTICAL_ALIGNMENT:
                try {
                    if (board[pivot - prefixLength - 1][constant].state == Tile.PLACED_TILE) {
                        return false;
                    }
                    if (board[pivot + suffixLength + 1][constant].state == Tile.PLACED_TILE) {
                        return false;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    if (pivot - prefixLength - 1 < 0 || pivot + suffixLength + 1 >= 15) {
                        return false;
                    }
                }
                break;
        }

        /**
         * Since spelling words parallel to other words is seriously hard to figure out
         * with our given architecture, we're choosing not to do it.
         *
         * You always end up playing words like en, ah, and ow with it anyway and that's
         * boring and in our opinion detracts from the spirit of the game.
         *
         * So as a result, we're checking that there are no tiles placed parallel.
         */

        for (int i = pivot - prefixLength; i < pivot; i++) {
            try {
                if (alignment == HORIZONTAL_ALIGNMENT) {
                    for (int j = -1; j <= 1; j += 2) {
                        if (board[constant + j][i].state == Tile.PLACED_TILE) {
                            return false;
                        }
                    }
                } else {
                    for (int j = -1; j <= 1; j += 2) {
                        if (board[i][constant + j].state == Tile.PLACED_TILE) {
                            return false;
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                //This will be called if constant + j is outside of the board but this
                //is a corner case, and since words can be spelled on the sides,
                //there should be nothing done about it.
            }
        }

        return true;
    }

    /**
     * With a given list of placed tiles, get the score that they yield.
     * @param list - placed tiles with coordinates
     * @return - integer score
     */
    public int getScore(Tile[] list) {
        int score = 0;
        int wordMultiplier = 1;
        int len = 0;
        for (int i = 0; i < list.length; i++) {
            Tile t = list[i];
            if (!t.isFixed) {
                len++;
                switch (getReferenceState(t)) {
                    case Tile.DL:
                        score += Dictionary.getLetterScore(t.letter) * 2;
                        break;
                    case Tile.DW:
                        score += Dictionary.getLetterScore(t.letter);
                        wordMultiplier *= 2;
                        break;
                    case Tile.STAR:
                        wordMultiplier *= 2;
                        score += Dictionary.getLetterScore(t.letter);
                        break;
                    case Tile.TL:
                        score += Dictionary.getLetterScore(t.letter) * 3;
                        break;
                    case Tile.TW:
                        score += Dictionary.getLetterScore(t.letter);
                        wordMultiplier *= 3;
                        break;
                    default:
                        score += Dictionary.getLetterScore(t.letter);
                        break;
                }
            } else {
                score += Dictionary.getLetterScore(t.letter);

            }
        }
        if (len == 7) {
            score += 50;
        }
        return score * wordMultiplier;
    }

    /**
     * Modifies the board with the newly placed tiles.
     * @param tiles - tiles to place
     */
    void doMove(Tile[] tiles) {
        int alignment = Board.findAlignment(tiles[0], tiles[1]);
        for (Tile t : tiles) {
            int x = t.coords[0];
            int y = t.coords[1];
            board[x][y] = t;
            board[x][y].state = Tile.PLACED_TILE;
            board[x][y].isFixed = true;
            board[x][y].alignment = alignment;
        }
    }

    /**
     * Checks the area around a tile and gets the number of fixed tiles
     * surrounding it.
     *
     * So if you are checking this situation
     *
     *  _ _ _
     *  A S K
     *  _ O _
     *
     *  and you call this method on S, it will return the 4 surrounding tiles.
     *
     *
     * @param tile
     * @return - array
     */
    ArrayList<Tile> getFixedTiles(Tile tile) {
        ArrayList<Tile> list = new ArrayList<>();
        for (int i = -1; i <= 1; i += 2) {
            try {
                Tile c = board[tile.coords[0] + i][tile.coords[1]];
                if (c.state == Tile.PLACED_TILE) {
                    list.add(c);
                }
            } catch (ArrayIndexOutOfBoundsException exc) {

            }
        }
        for (int i = -1; i <= 1; i += 2) {
            try {
                Tile c = board[tile.coords[0]][tile.coords[1] + i];
                if (c.state == Tile.PLACED_TILE) {
                    list.add(c);
                }
            } catch (ArrayIndexOutOfBoundsException exc) {
//
            }
        }
        return list;
    }

    public boolean isGameOver() {
        return false;
    }
}
