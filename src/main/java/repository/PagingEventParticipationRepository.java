package repository;

import model.EventParticipation;
import repository.paging.Page;
import repository.paging.Pageable;

import java.time.LocalDate;

public interface PagingEventParticipationRepository extends EventParticipationRepository{
    /**
     * method that returns a Page object with the Events a given User participates in wrapped in a EventParticipation object
     * @param username - String
     * @param currentTime - LocalDate
     * @param pageable - Pageable
     * @return - Page of EventParticipation
     */
    Page<EventParticipation> getEventsUserParticipatesIn(String username, LocalDate currentTime, Pageable pageable);

    /**
     * method that returns the number of Events a given User participates in
     * @param username - String(User ID)
     * @param currentTime - LocalDate
     * @return
     */
    int getNumberOfEventsUserParticipatesIn(String username, LocalDate currentTime);
}
