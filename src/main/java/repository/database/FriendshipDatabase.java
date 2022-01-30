package repository.database;

import exceptions.UserNotFoundException;
import model.Friendship;
import model.User;
import repository.FriendshipRepository;
import utils.OrderedTuple;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FriendshipDatabase extends AbstractDatabaseRepository<OrderedTuple<String>, Friendship> implements FriendshipRepository {

    public FriendshipDatabase(String url, String username, String password, String table) {
        super(url, username, password, table);
    }

    @Override
    public String updateQuery() {
        return "UPDATE " + table + " SET date=? WHERE user1=? AND user2=?";
    }

    @Override
    public void setUpdateQueryValues(PreparedStatement statement, Friendship entity) throws SQLException {
        statement.setString(1, entity.getId().getFirst());
        statement.setString(2, entity.getId().getSecond());
        statement.setDate(3, java.sql.Date.valueOf(entity.getDate()));
    }

    @Override
    public String findOneQuery() {
        return "select * from " + table + " where user1=? and user2=?";
    }

    @Override
    public void setFindOneQueryValues(PreparedStatement statement, OrderedTuple<String> stringOrderedTuple) throws SQLException {
        statement.setString(1, stringOrderedTuple.getFirst());
        statement.setString(2, stringOrderedTuple.getSecond());
    }

    @Override
    public String saveQuery() {
        return "insert into " + table + " values(?,?,?)";
    }

    @Override
    public void setSaveQueryValues(PreparedStatement statement, Friendship entity) throws SQLException {
        statement.setString(1, entity.getId().getFirst());
        statement.setString(2, entity.getId().getSecond());
        statement.setDate(3, java.sql.Date.valueOf(entity.getDate()));
    }

    @Override
    public String removeQuery() {
        return "delete from " + table + " where user1=? and user2=?";
    }

    @Override
    public void setRemoveQueryValues(PreparedStatement statement, OrderedTuple<String> stringOrderedTuple) throws SQLException {
        statement.setString(1, stringOrderedTuple.getFirst());
        statement.setString(2, stringOrderedTuple.getSecond());
    }

    @Override
    public Friendship createEntityFromQuery(ResultSet resultSet) throws SQLException{
        OrderedTuple<String> id = new OrderedTuple<>(resultSet.getString("user1"), resultSet.getString("user2"));
        LocalDate date = resultSet.getDate("date").toLocalDate();
        Friendship friendship = new Friendship(date);
        friendship.setId(id);
        return friendship;
    }

    /**
     * method that returns a list of all Friendship for a certain User that were created in a given period of time
     * @param username - String(id of the User)
     * @param startDate - LocalDate
     * @param endDate - LocalDate
     * @return - List(Friendship)
     */
    @Override
    public List<Friendship> getFriendshipsInPeriod(String username, LocalDate startDate, LocalDate endDate){
        String sql = "SELECT * FROM friendships F WHERE (user1 = ? OR user2 = ?) AND date BETWEEN ? AND ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, username);
            preparedStatement.setDate(3, Date.valueOf(startDate));
            preparedStatement.setDate(4, Date.valueOf(endDate));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Friendship> friendshipList = new ArrayList<>();
            while(resultSet.next()){
                friendshipList.add(createEntityFromQuery(resultSet));
            }
            return friendshipList;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public long getNewFriendsCount(String username) {
        String query = "SELECT COUNT(*) AS newFriendsCount FROM friendships F, users U " +
                "WHERE U.username = ? AND (F.user1 = ? OR F.user2 = ?) AND F.date >= U.lastloginat";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            long newFriendsCount = resultSet.getLong(1);
            return newFriendsCount;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return -1;
    }
}
