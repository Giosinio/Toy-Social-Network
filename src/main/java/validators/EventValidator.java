package validators;

import model.Event;

public class EventValidator implements Validator<Long, Event> {
    /**
     * method that validates the given entity
     *
     * @param entity - entity which will be validated
     * @throws ValidationException - if the entity is not valid
     */
    @Override
    public void validateEntity(Event entity) throws ValidationException {
        String errors = "";
        if(entity.getEventName().equals(""))
            errors += "The event name cannot be empty!\n";
        if(entity.getStartDate() == null)
            errors += "You must select a start date!\n";
        if(entity.getEndDate() == null)
            errors += "You must select an end date!\n";
        if(entity.getStartDate() != null && entity.getEndDate() != null && entity.getStartDate().compareTo(entity.getEndDate()) > 0)
            errors += "Time interval for event is invalid!\n";
        if(entity.getLocation().equals(""))
            errors += "The location cannot be empty!\n";
        if(entity.getOrganiser().equals(""))
            errors += "The organizer cannot be empty!\n";
        if(entity.getCategory().equals(""))
            errors += "The category cannot be empty!\n";
        if(entity.getDescription().equals(""))
            errors  += "The description cannot be empty\n";
        if(errors.length() > 0)
            throw new RuntimeException(errors);
    }

    /**
     * method that validates the ID of the entity
     *
     * @param aLong - identity of the object to be validated
     * @throws ValidationException - if the ID is null or
     */
    @Override
    public void validateID(Long aLong) throws ValidationException {

    }
}
