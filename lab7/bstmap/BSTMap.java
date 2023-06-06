package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B{

    /**
        BST Node imply data(key + value) reserved
        Every Node at the right is greater than this one
        Evert Node at the left is less than this one
     */
    private class Node{
        K key;
        V value;
        Node left;
        Node right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    private int size;
    private Node root;

    public BSTMap() {
        this.size = 0;
        this.root = null;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return containsKey((K)key, root);
    }

    private boolean containsKey(K key, Node node) {
        if (node == null) {
            return false;
        }
        if (node.key.equals(key)) {
            return true;
        }
        if (node.key.compareTo(key) > 0) {
            return containsKey(key, node.right);
        } else {
            return containsKey(key, node.left);
        }
    }

    @Override
    public Object get(Object key) {
        return get((K) key, root);
    }

    private V get(K key, Node node) {
        if (node == null) {
            return null;
        }
        if (node.key.equals(key)) {
            return node.value;
        }
        if (node.key.compareTo(key) > 0) {
            return get(key, node.right);
        } else {
            return get(key, node.left);
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(Object key, Object value) {
        if (root == null) {
            root = new Node((K)key, (V)value);
        } else {
            put((K)key, (V)value, root);
        }
        size += 1;
    }

    private Node put(K key, V value, Node node) {
        if (node == null) {
            return new Node(key, value);
        } else {
            if (node.key.compareTo(key) >= 0) {
                node.right = put(key, value, node.right);
            } else {
                node.left = put(key, value, node.left);
            }
        }
        return node;
    }

    /**
        Traverse all Node in-order
     */
    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(Node node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println();
        System.out.println(node);
        printInOrder(node.right);
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException("Not support keySet yet");
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Not support remove yet");
    }

    @Override
    public Object remove(Object key, Object value) {
        throw new UnsupportedOperationException("Not support remove yet");

    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("Not support iterator yet");
    }
}
