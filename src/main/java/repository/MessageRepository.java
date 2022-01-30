package repository;

import model.Message;

import java.time.LocalDate;
import java.util.List;

/**
 * Message Repository interface
 */
public interface MessageRepository extends Repository<Long, Message>{
    /**
     * method that returns all the messages between 2 users, sorted ascending by the date when the message was
     * sent
     * @param username1 - String
     * @param username2 - String
     * @return - List(Message)
     */
    List<Message> getConversationsBetweenUsers(String username1, String username2);

    /**
     * method that returns a list of all the received Messages for a given user
     * @param username - String(id of the User entity)
     * @return - List(String)
     */
    List<Message> getReceivedMessagesForUser(String username);

    /**
     * method that returns a list of all sent Messages for a given user
     * @param username - String(id of the User entity)
     * @return - List(String)
     */
    List<Message> getSentMessagesForUser(String username);

    /**
     * method that returns the received Message entities for a certain User in a certain period of time
     * @param username - String(id of a User entity)
     * @param startDate - LocalDate
     * @param endDate - LocalDate
     * @return - List(Message)
     */
    List<Message> getReceivedMessagesInPeriod(String username, LocalDate startDate, LocalDate endDate);

    /**
     * method that returns a list of received Message entities for a given User that were received by a friend in a
     * certain period of time
     * @param username - String(id of a User entity)
     * @param friendUsername - String(id)
     * @return - List(Message)
     */
    List<Message> getReceivedMessagesFromFriendInPeriod(String username, String friendUsername, LocalDate startDate, LocalDate endDate);
}
