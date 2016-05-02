import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chris on 4/23/16.
 */
public class main {

    static Display display;
    static Tile[] home = new Tile[7];
    static Tile[] away = new Tile[7];
    static Tile[] currentHand;

    static int homeScore = 0;
    static int awayScore = 0;

    static boolean turnisOver = false;

    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("Scrabble");
        Dictionary dictionary = new Dictionary(new File("resources/ospd.txt"));
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        Board board = new Board();
        display = new Display(board);
        frame.add(display);

        Timer time = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.repaint();
            }
        });

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        display.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {


            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() > 30 && e.getX() < 30 + display.squareSize) {
                    int y = e.getY() / display.squareSize - 1;
                    if (y <= 7) {
                        display.beingHeld = e.getY() / display.squareSize - 1;
                        time.start();
                    }

                }
                if (display.withinBoard(e.getPoint())) {
                    int x = (e.getX() - display.leftBorder) / display.squareSize;
                    int y = (e.getY() - display.topBorder) / display.squareSize;
                    Tile t = new Tile('a');
                    t.coords[0] = x;
                    t.coords[1] = y;
                    for (int i = 0; i < currentHand.length; i++) {
                        if (currentHand[i].equals(t)) {
                            display.beingHeld = i;
                            currentHand[i].placed = false;
                            display.tilesPlaced.remove(currentHand[i]);
                            time.start();
                        }
                    }

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (display.beingHeld != -1) {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(p, display);
                    int x = (p.x - display.leftBorder) / display.squareSize;
                    int y = (p.y - display.topBorder) / display.squareSize;
                    if (x >= 0 && x < 15 && y >= 0 && y < 15
                            && !display.tilesPlaced.contains(display.handToDisplay[display.beingHeld])) {
                        display.handToDisplay[display.beingHeld].coords[0] = x;
                        display.handToDisplay[display.beingHeld].coords[1] = y;
                        display.handToDisplay[display.beingHeld].placed = true;
                        display.tilesPlaced.add(display.handToDisplay[display.beingHeld]);
                    }
                    display.beingHeld = -1;
                    time.stop();
                    display.repaint();
                    display.tilesPlaced.sort(new TileComparator<>());

                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == ' ' && Dictionary.isWord(getCurrentWord())) {
                    turnisOver = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        ArrayList<Tile> bag = new ArrayList<>();
        int[] distributions = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < distributions[i]; j++) {
                bag.add(new Tile((char)(97+i)));
            }
        }

        int temp;
        for (int i = 0; i < 7; i++) {
            temp = (int)(Math.random()*(bag.size()-1));
            home[i] = bag.get(temp);
            bag.remove(temp);

            temp = (int)(Math.random()*(bag.size()-1));
            away[i] = bag.get(temp);
            bag.remove(temp);

        }
        display.handToDisplay = home;
        currentHand = home;
        System.out.println(Dictionary.findBestWord(getHandsLetterDistribution(currentHand)));
        frame.setVisible(true);

        while (!board.isGameOver()) {
            display.handToDisplay = home;
            currentHand = home;
            while (!turnisOver) {
            }
            homeScore += board.getScore(display.tilesPlaced);


        }
    }

    static String getCurrentWord() {
        String t = "";
        for (Tile temp : display.tilesPlaced) {
            t += temp.letter;
        }
        return t;
    }

    public static byte[] getHandsLetterDistribution(Tile[] ar) {
        byte[] toReturn = new byte[26];
        for (Tile t : ar) {
            toReturn[t.letter - 97]++;
        }
        return toReturn;
    }

}
