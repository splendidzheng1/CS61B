package deque;

public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private int capacity;
    private final int INITIALSIZE = 8;
    /** Creates an empty list. */
    public ArrayDeque() {
        items = (Item[]) new Object[INITIALSIZE];
        size = 0;
        nextFirst = INITIALSIZE - 1;
        nextLast = 0;
        capacity = INITIALSIZE;
    }

    /** Resizes the underlying array to the target capacity. */
    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
        nextFirst = capacity - 1;
        nextLast = size - 1;
        this.capacity = capacity;
    }
    /** Inserts X into the front of the list. */
    public void addFirst(Item x){
        if (size == items.length) {
            resize(size * 2);
        }

        items[nextFirst] = x;
        size = size + 1;
        nextFirst = (nextFirst - 1) % capacity;
    }

    /** Inserts X into the back of the list. */
    public void addLast(Item x) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[nextLast] = x;
        size = size + 1;
        nextLast = (nextLast + 1) % capacity;
    }
    /** Returns the item from the front of the list. */
    public Item getFirst() {
        return items[(nextFirst + 1)%capacity];
    }

    /** Returns the item from the back of the list. */
    public Item getLast() {
        return items[(nextLast - 1)%capacity];
    }
    /** Gets the ith item in the list (0 is the front). */
    public Item get(int i) {
        return items[i];
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }
    /** Deletes item from front of the list and
     * returns deleted item. */
    public Item removeFirst() {
        Item x = getFirst();
        items[(nextFirst + 1)%capacity] = null;
        size = size - 1;
        nextFirst = (nextFirst + 1) % capacity;
        if(size <=  capacity / 4){
            resize(capacity / 2);
        }
        return x;
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public Item removeLast() {
        Item x = getLast();
        items[(nextLast - 1)%capacity] = null;
        size = size - 1;
        nextLast = (nextLast - 1) % capacity;
        if(size <=  capacity / 4){
            resize(capacity / 2);
        }
        return x;
    }
}
