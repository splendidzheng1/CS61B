package deque;

public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private final int INITIALSIZE = 8;

    /** Return true if Deque is empty */
    public boolean isEmpty(){
        return size==0?true:false;
    }

    /** Creates an empty list. */
    public ArrayDeque() {
        items = (Item[]) new Object[INITIALSIZE];
        size = 0;
        nextFirst = INITIALSIZE - 1;
        nextLast = 0;
    }

    /** Resizes the underlying array to the target items.length. */
    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        // If nextFirst >= nextLast, divide into two part begin with nextFirst
        // else only copy start with nextFirst and end with nextLast
        if(nextFirst >= nextLast){
            System.arraycopy(items, (nextFirst+1)%items.length, a, 0, items.length - nextFirst - 1);
            System.arraycopy(items, 0, a, items.length - nextFirst - 1, size - (items.length - nextFirst - 1));
        }else{
            System.arraycopy(items, (nextFirst+1)%items.length, a, 0, size);
        }
        items = a;
        nextFirst = capacity - 1;
        nextLast = size;
    }
    /** Inserts X into the front of the list. */
    public void addFirst(Item x){
        if (size == items.length) {
            resize(size * 2);
        }

        items[nextFirst] = x;
        size = size + 1;
        nextFirst = (nextFirst - 1) % items.length;
    }

    /** Inserts X into the back of the list. */
    public void addLast(Item x) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[nextLast] = x;
        size = size + 1;
        nextLast = (nextLast + 1) % items.length;
    }
    /** Returns the item from the front of the list. */
    public Item getFirst() {
        return items[(nextFirst + 1)%items.length];
    }

    /** Returns the item from the back of the list. */
    public Item getLast() {
        return items[(nextLast - 1 < 0? nextLast - 1 + items.length : nextLast - 1)%items.length];
    }
    /** Gets the ith item in the list (0 is the front). */
    public Item get(int i) {
        return items[(nextFirst + i) % items.length];
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }
    /** Deletes item from front of the list and
     * returns deleted item. */
    public Item removeFirst() {
        Item x = getFirst();
        if(x == null)   return x;
        items[(nextFirst + 1)%items.length] = null;
        size = size - 1;
        nextFirst = (nextFirst + 1) % items.length;
        if(size <=  items.length / 4 && items.length > 16){
            resize(items.length / 2);
        }
        return x;
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public Item removeLast() {
        Item x = getLast();
        if(x == null)   return x;
        items[(nextLast - 1 < 0? nextLast - 1 + items.length : nextLast - 1)%items.length] = null;
        size = size - 1;
        nextLast = (nextLast - 1 < 0? nextLast - 1 + items.length : nextLast - 1) % items.length;
        if(size <=  items.length / 4 && items.length > 16){
            resize(items.length / 2);
        }
        return x;
    }

    public void printDeque() {
        int i = 0;
        while(i != size){
            System.out.print(items[(nextFirst + 1 + i) % items.length] + " ");
            i += 1;
        }
        System.out.println();
    }
}
