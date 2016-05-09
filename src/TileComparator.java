import java.util.Comparator;

/**
 * Created by chris and seth on 4/26/16.
 */
public class TileComparator<T> implements Comparator {
    @Override
    public final int compare(Object o1, Object o2) {
        Tile t1 = (Tile) o1;
        Tile t2 = (Tile) o2;
        if (t1.coords[0] == t2.coords[0]) {
            if (t1.coords[1] < t2.coords[1]) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (t1.coords[0] < t2.coords[0]) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
