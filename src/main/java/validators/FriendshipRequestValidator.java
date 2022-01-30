package validators;

import model.FriendshipRequest;
import utils.Tuple;

/**
 * Class that validates entities of type FriendshipRequest
 */

public class FriendshipRequestValidator implements Validator<Tuple<String, String>, FriendshipRequest> {

    private final UserValidator userValidator;

    public FriendshipRequestValidator() {
        userValidator = new UserValidator();
    }

    /**
     * method that validates a FriendshipRequest entity
     * @param entity - entity which will be validated (instanceof FriendshipRequest)
     * @throws ValidationException if any of the usernames or the state are invalid
     */
    @Override
    public void validateEntity(FriendshipRequest entity) throws ValidationException {
        if (entity.getCreated() == null)
            throw new ValidationException("Creation date cannot be null!\n");
        validateID(entity.getId());
    }

    /**
     * method that validates the identity of the FriendshipRequest entity
     * @param id - identity of the object to be validated
     * @throws ValidationException - if the id is null or contains invalid usernames
     */
    @Override
    public void validateID(Tuple<String, String> id) throws ValidationException {
        String errors = "";
        if (id == null) {
            errors += "The ID cannot be null!\n";
        } else {
            userValidator.validateID(id.getFirst());
            userValidator.validateID(id.getSecond());
            if (id.getFirst().equals(id.getSecond())) {
                errors += "The IDs cannot be equal!\n";
            }
        }
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }
}

