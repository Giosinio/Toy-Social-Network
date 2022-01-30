package repository;

import model.EventParticipation;
import utils.Tuple;

import java.time.LocalDate;
import java.util.List;

public interface EventParticipationRepository extends Repository<Tuple<Long, String>, EventParticipation>{
    /**
     * method that returns a list of all Event Participations for a certain user
     * @param username - String(User ID)
     * @return - List(EventParticipation)
     */
    List<EventParticipation> getEventsUserParticipatesIn(String username, LocalDate currentTime);

    /**
     * method that returns the number of participants for a given Event
     * @param eventID - id of the interrogated Event
     * @return - int
     */
    int getNumberOfParticipantsForEvent(Long eventID);
}
