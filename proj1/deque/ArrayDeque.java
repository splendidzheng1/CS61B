package deque;


import java.util.Arrays;
import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private final int INITIALSIZE = 8;

    /** Creates an empty list. */
    public ArrayDeque() {
        items = (T[]) new Object[INITIALSIZE];
        size = 0;
        nextFirst = INITIALSIZE - 1;
        nextLast = 0;
    }

    /** Resizes the underlying array to the target items.length. */
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int getFirst = (nextFirst + 1) % items.length;
        int getLast = (nextLast + items.length - 1) % items.length;
        // If getFirst > getLast, divide into front and back part
        // else only copy start with nextFirst and end with nextLast
        if (getFirst > getLast) {
            int frontLength = items.length - getFirst;
            System.arraycopy(items, getFirst, newItems, 0, frontLength);
            System.arraycopy(items, 0, newItems, frontLength, size - frontLength);
        } else {
            System.arraycopy(items, getFirst, newItems, 0, size);
        }
        items = newItems;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    /** Inserts X into the front of the list. */
    public void addFirst(T x) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[nextFirst] = x;
        size += 1;
        nextFirst = (nextFirst + items.length - 1) % items.length;
    }

    /** Inserts X into the back of the list. */
    public void addLast(T x) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[nextLast] = x;
        size += 1;
        nextLast = (nextLast + 1) % items.length;
    }

    /** Gets the ith item in the list (0 is the front). */
    public T get(int i) {
        return items[(nextFirst + i + 1) % items.length];
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }

    /** Deletes item from front of the list and
     * returns deleted item. */
    public T removeFirst() {
        T x = get(0);
        if (x == null) {
            return null;
        }
        int getFirst = (nextFirst + 1) % items.length;
        items[getFirst] = null;
        size = size - 1;
        nextFirst = getFirst;
        if (size <=  items.length / 4 && items.length > 2 * INITIALSIZE) {
            resize(items.length / 2);
        }
        return x;
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public T removeLast() {
        int getLast = (nextLast + items.length - 1) % items.length;
        T x = get(size - 1);
        if (x == null) {
            return x;
        }
        items[getLast] = null;
        size = size - 1;
        nextLast = getLast;
        if (size <=  items.length / 4 && items.length > 2 * INITIALSIZE) {
            resize(items.length / 2);
        }
        return x;
    }

    public void printDeque() {
        int i = 0;
        while (i != size) {
            System.out.print(items[(nextFirst + 1 + i) % items.length] + " ");
            i += 1;
        }
        System.out.println();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        ArrayDeque<T> other = (ArrayDeque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private int wizPos;
        public DequeIterator() {
            wizPos = 0;
        }
        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T returnT = get(wizPos);
            wizPos += 1;
            return returnT;
        }
    }

    @Override
    public String toString() {
        return "ArrayDeque{" + "items=" + Arrays.toString(items)
                + ", size=" + size + '}';
    }
}
