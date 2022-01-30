package controller;

import business.FriendshipService;
import business.MessageService;
import business.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.dto.MessageDTO;
import model.dto.UserDTO;
import org.controlsfx.control.textfield.TextFields;
import utils.Constants;
import utils.config.ApplicationContext;
import utils.events.MessageEvent;
import utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MessagesController implements Observer<MessageEvent> {

    private static class MessageCell extends VBox{
        private final MessageDTO messageDTO;
        private MessageCell(MessageDTO messageDTO){
            super();
            this.setSpacing(5.0);
            this.messageDTO = messageDTO;
            Label labelUser = new Label();
            Label labelSubject = new Label();
            labelUser.setText(messageDTO.getFrom().getFullName() +
                    " (" + messageDTO.getSendDate().format(Constants.SENT_DATE_MESSAGE) + ")");
            labelSubject.setText(messageDTO.getSubject());
            this.getChildren().addAll(labelUser, labelSubject);
        }

        public MessageDTO getMessageDTO() {
            return messageDTO;
        }
    }

    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private ApplicationContext applicationContext;
    private UserDTO loggedUser;
    private MessageDTO selectedMessage;
    private final ObservableList<MessageCell> modelInbox = FXCollections.observableArrayList();
    private final ObservableList<MessageCell> modelSentMails = FXCollections.observableArrayList();
    private final ObservableList<MessageDTO> modelConversation = FXCollections.observableArrayList();
    private Button lastMenuPushedButton;
    private int currentInboxPage, currentSentMailsPage, noInboxPages, noSentMailsPages;

    @FXML
    private ListView<MessageCell> messageListView;

    @FXML
    private Label fullNameLabel, usernameLabel, dateLabel, toLabel, replyFullNameLabel, replyUsernameLabel,
            replyDateLabel, replyToLabel, emailAddress, subjectLabel, replySubjectLabel;

    @FXML
    private ImageView background;

    @FXML
    private StackPane stackPane;

    @FXML
    private TextArea messageTextArea, replyMessageTextArea;

    @FXML
    private VBox mailVBox, replyMailBox, tagBarParent;

    @FXML
    private Button inboxButton, sentMailsButton, backButton, newMailButton;

    @FXML
    private AnchorPane newMailPane, replyPane;

    @FXML
    private Button replyButton, replyAllButton;

    @FXML
    private Tagbar<UserDTO> tagBar;

    @FXML
    private ComboBox<UserDTO> tagField;

    @FXML
    private ListView<MessageDTO> restOfThreadListView;

    /**
     * method that uses MessageService to bring to the UI layer the rest of the thread for the given MessageDTO
     * @param lastMessageDTO - MessageDTO
     * @return - List of MessageDTO
     */
    private List<MessageDTO> getTheRestOfTheConversation(MessageDTO lastMessageDTO){
        return messageService.getTheRestOfTheThread(lastMessageDTO);
    }

    @FXML
    protected void initialize() throws IOException {
        restOfThreadListView.setItems(modelConversation);
        messageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                selectedMessage = newValue.getMessageDTO();
                newMailPane.setVisible(false);
                clearNewMailArea();
                replyPane.setVisible(false);
                clearReplyMailArea();
                mailVBox.setVisible(true);
                fullNameLabel.setText(newValue.getMessageDTO().getFrom().getFullName());
                usernameLabel.setText(newValue.getMessageDTO().getFrom().getUsername());
                dateLabel.setText(newValue.getMessageDTO().getSendDate().format(Constants.SENT_DATE_MESSAGE));
                subjectLabel.setText(newValue.getMessageDTO().getSubject());
                toLabel.setText(newValue.getMessageDTO().getTo().toString());
                messageTextArea.setText(newValue.getMessageDTO().getMessage());
                MessageDTO repliedMessageDTO = newValue.getMessageDTO().getRepliesTo();
                if(repliedMessageDTO != null){
                    replyMailBox.setVisible(true);
                    MessageDTO replyMessage = newValue.getMessageDTO().getRepliesTo();
                    replyFullNameLabel.setText(replyMessage.getFrom().getFullName());
                    replyUsernameLabel.setText(replyMessage.getFrom().getUsername());
                    replyDateLabel.setText(replyMessage.getSendDate().format(Constants.SENT_DATE_MESSAGE));
                    replySubjectLabel.setText(replyMessage.getSubject());
                    replyToLabel.setText(replyMessage.getTo().toString());
                    replyMessageTextArea.setText(replyMessage.getMessage());

                    //here we bring to the UI the rest of the thread(conversation)
                    modelConversation.setAll(getTheRestOfTheConversation(repliedMessageDTO));
                }

                else
                    replyMailBox.setVisible(false);
            }
            else{
                mailVBox.setVisible(false);
            }
        });
        messageListView.setItems(modelInbox);

        initTagBar();

        /*currentPage.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                int newCurrentPage = Integer.parseInt(newValue);
                currentPage.setText(String.valueOf(newCurrentPage));
                if(newCurrentPage > 0){
                    if(newCurrentPage <= noInboxPages && lastMenuPushedButton.getText().equals("Inbox")){
                        currentInboxPage = newCurrentPage;
                        initInboxModel();
                    }
                    else if(newCurrentPage <= noSentMailsPages && lastMenuPushedButton.getText().equals("Sent Mails")){
                        currentSentMailsPage = newCurrentPage;
                        initSentMailsModel();
                    }
                }
            }catch(NumberFormatException ignored){

            }
        });*/

        background.fitWidthProperty().bind(((Pane)background.getParent()).widthProperty());
        background.fitHeightProperty().bind(((Pane)background.getParent()).heightProperty());
    }

    @FXML
    private void initTagBar() throws IOException {

        StringConverter<UserDTO> userConverter = new StringConverter<>() {
            @Override
            public String toString(UserDTO object) {
                if (object == null)
                    return null;
                return object.getFirstName() + " " + object.getLastName();
            }

            @Override
            public UserDTO fromString(String string) {
                return userService.findUserByName(string);
            }
        };

        tagBar = new Tagbar<>();
        FXMLLoader tagBarLoader = new FXMLLoader(getClass().getResource("../views/tagBar.fxml"));
        tagBarLoader.setController(tagBar);
        Node tagBarNode = tagBarLoader.load();
        tagBar.setPromptText("To");
        tagBarParent.getChildren().add(tagBarNode);
        tagBar.trackWidthOf((Pane) tagBarParent.getParent());

        tagField = tagBar.getTagField();
        tagField.setConverter(userConverter);
        TextFields.bindAutoCompletion(tagField.getEditor(), param -> userService.filterUsersByName(param.getUserText()), userConverter).setDelay(300);
    }

    @FXML
    private void onBackButtonClicked(){
        Stage primaryStage = applicationContext.getPrimaryStage();

        try {
            messageService.removeObserver(this);
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/mainMenuView.fxml"));
            Parent layout = root.load();
            applicationContext.getWindowController().setTitle("Home page");

            MainMenuController mainMenuController = root.getController();
            mainMenuController.setLoggedUser(loggedUser);
            mainMenuController.setApplicationContext(applicationContext);

            applicationContext.getWindowController().setContents(layout);
        } catch (IOException e) {
            e.printStackTrace();
            primaryStage.close();
        }
    }

    @FXML
    private void onNewMailButtonClicked(){
        mailVBox.setVisible(false);
        replyPane.setVisible(false);
        newMailPane.setVisible(true);
    }

    @FXML
    private TextField toWriteTextField, subjectWriteTextField;

    @FXML
    private TextArea messageWriteTextArea;

    private void clearNewMailArea(){
        toWriteTextField.setText("");
        tagBar.clear();
        subjectWriteTextField.setText("");
        messageWriteTextArea.setText("");
    }

    @FXML
    private void onDiscardButtonClicked(){
        clearNewMailArea();
        newMailPane.setVisible(false);
    }

    @FXML
    private void onSendButtonClicked(){
        List<UserDTO> to = tagBar.getTagValues().stream().toList();
        String subject = subjectWriteTextField.getText();
        String message = messageWriteTextArea.getText();
        try{
            messageService.addMessage(new MessageDTO(null, loggedUser, to, message, subject, LocalDateTime.now(), null));
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Message sent successfully!");
            alert.show();
            clearNewMailArea();
            newMailPane.setVisible(false);
        }
        catch (RuntimeException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
            alert.show();
        }
    }


    private void initMessagePage(){
        lastMenuPushedButton = inboxButton;
        currentInboxPage = currentSentMailsPage = 1; //the page that is currently displayed in the corresponding menus
        initInboxModel();
        initSentMailsModel();
        noInboxPages = messageService.getNoPagesReceivedMessages(loggedUser.getUsername());
        noSentMailsPages = messageService.getNoPagesSentMessages(loggedUser.getUsername());
        if(noInboxPages == 0){
            currentPage.setText("0");
            currentInboxPage = 0;
            previousPageButton.setDisable(true);
            nextPageButton.setDisable(true);
        }
        else
            currentPage.setText("1");
        if(noSentMailsPages == 0){
            currentSentMailsPage = 0;
        }
        noPages.setText(String.valueOf(noInboxPages));
        if(Integer.parseInt(currentPage.getText()) != 0 && Integer.parseInt(noPages.getText()) != 1)
            nextPageButton.setDisable(false);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.userService = applicationContext.getUserService();
        this.friendshipService = applicationContext.getFriendshipService();
        this.messageService = applicationContext.getMessageService();
        messageService.addObserver(this);
        initMessagePage();
        applicationContext.getWindowController().setLoadingScreenVisible(false);
    }

    public void setLoggedUser(UserDTO loggedUser){
        this.loggedUser = loggedUser;
        emailAddress.setText(loggedUser.getUsername());
    }

    private void initInboxModel(){
        if(currentInboxPage > 0){
            List<MessageDTO> messageDTOList = messageService.getInboxPage(loggedUser.getUsername(), currentInboxPage);
            List<MessageCell> messageCells = messageDTOList.stream().map(MessageCell::new).toList();
            modelInbox.setAll(messageCells);
        }
    }

    private void initSentMailsModel(){
        if(currentSentMailsPage > 0){
            List<MessageDTO> messageDTOList = messageService.getSentMessagesPage(loggedUser.getUsername(), currentSentMailsPage);
            List<MessageCell> messageCells = messageDTOList.stream().map(MessageCell::new).toList();
            modelSentMails.setAll(messageCells);
        }
    }

    @FXML
    private void onInboxButtonClicked(){
        messageListView.setItems(modelInbox);
        lastMenuPushedButton = inboxButton;
        currentPage.setText(String.valueOf(currentInboxPage));
        noPages.setText(String.valueOf(noInboxPages));
        previousPageButton.setDisable(currentInboxPage <= 1);
        nextPageButton.setDisable(currentInboxPage == noInboxPages);
    }

    @FXML
    private void onSentMailButtonClicked(){
        messageListView.setItems(modelSentMails);
        lastMenuPushedButton = sentMailsButton;
        currentPage.setText(String.valueOf(currentSentMailsPage));
        noPages.setText(String.valueOf(noSentMailsPages));
        previousPageButton.setDisable(currentSentMailsPage <= 1);
        nextPageButton.setDisable(currentSentMailsPage == noSentMailsPages);
    }

    @FXML
    TextField subjectReplyTextField;

    @FXML
    TextArea messageReplyTextArea;

    private void clearReplyMailArea(){
        subjectReplyTextField.setText("");
        messageReplyTextArea.setText("");
        modelConversation.clear();
    }

    private Button lastPushedReplyButton;

    @FXML
    private void onReplyButtonClicked(){
        newMailPane.setVisible(false);
        mailVBox.setVisible(false);
        clearReplyMailArea();
        replyPane.setVisible(true);
        lastPushedReplyButton = replyButton;
    }

    @FXML
    private void onReplyAllButtonClicked(){
        newMailPane.setVisible(false);
        mailVBox.setVisible(false);
        clearReplyMailArea();
        replyPane.setVisible(true);
        lastPushedReplyButton = replyAllButton;
    }

    @FXML
    private void onReplyDiscardButtonClicked(){
        clearReplyMailArea();
        replyPane.setVisible(false);
    }

    @FXML
    private void onReplySendButtonClicked(){
        String subject = subjectReplyTextField.getText();
        String message = messageReplyTextArea.getText();
        try{
            MessageDTO newMessage = new MessageDTO(null, loggedUser, null, message, subject, LocalDateTime.now(), selectedMessage);
            if(lastPushedReplyButton.getText().equals("Reply")){
                messageService.addReplyMessage(newMessage);
            }
            else{
                messageService.replyAll(newMessage);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Message sent successfully");
            alert.show();
            clearReplyMailArea();
            replyPane.setVisible(false);
            mailVBox.setVisible(true);
        }
        catch (RuntimeException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
            alert.show();
        }
    }

    @Override
    public void update(MessageEvent event) {
        if(event.getMessageType() == MessageEvent.MessageType.SENT ){
            initSentMailsModel();
            noSentMailsPages = messageService.getNoPagesSentMessages(loggedUser.getUsername());
            if(lastMenuPushedButton.getText().equals("Sent Mails")){
                noPages.setText(String.valueOf(noSentMailsPages));
            }
        }
        if(event.getMessageType() == MessageEvent.MessageType.RECEIVED){
            initInboxModel();
            noInboxPages = messageService.getNoPagesReceivedMessages(loggedUser.getUsername());
            if(lastMenuPushedButton.getText().equals("Inbox"))
                noPages.setText(String.valueOf(noInboxPages));
        }
    }

    @FXML
    private Button previousPageButton, nextPageButton;

    @FXML
    private TextField currentPage, noPages;

    @FXML
    private void onPreviousPageButtonClicked(){
        if(lastMenuPushedButton.getText().equals("Inbox")){
            if(currentInboxPage > 1)
                currentInboxPage--;
            currentPage.setText(String.valueOf(currentInboxPage));
            previousPageButton.setDisable(currentInboxPage == 1);
            nextPageButton.setDisable(currentInboxPage == noInboxPages);
            List<MessageDTO> messageDTOList = messageService.getInboxPage(loggedUser.getUsername(), currentInboxPage);
            List<MessageCell> messageCells = messageDTOList.stream().map(MessageCell::new).toList();
            modelInbox.setAll(messageCells);
        }
        else{
            if(currentSentMailsPage > 1)
                currentSentMailsPage--;
            currentPage.setText(String.valueOf(currentSentMailsPage));
            previousPageButton.setDisable(currentSentMailsPage == 1);
            nextPageButton.setDisable(currentSentMailsPage == noSentMailsPages);
            List<MessageDTO> messageDTOList = messageService.getSentMessagesPage(loggedUser.getUsername(), currentSentMailsPage);
            List<MessageCell> messageCells = messageDTOList.stream().map(MessageCell::new).toList();
            modelSentMails.setAll(messageCells);
        }
    }

    @FXML
    private void onNextPageButtonClicked(){
        if(lastMenuPushedButton.getText().equals("Inbox")){
            currentInboxPage++;
            currentPage.setText(String.valueOf(currentInboxPage));
            previousPageButton.setDisable(currentInboxPage == 1);
            nextPageButton.setDisable(currentInboxPage == noInboxPages);
            List<MessageDTO> messageDTOList = messageService.getInboxPage(loggedUser.getUsername(), currentInboxPage);
            List<MessageCell> messageCells = messageDTOList.stream().map(MessageCell::new).toList();
            modelInbox.setAll(messageCells);
        }
        else{
            if(currentSentMailsPage < noSentMailsPages)
                currentSentMailsPage++;
            currentPage.setText(String.valueOf(currentSentMailsPage));
            previousPageButton.setDisable(currentSentMailsPage == 1);
            nextPageButton.setDisable(currentSentMailsPage == noSentMailsPages);
            List<MessageDTO> messageDTOList = messageService.getSentMessagesPage(loggedUser.getUsername(), currentSentMailsPage);
            List<MessageCell> messageCells = messageDTOList.stream().map(MessageCell::new).toList();
            modelSentMails.setAll(messageCells);
        }
    }
}
