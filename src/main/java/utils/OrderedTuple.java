package utils;

import java.util.Objects;


/**
 * Class that stores a parametrized OrderedTuple
 * @param <E> - type of the attributes stored in the OrderedTuple
 */
public class OrderedTuple<E extends Comparable<E>> {
    private final E element1;
    private final E element2;

    /**
     * Parametrized constructor
     * @param element1 - first value
     * @param element2 - second value
     */
    public OrderedTuple(E element1, E element2){
        if(element1.compareTo(element2) > 0){
            var aux = element1;
            element1 = element2;
            element2 = aux;
        }
        this.element1 = element1;
        this.element2 = element2;
    }

    /**
     * method that returns the first value from the OrderedTuple
     * @return - E
     */
    public E getFirst(){
        return element1;
    }

    /**
     * method that returns the second value from the OrderedTuple
     * @return - E
     */
    public E getSecond(){
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
        OrderedTuple<E> orderedTuple = (OrderedTuple<E>) o;
        return element1.equals(orderedTuple.getFirst()) && element2.equals(orderedTuple.getSecond());
    }
}
