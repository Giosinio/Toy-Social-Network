package utils.observer;

import utils.events.Event;

public interface Observer<E extends Event>{
    public void update(E event);
}
