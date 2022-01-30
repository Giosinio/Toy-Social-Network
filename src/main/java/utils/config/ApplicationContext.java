package utils.config;

import business.*;
import controller.WindowController;
import javafx.stage.Stage;

import java.util.Properties;

public class ApplicationContext {
    private static final Properties properties = Config.getProperties();

    public static Properties getProperties() {
        return properties;
    }

    private final UserService userService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final EventService eventService;
    private final EventParticipationService eventParticipationService;
    private final ReportService reportService;
    private final NotificationService notificationService;
    private final PageService pageService;
    private final Stage primaryStage;

    private WindowController windowController;

    public ApplicationContext(UserService userService,
                              FriendshipService friendshipService,
                              MessageService messageService,
                              EventService eventService,
                              EventParticipationService eventParticipationService,
                              ReportService reportService,
                              NotificationService notificationService,
                              PageService pageService,
                              Stage primaryStage,
                              WindowController windowController) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.eventService = eventService;
        this.eventParticipationService = eventParticipationService;
        this.reportService = reportService;
        this.notificationService = notificationService;
        this.pageService = pageService;
        this.primaryStage = primaryStage;
        this.windowController = windowController;
    }

    public UserService getUserService() {
        return userService;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public EventParticipationService getEventParticipationService() {
        return eventParticipationService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public PageService getPageService() {
        return pageService;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setWindowController(WindowController windowController) {
        this.windowController = windowController;
    }

    public WindowController getWindowController() {
        return windowController;
    }
}
