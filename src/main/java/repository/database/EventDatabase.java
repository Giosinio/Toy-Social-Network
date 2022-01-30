package repository.database;

import model.Event;
import repository.EventRepository;
import repository.PagingEventRepository;
import repository.paging.Page;
import repository.paging.PageImplementation;
import repository.paging.Pageable;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventDatabase extends AbstractDatabaseRepository<Long, Event> implements PagingEventRepository {

    public EventDatabase(String url, String user, String password, String table){
        super(url, user, password, table);
    }

    @Override
    public String updateQuery() {
        return "update " + table + "set event_name = ?, start_date = ?, end_date = ?, organiser = ?, category = ?, description = ?, location = ? where id = ?";
    }

    private void setCommonSaveUpdateQueryValues(PreparedStatement statement, Event entity) throws SQLException{
        statement.setString(1, entity.getEventName());
        statement.setDate(2, Date.valueOf(entity.getStartDate()));
        statement.setDate(3, Date.valueOf(entity.getEndDate()));
        statement.setString(4, entity.getOrganiser());
        statement.setString(5, entity.getCategory());
        statement.setString(6, entity.getDescription());
        statement.setString(7, entity.getLocation());
    }

    @Override
    public void setUpdateQueryValues(PreparedStatement statement, Event entity) throws SQLException {
        setCommonSaveUpdateQueryValues(statement, entity);
        statement.setLong(8, entity.getId());
    }

    @Override
    public String findOneQuery() {
        return "SELECT * FROM " + table + " WHERE id = ?";
    }

    @Override
    public void setFindOneQueryValues(PreparedStatement statement, Long aLong) throws SQLException {
        statement.setLong(1, aLong);
    }

    @Override
    public String saveQuery() {
        return "INSERT INTO " + table + "(event_name, start_date, end_date, organiser, category, description, location) VALUES (?,?,?,?,?,?,?)";
    }

    @Override
    public void setSaveQueryValues(PreparedStatement statement, Event entity) throws SQLException {
        setCommonSaveUpdateQueryValues(statement, entity);
    }

    @Override
    public String removeQuery() {
        return "DELETE FROM " + table + " WHERE id = ?";
    }

    @Override
    public void setRemoveQueryValues(PreparedStatement statement, Long aLong) throws SQLException {
        statement.setLong(1, aLong);
    }

    @Override
    public Event createEntityFromQuery(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String eventName = resultSet.getString("event_name");
        LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
        LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
        String organiser = resultSet.getString("organiser");
        String category = resultSet.getString("category");
        String description = resultSet.getString("description");
        String location = resultSet.getString("location");
        Event event = new Event(eventName, startDate, endDate, organiser, category, description, location);
        event.setId(id);
        return event;
    }

    /**
     * method that returns a list of Events that represents all the Events(that didn't start at the moment of interrogation) that the given user isn't participating at the moment of interrogation , ordered by start date
     * @param username - String(ID of the User)
     * @param currentTime - LocalDateTime
     * @return - List(Event)
     */
    @Override
    public List<Event> getRecommendedEventsForUser(String username, LocalDate currentTime) {
        String sql = """
                SELECT * FROM events E
                WHERE E.id NOT IN (
                SELECT E.id FROM events E
                INNER JOIN event_participants EP
                ON E.id = EP.event_id
                WHERE user_id = ?
                ) AND E.start_date >= ?
                ORDER BY E.start_date""";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            statement.setDate(2, Date.valueOf(currentTime));
            ResultSet resultSet = statement.executeQuery();
            List<Event> eventList = new ArrayList<>();
            while(resultSet.next()){
                Event newEvent = createEntityFromQuery(resultSet);
                eventList.add(newEvent);
            }
            return eventList;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that returns the required Page of the Events that will be recommended for a given User
     * @param username - String
     * @param currentTime - LocalDate
     * @param pageable - Pageable
     * @return - Page of Event
     */
    @Override
    public Page<Event> getRecommendedEventsForUser(String username, LocalDate currentTime, Pageable pageable) {
        String sql = """
                SELECT * FROM events E
                WHERE E.id NOT IN (
                SELECT E.id FROM events E
                INNER JOIN event_participants EP
                ON E.id = EP.event_id
                WHERE user_id = ?
                ) AND E.start_date >= ?
                ORDER BY E.start_date
                OFFSET ?
                LIMIT ?""";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            statement.setDate(2, Date.valueOf(currentTime));
            statement.setInt(3, pageable.getPageSize() * pageable.getPageNumber());
            statement.setInt(4, pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            List<Event> eventList = new ArrayList<>();
            while(resultSet.next()){
                Event newEvent = createEntityFromQuery(resultSet);
                eventList.add(newEvent);
            }
            return new PageImplementation<>(pageable, eventList.stream());
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that returns the number of Events that will be recommended for a certain User
     * @param username    - String(ID)
     * @param currentTime - LocalDate
     * @return - int
     */
    @Override
    public int getNumberOfRecommendedEventsForUser(String username, LocalDate currentTime) {
        String sql = """
                     SELECT COUNT(*) AS no_events FROM events E
                    WHERE E.id NOT IN (
                    SELECT E.id FROM events E
                    INNER JOIN event_participants EP
                    ON E.id = EP.event_id
                    WHERE user_id = ?
                    ) AND E.start_date >= ?
                     """;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setDate(2, Date.valueOf(currentTime));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("no_events");
            }
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }

        return 0;
    }
}