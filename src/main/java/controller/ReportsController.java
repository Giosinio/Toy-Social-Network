package controller;

import business.FriendshipService;
import business.MessageService;
import business.ReportService;
import business.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.dto.FriendDTO;
import model.dto.UserDTO;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import utils.Constants;
import utils.config.ApplicationContext;
import utils.events.FriendshipEvent;
import utils.events.FriendshipServiceEvent;
import utils.observer.Observer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

public class ReportsController implements Observer<FriendshipServiceEvent> {

    ObservableList<FriendDTO> friendsModel = FXCollections.observableArrayList();

    @FXML
    Button backButton;

    @FXML
    DatePicker startDatePicker1;
    @FXML
    DatePicker endDatePicker1;
    @FXML
    ComboBox<FriendDTO> friendComboBox;
    @FXML
    DatePicker startDatePicker2;
    @FXML
    DatePicker endDatePicker2;

    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private ReportService reportService;
    private ApplicationContext applicationContext;
    private UserDTO loggedUser;

    private void updateModel(){
        Iterable<FriendDTO> friendshipRequests = friendshipService.filterFriendsByName(loggedUser.getUsername(),
                friendComboBox.getEditor().getText());
        List<FriendDTO> requestDTOList = StreamSupport.stream(friendshipRequests.spliterator(), false).toList();
        friendsModel.setAll(requestDTOList);
    }

    @FXML
    public void initialize() {

        StringConverter<FriendDTO> friendConverter = new StringConverter<FriendDTO>() {
            @Override
            public String toString(FriendDTO object) {
                if (object == null)
                    return null;
                return object.getFriend().getFirstName() + " " + object.getFriend().getLastName();
            }

            @Override
            public FriendDTO fromString(String string) {
                return friendshipService.findFriendByName(loggedUser.getUsername(), string);
            }
        };

        StringConverter<LocalDate> dateConverter = new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                return object.format(Constants.DISPLAY_DATE_FORMATTER);
            }

            @Override
            public LocalDate fromString(String string) {
                return LocalDate.parse(string, Constants.DISPLAY_DATE_FORMATTER);
            }
        };

        startDatePicker1.setConverter(dateConverter);
        endDatePicker1.setConverter(dateConverter);
        startDatePicker1.setValue(LocalDate.now().minusMonths(1));
        endDatePicker1.setValue(LocalDate.now());

        friendComboBox.setConverter(friendConverter);
        TextFields.bindAutoCompletion(friendComboBox.getEditor(), new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<FriendDTO>>() {
            @Override
            public Collection<FriendDTO> call(AutoCompletionBinding.ISuggestionRequest param) {
                return friendshipService.filterFriendsByName(loggedUser.getUsername(), param.getUserText());
            }
        }, friendConverter).setDelay(300);
        friendComboBox.setItems(friendsModel);

        startDatePicker2.setConverter(dateConverter);
        endDatePicker2.setConverter(dateConverter);
        startDatePicker2.setValue(LocalDate.now().minusMonths(1));
        endDatePicker2.setValue(LocalDate.now());

    }

    public void onGenerateButton1Pressed() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save report");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                applicationContext.getWindowController().setLoadingScreenVisible(true);
                LocalDate startDate = startDatePicker1.getValue();
                LocalDate endDate = endDatePicker1.getValue();
                reportService.newFriendsAndMessagesInPeriod(loggedUser, file.getAbsolutePath(), startDate, endDate);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report was generated successfully!");
                alert.showAndWait();
            } catch (IOException | RuntimeException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error occured while generating the report: " + e.getMessage());
                alert.showAndWait();
                return;
            }
            applicationContext.getWindowController().setLoadingScreenVisible(false);
        }
    }

    public void onGenerateButton2Pressed() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save report");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                applicationContext.getWindowController().setLoadingScreenVisible(true);
                FriendDTO friendDTO = friendComboBox.getValue();
                if (friendDTO == null) {
                    throw new IllegalArgumentException("The friend wasn't properly selected!");
                }
                UserDTO friend = new UserDTO(friendDTO.getFriend().getUsername(), friendDTO.getFriend().getFirstName(),
                        friendDTO.getFriend().getLastName(), null);
                LocalDate startDate = startDatePicker2.getValue();
                LocalDate endDate = endDatePicker2.getValue();
                reportService.newMessagesFromFriendInPeriod(loggedUser, file.getAbsolutePath(), friend, startDate, endDate);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report was generated successfully!");
                alert.showAndWait();
            } catch (IOException | RuntimeException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error occured while generating the report: " + e.getMessage());
                alert.showAndWait();
                return;
            }
            applicationContext.getWindowController().setLoadingScreenVisible(false);
        }
    }

    public void onBackButtonPressed() {
        Stage stage = applicationContext.getPrimaryStage();
        applicationContext.getWindowController().setTitle("ToySocialNetwork");

        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/mainMenuView.fxml"));
            Parent layout = root.load();
            Scene mainMenu = new Scene(layout);

            MainMenuController mainMenuController = root.getController();
            mainMenuController.setApplicationContext(applicationContext);
            mainMenuController.setLoggedUser(loggedUser);

            stage.setScene(mainMenu);
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    public void setLoggedUser(UserDTO user) {
        loggedUser = user;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.userService = applicationContext.getUserService();
        this.friendshipService = applicationContext.getFriendshipService();
        friendshipService.addObserver(this);
        updateModel();
        this.messageService = applicationContext.getMessageService();
        this.reportService = applicationContext.getReportService();
    }

    @Override
    public void update(FriendshipServiceEvent event) {
        updateModel();
    }

}
