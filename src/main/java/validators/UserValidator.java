package validators;

import model.User;

/**
 * Class that validates entities of type User
 */
public class UserValidator implements Validator<String, User>{

    /**
     * method that validates a User entity
     * @param entity - entity which will be validated(instanceof User)
     * @throws ValidationException if the user id is null or less or equal than 0, or firstname or lastname is null
     */
    @Override
    public void validateEntity(User entity) throws ValidationException {
        validateID(entity.getId());
        String errors = "";
        if(entity.getFirstName().equals(""))
            errors += "First name should not be null!\n";
        if(entity.getLastName().equals(""))
            errors += "Last name should not be null!\n";
        if (entity.getPassword().equals(""))
            errors += "Password shouldn't be empty";
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }

    /**
     * method that validates the identity of the User entity
     * @param id - identity of the object to be validated
     * @throws ValidationException - if the id is null or empty
     */
    @Override
    public void validateID(String id) throws ValidationException{
        String errors = "";
        if(id == null || id.equals(""))
            errors += "Email address should not be empty!\n";
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }
}