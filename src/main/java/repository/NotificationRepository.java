package repository;

import model.Notification;

import java.time.LocalDate;
import java.util.List;

public interface NotificationRepository extends Repository<Long, Notification> {
    /**
     * method that returns a List of all Notification entities that will be displayed on User Feed
     * @param username - String(User ID)
     * @param currentTime - LocalDate
     * @return - List of Notification entities
     */
    List<Notification> getNotificationsForUser(String username, LocalDate currentTime);

    /**
     * method that deletes all the Notification entities for a certain Event that have displayDate greater than the currentTime
     * @param username - String(User ID)
     * @param eventID - Long
     * @param currentTime - LocalDate
     */
    void deleteNotificationUserRenounce(String username, Long eventID, LocalDate currentTime);

    /**
     * method that changes the 'disabled' parameter when subscription state changes for a certain Event
     * @param eventID - Long
     * @param username - String
     * @param currentTime - LocalDate
     * @param newStatus - Boolean
     */
    void changeDisableStatusWhenSubscriptionChange(Long eventID, String username, LocalDate currentTime, Boolean newStatus);

    /**
     * method that changes the 'disable' status for the User Notifications for the time the User was logged off
     * @param username - String
     * @param currentTime - LocalDate
     */
    void changeDisableStatusForNotificationInInterval(String username, LocalDate currentTime);

    /**
     * method that checks whether a Notification with the given parameters exists
     * @param eventID - Long
     * @param username - String
     * @param date - LocalDate
     * @return - null, if the searched Notification doesn't exist
     *           Notification, otherwise
     */
    Notification findNotification(Long eventID, String username, LocalDate date);
}
