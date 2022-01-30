package model;

import utils.Tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventParticipation extends Entity<Tuple<Long, String>> {
    private final LocalDateTime enrollmentDate;
    private final Boolean subscribed;
    private final LocalDate subscriptionDate;

    public EventParticipation(Long eventID, String userID, LocalDateTime enrollmentDate, Boolean subscribed, LocalDate subscriptionDate){
        this.setId(new Tuple<>(eventID, userID));
        this.enrollmentDate = enrollmentDate;
        this.subscribed = subscribed;
        this.subscriptionDate = subscriptionDate;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public LocalDate getSubscriptionDate(){
        return subscriptionDate;
    }
}
