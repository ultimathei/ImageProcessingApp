package app.utils;

/**
 * A simple data structure to create a mapping 
 * between a key and a value object.
 */
public class Pair<T, E> {
    private T key;
    private E value;

    public Pair(T k, E v){
        key = k;
        value = v;
    }

    public T getKey() {
        return key;
    }
    public void setKey(T key) {
        this.key = key;
    }
    public E getValue() {
        return value;
    }
    public void setValue(E value) {
        this.value = value;
    }
}
