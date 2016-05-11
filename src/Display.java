import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chris and seth on 4/23/16.
 */
public class Display extends JPanel {
    /**
     * Images for resources on board
     */
    private static Image star;
    private static Image TWS;
    private static Image DWS;
    private static Image TLS;
    private static Image DLS;
    private static Image[] letters = new Image[26];

    //Screen dimensions
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //Location and dimensions of board elements
    public int squareSize;
    public int boardSize;
    public int leftBorder;
    public int topBorder;

    //Index of the tile being held
    public int beingHeld = -1;

    //Hand of tiles to display
    public Tile[] handToDisplay;

    //Scores. Only read from. Ownership is in main class.
    public int homeScore = 0;
    public int awayScore = 0;

    //Pointer to game board
    Board board;

    //Buffer image of board to paint
    BufferedImage boardBuffer;

    //Tiles that have been placed on the board.
    ArrayList<Tile> tilesPlaced = new ArrayList<>();

    /**
     * Reads in resources, and creates buffer.
     *
     * @param board - board that is being drawn. Is owned by main, so this is a pointer
     * @throws IOException - only thrown if resources aren't found
     */
    public Display(Board board) throws IOException {
        super();
        this.board = board;
        star = ImageIO.read(new File("resources/star.png"));
        TWS = ImageIO.read(new File("resources/TWS.png"));
        DWS = ImageIO.read(new File("resources/DWS.png"));
        TLS = ImageIO.read(new File("resources/TLS.png"));
        DLS = ImageIO.read(new File("resources/DLS.png"));
        for (int i = 0; i < 26; i++) {
            letters[i] = ImageIO.read(new File("resources/" + (char) (i + 97) + ".png"));
        }

        //fullscreen
        setPreferredSize(screenSize);

        //get square size
        boardSize = (int) (screenSize.height * .8);
        squareSize = boardSize / 15;
        board.squareSize = squareSize;
        topBorder = (int) (screenSize.height * .05);
        leftBorder = (int) (screenSize.width * .9 - squareSize * 15);

        /**
         * Creating the board buffer from the board states
         */
        boardBuffer = new BufferedImage(boardSize, boardSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = boardBuffer.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, boardSize, boardSize);
        g.setColor(Color.black);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                switch (board.board[i][j].state) {
                    case 0:
                        g.drawRect(i * squareSize, j * squareSize, squareSize, squareSize);
                        break;
                    case 1:
                        g.drawImage(star, i * squareSize, j * squareSize, squareSize, squareSize, null);
                        break;
                    case 2:
                        g.drawImage(DLS, i * squareSize, j * squareSize, squareSize, squareSize, null);
                        break;
                    case 3:
                        g.drawImage(TLS, i * squareSize, j * squareSize, squareSize, squareSize, null);
                        break;
                    case 4:
                        g.drawImage(DWS, i * squareSize, j * squareSize, squareSize, squareSize, null);
                        break;
                    case 5:
                        g.drawImage(TWS, i * squareSize, j * squareSize, squareSize, squareSize, null);
                        break;
                    case 6:
                        g.drawImage(letters[97 - (int) board.board[i][j].letter], i * squareSize, j * squareSize,
                                squareSize, squareSize, null);
                        break;
                }
            }
        }

    }

    /**
     * Drawing method. Handles all painting.
     * @param g - graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        //Draw the board
        g.drawImage(boardBuffer, leftBorder, topBorder, boardBuffer.getWidth(), boardBuffer.getHeight(), null);

        //Get location of mouse relative to panel
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, this);

        //color placed tiles
        for (Tile t : tilesPlaced) {
            boolean isValid = board.isValidMove(tilesPlaced);
            g.drawImage(letters[t.letter - 97], t.coords[0] * squareSize + leftBorder,
                    t.coords[1] * squareSize + topBorder, squareSize, squareSize, null);

            if (isValid) {
                g.setColor(Color.green);
                for (int j = 1; j <= 2; j++) {
                    g.drawRect(t.coords[0] * squareSize + leftBorder - j,
                            t.coords[1] * squareSize + topBorder - j,
                            squareSize + 2 * j,
                            squareSize + 2 * j);
                }
            } else {
                g.setColor(Color.red);
                for (int j = 1; j <= 2; j++) {
                    g.drawRect(t.coords[0] * squareSize + leftBorder - j,
                            t.coords[1] * squareSize + topBorder - j,
                            squareSize + 2 * j,
                            squareSize + 2 * j);
                }
            }
        }

        //Draw the hand and the tile being held
        for (int i = 0; i < handToDisplay.length; i++) {
            if (!handToDisplay[i].placed) {
                if (i != beingHeld) {
                    g.drawImage(letters[handToDisplay[i].letter - 97], 30, (i + 1) * squareSize,
                            squareSize, squareSize, null);
                } else {
                    //Find coordinates of square you would place in and draw you holding tile.
                    if (p.x > leftBorder && p.x < leftBorder + boardSize && p.y > topBorder
                            && p.y < topBorder + boardSize) {
                        int x = (p.x - leftBorder) / squareSize;
                        int y = (p.y - topBorder) / squareSize;
                        g.drawImage(letters[handToDisplay[i].letter - 97], p.x, p.y, squareSize, squareSize, null);
                        for (int j = 1; j <= 2; j++) {
                            g.drawRect(leftBorder + x * squareSize - j, topBorder + y * squareSize - j,
                                    squareSize + 2 * j, squareSize + 2 * j);
                        }
                    } else {
                        g.drawImage(letters[handToDisplay[i].letter - 97], p.x, p.y, squareSize, squareSize, null);
                    }
                }
            }
        }
        g.setColor(Color.black);
        g.drawString("Home: " + homeScore, 15, (int) (screenSize.height * .7));
        g.drawString("Away: " + awayScore, 15, (int) (screenSize.height * .7 - 30));
    }

    /**
     * Paint a set of tiles
     * @param t - set of tiles
     */
    public void paintMove(Tile[] t) {
        for (Tile tile : t) {
            paintTile(tile);
        }
    }

    /**
     * Paint an individual tile to the board buffer
     * @param t - individual tile to paint to buffer
     */
    private void paintTile(Tile t) {
        boardBuffer.createGraphics().drawImage(letters[t.letter - 97], t.coords[0] * squareSize, t.coords[1] * squareSize,
                squareSize, squareSize, null);
    }

    /**
     * Determines whether the point provided is within the range of coordinates covered by the board buffer
     * @param p - point
     * @return - whether or not you are within board
     */
    boolean withinBoard(Point p) {
        return p.x > leftBorder && p.x < leftBorder + boardSize && p.y > topBorder && p.y < topBorder + boardSize;
    }
}
