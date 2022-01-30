package validators;

import exceptions.EventParticipationException;
import model.EventParticipation;
import utils.Tuple;

public class EventParticipationValidator implements Validator<Tuple<Long, String>, EventParticipation> {
    /**
     * method that validates the given entity
     *
     * @param entity - entity which will be validated
     * @throws ValidationException - if the entity is not valid
     */
    @Override
    public void validateEntity(EventParticipation entity) throws ValidationException {
        String errors = "";
        if(entity.getEnrollmentDate() == null)
            errors += "Enrollment date cannot be null!\n";
        if(entity.getSubscribed() == null)
            errors += "Subscription cannot be null \n";
        if(errors.length() > 0)
            throw new EventParticipationException(errors);
    }

    /**
     * method that validates the ID of the entity
     *
     * @param longStringTuple - identity of the object to be validated
     * @throws ValidationException - if the ID is null or eventID is less equal than 0 or userID is empty
     */
    @Override
    public void validateID(Tuple<Long, String> longStringTuple) throws ValidationException {
        String errors = "";
        if(longStringTuple.getFirst() == null || longStringTuple.getFirst() <= 0)
            errors += "Event ID should be greater than 0!\n";
        if (longStringTuple.getSecond() == null || longStringTuple.getSecond().equals(""))
            errors += "User ID cannot be empty!\n";
        if(errors.length() > 0)
            throw new EventParticipationException(errors);
    }
}
