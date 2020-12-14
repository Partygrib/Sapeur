import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    JMenuBar menuBar;
    static JMenu menu;
    JMenuItem easy, mid, hard;

    private Menu()
    {
        setTitle("Меню");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        getContentPane().setBackground(Color.darkGray);
        menuBar=new JMenuBar();
        menuBar.setBorderPainted(false);
        menu=new JMenu("Выберите сложность");
        menu.setBorderPainted(false);
        easy=new JMenuItem("Легко");
        mid=new JMenuItem("Средне");
        hard=new JMenuItem("Сложно");
        menu.add(easy);
        menu.add(mid);
        menu.add(hard);
        menuBar.add(menu);
        add(menuBar);

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
