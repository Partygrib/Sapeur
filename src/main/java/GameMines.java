import java.util.List;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.Timer;

class GameMines extends JFrame {

    final ArrayList<Group> groups = new ArrayList<>();
    final String TITLE_OF_PROGRAM = "Mines";
    final String SIGN_OF_FLAG = "F";
    final int BLOCK_SIZE = 30;
    static int FIELD_SIZE = 9;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28 + 17;
    final int START_LOCATION = 200;
    final int MOUSE_BUTTON_LEFT = 1;
    final int MOUSE_BUTTON_RIGHT = 3;
    static int NUMBER_OF_MINES = 10;
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0, 0x0, 0x0, 0x0};

    public static void changeMod(int mod) { //сложность
        if (mod == 0) { //легко
            FIELD_SIZE = 9;
            NUMBER_OF_MINES = 10;
            CURRENT_MINES = NUMBER_OF_MINES;
        } if (mod == 1) { //средне
            FIELD_SIZE = 16;
            NUMBER_OF_MINES = 40;
            CURRENT_MINES = NUMBER_OF_MINES;
        } if (mod == 2) { //сложно
            FIELD_SIZE = 22;
            NUMBER_OF_MINES = 99;
            CURRENT_MINES = NUMBER_OF_MINES;
        }
    }
    static int CURRENT_MINES = NUMBER_OF_MINES;
    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];
    Random random = new Random();
    int countOpenedCells;
    boolean youWon, bangMine; //проверка
    int bangX, bangY;

    public static void main(String[] args) {
            new GameMines();
    }

    private GameMines() {
            JButton button2 = new JButton("Button 2");
            button2.setActionCommand("Button 2 was pressed!");
            setTitle(TITLE_OF_PROGRAM);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX, FIELD_SIZE * BLOCK_SIZE + FIELD_DY);
            setResizable(false);
            final TimerLabel timeLabel = new TimerLabel();
            timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            final Canvas canvas = new Canvas();
            canvas.setBackground(Color.white);
            canvas.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    int x = e.getX()/BLOCK_SIZE;
                    int y = e.getY()/BLOCK_SIZE;
                    if (!bangMine && !youWon) {
                        if (e.getButton() == MOUSE_BUTTON_LEFT) // left button mouse
                            if (field[y][x].isNotOpen()) {
                                System.out.println(field.length);
                                openCells(x, y);
                                youWon = countOpenedCells == FIELD_SIZE*FIELD_SIZE - NUMBER_OF_MINES; // winning check
                                if (bangMine) {
                                    bangX = x;
                                    bangY = y;
                                }
                            }
                        if (e.getButton() == MOUSE_BUTTON_RIGHT) {
                            int k = 0;
                            for (int x1 = 0; x1 < FIELD_SIZE; x1++) {
                                for (int y1 = 0; y1 < FIELD_SIZE; y1++) {
                                    if (field[y1][x1].isFlag || !field[y1][x1].isNotOpen()) k++;
                                }
                            }
                            setGroups();
                            for (int x1 = 0; x1 < FIELD_SIZE; x1++) {
                                for (int y1 = 0; y1 < FIELD_SIZE; y1++) {
                                    if (field[y1][x1].isFlag || !field[y1][x1].isNotOpen()) k--;
                                }
                            }
                            if (k == 0) openUp();
                            youWon = countOpenedCells == FIELD_SIZE*FIELD_SIZE - NUMBER_OF_MINES;
                            canvas.repaint();
                        }
                        if (bangMine || youWon || CURRENT_MINES == 0) {
                            timeLabel.stopTimer();
                            if (bangMine) JOptionPane.showMessageDialog(canvas,
                                    "ВЗРЫВ!!! ВЫ ПРОИГРАЛИ(");
                            else JOptionPane.showMessageDialog(canvas,
                                    "ПОБЕДА!!! ВЫ ОБЕЗВРЕДИЛИ ВСЕ БОМБЫ)");
                            setVisible(false);
                            dispose();
                        }
                        canvas.repaint();
                    }
                }
            });
            add(BorderLayout.CENTER, canvas);
            add(BorderLayout.SOUTH, timeLabel);
            setVisible(true);
            initField();
        }

        void openCells(int x, int y) { // рекурсивное открытие ячеек
            if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return; // неверные координаты
            if (!field[y][x].isNotOpen()) return; // ячейка уже открыта
            field[y][x].open();
            if (field[y][x].getCountBomb() > 0 || bangMine) return; // ячейка не пуста
            for (int dx = -1; dx < 2; dx++)
                for (int dy = -1; dy < 2; dy++) openCells(x + dx, y + dy);
        }

        void initField() { // создание поля
            int x, y, countMines = 0;
            for (x = 0; x < FIELD_SIZE; x++)
                for (y = 0; y < FIELD_SIZE; y++)
                    field[y][x] = new Cell(x, y);   // создание ячеек поля
            while (countMines < NUMBER_OF_MINES) {  // создание мин
                do {
                    x = random.nextInt(FIELD_SIZE);
                    y = random.nextInt(FIELD_SIZE);
                } while (field[y][x].isMined());
                field[y][x].mine();
                countMines++;
            }
            for (x = 0; x < FIELD_SIZE; x++)        // считаем количество мин в зоне
                for (y = 0; y < FIELD_SIZE; y++)
                    if (!field[y][x].isMined()) {
                        int count = 0;
                        for (int dx = -1; dx < 2; dx++)
                            for (int dy = -1; dy < 2; dy++) {
                                int nX = x + dx;
                                int nY = y + dy;
                                if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                    nX = x;
                                    nY = y;
                                }
                                count += (field[nY][nX].isMined()) ? 1 : 0;
                            }
                        field[y][x].setCountBomb(count);
                    }
        }

    class Cell {
        private int countBombNear;
        private boolean isOpen, isMine, isFlag;
        private final int x;
        private final int y;
        private double pos = 1.0;

        void open() {
            isOpen = true;
            bangMine = isMine;
            if (!isMine) countOpenedCells++;
        }

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void setPossibility(double p) { pos = p; }

        double getPossibility() { return pos; }

        void mine() { isMine = true; }

        void setCountBomb(int count) { countBombNear = count; }

        int getCountBomb() { return countBombNear; }

        boolean isNotOpen() { return !isOpen; }

        boolean isMined() { return isMine; }

        void inverseFlag() { isFlag = !isFlag; }

        void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x*BLOCK_SIZE + 7, y*BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x*BLOCK_SIZE + 9, y*BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 10, 4, 4);
        }

        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x*BLOCK_SIZE + 8, y*BLOCK_SIZE + 26);
        }

        void paint(Graphics g, int x, int y) {
            g.setColor(Color.lightGray);
            g.drawRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            if (!isOpen) {
                if ((bangMine || youWon) && isMine) paintBomb(g, x, y, Color.black);
                else {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g, SIGN_OF_FLAG, x, y, Color.red);
                }
            } else
            if (isMine) paintBomb(g, x, y, bangMine? Color.red : Color.black);
            else if (countBombNear > 0)
                paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return isOpen == cell.isOpen &&
                    isFlag == cell.isFlag &&
                    x == cell.x &&
                    y == cell.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(isOpen, isFlag, x, y);
        }
    }

        static class TimerLabel extends JLabel {
            Timer timer = new Timer();

            TimerLabel() { timer.scheduleAtFixedRate(timerTask, 0, 1000); }

            TimerTask timerTask = new TimerTask() {
                volatile int time;
                final Runnable refresher = new Runnable() {
                    public void run() {
                        TimerLabel.this.setText(String.format("%02d:%02d        Всего мин: %02d", time / 60, time % 60, CURRENT_MINES));
                    }
                };
                public void run() {
                    time++;
                    SwingUtilities.invokeLater(refresher);
                }
            };

            void stopTimer() { timer.cancel(); }
        }

        class Canvas extends JPanel {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                for (int x = 0; x < FIELD_SIZE; x++)
                    for (int y = 0; y < FIELD_SIZE; y++) {
                        field[y][x].paint(g, x, y);
                    }
            }
        }

    private Group setGroup(int x, int y) {
        Cell cell = field[y][x];
        ArrayList<Cell> cells = new ArrayList<>();
        if (y > 0 && x > 0 && field[y - 1][x - 1].isNotOpen()) cells.add(field[y - 1][x - 1]);
        if (y > 0 && field[y - 1][x].isNotOpen()) cells.add(field[y - 1][x]);
        if (y > 0 && x < field.length - 1 && field[y - 1][x + 1].isNotOpen())
            cells.add(field[y - 1][x + 1]);
        if (x > 0 && field[y][x - 1].isNotOpen()) cells.add(field[y][x - 1]);
        if (x < field.length - 1 && field[y][x + 1].isNotOpen())
            cells.add(field[y][x + 1]);
        if (y < field.length - 1 && x > 0 && field[y + 1][x - 1].isNotOpen())
            cells.add(field[y + 1][x - 1]);
        if (y < field.length - 1 && field[y + 1][x].isNotOpen())
            cells.add(field[y + 1][x]);
        if (y < field.length - 1 && x < field.length - 1
                && field[y + 1][x + 1].isNotOpen()) cells.add(field[y + 1][x + 1]);
        return new Group(cells, cell.getCountBomb());
    }

    private boolean overlaps(ArrayList<Cell> t1, ArrayList<Cell> t2) {     //неиспользуемый метод
        for (Cell value1 : t1) {
            if (t2.contains(value1)) return true;
        }
        return false;
    }

    private void setGroups() {
        groups.clear();
        for (int x = 0; x < FIELD_SIZE; x++) {
            for (int y = 0; y < FIELD_SIZE; y++) {
                if (!field[y][x].isNotOpen() && field[y][x].getCountBomb() > 0) {           // создание групп
                    groups.add(setGroup(x, y));
                }
            }
        }
        boolean repeat;
        do {
            repeat = false;
            for (int i = 0; i < groups.size() - 1; i++) {
                Group groupI = groups.get(i);
                for (int j = i + 1; j < groups.size(); j++) {   // сравниваем ее с остальными меньшими группами
                    Group groupJ = groups.get(j);
                    if (groupI.equals(groupJ)) {                // удаляем одинаковые группы
                        groups.remove(j--);
                        break;
                    }
                    Group parent;                               // большая группа
                    Group child;                                // меньшая группа
                    if (groupI.t.size() > groupJ.t.size())            // определяем большую и меньшую группы по кол-ву ячеек
                    {
                        parent = groupI;
                        child = groupJ;
                    } else {
                        child = groupI;
                        parent = groupJ;
                    }
                    if (parent.t.containsAll(child.t)) {               // если большая содержит меньшую, то вычитаем меньшую из большей
                        parent.subtraction(child);
                        repeat = true;                                 //  фиксируем факт изменения групп
                    }
                }
            }
        } while (repeat);
        split();
    }

    private void correctPosibilities(){
        for (Group group : groups) { // цикл устанавливает единое значение вероятности в каждой ячейке, учитывая различные значения вероятностей в ячейке от разных групп
            for (Cell cell: group.t) {
                if (cell.getPossibility() == 1.0) {
                    cell.setPossibility((double) group.u / group.t.size());
                }
                else {
                    double dd = 1 - (1 - cell.getPossibility())*(1 - ((double) group.u / group.t.size()));
                    cell.setPossibility(dd);
                }
            }
        }
        boolean repeat; // цикл корректирует значения с учетом того, что сумма вероятностей в группе должна быть равна количеству мин в группе
        do{
            repeat=false;
            for (Group group : groups){                      // для каждой группы
                List<Double> prob = group.getProbabilities(); //  берем список вероятностей всех ячеек в группе
                Double sum = 0.0;
                for (Double elem : prob)sum += elem;             //  вычисляем ее сумму
                double mines= group.u;
                if (Math.abs(sum-mines) > 1.0){                  //  если разница между ними велика, то проводим корректировку
                    repeat = true;                             //   фиксируем факт корректировки
                    prob = correct(prob,mines, sum);                //   корректируем список
                    for (int i = 0;i < group.t.size();i++){       //   заносим откорректированные значения из списка в ячейки
                        double value = prob.get(i);
                        group.t.get(i).setPossibility(value);
                    }
                }
            }
        }
        while (repeat);
        for (Group group : groups) {
            for (Cell cell : group.t) {
                if (cell.getPossibility() > 0.99) cell.setPossibility(0.99);
                if (cell.getPossibility() < 0) cell.setPossibility(0);
            }
        }
    }

    List<Double> correct(List<Double> prob, double mines, double sum) {
        List<Double> list = new ArrayList<>();
        double s = mines / sum;
        for (Double element : prob) {
            list.add(element * s);
        }
        return list;
    }

    private void openUp() {
        correctPosibilities();
        int k = (int) ( Math.random() * (FIELD_SIZE-1) ) + 1;
        Cell cell1 = field[k][k];
        for (Group group : groups) {
            for (Cell cell: group.t) {
                if (cell.isNotOpen() && !cell.isFlag &&
                        cell.getPossibility() < cell1.getPossibility())
                    cell1 = cell;
            }
        }
        System.out.println("ОТКРЫЛИ " + cell1.getPossibility());
        openCells(cell1.x, cell1.y);
    }

    private void split() {                        //разделяем группы, открываем и помечаем ячейки
        for (Group value : groups) {
            if (value.u == 0) {
                for (Cell cell : value.t) {
                    openCells(cell.x, cell.y);
                }
            }
            else {
                if (value.u == value.t.size()) {
                    for (Cell cell : value.t) {
                        if (!cell.isFlag) {
                            cell.inverseFlag();
                            CURRENT_MINES--;
                        }
                    }
                }
            }
        }
    }
}
