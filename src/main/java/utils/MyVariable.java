package utils;

/**
 * Wrapper class that encapsulates an E type attribute
 * @param <E> - entity must have a type E attribute
 */
public class MyVariable<E> {
    private E value;
    public MyVariable(E value){
        this.value = value;
    }

    /**
     * method that returns the wrapped value
     * @return - E
     */
    public E getValue(){
        return value;
    }

    /**
     * method that sets the wrapped value to a new one
     * @param otherValue - the new value of the wrapped value
     */
    public void setValue(E otherValue){
        this.value = otherValue;
    }
}
