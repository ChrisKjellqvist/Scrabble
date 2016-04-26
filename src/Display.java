import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chris on 4/23/16.
 */
public class Display extends JPanel {
    private static Image star;
    private static Image TWS;
    private static Image DWS;
    private static Image TLS;
    private static Image DLS;
    private static Image[] letters = new Image[26];
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public int squareSize;
    //@TODO make private again
    public int boardSize;
    public int leftBorder;
    public int topBorder;
    public int beingHeld = -1;
    public Point mousePoint;
    public Tile[] handToDisplay;

    Board board = new Board();
    BufferedImage boardBuffer;
    ArrayList<Tile> tilesPlaced = new ArrayList<>();


    public Display() throws IOException {
        super();

        star = ImageIO.read(new File("resources/star.png"));
        TWS = ImageIO.read(new File("resources/TWS.png"));
        DWS = ImageIO.read(new File("resources/DWS.png"));
        TLS = ImageIO.read(new File("resources/TLS.png"));
        DLS = ImageIO.read(new File("resources/DLS.png"));
        for (int i = 0; i < 26; i++) {
            letters[i] = ImageIO.read(new File("resources/" + (char) (i + 97) + ".png"));
        }

        setPreferredSize(screenSize);

        //get square size
        boardSize = (int) (screenSize.height * .8);
        squareSize = boardSize / 15;
        board.squareSize = squareSize;
        topBorder = (int) (screenSize.height * .05);
        leftBorder = (int) (screenSize.width * .9 - squareSize * 15);

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

    //@TODO get rid of artifacts upon placing tile
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(boardBuffer, leftBorder, topBorder, boardBuffer.getWidth(), boardBuffer.getHeight(), null);
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, this);
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
        for (int i = 0; i < handToDisplay.length; i++) {
            if (i != beingHeld && !handToDisplay[i].placed) {
                g.drawImage(letters[handToDisplay[i].letter - 97], 30, (i + 1) * squareSize, squareSize, squareSize, null);
            } else {
                if (p.x > leftBorder && p.x < leftBorder + boardSize && p.y > topBorder && p.y < topBorder + boardSize) {
                    int x = (p.x - leftBorder) / squareSize;
                    int y = (p.y - topBorder) / squareSize;
                    g.drawImage(letters[handToDisplay[i].letter - 97], p.x, p.y, squareSize, squareSize, null);
                    for (int j = 1; j <= 2; j++) {
                        g.drawRect(leftBorder + x * squareSize - j, topBorder + y * squareSize - j, squareSize + 2 * j, squareSize + 2 * j);
                    }
                } else {
                    g.drawImage(letters[handToDisplay[i].letter - 97], p.x, p.y, squareSize, squareSize, null);
                }
            }
        }
    }

}
