package business;

import exceptions.NotificationException;
import model.Event;
import model.Notification;
import model.User;
import model.dto.NotificationDTO;
import model.dto.UserDTO;
import repository.EventRepository;
import repository.NotificationRepository;
import repository.Repository;
import utils.events.ChangeEventType;
import utils.events.NotificationEvent;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotificationService implements Observable<NotificationEvent> {
    private final Repository<String, User> userRepository;
    private final EventRepository eventRepository;
    private final NotificationRepository notificationRepository;
    private final Validator<Long, Notification> notificationValidator;
    private final List<Observer<NotificationEvent>> observerList;

    public NotificationService(Repository<String, User> userRepository, EventRepository eventRepository, NotificationRepository notificationRepository, Validator<Long, Notification> notificationValidator) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.notificationRepository = notificationRepository;
        this.notificationValidator = notificationValidator;
        this.observerList = new ArrayList<>();
    }

    public Notification addNotification(NotificationDTO notificationDTO){
        notificationValidator.validateID(notificationDTO.getNotificationID());
        if(eventRepository.findOne(notificationDTO.getEvent().getId()) == null)
            throw new NotificationException("The event from the notification doesn't exist!\n");
        if(userRepository.findOne(notificationDTO.getUser().getId()) == null)
            throw new NotificationException("The user from the notification doesn't exist!\n");
        Notification notification = new Notification(notificationDTO.getEvent().getId(), notificationDTO.getUser().getId(), notificationDTO.getDisplayDate(), notificationDTO.getDisabled());
        return notificationRepository.save(notification);
    }

    /**
     * method that changes whether a Notification is disabled or not
     * @return -
     */
    public Notification updateNotificationStatus(Long notificationID, Boolean newStatus){
        notificationValidator.validateID(notificationID);
        Notification updatedNotification = new Notification(null, null, null, newStatus);
        return notificationRepository.update(updatedNotification);
    }

    /**
     * method that removes a Notification
     * @param notificationID - Notification ID
     * @return - null, if the deletion didn't occur
     *           deleted Notification, otherwise
     */
    public Notification removeNotification(Long notificationID){
        notificationValidator.validateID(notificationID);
        Notification result =  notificationRepository.remove(notificationID);
        if(result != null){
            NotificationEvent event = new NotificationEvent(result, ChangeEventType.REMOVE);
            notifyAll(event);
        }
        return result;
    }

    /**
     * method that returns a list of all Notification entities for a given User
     * @param userDTO - UserDTO
     * @return - List of NotificationDTO
     */
    public List<NotificationDTO> getNotificationListForUser(UserDTO userDTO){
        List<Notification> notificationList = notificationRepository.getNotificationsForUser(userDTO.getUsername(), LocalDate.now());
        return notificationList.stream().map(this::createNotificationDTO).toList();
    }

    /**
     * method that converts a Notification entity into a NotificationDTO
     * @param notification - Notification
     * @return - NotificationDTO
     */
    private NotificationDTO createNotificationDTO(Notification notification){
        if(notification == null)
            return null;
        Event event = eventRepository.findOne(notification.getEventID());
        User user = userRepository.findOne(notification.getUserID());
        return new NotificationDTO(notification.getId(), event, user, notification.getDisplayDate(), notification.getDisabled());
    }

    /**
     * method that solves the problem when a User doesn't connect to his account for multiple days in a row
     * @param username - String(User ID)
     */
    public void changeDisableStatusForNotificationsInInterval(String username){
        LocalDate currentTime = LocalDate.now();
        notificationRepository.changeDisableStatusForNotificationInInterval(username, currentTime);
    }

    @Override
    public void addObserver(Observer<NotificationEvent> observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer<NotificationEvent> observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyAll(NotificationEvent event) {
        observerList.forEach(x -> x.update(event));
    }
}
