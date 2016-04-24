import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by chris on 4/23/16.
 */
public class Display extends JPanel{
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static int squareSize;
    private static int leftBorder;
    private static int topBorder;
    public static int beingHeld = -1;

    public Point mousePoint;

    private static Image star;
    private static Image TWS;
    private static Image DWS;
    private static Image TLS;
    private static Image DLS;
    private static Image[] letters = new Image[26];

    public static Tile[] handToDisplay;

    Tile[][] board = new Tile[15][15];
    BufferedImage boardBuffer;

    public Display() throws IOException{
        super();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = new Tile(0);
            }
        }

        star = ImageIO.read(new File("resources/star.png"));
        TWS = ImageIO.read(new File("resources/TWS.png"));
        DWS = ImageIO.read(new File("resources/DWS.png"));
        TLS = ImageIO.read(new File("resources/TLS.png"));
        DLS = ImageIO.read(new File("resources/DLS.png"));
        for (int i = 0; i < 26; i++) {
            letters[i] = ImageIO.read(new File("resources/" + (char)(i+97) + ".png"));
        }

        setPreferredSize(screenSize);

        //get square size
        int boardSize = (int)(screenSize.height*.8);
        squareSize  = boardSize / 15;
        topBorder = (int)(screenSize.height*.05);
        leftBorder = (int)(screenSize.width*.9 - squareSize * 15);

        boardBuffer = new BufferedImage(boardSize, boardSize, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(i==j || i == 14 - j){
                    if(i == 0 || i == 14){
                        board[i][j] = new Tile(5);
                    } else if((i > 0 && i < 5) || (i > 9 && i < 14)) {
                        board[i][j] = new Tile(4);
                    } else if(i == 5 || i == 9){
                        board[i][j] = new Tile(3);
                    } else if (i == 6 || i == 8){
                        board[i][j] = new Tile(2);
                    } else if(i == 7){
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

        Graphics2D g = boardBuffer.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, boardSize, boardSize);
        g.setColor(Color.black);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                switch (board[i][j].state){
                    case 0:
                        g.drawRect(i*squareSize, j*squareSize, squareSize, squareSize);
                        break;
                    case 1:
                        g.drawImage(star, i*squareSize, j*squareSize, squareSize, squareSize, null);
                        break;
                    case 2:
                        g.drawImage(DLS, i*squareSize, j*squareSize, squareSize, squareSize, null);
                        break;
                    case 3:
                        g.drawImage(TLS, i*squareSize, j*squareSize, squareSize, squareSize, null);
                        break;
                    case 4:
                        g.drawImage(DWS, i*squareSize, j*squareSize, squareSize, squareSize, null);
                        break;
                    case 5:
                        g.drawImage(TWS, i*squareSize, j*squareSize, squareSize, squareSize, null);
                        break;
                    case 6:
                        g.drawImage(letters[97-(int)board[i][j].letter], i*squareSize, j*squareSize,
                                squareSize, squareSize, null);
                        break;
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g){
        g.drawImage(boardBuffer, leftBorder, topBorder, boardBuffer.getWidth(), boardBuffer.getHeight(), null);
        for (int i = 0; i < handToDisplay.length; i++) {
            if(i!=beingHeld) {
                g.drawImage(letters[handToDisplay[i].letter - 97], 30, (i+1) * squareSize, squareSize, squareSize, null);
            } else {
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, this);
                g.drawImage(letters[handToDisplay[i].letter-97], p.x, p.y, squareSize, squareSize, null);
            }
        }
    }

}
