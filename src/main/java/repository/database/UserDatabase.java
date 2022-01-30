package repository.database;

import model.Message;
import model.User;
import repository.UserRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class UserDatabase extends AbstractDatabaseRepository<String, User> implements UserRepository {

    public UserDatabase(String url, String username, String password, String table) {
        super(url, username, password, table);
    }

    @Override
    public String updateQuery() {
        return "update " + table + " set firstname=?, lastname=?, where username=?";
    }

    @Override
    public void setUpdateQueryValues(PreparedStatement statement, User entity) throws SQLException {
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getId());
    }

    @Override
    public String findOneQuery() {
        return "select * from " + table + " where username=?";
    }

    @Override
    public void setFindOneQueryValues(PreparedStatement statement, String s) throws SQLException {
        statement.setString(1, s);
    }

    @Override
    public String saveQuery() {
        return "insert into " + table + " (username, password, salt, firstName, lastName) values (?,?,?,?,?)";
    }

    @Override
    public void setSaveQueryValues(PreparedStatement statement, User entity) throws SQLException {
        statement.setString(1, entity.getId());
        statement.setString(2, entity.getPassword());
        statement.setString(3, entity.getSaltValue());
        statement.setString(4, entity.getFirstName());
        statement.setString(5, entity.getLastName());
    }

    @Override
    public String removeQuery() {
        return "delete from " + table + " where username=?";
    }

    @Override
    public void setRemoveQueryValues(PreparedStatement statement, String s) throws SQLException {
        statement.setString(1, s);
    }

    @Override
    public User createEntityFromQuery(ResultSet resultSet) throws SQLException {
        String username = resultSet.getString("username");
        String saltValue = resultSet.getString("salt");
        String password = resultSet.getString("password");
        String firstName = resultSet.getString("firstname");
        String lastName = resultSet.getString("lastname");
        Date loginDate = resultSet.getDate("lastloginat");
        LocalDate lastLoginAt;
        if (loginDate != null)
            lastLoginAt = loginDate.toLocalDate();
        else
            lastLoginAt = null;
        User user = new User(password, saltValue, firstName, lastName, lastLoginAt);
        user.setId(username);
        return user;
    }

    private String getFriendListQuery(){
        return "select username, firstname, lastname from friendships" +
                " inner join users on user2 = username" +
                " where user1=?" +
                " union" +
                " select username, firstname, lastname from friendships" +
                " inner join users on user1 = username" +
                " where user2=?";
    }

    private void setFriendListQueryValues(PreparedStatement statement, String username) throws SQLException{
        statement.setString(1, username);
        statement.setString(2, username);
    }

    @Override
    public void updateLastLoginAt(String username, LocalDate newLastLoginAt) {
        String query = "UPDATE " + table + " SET lastloginat=? WHERE username=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setDate(1, Date.valueOf(newLastLoginAt));
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
    }

}