import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by chris on 4/23/16.
 */
public class main {

    static Display display;
    static Board board = new Board();
    static ArrayList<Tile> bag = new ArrayList<>();

    static Tile[] home = new Tile[7];
    static Tile[] away = new Tile[7];
    static Tile[] currentHand;

    static int homeScore = 0;
    static int awayScore = 0;

    static boolean turnisOver = false;

    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("Scrabble");
        makeBag();
        Dictionary dictionary = new Dictionary(new File("resources/ospd.txt"));
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
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
                        for (int i = 0; i < display.tilesPlaced.size(); i++) {
                            ArrayList<Tile> list = board.getFixedTiles(display.handToDisplay[display.beingHeld]);
                            for (Tile t : list) {
                                if (!display.tilesPlaced.contains(t)) {
                                    display.tilesPlaced.add(t);
                                }
                            }
                        }
                    }
                    display.beingHeld = -1;
                    time.stop();
                    display.repaint();
                    display.tilesPlaced.sort(new TileComparator<>());
                    System.out.println(display.tilesPlaced);

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
                } else if (e.getKeyChar() == 'r') {
                    display.tilesPlaced.clear();
                    for (Tile t : display.handToDisplay) {
                        t.placed = false;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


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

        long t1 = System.currentTimeMillis();
        while (!board.isGameOver()) {
            currentHand = home;
            long y = t1 + 1000;
            while (!turnisOver) {
                if (System.currentTimeMillis() >= y) {
                    y += 1000;
                    System.out.print(" ");
                }
            }
            System.out.println();
            System.out.println((y - t1) / 1000);
            turnisOver = false;
            Tile[] move = new Tile[display.tilesPlaced.size()];
            display.tilesPlaced.toArray(move);
            doTurn(move);

            homeScore += board.getScore(move);
            
            if(board.isGameOver()) break;

            /**
             *
             *  SWITCHING SIDES
             *
             */

            currentHand = away;

            Tile[] CPMove = ComputerPlayer.getNextMove(board, currentHand);
            doTurn(CPMove);
            System.out.println(Arrays.toString(CPMove));
            awayScore += board.getScore(CPMove);

            if(board.isGameOver()) break;
            
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

    private static void doTurn(Tile[] move) {
        int temp;
        display.paintMove(move);
        display.repaint();

        board.doMove(move);
        Tile[] newHand = new Tile[7];

        for (int i = 0; i < currentHand.length; i++) {
            if (!display.tilesPlaced.contains(currentHand[i])) {
                newHand[i] = currentHand[i];
            } else {
                if (bag.size() >= 1) {
                    temp = (int) (Math.random() * (bag.size() - 1));
                    currentHand[i] = bag.get(temp);
                    bag.remove(temp);
                }
            }
        }
        display.tilesPlaced.clear();
        currentHand = newHand;
    }

    private static void makeBag() {
        int[] distributions = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < distributions[i]; j++) {
                bag.add(new Tile((char) (97 + i)));
            }
        }
    }
}
