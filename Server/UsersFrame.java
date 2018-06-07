package Server;

import javax.swing.*;
import java.awt.*;

public class UsersFrame extends JFrame {
    UsersFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        int width = 650;
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setSize(width, 400);
        setResizable(false);



        setVisible(true);
    }

}
