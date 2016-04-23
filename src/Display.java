import javax.swing.*;
import java.awt.*;

/**
 * Created by chris on 4/23/16.
 */
public class Display extends JPanel{
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static int squareSize;


    public Display(){
        super();
        setPreferredSize(screenSize);

        //get square size
    }

    @Override
    public void paintComponent(Graphics g){

    }
}
