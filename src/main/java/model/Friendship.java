package model;

import utils.OrderedTuple;

import java.time.LocalDate;

/**
 * Class that extends generic class Entity, representing Friendship entity
 */
public class Friendship extends Entity<OrderedTuple<String>> {
    private final LocalDate date;

    /**
     * parametrized constructor of the Friendship
     * @param date - LocalDate
     */
    public Friendship(LocalDate date){
        this.date = date;
    }

    /**
     * Method that returns the date when friendship was created
     * @return LocalDate
     */
    public LocalDate getDate(){ return date;}

    @Override
    public String toString(){
        return getId().getFirst() + ";" + getId().getSecond() + ";" + getDate();
    }
}
