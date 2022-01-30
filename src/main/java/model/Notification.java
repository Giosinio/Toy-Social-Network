package model;

import java.time.LocalDate;

public class Notification extends Entity<Long>{
    private Long eventID;
    private String userID;
    private LocalDate displayDate;
    private Boolean disabled;

    public Notification(Long eventID, String userID, LocalDate displayDate, Boolean disabled) {
        this.eventID = eventID;
        this.userID = userID;
        this.displayDate = displayDate;
        this.disabled = disabled;
    }

    /**
     * method that returns the ID of the Event from the notification
     * @return - Long
     */
    public Long getEventID() {
        return eventID;
    }

    /**
     * method that returns the ID of the User from the notification
     * @return - String
     */
    public String getUserID() {
        return userID;
    }

    /**
     * method that returns the date when the notification will become available to be displayed in User Feed
     * @return - LocalDate
     */
    public LocalDate getDisplayDate() {
        return displayDate;
    }

    /**
     * method that returns whether a notification is disabled or not
     * @return - false, if the notification still exists(should be displayed in UI)
     *         - true, otherwise
     */
    public Boolean getDisabled(){
        return disabled;
    }
}
