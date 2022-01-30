package validators;

import model.Friendship;
import utils.OrderedTuple;

/**
 * Class that validates entities of type Friendship
 */
public class FriendshipValidator implements Validator<OrderedTuple<String>, Friendship>{
    /**
     * method that validates a Friendship entity
     * @param entity - entity which will be validated(instanceof Friendship)
     * @throws ValidationException if one of the user's username is  equal to "", or both usernames of the Friendship entity
     * are equal
     */
    @Override
    public void validateEntity(Friendship entity) throws ValidationException {
        validateID(entity.getId());
    }

    /**
     * method that validates the identity of the Friendship entity
     * @param id - identity of the object to be validated(type )
     * @throws ValidationException - if the id is null or equal to ""
     */
    @Override
    public void validateID(OrderedTuple<String> id) throws ValidationException {
        String errors = "";
        if(id.getFirst().equals(""))
            errors += "The first User id should be greater than 0!\n";
        if(id.getSecond().equals(""))
            errors += "The second User id should be greater than 0!\n";
        if(id.getFirst().equals(id.getSecond()))
            errors += "You cannot add yourself as a friend!\n";
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }
}
