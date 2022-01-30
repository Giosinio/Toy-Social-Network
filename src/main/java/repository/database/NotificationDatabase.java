package repository.database;

import model.Notification;
import repository.NotificationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotificationDatabase extends AbstractDatabaseRepository<Long, Notification> implements NotificationRepository {
    public NotificationDatabase(String url, String username, String password, String table) {
        super(url, username, password, table);
    }

    @Override
    public String updateQuery() {
        return "UPDATE notifications SET disabled = ? WHERE id = ?";
    }

    @Override
    public void setUpdateQueryValues(PreparedStatement statement, Notification entity) throws SQLException {
        statement.setBoolean(1, entity.getDisabled());
        statement.setLong(2, entity.getId());
    }

    @Override
    public String findOneQuery() {
        return "SELECT * FROM notifications WHERE id = ?";
    }

    @Override
    public void setFindOneQueryValues(PreparedStatement statement, Long aLong) throws SQLException {
            statement.setLong(1, aLong);
    }

    @Override
    public String saveQuery() {
        return "INSERT INTO notifications(event_id, user_id, display_date, disabled) VALUES (?, ?, ?, ?)";
    }

    @Override
    public void setSaveQueryValues(PreparedStatement statement, Notification entity) throws SQLException {
        statement.setLong(1, entity.getEventID());
        statement.setString(2, entity.getUserID());
        statement.setDate(3, Date.valueOf(entity.getDisplayDate()));
        statement.setBoolean(4, false);
    }

    @Override
    public String removeQuery() {
        return "DELETE FROM notifications WHERE id = ?";
    }

    @Override
    public void setRemoveQueryValues(PreparedStatement statement, Long aLong) throws SQLException {
        statement.setLong(1, aLong);
    }

    @Override
    public Notification createEntityFromQuery(ResultSet resultSet) throws SQLException {
        Long notificationID = resultSet.getLong("id");
        Long eventID = resultSet.getLong("event_id");
        String userID = resultSet.getString("user_id");
        LocalDate date = resultSet.getDate("display_date").toLocalDate();
        Boolean disabled = resultSet.getBoolean("disabled");
        Notification notification = new Notification(eventID, userID, date, disabled);
        notification.setId(notificationID);
        return notification;
    }

    /**
     * method that returns a List of all Notification entities that will be displayed on User Feed(no older than 2 weeks)
     * @param username    - String(User ID)
     * @param currentTime - LocalDate
     * @return - List of Notification
     */
    @Override
    public List<Notification> getNotificationsForUser(String username, LocalDate currentTime) {
        String sql = "SELECT * FROM notifications N WHERE user_id = ? AND display_date <= ? AND disabled = false AND ? - display_date <= 14";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setDate(2, Date.valueOf(currentTime));
            preparedStatement.setDate(3, Date.valueOf(currentTime));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Notification> notificationList = new ArrayList<>();
            while (resultSet.next()){
                Notification notification = createEntityFromQuery(resultSet);
                notificationList.add(notification);
            }
            return notificationList;
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that deletes all the Notification entities for a certain Event that have displayDate greater than the currentTime
     * @param username    - String(User ID)
     * @param eventID     - Long
     * @param currentTime - LocalDate
     */
    @Override
    public void deleteNotificationUserRenounce(String username, Long eventID, LocalDate currentTime) {
        String sql = "DELETE FROM notifications WHERE event_id = ? AND user_id = ? AND display_date > ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setLong(1, eventID);
            preparedStatement.setString(2, username);
            preparedStatement.setDate(3, Date.valueOf(currentTime));
            preparedStatement.executeUpdate();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    /**
     * method that changes the 'disabled' parameter when subscription state changes for a certain Event
     *
     * @param eventID     - Long
     * @param username    - String
     * @param currentTime - LocalDate
     * @param newStatus   - Boolean
     */
    @Override
    public void changeDisableStatusWhenSubscriptionChange(Long eventID, String username, LocalDate currentTime, Boolean newStatus) {
        String sql = "UPDATE notifications SET disabled = ? WHERE event_id = ?  AND user_id = ? AND display_date > ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setBoolean(1, newStatus);
            preparedStatement.setLong(2, eventID);
            preparedStatement.setString(3, username);
            preparedStatement.setDate(4, Date.valueOf(currentTime));
            preparedStatement.executeUpdate();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    /**
     * method that changes the 'disable' status for the User Notifications for the time the User was logged off
     * @param username    - String
     * @param currentTime - LocalDate
     */
    @Override
    public void changeDisableStatusForNotificationInInterval(String username, LocalDate currentTime) {
        String sql = """
                UPDATE notifications N
                SET disabled = NOT(SELECT subscribed FROM event_participants EP WHERE EP.event_id = N.event_id AND EP.user_id = ?)
                WHERE user_id = ? AND display_date > (
                SELECT subscription_date FROM event_participants
                WHERE event_id = N.event_id AND user_id = ?)
                AND display_date <= ?
                """;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, username);
            preparedStatement.setDate(4, Date.valueOf(currentTime));
            preparedStatement.executeUpdate();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    /**
     * method that checks whether a Notification with the given parameters exists
     * @param eventID  - Long
     * @param username - String
     * @param date     - LocalDate
     * @return - null, if the searched Notification doesn't exist
     *           Notification, otherwise
     */
    @Override
    public Notification findNotification(Long eventID, String username, LocalDate date) {
        String sql = "SELECT * FROM notifications where event_id = ? AND user_id = ? and display_date = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setLong(1, eventID);
            preparedStatement.setString(2, username);
            preparedStatement.setDate(3, Date.valueOf(date));
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return createEntityFromQuery(resultSet);
        }catch (SQLException exception){
            exception.printStackTrace();
        }

        return null;
    }
}
