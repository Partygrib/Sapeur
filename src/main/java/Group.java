import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group {
        final ArrayList<GameMines.Cell> t;
        int u;

        Group(ArrayList<GameMines.Cell> t, int u) {
            this.t = t;
            this.u = u;
        }

        void subtraction(Group t1) {
            this.t.removeAll(t1.t);
            this.u = this.u - t1.u;
            System.out.println("УДАЛЕНИЕ");
        }

        private Group getOverlap(Group t1) {                        //неиспользуемый метод
            ArrayList<GameMines.Cell> cells = new ArrayList<>();
            for (GameMines.Cell value1 : this.t) {
                if (t1.t.contains(value1)) cells.add(value1);
            }
            int k = t1.t.size() - cells.size();
            int bombs = this.u - k;
            return new Group(cells, bombs);
        }

        List<Double> getProbabilities() {
            ArrayList<Double> list = new ArrayList<>();
            for (GameMines.Cell cell : t) {
                list.add(cell.getPossibility());
            }
            return list;
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
