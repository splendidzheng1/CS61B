package deque;

public class LinkedListDeque <T>{
    private int size;
    private Node sentinel;
    private class Node{
        T item;
        Node next;
        Node previous;

        public Node(){}
        public Node(T item, Node next, Node previous){
            this.item = item;
            this.next = next;
            this.previous = previous;
        }
    }

    public LinkedListDeque(){
        sentinel = new Node();
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
    }

    public void addFirst(T item){
        sentinel.next = new Node(item, sentinel.next, sentinel);
        if(sentinel.previous == sentinel){
            sentinel.previous = sentinel.next;
        }
        size++;
    }
    public void addLast(T item){
        sentinel.previous.next = new Node(item, sentinel, sentinel.previous);
        sentinel.previous = sentinel.previous.next;
        size++;
    }
    public boolean isEmpty(){
        return size==0?true:false;
    }
    public int size(){
        return size;
    }

    public void printDeque(){
        Node n = sentinel.next;
        while(n != sentinel){
            System.out.print(n.item + " ");
            n = n.next;
        }
        System.out.println();
    }
    public T removeFirst(){
        if(sentinel.next == sentinel)   return null;
        T item = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        if(sentinel.next == sentinel)   sentinel.previous = sentinel;
        size--;
        return item;
    }
    public T removeLast(){
        if(sentinel.previous == sentinel)   return null;
        T item = sentinel.previous.item;
        sentinel.previous = sentinel.previous.previous;
        if(sentinel.previous == sentinel)   sentinel.next = sentinel;
        size--;
        return item;
    }
    // iteration ver
    public T get(int index){
        T item = null;
        return item;
    }
    public boolean equals(Object o){
        if(!(o instanceof LinkedListDeque)){
            return false;
        }
        return true;
    }
/*    public Iterator<T> iterator(){
        return null
    }*/
}
