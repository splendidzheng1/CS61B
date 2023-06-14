package bstmap;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    /**
        BST Node imply data(key + value) reserved
        Every Node at the right is greater than this one
        Evert Node at the left is less than this one
     */
    private class Node {
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
    public boolean containsKey(K key) {
        return containsKey(key, root);
    }

    private boolean containsKey(K key, Node node) {
        if (node == null) {
            return false;
        }
        if (node.key.equals(key)) {
            return true;
        }
        if (node.key.compareTo(key) < 0) {
            return containsKey(key, node.right);
        } else {
            return containsKey(key, node.left);
        }
    }

    @Override
    public V get(K key) {
        return get(key, root);
    }

    private V get(K key, Node node) {
        if (node == null) {
            return null;
        }
        if (node.key.equals(key)) {
            return node.value;
        }
        if (node.key.compareTo(key) < 0) {
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
    public void put(K key, V value) {
        if (root == null) {
            root = new Node(key, value);
        } else {
            put(key, value, root);
        }
        size += 1;
    }

    private Node put(K key, V value, Node node) {
        if (node == null) {
            return new Node(key, value);
        } else {
            if (node.key.compareTo(key) < 0) {
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
    public Set<K> keySet() {
        return keySet(root);
    }

    private Set<K> keySet(Node node) {
        Set<K> res = new TreeSet<>();
        keySet(res, node);
        return res;
    }

    private void keySet(Set<K> res, Node node) {
        if (node == null) {
            return;
        }
        keySet(res, node.left);
        res.add(node.key);
        keySet(res, node.right);
    }

    private void substitudeWithRightLeftMostNode(Node node, Node parent) {
        Node pre = node;
        //  Find right-left most node
        if (node.right == null) {
            if (node.left == null) {
                if (node == root) {
                    root = null;
                } else if (parent.left == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
            } else {
                node.key = node.left.key;
                node.value = node.left.value;
                node.right = node.left.right;
                node.left = node.left.left;
            }
        } else {
            Node cur = node.right;
            while (cur.left != null) {
                pre = cur;
                cur = cur.left;
            }
            if (pre == node) {
                cur.left = node.left;
                node.key = cur.key;
                node.value = cur.value;
            } else {
                cur.left = node.left;
                cur.right  = node.right;
                node.key = cur.key;
                node.value = cur.value;
                pre.left = null;
            }
        }
    }

    @Override
    public V remove(K key) {
        Node node = root;
        Node pre = null;
        while (node != null) {
            int cmp = node.key.compareTo(key);
            if (cmp == 0) {
                V res = node.value;
                // substitute this node with its successor
                substitudeWithRightLeftMostNode(node, pre);
                size -= 1;
                return res;
            } else if (cmp > 0) {
                pre = node;
                node = node.left;
            } else {
                pre = node;
                node = node.right;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Not support remove yet");

    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Not support iterator yet");
    }
}
