import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chris and seth on 4/23/16.
 * This is the class that runs the game. It contains the majority of the game logic.
 */
public class main {

    /**
     * Used for getGameDecision
     */
    static final int PLAY = 0;
    static final int REDRAW_TILES = 1;
    static final int GAME_OVER = 2;
    //Instance variables vital to gameplay. Display runs the GUI, the board contains all game board info, and the bag will contain Tiles
    static Display display;
    static Board board = new Board();
    static ArrayList<Tile> bag = new ArrayList<>();
    //home and away are the "hands" of the players, home is the human player and away is the AI. Current hand is a temp holder used to update hands.
    static Tile[] home = new Tile[7];
    static Tile[] away = new Tile[7];
    static Tile[] currentHand;
    //variables storing the scores for the human player. homeScore, and the AI, awayScore
    static int homeScore = 0;
    static int awayScore = 0;
    //stores whether or not the human's turn is over.
    static boolean turnisOver = false;
    //First turn must be played on center star, this variable which is only true the first round ensures this rule is followed.
    static boolean firstTurn = true;

    /**
     * main method
     *
     * @param args
     * @throws IOException
     */
    
    //Main method
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Scrabble");
        makeBag();
        //creates the dictionary for this Scrabble game
        Dictionary dictionary = new Dictionary(new File("resources/ospd.txt"));
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        display = new Display(board);
        frame.add(display);
        Timer time = new Timer(1000 / 30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.repaint();
            }
        });
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Creates the means for tracking mouse clicks
        display.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {


            }

            @Override
            public void mousePressed(MouseEvent e) {
                //When the mouse is click, this determines the location of the cursor and responds accordingly. 
                //for example, determines which tile has been picked up
                //Below is when a tile has been grabbed from the player's hand
                if (e.getX() > 30 && e.getX() < 30 + display.squareSize) {
                    int y = e.getY() / display.squareSize - 1;
                    if (y <= 7) {
                        display.beingHeld = e.getY() / display.squareSize - 1;
                        time.start();
                    }

                }
                //When a tile from the board has been clicked
                if (display.withinBoard(e.getPoint())) {
                    int x = (e.getX() - display.leftBorder) / display.squareSize;
                    int y = (e.getY() - display.topBorder) / display.squareSize;
                    //Placeholder for spot on board that has been clicked
                    Tile t = new Tile('a');
                    t.coords[0] = x;
                    t.coords[1] = y;
                    for (int i = 0; i < currentHand.length; i++) {
                        //Should only pick the tile up if it is currently part of the word being formed by the player
                        if (currentHand[i].equals(t)) {
                            display.beingHeld = i;
                            currentHand[i].placed = false;
                            display.tilesPlaced.remove(currentHand[i]);
                            time.start();
                        }
                    }

                }
            }
            
            //Governs rules for when the mouse is released
            @Override
            public void mouseReleased(MouseEvent e) {
                if (display.beingHeld != -1) {
                    //determins where the mouse is upon release
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    //converts the location of the pointer to account for top frame
                    SwingUtilities.convertPointFromScreen(p, display);
                    int x = (p.x - display.leftBorder) / display.squareSize;
                    int y = (p.y - display.topBorder) / display.squareSize;
                    //If the tile is released onto the board, it determines the location and displays it accordingly
                    if (x >= 0 && x < 15 && y >= 0 && y < 15
                            && !display.tilesPlaced.contains(display.handToDisplay[display.beingHeld])) {
                        display.handToDisplay[display.beingHeld].coords[0] = x;
                        display.handToDisplay[display.beingHeld].coords[1] = y;
                        display.handToDisplay[display.beingHeld].placed = true;
                        //updates what tiles are placed
                        display.tilesPlaced.clear();
                        //displayes the current hand
                        for (int i = 0; i < display.handToDisplay.length; i++) {
                            Tile temp = display.handToDisplay[i];
                            if (temp.placed) {
                                display.tilesPlaced.add(temp);
                                ArrayList<Tile> list = board.getFixedTiles(temp);
                                for (Tile t : list) {
                                    if (!display.tilesPlaced.contains(t)) {
                                        display.tilesPlaced.add(t);
                                    }
                                }
                            }
                        }
                    }
                    display.beingHeld = -1;
                    time.stop();
                    display.repaint();
                    display.tilesPlaced.sort(new TileComparator<>());

                }
            }
            //If nothing is clicked nothing should happen
            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        //Pressing the spacebar submits the current word. 
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }
            //If the spacebar is pressed and a current legal move is on the board, the game proceeds accordingly
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == ' ') {
                    if (Dictionary.isWord(getCurrentWord())) {
                        turnisOver = true;
                        display.message = "";
                    } else {
                        if (display.message.contains("Not a word")) {
                            display.message = display.message + "!!";
                        } else {
                            display.message = "Not a word";
                        }
                        display.repaint();
                    }
                } else if (e.getKeyChar() == 'r') {
                    display.tilesPlaced.clear();
                    for (Tile t : display.handToDisplay) {
                        t.placed = false;
                    }
                }
            }
            //Nothing happens upon release of a key
            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        
        //fills each player's hand from the bag at the beginning of the game
        drawTiles(away);
        drawTiles(home);

        //tells the display to show the player's hand
        display.handToDisplay = home;
        //It is the player's turn, so his hand is the current hand
        currentHand = home;
        //For debugging only; displays the best word possible given the player's hand
        System.out.println(Dictionary.findBestWord(getHandsLetterDistribution(currentHand)));
        frame.setVisible(true);
        
        //This will ensure the loop does not run so rapidly that it loses function
        long t1 = System.currentTimeMillis();
        
        //This is the overall loop that contains game logic. The game will proceed on a turn by turn basis inside this loop

        //boolean for determining game progress
        boolean continueGame = true;
        //decision for whether game should continue
        int decision;

        while (continueGame) {
            decision = getGameDecision();
            if (!firstTurn) {
                switch (decision) {
                    case REDRAW_TILES:
                        if (ComputerPlayer.getNextMove(board, home) == null) {
                            redrawTiles(home);
                        } else {
                            redrawTiles(away);
                        }
                        break;
                    case GAME_OVER:
                        continueGame = false;
                        break;

                }
            }
            firstTurn = false;

            //This is the beginning of the player's turn
            currentHand = home;
            //This sequence defines the time in which the player is playing his move.
            long y = t1 + 15000;
            //This loop is buffered so that it does not lose functionality
            while (!turnisOver) {
                if (System.currentTimeMillis() >= y) {
                    y += 15000;
                    System.out.print(" ");
                }
            }
            //When the spacebar is pressed, the turn is over and the variable is set to true, ending the previous loop. This resets it
            turnisOver = false;
            //This will store the move in a format from which score can be calculated
            Tile[] move = new Tile[display.tilesPlaced.size()];
            display.tilesPlaced.toArray(move);
            //Calculates the score from the move that was played
            homeScore += board.getScore(move);
            //calls the helper method that executes the move
            home = doTurn(move, home);
            display.handToDisplay = home;
    
            //displays the updated score for the human player and paints it
            display.homeScore = homeScore;
            display.repaint();
            
            //If the game is over, then the main loop should end and code proceeds with end of game
            decision = getGameDecision();
            switch (decision) {
                case REDRAW_TILES:
                    if (ComputerPlayer.getNextMove(board, home) == null) {
                        redrawTiles(home);
                    } else {
                        redrawTiles(away);
                    }
                    break;
                case GAME_OVER:
                    continueGame = false;
                    break;

            }
            /**
             *
             *  SWITCHING SIDES
             *
             */
            display.repaint();
            //This is the beginning of the AI's turn, and his hand is the current Hand now
            currentHand = away;

            //Calls the method of the AI class to get the next move
            Tile[] CPMove = ComputerPlayer.getNextMove(board, currentHand);
            //Updates the score, executes the move, and displays the game changes
            awayScore += board.getScore(CPMove);
            away = doTurn(CPMove, away);
            display.awayScore = awayScore;
            display.repaint();
        }
    }

    //Returns the word that is currently played on the screen as a String. 
    static String getCurrentWord() {
        String t = "";
        for (Tile temp : display.tilesPlaced) {
            t += temp.letter;
        }
        return t;
    }

    //This takes an array of tiles and returns the distribution of the represented word (see Dictionary class for "distribution")
    public static byte[] getHandsLetterDistribution(Tile[] ar) {
        byte[] toReturn = new byte[26];
        for (Tile t : ar) {
            toReturn[t.letter - 97]++;
        }
        return toReturn;
    }

    //This method takes a valid move in the form of an array of
    // Tiles and executes the move, including calling display updates

    /**
     * Takes a move and plays it and gets a new hand.
     *
     * @param move - move to play
     * @param hand - hand of player
     * @return - the new hand that the player should have after the turn
     */
    private static Tile[] doTurn(Tile[] move, Tile[] hand) {
        display.paintMove(move);

        //display.tilesPlaced.clear();
        //for(Tile t: move){
        //    display.tilesPlaced.add(t);
        //}

        Tile[] newHand = new Tile[7];

        //fills the new hand with either new tiles from the bag or with unused tiles from the player's hand
        for (int i = 0; i < hand.length; i++) {
            boolean triggered = true;
            for (int j = 0; j < move.length; j++) {
                if (move[j].letter == hand[i].letter && !move[j].isFixed) {
                    if (bag.size() >= 1) {
                        int t = (int) (Math.random() * (bag.size() - 1));
                        newHand[i] = bag.get(t);
                        bag.remove(t);
                    }
                    j = move.length;
                    triggered = false;
                }
            }
            if (triggered) {
                newHand[i] = hand[i];
            }
        }


        //executes the move in the board class
        board.doMove(move);
        display.tilesPlaced.clear();
        display.repaint();
        return newHand;
    }
    
    //Fills the bag with letters based on the distribution of the tiles in a normal scrabble bag. Called once at the beginning of the game
    private static void makeBag() {
        //hardcoded distributions that govern the number of tiles in existence 
        int[] distributions = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        //i = letters in the alphabet
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < distributions[i]; j++) {
                bag.add(new Tile((char) (97 + i)));
            }
        }
    }

    /**
     * Determines whether game should continue and what move the game should make
     *
     * @return - the decision
     */
    public static int getGameDecision() {
        if (ComputerPlayer.getNextMove(board, home) == null && ComputerPlayer.getNextMove(board, away) == null) {
            if (bag.size() == 0) {
                return GAME_OVER;
            } else {
                return REDRAW_TILES;
            }
        } else {
            return PLAY;
        }
    }

    /**
     * Redraw tiles from hand
     *
     * @param hand - hand to redo
     */
    public static void redrawTiles(Tile[] hand) {
        for (int i = 0; i < hand.length; i++) {
            bag.add(hand[i]);
        }
        drawTiles(hand);

    }

    /**
     * Draws tile from bag and places them in the hand
     *
     * @param hand - hand of tiles
     */
    public static void drawTiles(Tile[] hand) {
        int temp;
        for (int i = 0; i < 7; i++) {
            temp = (int) (Math.random() * (bag.size() - 1));
            hand[i] = bag.get(temp);
            bag.remove(temp);
        }
    }
}
