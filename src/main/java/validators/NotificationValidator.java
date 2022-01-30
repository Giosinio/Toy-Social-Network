package validators;

import exceptions.NotificationException;
import model.Notification;

public class NotificationValidator implements Validator<Long, Notification> {
    /**
     * method that validates the given entity
     *
     * @param entity - entity which will be validated
     * @throws ValidationException - if the entity is not valid
     */
    @Override
    public void validateEntity(Notification entity) throws ValidationException {
        return;
    }

    /**
     * method that validates the ID of the entity
     *
     * @param aLong - identity of the object to be validated
     * @throws ValidationException - if the ID is null or
     */
    @Override
    public void validateID(Long aLong) throws ValidationException {
        if(aLong <= 0){
            throw new NotificationException("Id of the notification should be greater than 0!\n");
        }
    }
}
