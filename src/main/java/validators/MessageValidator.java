package validators;

import model.Message;

public class MessageValidator implements Validator<Long, Message> {

    /**
     * method that validates the given entity
     *
     * @param entity - entity which will be validated
     * @throws ValidationException - if the entity is not valid
     */
    @Override
    public void validateEntity(Message entity) throws ValidationException {
        String errors = "";
        if(entity.getMessage().equals(""))
            errors += "The content of the message can't be empty!\n";
        if(entity.getSubject().equals(""))
            errors += "The subject of the message can't be empty!\n";
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }

    /**
     * method that validates the ID of the entity
     *
     * @param integer - identity of the object to be validated
     * @throws ValidationException - if the ID is null or
     */
    @Override
    public void validateID(Long integer) throws ValidationException {
        if(integer <= 0)
            throw new ValidationException("The id of the message should be greater than 0!");
    }
}
