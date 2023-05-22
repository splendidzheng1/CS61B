package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    private int size;
    private Node sentinel;
    private class Node {
        T item;
        Node next;
        Node previous;

        Node() { }
        Node(T item, Node next, Node previous) {
            this.item = item;
            this.next = next;
            this.previous = previous;
        }
    }

    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
    }

    public void addFirst(T item) {
        Node oldFirst = sentinel.next;
        sentinel.next = new Node(item, oldFirst, sentinel);
        if (sentinel.previous == sentinel) {
            sentinel.previous = sentinel.next;
        } else {
            oldFirst.previous = sentinel.next;
        }
        size++;
    }
    public void addLast(T item) {
        Node oldLast = sentinel.previous;
        sentinel.previous = new Node(item, sentinel, oldLast);
        if (sentinel.next == sentinel) {
            sentinel.next = sentinel.previous;
        } else {
            oldLast.next = sentinel.previous;
        }
        size++;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node n = sentinel.next;
        while (n != sentinel) {
            System.out.print(n.item + " ");
            n = n.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (sentinel.next == sentinel) {
            return null;
        }
        T item = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        if (sentinel.next == sentinel) {
            sentinel.previous = sentinel;
        }
        size--;
        return item;
    }

    public T removeLast() {
        if (sentinel.previous == sentinel) {
            return null;
        }
        T item = sentinel.previous.item;
        sentinel.previous = sentinel.previous.previous;
        if (sentinel.previous == sentinel) {
            sentinel.next = sentinel;
        }
        size--;
        return item;
    }

    // iteration ver
    public T get(int index) {
        Node s = sentinel.next;
        int i = 0;
        while (s != null && i != index) {
            s = s.next;
            i += 1;
        }
        return s == null ? null : s.item;
    }

    public T getRecursive(int index) {
        return getRecursive(index, sentinel.next);
    }

    public T getRecursive(int index, Node n) {
        if (n == null) {
            return null;
        }
        if (index == 0) {
            return n.item;
        }
        return getRecursive(index - 1, n.next);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
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
        DequeIterator() {
            wizPos = 0;
        }
        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }
}
