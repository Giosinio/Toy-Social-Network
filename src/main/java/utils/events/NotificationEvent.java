package utils.events;

import model.Notification;

public class NotificationEvent implements Event{
    ChangeEventType changeEventType;
    Notification notification;

    public NotificationEvent(Notification notification, ChangeEventType changeEventType){
        this.notification = notification;
        this.changeEventType = changeEventType;
    }

    public Notification getNotification(){
        return notification;
    }

    public ChangeEventType getChangeEventType(){
        return changeEventType;
    }
}
