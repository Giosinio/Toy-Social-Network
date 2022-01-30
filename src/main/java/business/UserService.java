package business;

import exceptions.WrongPasswordException;
import model.User;
import model.dto.FriendDTO;
import model.dto.UserDTO;
import repository.Repository;
import repository.UserRepository;
import utils.PasswordEncryption;
import validators.ValidationException;
import validators.Validator;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service that
 */
public class UserService {
    private final UserRepository userRepository;
    private final Validator<String, User> userValidator;
    private static int saltLength = 32;

    public UserService(UserRepository userRepository, Validator<String, User> userValidator){
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    /**
     * method that receive the attributes of a user and uses serviceUser to add it in the repository
     * @param username - String
     * @param password - String
     * @param firstName - String
     * @param lastName - String
     * @return - UserDTO, if the user already exists
     *           null, if the user was successfully stored in the repository
     * @throws ValidationException - if the User created with the given attributes is invalid
     */
    public UserDTO addUser(String username, String password, String firstName, String lastName) {
        String saltValue = PasswordEncryption.getSaltValue(saltLength);
        User user = createEntity(username, Arrays.asList(password, saltValue, firstName, lastName, null));
        userValidator.validateEntity(user);
        String encryptedPassword = PasswordEncryption.generateEncryptedPassword(password, saltValue);
        user.setPassword(encryptedPassword);
        User result = userRepository.save(user);
        return createUserDTO(result);
    }

    /**
     * method that receives the id of a user and uses serviceUser to delete it from the repository
     * @param username - the identity of the user
     * @return - User, if the user was successfully deleted from the repository
     *           null, if there wasn't any User entity with the given id
     * @throws ValidationException - ife the identity is invalid
     */
    public User removeUser(String username){
        userValidator.validateID(username);
        return userRepository.remove(username);
    }

    /**
     * method that returns the User with the given username(id)
     * @param username - String
     * @return - User entity, if the User has been found
     *           null, otherwise
     * @throws ValidationException - if the username is invalid
     */
    public UserDTO findUser(String username){
        userValidator.validateID(username);
        User foundUser = userRepository.findOne(username);
        if(foundUser != null)
            return new UserDTO(userRepository.findOne(username));
        return null;
    }

    /**
     * method that receives a username and a password of an account and a new firstname, lastname and login date,
     * validates the data and if it is valid, checks if the password corresponds to the given username; if so, changes
     * the name of the account with the given one
     * @param username - String
     * @param password - String
     * @param firstName - String
     * @param lastName - String
     * @return - null, if the update was successfull
     *           UserDTO entity, if there isn't any entity with the given username
     * @throws WrongPasswordException - if the password doesn't correspond to any account
     */
    public UserDTO updateUser(String username, String password, String firstName, String lastName) {
        User user = createEntity(username, Arrays.asList(password, null, firstName, lastName));
        userValidator.validateEntity(user);
        if(!checkPassword(username, password))
            throw new WrongPasswordException("The password doesn't correspond to your account!");
        User result = userRepository.update(user);
        return new UserDTO(result);
    }

    /**
     * method that returns an iterable of all users stored in repository
     * @return - Iterable
     */
    public Iterable<UserDTO> getAllUsers(){
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : userRepository.findAll()){
            userDTOS.add(new UserDTO(user));
        }
        return userDTOS;
    }

    /**
     * returns a list of all users containing given string in name
     * @return list of UserDTOs
     */
    public List<UserDTO> filterUsersByName(String substring) {
        if (substring.equals("")) {
            return new LinkedList<UserDTO>();
        }

        Predicate<User> hasSubstringInName = user ->
                (user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(substring.toLowerCase());

        return userRepository.findAll().stream()
                .filter(hasSubstringInName)
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * returns a list of all users containing given string in name
     * @return list of UserDTOs
     */
    public UserDTO findUserByName(String name) {
        if (name.equals("")) {
            return null;
        }

        Predicate<UserDTO> hasName = user ->
                (user.getFirstName() + " " + user.getLastName()).equals(name);

        Optional<UserDTO> result = userRepository.findAll().stream()
                .map(UserService::createUserDTO)
                .filter(hasName)
                .findFirst();
        if (result.isPresent())
            return result.get();
        return null;
    }

    /**
     * method that creates a new User entity from the given attributes
     * @param username - ID of the entity to be created
     * @param attributes - the arguments of the entity
     * @return - new E instance
     */
    private User createEntity(String username, List<?> attributes) {
        String password = (String)attributes.get(0);
        String saltValue = (String)attributes.get(1);
        String firstName = (String)attributes.get(2);
        String lastName = (String)attributes.get(3);
        User newUser = new User(password, saltValue, firstName, lastName, null);
        newUser.setId(username);
        return newUser;
    }


    /**
     * method that checks if the provided password corresponds to the given account
     * @param username - String(id of the account)
     * @param providedPassword - String
     * @return - true, if the password is correct
     *           false, otherwise
     */
    public boolean checkPassword(String username, String providedPassword){
        User user = userRepository.findOne(username);
        if(user == null)
            return false;
        String securedPassword = user.getPassword();
        String saltValue = user.getSaltValue();
        String newSecuredPassword = PasswordEncryption.generateEncryptedPassword(providedPassword, saltValue);
        boolean result = newSecuredPassword.equalsIgnoreCase(securedPassword);
        if (result == true) {
            userRepository.updateLastLoginAt(username, LocalDate.now());
        }
        return result;
    }

    public static UserDTO createUserDTO(User user){
        if(user == null)
            return null;
        return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getLastLoginAt());
    }
}
