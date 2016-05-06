import java.util.ArrayList;

/**
 * Created by chris on 4/25/16.
 */
public class Board {
    public static final int UNALIGNED = -1;
    public static final int HORIZONTAL_ALIGNMENT = 0;
    public static final int VERTICAL_ALIGNMENT = 1;
    static Tile[][] referenceBoard;
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
        referenceBoard = board.clone();
    }

    /**
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
            return -1;
        }
    }

    /**
     * Functions by checking that every tile pair is adjacently aligned in
     * the same way as every other tile pair.
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
        System.out.println(bestWord);
        return bestWord;
    }

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
                temp.coords[0] = constant;
                temp.coords[1] = i;
            } else {
                temp.coords[0] = i;
                temp.coords[1] = constant;
            }
            tilesForWord[index] = temp;
            index++;
        }
        tilesForWord[index] = fixed;
        index++;
        for (int i = pivot + 1; i < pivot + suffixLength; i++) {
            Tile temp = new Tile(word.charAt(index));
            if (makeHorizontal) {
                temp.coords[0] = constant;
                temp.coords[1] = i;
            } else {
                temp.coords[0] = i;
                temp.coords[1] = constant;
            }
            tilesForWord[index] = temp;
            index++;
        }
        return tilesForWord;
    }

    public boolean fitsAtLetter(Tile[] tiles) {
        boolean temp = true;
        int prefixLength = 0;
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
        if (t.alignment == Board.HORIZONTAL_ALIGNMENT) {
            for (int i = t.coords[1] - prefixLength; i < t.coords[1]; i++) {
                try {
                    if (board[t.coords[0] + 1][i].state != Tile.BLANK || board[i][t.coords[1] - 1].state != Tile.BLANK) {
                        return false;
                    }
                } catch (ArrayIndexOutOfBoundsException exc) {
                    return false;
                }
            }
            for (int i = t.coords[1] + 1; i < t.coords[1] + suffixLength; i++) {
                try {
                    if (board[t.coords[0] + 1][i].state != Tile.BLANK || board[i][t.coords[1] - 1].state != Tile.BLANK) {
                        return false;
                    }
                } catch (ArrayIndexOutOfBoundsException exc) {
                    return false;
                }
            }
        } else {
            for (int i = t.coords[0] - prefixLength; i < t.coords[0]; i++) {
                try {
                    if (board[i][t.coords[1] + 1].state != Tile.BLANK || board[t.coords[0] - 1][i].state != Tile.BLANK) {
                        return false;
                    }
                } catch (ArrayIndexOutOfBoundsException exc) {
                    return false;
                }
            }
            for (int i = t.coords[0] + 1; i < t.coords[0] + suffixLength; i++) {
                try {
                    if (board[i][t.coords[1] + 1].state != Tile.BLANK || board[t.coords[0] - 1][i].state != Tile.BLANK) {
                        return false;
                    }
                } catch (ArrayIndexOutOfBoundsException exc) {
                    return false;
                }
            }
        }
        return true;
    }

    //@TODO fix this
    public boolean isGameOver() {
        return false;
    }

    public int getScore(Tile[] list) {
        int score = 0;
        int wordMultiplier = 1;
        for (Tile t : list) {
            if (t.isFixed) {
                score += Dictionary.getLetterScore(t.letter);
            } else {
                for (int i = 0; i < list.length; i++) {
                    int[] tempCoords = list[i].coords.clone();
                    Tile tileTemp = referenceBoard[tempCoords[0]][tempCoords[1]];
                    switch (tileTemp.state) {
                        case Tile.DL:
                            score += Dictionary.getLetterScore(t.letter) * 2;
                            break;
                        case Tile.DW:
                            score += Dictionary.getLetterScore(t.letter);
                            wordMultiplier *= 2;
                            break;
                        case Tile.BLANK:
                            score += Dictionary.getLetterScore(t.letter);
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
                    }
                }
            }
        }
        return score * wordMultiplier;
    }

    void doMove(Tile[] tiles) {
        for (Tile t : tiles) {
            int x = t.coords[0];
            int y = t.coords[1];
            board[x][y] = t;
            board[x][y].state = Tile.PLACED_TILE;
            board[x][y].isFixed = true;
            board[x][y].alignment = t.alignment;
        }
    }

    ArrayList<Tile> getFixedTiles(Tile tile) {
        ArrayList<Tile> list = new ArrayList<>();
        for (int i = -1; i <= 1; i += 2) {
            Tile c = board[tile.coords[0] + i][tile.coords[1]];
            if (c.state == Tile.PLACED_TILE) {
                list.add(c);
            }
        }
        for (int i = -1; i <= 1; i += 2) {
            Tile c = board[tile.coords[0]][tile.coords[1] + i];
            if (c.state == Tile.PLACED_TILE) {
                list.add(c);
            }
        }
        return list;
    }
}
