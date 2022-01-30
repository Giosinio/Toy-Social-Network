package repository;

import model.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends Repository<Long, Event>{
    /**
     * method that returns a list of Events that represents all the Events(that didn't start at the moment of interrogation) that the given user isn't participating at the moment of interrogation , ordered by start date
     * @param username - String(ID of the User)
     * @param currentTime - LocalDate
     * @return - List(Event)
     */
    List<Event> getRecommendedEventsForUser(String username, LocalDate currentTime);
}
