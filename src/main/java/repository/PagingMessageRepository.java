package repository;

import model.Message;
import repository.paging.Page;
import repository.paging.Pageable;

import java.util.List;

public interface PagingMessageRepository extends MessageRepository {
    /**
     * method that returns a required page(a part) of the received messages for a given User
     * @param username - String
     * @param pageable - Pageable
     * @return - Page
     */
    Page<Message> getReceivedMessagesForUser(String username, Pageable pageable);

    /**
     * method that returns a required page(a part) of the sent messages for a given User
     * @param username - String
     * @param pageable - Pageable
     * @return - Page
     */
    Page<Message> getSentMessagesForUser(String username, Pageable pageable);

    /**
     * method that returns the number of received messages for a given User
     * @param username - String(id)
     * @return - int
     */
    int getNoReceivedMessagesForUser(String username);

    /**
     * method that returns the number of sent messages for a given User
     * @param username - String(id)
     * @return - int
     */
    int getNoSentMessagesForUser(String username);
}
