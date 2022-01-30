package repository.database;

import model.EventParticipation;
import repository.PagingEventParticipationRepository;
import repository.paging.Page;
import repository.paging.PageImplementation;
import repository.paging.Pageable;
import utils.Tuple;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventParticipationDatabase extends AbstractDatabaseRepository<Tuple<Long, String>, EventParticipation> implements PagingEventParticipationRepository {

    public EventParticipationDatabase(String url, String username, String password, String table) {
        super(url, username, password, table);
    }

    @Override
    public String updateQuery() {
        return "UPDATE event_participants SET subscribed = ?, subscription_date = ? WHERE event_id = ? AND user_id = ?";
    }

    @Override
    public void setUpdateQueryValues(PreparedStatement statement, EventParticipation entity) throws SQLException {
        statement.setBoolean(1, entity.getSubscribed());
        statement.setDate(2, Date.valueOf(entity.getSubscriptionDate()));
        statement.setLong(3, entity.getId().getFirst());
        statement.setString(4, entity.getId().getSecond());
    }

    @Override
    public String findOneQuery() {
        return "SELECT * FROM event_participants WHERE event_id = ? AND user_id = ?";
    }

    @Override
    public void setFindOneQueryValues(PreparedStatement statement, Tuple<Long, String> longStringTuple) throws SQLException {
        statement.setLong(1, longStringTuple.getFirst());
        statement.setString(2, longStringTuple.getSecond());
    }

    @Override
    public String saveQuery() {
        return "INSERT INTO event_participants(event_id, user_id, enrollment_date, subscribed, subscription_date) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    public void setSaveQueryValues(PreparedStatement statement, EventParticipation entity) throws SQLException {
        statement.setLong(1, entity.getId().getFirst());
        statement.setString(2, entity.getId().getSecond());
        statement.setTimestamp(3, Timestamp.valueOf(entity.getEnrollmentDate()));
        statement.setBoolean(4, entity.getSubscribed());
        statement.setDate(5, Date.valueOf(entity.getSubscriptionDate()));
    }

    @Override
    public String removeQuery() {
        return "DELETE FROM event_participants WHERE event_id = ? AND user_id = ?";
    }

    @Override
    public void setRemoveQueryValues(PreparedStatement statement, Tuple<Long, String> longStringTuple) throws SQLException {
        statement.setLong(1, longStringTuple.getFirst());
        statement.setString(2, longStringTuple.getSecond());
    }

    @Override
    public EventParticipation createEntityFromQuery(ResultSet resultSet) throws SQLException {
        Long eventID = resultSet.getLong("event_id");
        String userID = resultSet.getString("user_id");
        LocalDateTime enrollmentDate = resultSet.getTimestamp("enrollment_date").toLocalDateTime();
        Boolean subscribed = resultSet.getBoolean("subscribed");
        LocalDate subscriptionDate = resultSet.getDate("subscription_date").toLocalDate();
        return new EventParticipation(eventID, userID, enrollmentDate, subscribed, subscriptionDate);
    }

    /**
     * method that returns a list of all Event Participation entities for a certain user
     * @param username - String(User ID)
     * @param currentTime - LocalDate
     * @return - List(EventParticipation)
     */
    @Override
    public List<EventParticipation> getEventsUserParticipatesIn(String username, LocalDate currentTime) {
        String sql = "SELECT event_id, user_id, enrollment_date, subscribed, subscription_date FROM event_participants EP INNER JOIN events E ON EP.event_id = E.id WHERE EP.user_id = ? AND E.start_date >= ? ORDER BY E.start_date DESC";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setDate(2, Date.valueOf(currentTime));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<EventParticipation> eventParticipationList = new ArrayList<>();
            while(resultSet.next()){
                EventParticipation eventParticipation = createEntityFromQuery(resultSet);
                eventParticipationList.add(eventParticipation);
            }
            return eventParticipationList;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * method that returns the number of participants for a given Event
     * @param eventID - id of the interrogated Event
     * @return - int
     */
    @Override
    public int getNumberOfParticipantsForEvent(Long eventID) {
        String sql = "SELECT COUNT(event_id) AS no_participants FROM event_participants WHERE event_id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setLong(1, eventID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("no_participants");
            }
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * method that returns a Page object with the Events a given User participates in wrapped in a EventParticipation object
     * @param username - String
     * @param currentTime - LocalDate
     * @param pageable - Pageable
     * @return - Page of EventParticipation
     */
    @Override
    public Page<EventParticipation> getEventsUserParticipatesIn(String username, LocalDate currentTime, Pageable pageable) {
        String sql = """
                     SELECT event_id, user_id, enrollment_date, subscribed, subscription_date
                     FROM event_participants EP
                     INNER JOIN events E ON EP.event_id = E.id
                     WHERE EP.user_id = ? AND E.start_date >= ?
                     ORDER BY E.start_date DESC
                     OFFSET ?
                     LIMIT ?
                     """;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setDate(2, Date.valueOf(currentTime));
            preparedStatement.setInt(3, pageable.getPageSize() * pageable.getPageNumber());
            preparedStatement.setInt(4, pageable.getPageSize());

            ResultSet resultSet = preparedStatement.executeQuery();
            List<EventParticipation> eventParticipations = new ArrayList<>();
            while(resultSet.next()){
                EventParticipation eventParticipation = createEntityFromQuery(resultSet);
                eventParticipations.add(eventParticipation);
            }
            return new PageImplementation<>(pageable, eventParticipations.stream());
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * method that returns the number of Events a given User participates in
     * @param username - String(User ID)
     * @param currentTime - LocalDate
     * @return - int
     */
    @Override
    public int getNumberOfEventsUserParticipatesIn(String username, LocalDate currentTime) {
        String sql = """
                     SELECT COUNT(*) AS no_events
                     FROM event_participants EP
                     INNER JOIN events E ON EP.event_id = E.id
                     WHERE EP.user_id = ? AND E.start_date >= ?
                     """;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setDate(2, Date.valueOf(currentTime));
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("no_events");
            }
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }

        return 0;
    }
}
