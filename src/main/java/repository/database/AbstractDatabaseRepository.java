package repository.database;

import org.postgresql.util.PSQLException;
import model.Entity;
import repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractDatabaseRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected final String table;
    protected Connection connection;

    protected AbstractDatabaseRepository(String url, String username, String password, String table){
        this.table = table;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    public abstract String updateQuery();

    public abstract void setUpdateQueryValues(PreparedStatement statement, E entity) throws SQLException;

    public abstract String findOneQuery();

    public abstract void setFindOneQueryValues(PreparedStatement statement, ID id) throws SQLException;

    public abstract String saveQuery();

    public abstract void  setSaveQueryValues(PreparedStatement statement, E entity) throws SQLException;

    public abstract String removeQuery();

    public abstract void setRemoveQueryValues(PreparedStatement statement, ID id) throws SQLException;

    public abstract E createEntityFromQuery(ResultSet resultSet) throws SQLException;

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    @Override
    public E findOne(ID id) {
        String sql = findOneQuery();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            setFindOneQueryValues(statement, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return createEntityFromQuery(resultSet);
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * @return all entities
     */
    @Override
    public List<E> findAll() {
            Set<E> entities = new HashSet<>();
            String sql = "SELECT * FROM " + table;
            try(PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet resultSet = ps.executeQuery()){

                while(resultSet.next()){
                    E entity = createEntityFromQuery(resultSet);
                    entities.add(entity);
                }
                return entities.stream().toList();
            }catch (SQLException exception){
                exception.printStackTrace();
            }
            return null;
    }

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws IllegalArgumentException if the given entity is null.*
     */
    @Override
    public E save(E entity) {
        String sql = saveQuery();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            setSaveQueryValues(statement, entity);
            try {
                statement.executeUpdate();
                return null;
            } catch (PSQLException exception) {
                return findOne(entity.getId());
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that saves a new entity into repository
     *
     * @param id - identity of the object to be searched
     * @return null - if the searched entity is not saved
     * otherwise returns the entity and deletes it from the repo
     * @throws IllegalArgumentException if the given id is null
     */
    @Override
    public E remove(ID id) {
        String sql = removeQuery();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            setRemoveQueryValues(statement, id);
            E deletedEntity = findOne(id);
            if(deletedEntity == null)
                return null;
            statement.executeUpdate();
            return deletedEntity;
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method  that updates an entity from the repository
     *
     * @param entity - the new entity which will replace the old entity
     * @return - null, if the update succeeded
     * entity, if there isn't an entity to be updated in the repository(with the same id as the given one)
     */
    @Override
    public E update(E entity) {
        String sql = updateQuery();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            setUpdateQueryValues(statement, entity);
            int affectedRows = statement.executeUpdate();
            if(affectedRows == 1)
                return null;
            return entity;
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }
}
