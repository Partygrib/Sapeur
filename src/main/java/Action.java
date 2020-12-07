import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Action implements ActionListener {

    int mod;

    Action(int mod) {
        this.mod = mod;
    }

    public void actionPerformed(ActionEvent e) {
        final String TITLE_OF_PROGRAM = "Mines";
        final String SIGN_OF_FLAG = "f";
        String[] seasons  = new String[2];
        seasons[0] = TITLE_OF_PROGRAM;
        seasons[1] = SIGN_OF_FLAG;
        GameMines.changeMod(mod);
        GameMines.main(seasons);
    }
}