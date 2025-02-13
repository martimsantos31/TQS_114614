package pt.ua.deti.tqs;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class TqsStack<T> {

    private final LinkedList<T> collection;
    private final int capacity;

    public TqsStack(int capacity) {
        this.collection = new LinkedList<>();
        this.capacity = capacity;
    }

    public void push(T item) {
        if (collection.size() >= capacity) {
            throw new IllegalStateException("Stack is full");
        }
        collection.add(item);
    }

    public T pop() {
        if (collection.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return collection.removeLast();
    }

    public T peek() {
        if (collection.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return collection.getLast();
    }

    public int size() {
        return collection.size();
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }
}

