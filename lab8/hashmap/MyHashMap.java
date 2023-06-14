package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private final int DEFAULTSIZE = 16;
    private int initialSize = DEFAULTSIZE;
    private double loadFactor = 0.75;
    private int size;
    private HashSet<K> keySet;
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */

    /** Constructors */
    public MyHashMap() {
        size = 0;
        buckets = createTable(initialSize);
        keySet = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        size = 0;
        buckets = createTable(initialSize);
        keySet = new HashSet<>();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        size = 0;
        buckets = createTable(initialSize);
        keySet = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] res = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            res[i] = createBucket();
        }
        return res;
    }

    // Your code won't compile until you do so!
    @Override
    public void clear() {
        initialSize = DEFAULTSIZE;
        buckets = createTable(initialSize);
        keySet = new HashSet<>();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    @Override
    public V get(K key) {
        int pos = Math.floorMod(key.hashCode(), initialSize);
        for (Node item : buckets[pos]) {
            if (item.key.equals(key)) {
                return item.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if ((double) size / initialSize >= loadFactor) {
            Collection<Node>[] newTable = createTable(initialSize * 2);
            initialSize *= 2;
            for (Collection<Node> collection : buckets) {
                for (Node item : collection) {
                    if (item.key == null) {
                        continue;
                    }
                    int pos = Math.floorMod(item.key.hashCode(), initialSize);
                    newTable[pos].add(item);
                }
            }
            buckets = newTable;
        }
        int pos = Math.floorMod(key.hashCode(), initialSize);
        if (!containsKey(key)) {
            buckets[pos].add(createNode(key, value));
            keySet.add(key);
            size += 1;
        } else {
            for (Node item : buckets[pos]) {
                if (item.key.equals(key)) {
                    item.value = value;
                }
            }
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        V value = null;
        int pos = Math.floorMod(key.hashCode(), initialSize);
        for (Node item : buckets[pos]) {
            if (item.key.equals(key)) {
                item.key = null;
                value = item.value;
            }
        }
        keySet.remove(key);
        return value;
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key)) {
            return null;
        }
        int pos = Math.floorMod(key.hashCode(), initialSize);
        for (Node item : buckets[pos]) {
            if (item.key.equals(key) && item.value.equals(value)) {
                item.key = null;
                keySet.remove(key);
                return value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new HSMapIter();
    }

    /** An iterator that iterates over the keys of the dictionary. */
    private class HSMapIter implements Iterator<K> {

        /**
         * Create a new HSMapIter by setting cur to the first node in the
         * linked list that stores the key-value pairs.
         */
        HSMapIter() {
            cur = keySet.iterator();
        }

        @Override
        public boolean hasNext() {
            return cur.hasNext();
        }

        @Override
        public K next() {
            return cur.next();
        }


        /** Stores the current key-value pair. */
        private Iterator<K> cur;

    }
}
