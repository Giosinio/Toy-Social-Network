package business;

import exceptions.FriendshipRequestException;
import exceptions.UserNotFoundException;
import model.*;
import model.dto.FriendDTO;
import model.dto.FriendshipDTO;
import model.dto.FriendshipRequestDTO;
import model.dto.UserDTO;
import repository.FriendshipRepository;
import repository.Repository;
import utils.OrderedTuple;
import utils.Tuple;
import utils.events.ChangeEventType;
import utils.events.FriendshipEvent;
import utils.events.FriendshipRequestEvent;
import utils.events.FriendshipServiceEvent;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.ValidationException;
import validators.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FriendshipService implements Observable<FriendshipServiceEvent> {
    private final FriendshipRepository friendshipRepository;
    private final Validator<OrderedTuple<String>, Friendship> friendshipValidator;
    private final Repository<String, User> userRepository;
    private final Repository<Tuple<String, String>, FriendshipRequest> friendshipRequestRepository;
    private final Validator<Tuple<String, String>, FriendshipRequest> friendshipRequestValidator;
    private final List<Observer<FriendshipServiceEvent>> observers = new ArrayList<>();

    public FriendshipService(FriendshipRepository friendshipRepository, Validator<OrderedTuple<String>, Friendship> friendshipValidator, Repository<String, User> userRepository, Repository<Tuple<String, String>, FriendshipRequest> friendshipRequestRepository, Validator<Tuple<String, String>, FriendshipRequest> friendshipRequestValidator) {
        this.friendshipRepository = friendshipRepository;
        this.friendshipValidator = friendshipValidator;
        this.userRepository = userRepository;
        this.friendshipRequestRepository = friendshipRequestRepository;
        this.friendshipRequestValidator = friendshipRequestValidator;
    }

    /**
     * method that receives two usernames, verifies if both usernames represent User entities; if so, stores
     * a new Friendship in the repository
     * @param username1 String
     * @param username2 String
     * @param date LocalDate
     * @return null, if the friendship was successfully stored
     *         Friendship entity, otherwise
     * @throws ValidationException if one of the usernames is invalid or the usernames are equal
     * @throws UserNotFoundException if at least one of the usernames doesn't belong to an existing User entity
     */
    public FriendshipDTO addFriendship(String username1, String username2, LocalDate date){
        OrderedTuple<String> idFriendship = new OrderedTuple<>(username1, username2);
        Friendship friendship = createEntity(idFriendship, List.of(date));
        friendshipValidator.validateEntity(friendship);
        User user1 = userRepository.findOne(idFriendship.getFirst());
        if(user1 == null)
            throw new UserNotFoundException("One of the users doesn't exist!");
        User user2 = userRepository.findOne(idFriendship.getSecond());
        if(user2 == null)
            throw new UserNotFoundException("One of the users doesn't exist!");
        Friendship result = friendshipRepository.save(friendship);
        if(result == null)
            notifyAll(new FriendshipEvent(friendship, ChangeEventType.ADD));
        return createFriendshipDTO(result);
    }

    /**
     * method that receives two usernames, tries to delete the corresponding Friendship entity from the repository; if
     * the deletion succeeded, updates friend lists of the affected User entities
     * @param username1 String
     * @param username2 String
     * @return Friendship, if the friendship was successfully deleted
     *         null, otherwise
     * @throws ValidationException if at least one of the username is invalid
     */
    public FriendshipDTO removeFriendship(String username1, String username2){
        OrderedTuple<String> friendshipID = new OrderedTuple<>(username1, username2);
        friendshipValidator.validateID(friendshipID);
        Friendship removedFriendship = friendshipRepository.remove(new OrderedTuple<>(username1, username2));
        if(removedFriendship != null)
            notifyAll(new FriendshipEvent(removedFriendship, ChangeEventType.REMOVE));
        return createFriendshipDTO(removedFriendship);
    }

    /**
     * method that searches a Friendship between the Users given by the usernames in the ID
     * @param id OrderedTuple of String that contains two usernames used to search for a Friendship between
     *           two Users
     * @return FriendshipDTO containing the information, if the Friendship was found; null otherwise
     */
    public FriendshipDTO findFriendship(OrderedTuple<String> id){
        friendshipValidator.validateID(id);
        Friendship friendship = friendshipRepository.findOne(id);
        return createFriendshipDTO(friendship);
    }

    /**
     * method that returns an iterable of FriendshipDTO entities
     * @return Iterable
     */
    public Iterable<FriendshipDTO> getAllFriendships(){
        List<FriendshipDTO> friendshipDTOS = new ArrayList<>();
        for(Friendship friendship : friendshipRepository.findAll()){
            friendshipDTOS.add(createFriendshipDTO(friendship));
        }
        return friendshipDTOS;
    }

    /**
     * returns a list of FriendDTOs of the User given by username
     * @param username the username of User for which friends are retrieved
     * @return Iterable containing FriendDTOs of the found friends
     */
    public Iterable<FriendDTO> getFriendsOfUser(String username) {
        Predicate<Friendship> isFirstUser = friendship -> friendship.getId().getFirst().equals(username);
        Predicate<Friendship> isSecondUser = friendship -> friendship.getId().getSecond().equals(username);
        Predicate<Friendship> userInFriendship = isFirstUser.or(isSecondUser);

        return friendshipRepository.findAll().stream()
                .filter(userInFriendship)
                .map(this::createFriendshipDTO)
                .map(x -> createFriendDTO(x, username))
                .collect(Collectors.toList());
    }

    public FriendDTO findFriendByName(String username, String friendName) {
        if (friendName.equals("")) {
            return null;
        }

        Predicate<Friendship> isFirstUser = friendship -> friendship.getId().getFirst().equals(username);
        Predicate<Friendship> isSecondUser = friendship -> friendship.getId().getSecond().equals(username);
        Predicate<Friendship> userInFriendship = isFirstUser.or(isSecondUser);
        Predicate<FriendDTO> hasName = friend ->
                (friend.getFriend().getFirstName() + " " + friend.getFriend().getLastName()).equals(friendName);

        Optional<FriendDTO> result = friendshipRepository.findAll().stream()
                .filter(userInFriendship)
                .map(this::createFriendshipDTO)
                .map(x -> createFriendDTO(x, username))
                .filter(hasName)
                .findFirst();
        if (result.isPresent())
            return result.get();
        return null;
    }

    public List<FriendDTO> filterFriendsByName(String username, String substring) {
        if (substring.equals("")) {
            return new LinkedList<FriendDTO>();
        }

        Predicate<Friendship> isFirstUser = friendship -> friendship.getId().getFirst().equals(username);
        Predicate<Friendship> isSecondUser = friendship -> friendship.getId().getSecond().equals(username);
        Predicate<Friendship> userInFriendship = isFirstUser.or(isSecondUser);
        Predicate<FriendDTO> hasSubstringInName = friend ->
                (friend.getFriend().getFirstName() + " " + friend.getFriend().getLastName())
                        .toLowerCase().contains(substring.toLowerCase());

        return friendshipRepository.findAll().stream()
                .filter(userInFriendship)
                .map(this::createFriendshipDTO)
                .map(x -> createFriendDTO(x, username))
                .filter(hasSubstringInName)
                .collect(Collectors.toList());
    }

    public List<UserDTO> findRecommendedFriends(String username, String substring) {
        Predicate<User> notSelf = user -> !user.getId().equals(username);
        Predicate<User> notFriends = user -> {
            OrderedTuple<String> friendshipID = new OrderedTuple<String>(username, user.getId());
            return findFriendship(friendshipID)==null;
        };
        Predicate<UserDTO> hasSubstringInName = userDTO ->
                userDTO.getFullName().toLowerCase().contains(substring.toLowerCase());
        Predicate<UserDTO> friendRequestNotSent = userDTO -> {
            Tuple<String, String> sentFriendRequestID = new Tuple<String, String>(username, userDTO.getUsername());
            if (friendshipRequestRepository.findOne(sentFriendRequestID)!=null)
                return false;
            Tuple<String, String> receivedFriendRequestID = new Tuple<String, String>(userDTO.getUsername(), username);
            if (friendshipRequestRepository.findOne(receivedFriendRequestID)!=null)
                return false;
            return true;
        };

        return userRepository.findAll().stream()
                .filter(notSelf)
                .filter(notFriends)
                .map(UserService::createUserDTO)
                .filter(hasSubstringInName)
                .filter(friendRequestNotSent)
                .collect(Collectors.toList());
    }

    /**
     * returns a list of FriendshipDTOs of the User given by username
     * @param username the username of User for which friendships are retrieved
     * @return Iterable containing FriendshipDTOs of the found Friendships
     */
    public Iterable<FriendshipDTO> getFriendshipsOfUser(String username){
        Predicate<Friendship> isFirstUser = friendship -> friendship.getId().getFirst().equals(username);
        Predicate<Friendship> isSecondUser = friendship -> friendship.getId().getSecond().equals(username);
        Predicate<Friendship> userInFriendship = isFirstUser.or(isSecondUser);

        return friendshipRepository.findAll().stream()
                .filter(userInFriendship)
                .map(this::createFriendshipDTO)
                .collect(Collectors.toList());
    }

    /**
     * returns a list of FriendshipDTOs including the User given by username and from the specified month
     * @param username the username of User for which Friendships are retrieved
     * @param month a natural number in the range 1-12 representing the month of the year
     * @return Iterable containing FriendshipDTOs of the found Friendships
     */
    public Iterable<FriendshipDTO> getFriendshipsOfUserFromMonth(String username, int month) {
        Predicate<Friendship> isFirstUser = friendship -> friendship.getId().getFirst().equals(username);
        Predicate<Friendship> isSecondUser = friendship -> friendship.getId().getSecond().equals(username);
        Predicate<Friendship> userInFriendship = isFirstUser.or(isSecondUser);
        Predicate<Friendship> friendshipInMonth = friendship -> friendship.getDate().getMonthValue() == month;
        Predicate<Friendship> friendshipCriteria = userInFriendship.and(friendshipInMonth);

        return friendshipRepository.findAll().stream()
                .filter(friendshipCriteria)
                .map(this::createFriendshipDTO)
                .collect(Collectors.toList());
    }

    /**
     * attempts to create a new FriendshipRequest from a User to another, given by their usernames
     * @param fromUsername String that represents the username of the sending User
     * @param toUsername String that represents the username of the receiving User
     * @throws ValidationException if one of the username is invalid or the usernames are equal
     * @throws UserNotFoundException if at least one of the usernames doesn't belong to an existing User entity
     * @throws FriendshipRequestException if a Friendship or FriendshipRequest between the two Users already exists
     */
    public void sendFriendshipRequest(String fromUsername, String toUsername) {
        Tuple<String, String> friendshipRequestID = new Tuple<>(fromUsername, toUsername);
        FriendshipRequest friendshipRequest = createFriendshipRequest(friendshipRequestID);
        friendshipRequestValidator.validateEntity(friendshipRequest);
        User fromUser = userRepository.findOne(fromUsername);
        if(fromUser == null) {
            throw new UserNotFoundException("Sending user doesn't exist!");
        }
        User toUser = userRepository.findOne(toUsername);
        if(toUser == null) {
            throw new UserNotFoundException("Receiving user doesn't exist!");
        }
        Tuple<String, String> mirroredFriendshipRequestID = new Tuple<>(toUsername, fromUsername);
        FriendshipRequest mirroredFriendshipRequest = friendshipRequestRepository.findOne(mirroredFriendshipRequestID);
        if (mirroredFriendshipRequest != null) {
            throw new FriendshipRequestException("You already have a friend request from this user!");
        }
        FriendshipRequest result = friendshipRequestRepository.findOne(friendshipRequestID);
        if (result != null) {
            throw new FriendshipRequestException("The friend request has already been sent!");
        }
        OrderedTuple<String> friendshipID = new OrderedTuple<>(fromUsername, toUsername);
        Friendship friendship = friendshipRepository.findOne(friendshipID);
        if (friendship != null) {
            throw new FriendshipRequestException("A friendship already exists between the users!");
        }
        result = friendshipRequestRepository.save(friendshipRequest);
        notifyAll(new FriendshipRequestEvent(result));
    }

    /**
     * returns a list of pending FriendshipRequestDTOs for User with given username
     * @param username a String representing the username of User for which pending FriendshipRequests are retrieved
     * @return Iterable containing FriendshipRequestDTOs of the found FriendshipRequests
     */
    public Iterable<FriendshipRequestDTO> getFriendshipRequestsToUser(String username) {
        Predicate<FriendshipRequest> isToUser = friendshipRequest -> friendshipRequest.getTo().equals(username);

        return friendshipRequestRepository.findAll().stream()
                .filter(isToUser)
                .map(this::createFriendshipRequestDTO)
                .collect(Collectors.toList());
    }

    /**
     * returns the count of new pending FriendshipRequestDTOs for User with given username
     * @param username a String representing the username of User for which pending FriendshipRequests are retrieved
     * @return long representing the count of FriendshipRequestDTOs created after the last login date of the User
     * @throws UserNotFoundException if given User cannot be found
     */
    public long getNewFriendshipRequestCount(String username) {
        User user = userRepository.findOne(username);
        if (user == null) {
            throw new UserNotFoundException("The given user cannot be found!");
        }

        Predicate<FriendshipRequest> isToUser = friendshipRequest -> friendshipRequest.getTo().equals(username);
        Predicate<FriendshipRequest> isNew = friendshipRequest -> friendshipRequest.getCreated().isAfter(user.getLastLoginAt())
                || friendshipRequest.getCreated().isEqual(user.getLastLoginAt());

        return friendshipRequestRepository.findAll().stream()
                .filter(isToUser.and(isNew))
                .count();
    }

    /**
     * accepts a pending FriendshipRequest and adds a Friendship between the Users
     * @param toUsername a String representing the username of the accepting User
     * @param fromUsername a String representing the username of the sending User
     * @throws ValidationException if one of the username is invalid or the usernames are equal
     * @throws UserNotFoundException if at least one of the usernames doesn't belong to an existing User entity
     * @throws FriendshipRequestException if there's no such pending
     * FriendshipRequest or a Friendship between the two Users already exists
     */
    public void acceptFriendshipRequest(String toUsername, String fromUsername) {
        Tuple<String, String> friendshipRequestID = new Tuple<>(fromUsername, toUsername);
        FriendshipRequest friendshipRequest = createFriendshipRequest(friendshipRequestID);
        friendshipRequestValidator.validateEntity(friendshipRequest);
        User fromUser = userRepository.findOne(fromUsername);
        if(fromUser == null) {
            throw new UserNotFoundException("Sending user doesn't exist!");
        }
        User toUser = userRepository.findOne(toUsername);
        if(toUser == null) {
            throw new UserNotFoundException("Receiving user doesn't exist!");
        }
        FriendshipRequest result = friendshipRequestRepository.remove(friendshipRequestID);
        if (result == null) {
            throw new FriendshipRequestException("There's no such pending friendship request!");
        }
        LocalDate date = LocalDate.now();
        FriendshipDTO friendshipDTO = addFriendship(fromUsername, toUsername, date);
        if (friendshipDTO != null) {
            throw new FriendshipRequestException("A friendship already exists between the users!");
        }
        notifyAll(new FriendshipRequestEvent(result));
    }

    /**
     * rejects a pending FriendshipRequest
     * @param toUsername a String representing the username of the rejecting User
     * @param fromUsername a String representing the username of the sending User
     * @throws ValidationException if one of the username is invalid or the usernames are equal
     * @throws UserNotFoundException if at least one of the usernames doesn't belong to an existing User entity
     * @throws FriendshipRequestException if there's no such pending FriendshipRequest
     */
    public void rejectFriendshipRequest(String toUsername, String fromUsername) {
        Tuple<String, String> friendshipRequestID = new Tuple<>(fromUsername, toUsername);
        FriendshipRequest friendshipRequest = createFriendshipRequest(friendshipRequestID);
        friendshipRequestValidator.validateEntity(friendshipRequest);
        User fromUser = userRepository.findOne(fromUsername);
        if(fromUser == null) {
            throw new UserNotFoundException("Sending user doesn't exist!");
        }
        User toUser = userRepository.findOne(toUsername);
        if(toUser == null) {
            throw new UserNotFoundException("Receiving user doesn't exist!");
        }
        FriendshipRequest result = friendshipRequestRepository.remove(friendshipRequestID);
        if (result == null) {
            throw new FriendshipRequestException("There's no such pending friendship request!");
        }
        notifyAll(new FriendshipRequestEvent(result));
    }

    /**
     * cancels a pending FriendshipRequest
     * @param fromUsername a String representing the username of the sending User
     * @param toUsername a String representing the username of the receiving User
     * @throws ValidationException if one of the username is invalid or the usernames are equal
     * @throws UserNotFoundException if at least one of the usernames doesn't belong to an existing User entity
     * @throws FriendshipRequestException if there's no such pending FriendshipRequest
     */
    public void cancelFriendshipRequest(String fromUsername, String toUsername) {
        Tuple<String, String> friendshipRequestID = new Tuple<>(fromUsername, toUsername);
        FriendshipRequest friendshipRequest = createFriendshipRequest(friendshipRequestID);
        friendshipRequestValidator.validateEntity(friendshipRequest);
        User fromUser = userRepository.findOne(fromUsername);
        if(fromUser == null) {
            throw new UserNotFoundException("Sending user doesn't exist!");
        }
        User toUser = userRepository.findOne(toUsername);
        if(toUser == null) {
            throw new UserNotFoundException("Receiving user doesn't exist!");
        }
        FriendshipRequest result = friendshipRequestRepository.remove(friendshipRequestID);
        if (result == null) {
            throw new FriendshipRequestException("There's no such pending friendship request!");
        }
        notifyAll(new FriendshipRequestEvent(result));
    }

    /**
     * returns a list of all FriendshipRequestDTOs from User with given username
     * @param username a String representing the username of User for which sent FriendshipRequests are retrieved
     * @return Iterable containing FriendshipRequestDTOs of the found FriendshipRequests
     */
    public Iterable<FriendshipRequestDTO> getFriendshipRequestsFromUser(String username) {
        Predicate<FriendshipRequest> isFromUser = friendshipRequest -> friendshipRequest.getFrom().equals(username);

        return friendshipRequestRepository.findAll().stream()
                .filter(isFromUser)
                .map(this::createFriendshipRequestDTO)
                .collect(Collectors.toList());
    }

    /**
     * method that creates a new Friendship entity from the given attributes
     * @param idFriendship identity of the friendship(OrderedTuple)
     * @param attributes the attributes of the new entity
     * @return Friendship
     */
    private Friendship createEntity(OrderedTuple<String> idFriendship, List<?> attributes) {
        Friendship friendship = new Friendship((LocalDate) attributes.get(0));
        friendship.setId(idFriendship);
        return friendship;
    }

    /**
     * method that returns a FriendshipDTO from a given Friendship entity
     * @param friendship - Friendship
     * @return - null, if the Friendship entity is null
     *           FriendshipDTO, otherwise
     */
    public FriendshipDTO createFriendshipDTO(Friendship friendship){
        if(friendship == null)
            return null;
        User user1 = userRepository.findOne(friendship.getId().getFirst());
        User user2 = userRepository.findOne(friendship.getId().getSecond());
        UserDTO user1DTO = new UserDTO(user1);
        UserDTO user2DTO = new UserDTO(user2);
        return new FriendshipDTO(user1DTO, user2DTO, friendship.getDate());
    }

    /**
     * private method that returns a FriendDTO from a given FriendshipDTO and username of the User it is for
     * @param friendship FriendshipDTO from which the essential information is extracted
     * @param username String that represents the username of the User for which the FriendDTO is created
     * @return FriendDTO which contains only the information relevant for the User
     */
    private FriendDTO createFriendDTO(FriendshipDTO friendship, String username){
        UserDTO friend = friendship.getFirstUser();
        if(friend.getUsername().equals(username))
            friend = friendship.getSecondUser();
        return new FriendDTO(friend, friendship.getDate());
    }

    /**
     * method that creates a new FriendshipRequest entity from the given ID
     * @param friendshipRequestID identity of the FriendshipRequest (Tuple)
     * @return Friendship
     */
    private FriendshipRequest createFriendshipRequest(Tuple<String, String> friendshipRequestID) {
        FriendshipRequest friendshipRequest = new FriendshipRequest(LocalDate.now());
        friendshipRequest.setId(friendshipRequestID);
        return friendshipRequest;
    }

    /**
     * private method that returns a DTO from a given FriendshipRequest entity
     * @param friendshipRequest FriendshipRequest
     * @return null, if the FriendshipRequest entity is null
     *         FriendshipRequestDTO, otherwise
     */
    private FriendshipRequestDTO createFriendshipRequestDTO(FriendshipRequest friendshipRequest) {
        if(friendshipRequest == null)
            return null;
        User fromUser = userRepository.findOne(friendshipRequest.getFrom());
        User toUser = userRepository.findOne(friendshipRequest.getTo());
        UserDTO fromUserDTO = new UserDTO(fromUser);
        UserDTO toUserDTO = new UserDTO(toUser);
        return new FriendshipRequestDTO(fromUserDTO, toUserDTO, friendshipRequest.getCreated());
    }

    @Override
    public void addObserver(Observer<FriendshipServiceEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<FriendshipServiceEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyAll(FriendshipServiceEvent event) {
        observers.forEach(x->x.update(event));
    }
}
