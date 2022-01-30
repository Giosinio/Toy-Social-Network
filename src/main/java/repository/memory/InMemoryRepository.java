package repository.memory;

import model.Entity;
import repository.paging.Page;
import repository.paging.Pageable;
import repository.paging.Paginator;
import repository.paging.PagingRepository;
import validators.ValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that stores entities into memory
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> - type of entities saved in Repo
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements PagingRepository<ID,E> {
    protected Map<ID,E> entities;

    public InMemoryRepository() {
        entities = new HashMap<>();
    }

    /**
     * Method that searches for the entity which has the given id
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return - E, if the entity is found
     *           null, otherwise
     */
    @Override
    public E findOne(ID id){
        if(id == null)
            throw new IllegalArgumentException("id must not be null");
        return entities.get(id);
    }

    /**
     * method that returns an Iterable with all the entities in the memory
     * @return - Iterable
     */
    @Override
    public List<E> findAll() {
        return entities.values().stream().toList();
    }

    /**
     * method that saves a given entity into the memory
     * @param entity entity must be not null
     * @return - E, if the entity is already saved in the repository
     *          null, if the entity was successfully stored
     * @throws ValidationException if the entity is not valid
     */
    @Override
    public E save(E entity) {
            if(entity == null)
            throw new IllegalArgumentException("entity must not be null");
        if(entities.get(entity.getId()) != null){
            return entities.get(entity.getId());
        }
        entities.put(entity.getId(), entity);
        return null;
    }

    /**
     * method that removes the entity with the given id
     * @param id - identity of the object to be removed
     * @return - null, if the entity doesn't exist
     *           E, if the entity was successfully removed
     * @throws IllegalArgumentException - if the id is null
     */
    @Override
    public E remove(ID id) {
        if(id == null)
            throw new IllegalArgumentException("id must not be null!");
        return entities.remove(id);
    }

    @Override
    public E update(E entity) {
        if(entities.get(entity.getId()) == null)
            return entity;
        entities.put(entity.getId(), entity);
        return null;
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        Paginator<E> paginator = new Paginator<>(pageable, findAll());
        return paginator.paginate();
    }
}
