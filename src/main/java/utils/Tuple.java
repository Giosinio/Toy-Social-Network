package utils;

import java.util.Objects;

/**
 * Class that represents a parametrized Tuple
 * @param <E1> - type of first element stored in Tuple
 * @param <E2> - type of second element stored in Tuple
 */

public class Tuple<E1, E2> {
    private final E1 element1;
    private final E2 element2;

    /**
     * Parametrized constructor
     * @param element1 - first value
     * @param element2 - second value
     */
    public Tuple(E1 element1, E2 element2){
        this.element1 = element1;
        this.element2 = element2;
    }

    /**
     * method that returns the first value from the OrderedTuple
     * @return - E
     */
    public E1 getFirst(){
        return element1;
    }

    /**
     * method that returns the second value from the OrderedTuple
     * @return - E
     */
    public E2 getSecond(){
        return element2;
    }

    @Override
    public int hashCode(){
        return Objects.hash(element1, element2);
    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        else if(!(o instanceof OrderedTuple))
            return false;
        Tuple<E1, E2> tuple = (Tuple<E1, E2>) o;
        return element1.equals(tuple.getFirst()) && element2.equals(tuple.getSecond());
    }
}
