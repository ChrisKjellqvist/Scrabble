import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chris on 4/23/16.
 */
public class main {

    static Display display;
    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("frame");
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        display = new Display();
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
                if(e.getX()>30 && e.getX()<30+Display.squareSize) {
                    Display.beingHeld = e.getY() / Display.squareSize - 1;
                    time.start();

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Display.beingHeld = -1;
                time.stop();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        ArrayList<Tile> bag = new ArrayList<>();
        int[] distributions = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < distributions[i]; j++) {
                bag.add(new Tile((char)(97+i)));
            }
        }

        Tile[] home = new Tile[7];
        Tile[] away = new Tile[7]; //This is the Computer player

        int temp;
        for (int i = 0; i < 7; i++) {
            temp = (int)(Math.random()*(bag.size()-1));
            System.out.println(temp);
            home[i] = bag.get(temp);
            bag.remove(temp);

            temp = (int)(Math.random()*(bag.size()-1));
            away[i] = bag.get(temp);
            bag.remove(temp);

        }

        Display.handToDisplay = home;

        frame.setVisible(true);

    }

}
