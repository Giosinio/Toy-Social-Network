import business.*;
import model.*;
import repository.*;
import repository.database.*;
import repository.database.FriendshipDatabase;
import repository.database.FriendshipRequestDatabase;
import ui.UserInterface;
import utils.Constants;
import utils.Tuple;
import utils.config.ApplicationContext;
import validators.*;
import ui.CommonInterface;

public class Main {

    public static UserService userService;
    public static FriendshipService friendshipService;
    public static NetworkService networkService;
    public static MessageService messageService;
    public static EventService eventService;
    public static EventParticipationService eventParticipationService;
    public static NotificationService notificationService;

    public static void main(String[] args) {
        switch (args.length) {
            case 0:
                MainGUI.main(args);
                break;
            case 1:
                switch (args[0].toLowerCase()) {
                    case "ui", "userinterface" -> runUserInterface();
                    case "admin", "commoninterface" -> runCommonInterface();
                    case "gui" -> MainGUI.main(args);
                }
                break;
        }
    }

    public static void runUserInterface() {
        initServices();
        UserInterface userInterface = new UserInterface(userService, friendshipService, messageService);
        userInterface.run();
    }

    public static void runCommonInterface() {
        initServices();
        CommonInterface commonInterface = new CommonInterface(userService, friendshipService, networkService, messageService);
        commonInterface.run();
    }

    public static void initServices() {
        String bdURL = ApplicationContext.getProperties().getProperty("data.db.url");
        String bdUsername = ApplicationContext.getProperties().getProperty("data.db.username");
        String bdPassword = ApplicationContext.getProperties().getProperty("data.db.password");
        UserValidator userValidator = new UserValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        FriendshipRequestValidator friendshipRequestValidator = new FriendshipRequestValidator();
        Validator<Long, Message> messageValidator = new MessageValidator();
        Validator<Long, Event> eventValidator = new EventValidator();
        Validator<Tuple<Long, String>, EventParticipation> eventParticipationValidator = new EventParticipationValidator();
        Validator<Long, Notification> notificationValidator = new NotificationValidator();
        //Repository<String, User> userRepository = new UserDatabase(bdURL, bdUsername, bdPassword, "users");
        UserRepository userRepository = new UserDatabase(bdURL, bdUsername, bdPassword, "users");
        FriendshipRepository friendshipRepository = new FriendshipDatabase(bdURL, bdUsername, bdPassword, "friendships");
        Repository<Tuple<String, String>, FriendshipRequest> friendshipRequestRepository = new FriendshipRequestDatabase(bdURL, bdUsername, bdPassword, "friendship_requests");
        PagingMessageRepository messageRepository = new MessageDatabase(bdURL, bdUsername, bdPassword);
        PagingEventRepository eventRepository = new EventDatabase(bdURL, bdUsername, bdPassword, "events");
        PagingEventParticipationRepository eventParticipationRepository = new EventParticipationDatabase(bdURL, bdUsername, bdPassword, "event_participants");
        NotificationRepository notificationRepository = new NotificationDatabase(bdURL, bdUsername, bdPassword, "notifications");
        userService = new UserService(userRepository, userValidator);
        friendshipService = new FriendshipService(friendshipRepository, friendshipValidator, userRepository, friendshipRequestRepository, friendshipRequestValidator);
        networkService = new NetworkService(userRepository, friendshipRepository);
        messageService = new MessageService(userRepository, userValidator, messageRepository, messageValidator);
        eventService = new EventService(eventRepository, eventParticipationRepository, eventValidator);
        eventService.setPageSize(Constants.NUMBER_EVENTS_ON_PAGE);
        eventParticipationService = new EventParticipationService(userRepository, eventRepository, eventParticipationRepository, notificationRepository, eventParticipationValidator);
        notificationService = new NotificationService(userRepository, eventRepository, notificationRepository, notificationValidator);
        eventParticipationService.setPageSize(Constants.NUMBER_MY_EVENTS_ON_PAGE);
    }
}