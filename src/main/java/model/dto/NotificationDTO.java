package model.dto;

import model.Event;
import model.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class NotificationDTO {
    private final Long notificationID;
    private final Event event;
    private final User user;
    private final LocalDate displayDate;
    private final Boolean disabled;

    public NotificationDTO(Long notificationID, Event event, User user, LocalDate displayDate, Boolean disabled) {
        this.notificationID = notificationID;
        this.event = event;
        this.user = user;
        this.displayDate = displayDate;
        this.disabled = disabled;
    }

    public Long getNotificationID(){
        return notificationID;
    }

    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDisplayDate() {
        return displayDate;
    }

    public Boolean getDisabled(){
        return disabled;
    }

    @Override
    public String toString(){
        long diff = getDateDiff(displayDate, event.getStartDate());
        String result = event.getEventName() + " organized by " + event.getOrganiser();
        if(diff == 0 )
            result += " has already begun!";
        else if(diff == 1)
            result += " will begin tomorrow!";
        else
            result += " will begin in " + diff + " days!";
        return result;
    }

    @Override
    public int hashCode(){
        return Objects.hash(notificationID);
    }

    @Override
    public boolean equals(Object object){
        if(object == null)
            return false;
        if(!(object instanceof NotificationDTO))
            return false;
        return this.notificationID.equals(((NotificationDTO) object).notificationID);
    }

    private long getDateDiff(LocalDate date1, LocalDate date2){
        return ChronoUnit.DAYS.between(date1, date2);
    }
}
