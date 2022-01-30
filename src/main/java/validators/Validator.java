package validators;

import model.Entity;
import model.User;

/**
 * Interface for Validator Class
 * @param <ID> - type of the data to be validated
 * @param <E> - E extends Entity(E has an attribute of type ID)
 */
public interface Validator<ID, E extends Entity<ID>> {
    /**
     * method that validates the given entity
     * @param entity - entity which will be validated
     * @throws ValidationException - if the entity is not valid
     */
    void validateEntity(E entity) throws ValidationException;

    /**
     * method that validates the ID of the entity
     * @param id - identity of the object to be validated
     * @throws ValidationException - if the ID is null or
     */
    void validateID(ID id) throws ValidationException;
}