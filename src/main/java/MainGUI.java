import business.*;
import controller.LoginController;
import controller.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.*;
import repository.*;
import model.FriendshipRequest;
import model.Message;
import model.User;
import repository.database.*;
import repository.database.FriendshipDatabase;
import repository.database.FriendshipRequestDatabase;
import repository.database.MessageDatabase;
import repository.database.UserDatabase;
import utils.Constants;
import utils.Tuple;
import utils.config.ApplicationContext;
import validators.*;

import java.io.IOException;

public class MainGUI extends Application {

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    EventService eventService;
    EventParticipationService eventParticipationService;
    ReportService reportService;
    NotificationService notificationService;
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
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
        //networkService = new NetworkService(userRepository, friendshipRepository);
        messageService = new MessageService(userRepository, userValidator, messageRepository, messageValidator);
        messageService.setPageSize(Constants.NUMBER_MESSAGES_ON_PAGE);
        eventService = new EventService(eventRepository, eventParticipationRepository, eventValidator);
        eventService.setPageSize(Constants.NUMBER_EVENTS_ON_PAGE);
        eventParticipationService = new EventParticipationService(userRepository, eventRepository, eventParticipationRepository, notificationRepository, eventParticipationValidator);
        eventParticipationService.setPageSize(Constants.NUMBER_MY_EVENTS_ON_PAGE);
        reportService = new ReportService(userRepository, userValidator, friendshipRepository, friendshipValidator, messageRepository, messageValidator);
        notificationService = new NotificationService(userRepository, eventRepository, notificationRepository, notificationValidator);
        PageService pageService = new PageService(userRepository, friendshipRepository, friendshipService, messageService, eventParticipationService);

        applicationContext = new ApplicationContext(
                userService,
                friendshipService,
                messageService,
                eventService,
                eventParticipationService,
                reportService,
                notificationService,
                pageService,
                primaryStage,
                null
        );

        initView(primaryStage);
        primaryStage.setWidth(1300);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
        primaryStage.show();

    }

    private void initView(Stage primaryStage) throws IOException {

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader root = new FXMLLoader();
        root.setLocation(getClass().getResource("views/loginWindow.fxml"));
        Parent layout = root.load();
        Scene loginScreen = new Scene(layout);
        primaryStage.setScene(loginScreen);

        WindowController windowController = root.getController();
        applicationContext.setWindowController(windowController);
        applicationContext.getWindowController().setTitle("Login");
        FXMLLoader contentFXMLLoader = new FXMLLoader(getClass().getResource("views/loginView.fxml"));
        Parent windowRoot = contentFXMLLoader.load();
        windowController.initWindow();
        windowController.setContents(windowRoot, 50d, null, 50d, 50d);
        LoginController loginController = contentFXMLLoader.getController();
        loginController.setApplicationContext(applicationContext);

    }
}
