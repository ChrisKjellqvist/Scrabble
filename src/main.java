import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by chris on 4/23/16.
 */
public class main {
    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("frame");
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.add(new Display());
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
