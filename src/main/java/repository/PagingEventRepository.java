package repository;

import model.Event;
import repository.paging.Page;
import repository.paging.Pageable;

import java.time.LocalDate;

public interface PagingEventRepository extends EventRepository{
    /**
     * method that returns the required Page of the Events that will be recommended for a given User
     * @param username - String
     * @param currentTime - LocalDate
     * @param pageable - Pageable
     * @return - Page of Event
     */
    Page<Event> getRecommendedEventsForUser(String username, LocalDate currentTime, Pageable pageable);

    /**
     * method that returns the number of Events that will be recommended for a certain User
     * @param username - String(ID)
     * @param currentTime - LocalDate
     * @return - int
     */
    int getNumberOfRecommendedEventsForUser(String username, LocalDate currentTime);
}
