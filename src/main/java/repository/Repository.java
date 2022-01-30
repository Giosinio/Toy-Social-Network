package repository;

import model.Entity;

import java.util.List;

/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>> {

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    E findOne(ID id);

    /**
     * @return all entities
     */
    List<E> findAll();

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    E save(E entity);

    /**
     * method that saves a new entity into repository
     * @param id - identity of the object to be searched
     * @return null - if the searched entity is not saved
     * otherwise returns the entity and deletes it from the repo
     * @throws  IllegalArgumentException if the given id is null
     */
    E remove(ID id);

    /**
     * method  that updates an entity from the repository
     * @param entity - the new entity which will replace the old entity
     * @return - null, if the update succeeded
     *           entity, if there isn't an entity to be updated in the repository(with the same id as the given one)
     */
    E update(E entity);
}


