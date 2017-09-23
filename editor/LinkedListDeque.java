package editor;
import java.util.ArrayList;
import javafx.scene.text.Text;

public class LinkedListDeque <Item> {

    class Node {
        public Node prev;
        public Item item;
        public Node next;

        public Node(Node p, Item i, Node n){
            prev = p;
            item = i;
            next = n;
        }
    }

    private int dequesize;
    Node sentinel;
    int currentPos;
    Node currentNode;
    ArrayList <Node> linetracker;
    Node bookmark;
    ArrayList <Node> word;


    public LinkedListDeque() {
        dequesize = 0;
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        currentPos = 0;
        currentNode = sentinel;
        linetracker = new ArrayList<>();
        bookmark = new Node(null, null, null);
    }

    public int lastlineY() {
        if (linetracker.size() == 0) {
            return 0;
        }
        else {
            return (int) ((Text) linetracker.get(linetracker.size()-1).item).getY() + (int) ((Text) linetracker.get(linetracker.size()-1).item).getLayoutBounds().getHeight();
        }
    }

    public void addFirst(Item x) {
        Node old;
        if (sentinel.next == null){
            old = sentinel;
        } else{
            old = sentinel.next;
        }
        Node inserted = new Node(sentinel, x, old);
        sentinel.next = inserted;
        old.prev = inserted;
        dequesize++;
        currentPos = 1;
        currentNode = inserted;
    }

    public void addLast(Item x) {
        Node old = sentinel.prev;
        Node inserted  = new Node(old, x, sentinel);
        sentinel.prev = inserted;
        old.next = inserted;
        dequesize++;
        currentPos++;
        currentNode = inserted;
    }

    public void add(Item x) {
        if (currentPos + 1 == dequesize) {
            addLast(x);
        }
        else if (currentPos == 0) {
            addFirst(x);
        } else{
            Node old = currentNode;
            Node oldNext = old.next;
            Node inserted = new Node(old, x, oldNext);
            oldNext.prev = inserted;
            old.next = inserted;
            dequesize++;
            currentPos++;
            currentNode = inserted;
        }
    }


    public int size() {
        return dequesize;
    }

    public void printDeque() {
        Node pointer = sentinel.next;
        while (pointer != sentinel) {
            System.out.print(pointer.item);
            pointer = pointer.next;
        }
    }

    public Item removeFirst() {
        if (sentinel.next == sentinel){
            return null;
        }
        Node removed = sentinel.next;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        dequesize--;
        currentNode = sentinel.next;
        currentPos = 0;
        return removed.item;
    }

    public Item removeLast() {
        if (sentinel.prev == sentinel){
            return null;
        }
        Node removed = sentinel.prev;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        dequesize--;
        currentPos--;
        currentNode = sentinel.prev;
        return removed.item;
    }

    public Item delete() {
        if (currentPos + 1 == dequesize) {
            return removeLast();
        }
        else if (currentPos == 0) {
            return removeFirst();
        } else {
            Node old = currentNode;
            Node oldNext = old.next;
            Node oldPrev = old.prev;
            oldPrev.next = oldNext;
            oldNext.prev = oldPrev;
            dequesize--;
            currentPos--;
            currentNode = oldPrev;
            return old.item;
        }
    }

    public Node get(int index) {
        if (index >= dequesize) {
            return null;
        }
        int counter = 0;
        Node pointer = sentinel.next;
        while (counter < index) {
            pointer = pointer.next;
            counter++;
        }
        return pointer;
    }

    public Node currentNode() {
        return currentNode;
    }

}
