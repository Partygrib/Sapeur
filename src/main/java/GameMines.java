import java.util.List;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.Timer;

class GameMines extends JFrame {

    ArrayList<Group> groups = new ArrayList<Group>();

    ArrayList<Group> groups1 = new ArrayList<Group>();

    ArrayList<Group> groups2 = new ArrayList<Group>();

    ArrayList<Group> groups3 = new ArrayList<Group>();

    final String TITLE_OF_PROGRAM = "Mines";
    final String SIGN_OF_FLAG = "F";
    final int BLOCK_SIZE = 30;
    static int FIELD_SIZE = 9;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28 + 17;
    final int START_LOCATION = 200;
    final int MOUSE_BUTTON_LEFT = 1; // for mouse listener
    final int MOUSE_BUTTON_RIGHT = 3;
    static int NUMBER_OF_MINES = 10;//сложность0
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0};

    public static void changeMod(int mod) {
        if (mod == 0) {
            FIELD_SIZE = 9;
            NUMBER_OF_MINES = 10;
        } if (mod == 1) {
            FIELD_SIZE = 16;
            NUMBER_OF_MINES = 20;
        } if (mod == 2) {
            FIELD_SIZE = 26;
            NUMBER_OF_MINES = 60;
        }
    }

    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];
    Random random = new Random();
    int countOpenedCells;
    boolean youWon, bangMine; // flags for win and bang/fail
    int bangX, bangY; // for fix the coordinates of the explosion

    public static void main(String[] args) {
            new GameMines();
    }

    GameMines() {
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
                            setGroups();
                        }
                        if (bangMine || youWon) timeLabel.stopTimer(); // game over
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
            // создание ячеек поля
            for (x = 0; x < FIELD_SIZE; x++)
                for (y = 0; y < FIELD_SIZE; y++)
                    field[y][x] = new Cell(x, y);
            // создание мин
            while (countMines < NUMBER_OF_MINES) {
                do {
                    x = random.nextInt(FIELD_SIZE);
                    y = random.nextInt(FIELD_SIZE);
                } while (field[y][x].isMined());
                field[y][x].mine();
                countMines++;
            }
            // считаем количество мин в зоне
            for (x = 0; x < FIELD_SIZE; x++)
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
            public int x;
            public int y;

            void open() {
                isOpen = true;
                bangMine = isMine;
                if (!isMine) countOpenedCells++;
            }

            Cell(int x, int y) {
                this.x = x;
                this.y = y;
            }

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
                else
                if (countBombNear > 0)
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
                        TimerLabel.this.setText(String.format("%02d:%02d        Всего мин: %02d", time / 60, time % 60, NUMBER_OF_MINES));
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

        class Group {
            private final ArrayList<Cell> t;
            private int u;

            Group(ArrayList<Cell> t, int u) {
                this.t = t;
                this.u = u;
            }

            private void subtraction(Group t1) {
                this.t.retainAll(t1.t);
                this.u =- t1.u;
                System.out.println("УДАЛЕНИЕ");
            }

            private Group getOverlap(Group t1) {
                ArrayList<Cell> cells = new ArrayList<Cell>();
                int k;
                for (Cell value1 : t1.t) {
                    if (this.t.contains(value1)) cells.add(value1);
                }
                k = this.t.size() - cells.size();
                int bombs = t1.u - k;
                return new Group(cells, bombs);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Group group = (Group) o;
                return Objects.equals(t, group.t);
            }

            @Override
            public int hashCode() {
                return Objects.hash(t);
            }
        }

        private Group setGroup(int x, int y) {
            Cell cell = field[y][x];
            ArrayList<Cell> cells = new ArrayList<Cell>();
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

        private boolean overlaps(ArrayList<Cell> t1, ArrayList<Cell> t2) {
        for (Cell value1 : t1) {
            if (t2.contains(value1)) return true;
        }
        return false;
        }

        private void setGroups() {
            groups.clear();
            for (int x = 0; x < FIELD_SIZE; x++) {
                for (int y = 0; y < FIELD_SIZE; y++) {
                    if (!field[y][x].isNotOpen() && field[y][x].getCountBomb() > 0) {
                        Group group1 = setGroup(x, y);                   // создание групп
                        groups.add(group1);
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
                            System.out.println("ПОВТОРНАЯ");
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
                            System.out.println("1");
                            repeat = true;                                 //  фиксируем факт изменения групп
                        } else if (overlaps(groupI.t, groupJ.t)) {    // иначе если группы пересекаются
                            if (groupI.u > groupJ.u)                  // определяем большую и меньшую группы по кол-ву мин
                            {
                                parent = groupI;
                                child = groupJ;
                            } else {
                                child = groupI;
                                parent = groupJ;
                            }
                            Group overlap = parent.getOverlap(child);   // то берем результат пересечения
                            if (overlap.t.size() != 0) {                  //  и если он имеет смысл (в результате пересечения выявились ячейки с 0% или 100%)
                                System.out.println("2");
                                groups.add(overlap);
                                parent.subtraction(overlap);
                                child.subtraction(overlap);              //  то вносим соответствующие коррективы в список
                                repeat = true;
                            }
                        }
                        System.out.println("3");
                    }
                    System.out.println("4");
                }
                System.out.println("5");
            } while (repeat);
            split();
            System.out.println("6");
        }

    private void split() {                        //разделяем группы, открываем и помечаем ячейки
        for (Group value : groups) {
            if (value.u == 0) {
                for (Cell cell : value.t) {
                    openCells(cell.x, cell.y);
                    System.out.println("ОТКРЫТИЕ");
                }
            }
            else {
                if (value.u == value.t.size()) {
                    for (Cell cell : value.t) {
                        cell.inverseFlag();
                        System.out.println("ПОМЕЧАЕМ");
                    }
                }
                else {
                    System.out.println(value);
                    groups3.add(value);
                }
            }
        }
    }
}
