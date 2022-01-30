package utils.observer;

import utils.events.Event;

public interface Observable<E extends Event>{
    public void addObserver(Observer<E> observer);
    public void removeObserver(Observer<E> observer);
    public void notifyAll(E event);
}
