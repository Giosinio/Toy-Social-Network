package repository.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import model.FriendshipRequest;
import utils.Tuple;

import java.sql.*;

public class FriendshipRequestDatabase extends AbstractDatabaseRepository<Tuple<String, String>, FriendshipRequest>{

    public FriendshipRequestDatabase(String url, String username, String password, String table) {
        super(url, username, password, table);
    }

    @Override
    public String updateQuery() {
        return "UPDATE " + table + " SET created=? WHERE from_username=? AND to_username=?";
    }

    @Override
    public void setUpdateQueryValues(PreparedStatement statement, FriendshipRequest entity) throws SQLException {
        statement.setDate(1, Date.valueOf(entity.getCreated()));
        statement.setString(2, entity.getFrom());
        statement.setString(3, entity.getTo());
    }

    @Override
    public String findOneQuery() {
        return "SELECT * FROM " + table + " WHERE from_username=? AND to_username=?";
    }

    @Override
    public void setFindOneQueryValues(PreparedStatement statement, Tuple<String, String> stringTuple) throws SQLException {
        statement.setString(1, stringTuple.getFirst());
        statement.setString(2, stringTuple.getSecond());
    }

    @Override
    public String saveQuery() {
        return "INSERT INTO " + table + "(from_username, to_username, created) VALUES(?,?,?)";
    }

    @Override
    public void setSaveQueryValues(PreparedStatement statement, FriendshipRequest entity) throws SQLException {
        statement.setString(1, entity.getFrom());
        statement.setString(2, entity.getTo());
        statement.setDate(3, Date.valueOf(entity.getCreated()));
    }

    @Override
    public String removeQuery() {
        return "DELETE FROM " + table + " WHERE from_username=? AND to_username=?";
    }

    @Override
    public void setRemoveQueryValues(PreparedStatement statement, Tuple<String, String> stringTuple) throws SQLException {
        statement.setString(1, stringTuple.getFirst());
        statement.setString(2, stringTuple.getSecond());
    }

    @Override
    public FriendshipRequest createEntityFromQuery(ResultSet resultSet) throws SQLException{
        Tuple<String, String> id = new Tuple<>(resultSet.getString("from_username"), resultSet.getString("to_username"));
        LocalDate created = resultSet.getDate("created").toLocalDate();
        FriendshipRequest friendshipRequest = new FriendshipRequest(created);
        friendshipRequest.setId(id);
        return friendshipRequest;
    }

}
