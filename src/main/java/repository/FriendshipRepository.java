package repository;

import exceptions.UserNotFoundException;
import model.Friendship;
import model.User;
import utils.OrderedTuple;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public interface FriendshipRepository extends Repository<OrderedTuple<String>, Friendship> {

    /**
     * method that returns a list of Friendship relationships created in a given period of time
     * @param username - String(User ID)
     * @param startDate - LocalDate
     * @param endDate - LocalDate
     * @return - List(Friendship)
     */
    List<Friendship> getFriendshipsInPeriod(String username, LocalDate startDate, LocalDate endDate);

    /**
     * returns the count of new friends for the User given by username
     * @param username the username of User for which friends are retrieved
     * @return long representing the number of new friends, made after the last login date, or -1 if there was an error
     */
    long getNewFriendsCount(String username);
}
