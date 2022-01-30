package repository.database;

import org.postgresql.util.PSQLException;
import model.Message;
import repository.PagingMessageRepository;
import repository.paging.Page;
import repository.paging.PageImplementation;
import repository.paging.Pageable;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDatabase implements PagingMessageRepository {
    private Connection connection;

    public MessageDatabase(String url, String username, String password) {
        try{
            connection = DriverManager.getConnection(url, username, password);
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    /**
     * @param id -the id of the entity to be returned
     *                id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    @Override
    public Message findOne(Long id) {
        String sql = "select * from messages where id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return createEntityFromQuery(connection, resultSet);
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that returns all Message entities
     * @return List(Message)
     */
    @Override
    public List<Message> findAll() {
        Set<Message> entities = new HashSet<>();
        String sql = "SELECT * FROM messages";
        try(PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery()){

            while(resultSet.next()){
                Message currentMessage = createEntityFromQuery(connection, resultSet);
                entities.add(currentMessage);
            }
            return entities.stream().toList();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved(always)
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    @Override
    public Message save(Message entity) {
        String sql = "insert into messages(from_user, message, subject, replies_to, sent_date) values(?,?,?,?,?)";
        try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, entity.getFrom());
            statement.setString(2, entity.getMessage());
            statement.setString(3, entity.getSubject());
            if(entity.getRepliesTo() == null)
                statement.setObject(4, null);
            else
                statement.setLong(4, entity.getRepliesTo());
            statement.setTimestamp(5, Timestamp.valueOf(entity.getSendDate()));
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            }
            String sqlRecipients = "insert into recipients(user_id, message_id) values(?, ?)";
            PreparedStatement statement1 = connection.prepareStatement(sqlRecipients);
            statement1.setLong(2, entity.getId());
            for(String recipient : entity.getRecipients()){
                statement1.setString(1, recipient);
                try {
                    statement1.executeUpdate();
                } catch (PSQLException exception){
                    System.out.println("The message is already added!");
                }
            }
        } catch(SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that saves a new entity into repository
     *
     * @param integer - identity of the object to be searched
     * @return null - if the searched entity is not saved
     * otherwise returns the entity and deletes it from the repo
     * @throws IllegalArgumentException if the given id is null
     */
    @Override
    public Message remove(Long integer) {
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
    public Message update(Message entity) {
        String sql = "update messages set message=?, subject=?, sent_date=? where id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, entity.getMessage());
            statement.setString(2, entity.getSubject());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getSendDate()));
            statement.setLong(4, entity.getId());
            int affectedRows = statement.executeUpdate();
            if(affectedRows == 1)
                return null;
            return entity;
        } catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that sets the list of recipients of a given message, searching in database
     * @param message - Message
     */
    public List<String> getRecipientsForMessage(Connection connection, Message message) throws SQLException{
        String sqlRecipients = "select user_id from recipients where message_id=?";
        PreparedStatement statement1 = connection.prepareStatement(sqlRecipients);
        statement1.setLong(1, message.getId());
        ResultSet resultSet = statement1.executeQuery();
        List<String> recipients = new ArrayList<>();
        while(resultSet.next()){
            String recipient = resultSet.getString("user_id");
            recipients.add(recipient);
        }
        return recipients;
    }

    /**
     * method that creates a Message entity from a ResultSet(after a database query)
     * @param resultSet - ResultSet
     * @return - Message
     * @throws SQLException - if the columnLabel is invalid
     */
    private Message createEntityFromQuery(Connection connection, ResultSet resultSet) throws SQLException{
        Long id = resultSet.getLong("id");
        String from = resultSet.getString("from_user");
        String message = resultSet.getString("message");
        LocalDateTime sentDate = resultSet.getTimestamp("sent_date").toLocalDateTime();
        String subject = resultSet.getString("subject");
        Long repliesTo = resultSet.getLong("replies_to");
        Message newMessage = new Message(from, null, message, subject, sentDate, repliesTo);
        newMessage.setId(id);
        List<String> recipients = getRecipientsForMessage(connection, newMessage);
        newMessage.setRecipients(recipients);
        return newMessage;
    }

    /**
     * method that returns all the messages between 2 users, sorted ascending by the date when the message was
     * sent
     *
     * @param username1 - String
     * @param username2 - String
     * @return - List(Message)
     */
    @Override
    public List<Message> getConversationsBetweenUsers(String username1, String username2) {
        String sql = "SELECT * FROM messages M INNER JOIN recipients R ON M.id = R.message_id WHERE M.from_user = ? AND R.user_id = ? OR M.from_user = ? AND R.user_id = ? ORDER BY M.sent_date";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username1);
            preparedStatement.setString(2, username2);
            preparedStatement.setString(3, username2);
            preparedStatement.setString(4, username1);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Message> conversation = new ArrayList<>();
            while(resultSet.next()){
                conversation.add(createEntityFromQuery(connection, resultSet));
            }
            return conversation;
        }catch (SQLException exception){
            exception.printStackTrace();
        }

        return null;
    }

    private List<Message> executeCommonReceivedSentMessages(String sql, String username){
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Message> messages = new ArrayList<>();
            while(resultSet.next()){
                messages.add(createEntityFromQuery(connection, resultSet));
            }
            return messages;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Message> getReceivedMessagesForUser(String username) {
        String sql = "SELECT DISTINCT M.id, M.from_user, M.message, M.subject, M.sent_date, M.replies_to FROM messages M INNER JOIN recipients R ON M.id = R.message_id WHERE R.user_id = ? ORDER BY M.sent_date DESC";
        return executeCommonReceivedSentMessages(sql, username);
    }

    @Override
    public List<Message> getSentMessagesForUser(String username) {
        String sql = "SELECT DISTINCT M.id, M.from_user, M.message, M.subject, M.sent_date, M.replies_to FROM messages M INNER JOIN recipients R ON M.id = R.message_id WHERE M.from_user = ? ORDER BY M.sent_date DESC";
        return executeCommonReceivedSentMessages(sql, username);
    }

    /**
     * method that returns the received Message entities for a certain User in a certain period of time
     *
     * @param username  - String(id of a User entity)
     * @param startDate - LocalDate
     * @param endDate   - LocalDate
     * @return - List(Message)
     */
    @Override
    public List<Message> getReceivedMessagesInPeriod(String username, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT M.id, M.from_user, M.message, M.subject, M.sent_date, M.replies_to FROM messages M INNER JOIN recipients R on M.id = R.message_id WHERE R.user_id = ? AND M.sent_date BETWEEN ? AND ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setDate(2, Date.valueOf(startDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Message> messageList = new ArrayList<>();
            while(resultSet.next()){
                messageList.add(createEntityFromQuery(connection, resultSet));
            }
            return messageList;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that returns a list of received Message entities for a given User that were received by a friend in a
     * certain period of time
     *
     * @param username - String(id of a User entity)
     * @param friendUsername - String(id)
     * @param startDate - LocalDate
     * @param endDate - LocalDate
     * @return - List(Message)
     */
    @Override
    public List<Message> getReceivedMessagesFromFriendInPeriod(String username, String friendUsername, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT M.id, M.from_user, M.message, M.subject, M.sent_date, M.replies_to FROM messages M INNER JOIN recipients R on M.id = R.message_id WHERE M.from_user = ? AND R.user_id = ? AND M.sent_date BETWEEN ? AND ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, friendUsername);
            preparedStatement.setString(2, username);
            preparedStatement.setDate(3, Date.valueOf(startDate));
            preparedStatement.setDate(4, Date.valueOf(endDate));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Message> messageList = new ArrayList<>();
            while(resultSet.next()){
                messageList.add(createEntityFromQuery(connection, resultSet));
            }
            return messageList;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that returns a required page(a part) of the received messages for a given User
     * @param username - String
     * @param pageable - Pageable
     * @return - Page
     */
    @Override
    public Page<Message> getReceivedMessagesForUser(String username, Pageable pageable) {
        String sql = "SELECT DISTINCT M.id, M.from_user, M.message, M.subject, M.sent_date, M.replies_to FROM messages M INNER JOIN recipients R ON M.id = R.message_id WHERE R.user_id = ? ORDER BY M.sent_date DESC OFFSET ? LIMIT ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, pageable.getPageNumber() * pageable.getPageSize());
            preparedStatement.setInt(3, pageable.getPageSize());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Message> messages = new ArrayList<>();
            while(resultSet.next()){
                messages.add(createEntityFromQuery(connection, resultSet));
            }
            return new PageImplementation<>(pageable, messages.stream());
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * method that returns a required page(a part) of sent messages for a given User
     * @param username - String
     * @param pageable - Pageable
     * @return - Page
     */
    @Override
    public Page<Message> getSentMessagesForUser(String username, Pageable pageable) {
        String sql = "SELECT DISTINCT M.id, M.from_user, M.message, M.subject, M.sent_date, M.replies_to FROM messages M INNER JOIN recipients R ON M.id = R.message_id WHERE M.from_user = ? ORDER BY M.sent_date DESC OFFSET ? LIMIT ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, pageable.getPageNumber() * pageable.getPageSize());
            preparedStatement.setInt(3, pageable.getPageSize());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Message> messages = new ArrayList<>();
            while(resultSet.next()){
                messages.add(createEntityFromQuery(connection, resultSet));
            }
            return new PageImplementation<>(pageable, messages.stream());
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    private int getNoMessagesForUser(String sql, String username){
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            int noMessages = 0;
            if(resultSet.next())
                noMessages = resultSet.getInt("no_messages");
            return noMessages;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }

        return 0;
    }

    /**
     * method that returns the number of received messages for a given User
     * @param username - String(id)
     * @return - int
     */
    @Override
    public int getNoReceivedMessagesForUser(String username) {
        String sql = "SELECT COUNT(*) AS no_messages FROM Recipients WHERE user_id = ?";
        return getNoMessagesForUser(sql, username);
    }

    /**
     * method that returns the number of sent messages for a given User
     * @param username - String(id)
     * @return - int
     */
    @Override
    public int getNoSentMessagesForUser(String username) {
        String sql = "SELECT COUNT(*) AS no_messages FROM messages WHERE from_user = ?";
        return getNoMessagesForUser(sql, username);
    }
}