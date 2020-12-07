import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem easy, mid, hard;

    public Menu()
    {
        setTitle("Меню");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Set a background for menubutton to have a visible look
        getContentPane().setBackground(Color.darkGray);

        menuBar=new JMenuBar();
        menuBar.setBorderPainted(false);

        menu=new JMenu("Выберите сложность");

        // It's my style!
        menu.setBorderPainted(false);

        easy=new JMenuItem("Легко");
        mid=new JMenuItem("Средне");
        hard=new JMenuItem("Сложно");

        menu.add(easy);
        menu.add(mid);
        menu.add(hard);

        // Add menu to menubar
        menuBar.add(menu);

        // Add(don't set) menubar to frame
        add(menuBar);

        // Make frame maximized for a good look
        //setExtendedState(MAXIMIZED_BOTH);

        // or pack

        pack();

        ActionListener actionListener1 = new Action(0);
        ActionListener actionListener2 = new Action(1);
        ActionListener actionListener3 = new Action(2);
        easy.addActionListener(actionListener1);
        mid.addActionListener(actionListener2);
        hard.addActionListener(actionListener3);
    }
    public static void main(String args[])
    {
        new Menu();
    }
}
