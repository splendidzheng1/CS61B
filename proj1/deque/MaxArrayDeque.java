package deque;


import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    public MaxArrayDeque(Comparator<T> c) {
        cP = c;
    }

    public T max() {
        return max(null);
    }

    public T max(Comparator<T> c) {
        int maxPos = 0;
        if (c == null) {
            c = cP;
        }
        for (int i = 0; i < size(); i++) {
            int compare = c.compare(get(maxPos), get(i));
            if (compare < 0) {
                maxPos = i;
            }
        }
        return get(maxPos);
    }

    private Comparator<T> cP;
}
