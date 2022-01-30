package business;

import model.Friendship;
import model.Message;
import model.User;
import model.dto.FriendDTO;
import model.dto.FriendshipDTO;
import model.dto.MessageDTO;
import model.dto.UserDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import repository.FriendshipRepository;
import repository.MessageRepository;
import repository.Repository;
import utils.Constants;
import validators.FriendshipValidator;
import validators.Validator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {

    private final Repository<String, User> userRepository;
    private final Validator<String, User> userValidator;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipValidator friendshipValidator;
    private final MessageRepository messageRepository;
    private final Validator<Long, Message> messageValidator;
    private final DateTimeFormatter dateFormat = Constants.DISPLAY_DATE_FORMATTER;
    private final String tab = " ".repeat(4);

    public ReportService(Repository<String, User> userRepository,
                         Validator<String, User> userValidator,
                         FriendshipRepository friendshipRepository,
                         FriendshipValidator friendshipValidator,
                         MessageRepository messageRepository,
                         Validator<Long, Message> messageValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.friendshipRepository = friendshipRepository;
        this.friendshipValidator = friendshipValidator;
        this.messageRepository = messageRepository;
        this.messageValidator = messageValidator;
    }

    public void newFriendsAndMessagesInPeriod(UserDTO user, String path, LocalDate startDate, LocalDate endDate) throws IOException {
        if (path == null || path.equals("")) {
            return;
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("At least one of the two required dates are missing!");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("The given dates do not represent a valid time interval!");
        }
        List<Friendship> friendships = friendshipRepository.getFriendshipsInPeriod(user.getUsername(), startDate, endDate);
        List<Message> messages = messageRepository.getReceivedMessagesInPeriod(user.getUsername(), startDate, endDate);
        generateNewFriendsAndMessagesReport(user, path, startDate, endDate, friendships, messages);
    }

    private void generateNewFriendsAndMessagesReport(UserDTO user, String path, LocalDate startDate, LocalDate endDate, List<Friendship> friendships, List<Message> messages) throws IOException {
        PDDocument document = new PDDocument();
        initializeDocumentAttributes(user, document);
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        initializeTextFormat(contentStream);
        int lineCounter = 6, pageCount = 1;
        contentStream.newLineAtOffset(50,750);
        writeNewFriendsAndMessagesReportHeader(user, startDate, endDate, contentStream);
        writeLine(contentStream, "New friends: ");
        for (Friendship friendship : friendships) {
            FriendDTO friendDTO = createFriendDTO(friendship, user.getUsername());
            writeLine(contentStream, tab + friendDTO.getFriend().getFirstName() + " " +
                    friendDTO.getFriend().getLastName() + " (" +
                    friendDTO.getFriend().getUsername() + ")" + " - " +
                    friendDTO.getDate().format(dateFormat));
            lineCounter++;
            if (lineCounter >= Constants.PDF_MAX_LINE_COUNT) {
                lineCounter = 5;
                writePageNumber(contentStream, pageCount);
                pageCount++;
                contentStream.endText();
                contentStream.close();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                initializeTextFormat(contentStream);
                contentStream.newLineAtOffset(50,750);
                writeNewFriendsAndMessagesReportHeader(user, startDate, endDate, contentStream);
            }
        }
        writePageNumber(contentStream, pageCount);
        pageCount++;
        contentStream.endText();
        contentStream.close();
        page = new PDPage();
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        initializeTextFormat(contentStream);
        lineCounter = 6;
        contentStream.newLineAtOffset(50,750);
        writeNewFriendsAndMessagesReportHeader(user, startDate, endDate, contentStream);
        writeLine(contentStream, "New messages: ");
        for (Message message : messages) {
            MessageDTO messageDTO = createMessageDTO(message);
            User sender = userRepository.findOne(message.getFrom());
            String recipients = message.getRecipients().stream()
                    .map(username -> userRepository.findOne(username))
                    .map(foundUser -> getDetailedNameForUser(foundUser))
                    .reduce((old, found) -> old + ", " + found).get();
            if (message.getRepliesTo() == 0 || message.getRepliesTo() == null) {
                writeLine(contentStream, tab + getDetailedNameForUser(sender)
                        + " => " + recipients + " (" + message.getSendDate().format(dateFormat) + ")");
                writeLine(contentStream, tab + "Subject: " + message.getSubject());
                int extraLines = writeMessage(contentStream, message.getMessage());
                writeLine(contentStream, "");
                lineCounter += 4 + extraLines;
            } else {
                writeLine(contentStream, tab + "(reply) " + getDetailedNameForUser(sender)
                        + " => " + recipients + " (" + message.getSendDate().format(dateFormat) + ")");
                writeLine(contentStream, tab + "Subject: " + message.getSubject());
                int extraLines = writeMessage(contentStream, message.getMessage());
                writeLine(contentStream, tab + "-".repeat(100));
                writeLine(contentStream, tab + "Original message: ");
                writeLine(contentStream, tab + messageDTO.getRepliesTo().getFrom()
                        + " => " + recipients + " (" + messageDTO.getRepliesTo().getSendDate().format(dateFormat) + ")");
                writeLine(contentStream, tab + "Subject: " + messageDTO.getRepliesTo().getSubject());
                int extraLinesReply = writeMessage(contentStream,  messageDTO.getRepliesTo().getMessage());
                writeLine(contentStream, "");
                lineCounter += 8 + extraLines + extraLinesReply;
            }
            if (lineCounter >= Constants.PDF_MAX_LINE_COUNT) {
                lineCounter = 5;
                writePageNumber(contentStream, pageCount);
                pageCount++;
                contentStream.endText();
                contentStream.close();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                initializeTextFormat(contentStream);
                contentStream.newLineAtOffset(50,750);
                writeNewFriendsAndMessagesReportHeader(user, startDate, endDate, contentStream);
            }
        }
        writePageNumber(contentStream, pageCount);
        contentStream.endText();
        contentStream.close();
        document.save(path);
        document.close();
    }

    private String getDetailedNameForUser(User user) {
        return user.getFirstName() + " " + user.getLastName() + " (" + user.getId() + ")";
    }

    private void writeLine(PDPageContentStream contentStream, String text) throws IOException {
        contentStream.newLine();
        contentStream.showText(text);
    }

    private int writeMessage(PDPageContentStream contentStream, String text) throws IOException {
        int lineCount = 0;
        for (int i = 0; i < text.length(); i+=Constants.PDF_MAX_CHARS_PER_LINE) {
            contentStream.newLine();
            int lineEnd = Math.min(i + Constants.PDF_MAX_CHARS_PER_LINE, text.length());
            String hyphen = (lineEnd != i + Constants.PDF_MAX_CHARS_PER_LINE || text.substring(lineEnd - 1, lineEnd)
                    .contains(" ")) ? "" : "-";
            contentStream.showText(tab + text.substring(i, lineEnd) + hyphen);
            lineCount++;
        }
        return lineCount;
    }

    private void initializeTextFormat(PDPageContentStream contentStream) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.setLeading(14.5f);
    }

    private void initializeDocumentAttributes(UserDTO user, PDDocument document) {
        PDDocumentInformation information = document.getDocumentInformation();
        information.setTitle("Report");
    }

    private void writeNewFriendsAndMessagesReportHeader(UserDTO user, LocalDate startDate, LocalDate endDate, PDPageContentStream contentStream) throws IOException {
        contentStream.showText("Report - New Friends and Messages");
        writeLine(contentStream, "Current date: " + LocalDate.now().format(dateFormat));
        writeLine(contentStream, "User: " + user.getFirstName() + " " + user.getLastName() + " (" + user.getUsername() + ")");
        writeLine(contentStream, "Selected time interval: " + startDate.format(dateFormat) + " - " + endDate.format(dateFormat));
        writeLine(contentStream, "");
    }

    private void writePageNumber(PDPageContentStream contentStream, int pageCount) throws IOException {
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(540, 25);
        contentStream.showText(String.valueOf(pageCount));
        contentStream.endText();
        contentStream.beginText();
    }

    /**
     * private method that returns a FriendDTO from a given Friendship and username of the User it is for
     * @param friendship Friendship from which the essential information is extracted
     * @param username String that represents the username of the User for which the FriendDTO is created
     * @return FriendDTO which contains only the information relevant for the User
     */
    private FriendDTO createFriendDTO(Friendship friendship, String username){
        String friendUsername = friendship.getId().getFirst();
        if(friendUsername.equals(username))
            friendUsername = friendship.getId().getSecond();
        UserDTO friend = new UserDTO(userRepository.findOne(friendUsername));
        return new FriendDTO(friend, friendship.getDate());
    }

    public MessageDTO createMessageDTO(Message message){
        if (message == null)
            return null;
        UserDTO from = new UserDTO(userRepository.findOne(message.getFrom()));

        Message replyMessage = null;
        if(message.getRepliesTo() != null)
            replyMessage = messageRepository.findOne(message.getRepliesTo());
        MessageDTO replyDTO = null;
        if(replyMessage != null)
            replyDTO = new MessageDTO(replyMessage.getId(), new UserDTO(userRepository.findOne(replyMessage.getFrom())), replyMessage.getRecipients().stream().map(userRepository::findOne).map(UserDTO::new).collect(Collectors.toList()), replyMessage.getMessage(), replyMessage.getSubject(), replyMessage.getSendDate(), null );

        return new MessageDTO(message.getId(), from, message.getRecipients().stream().map(userRepository::findOne).map(UserDTO::new).collect(Collectors.toList()), message.getMessage(), message.getSubject(), message.getSendDate(), replyDTO);
    }

    public void newMessagesFromFriendInPeriod(UserDTO user, String path, UserDTO friend, LocalDate startDate, LocalDate endDate) throws IOException {
        if (path == null || path.equals("")) {
            return;
        }
        if (friend == null ||
                friend.getUsername() == null ||
                userRepository.findOne(friend.getUsername()) == null) {
            throw new IllegalArgumentException("The given friend does not exist!");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("At least one of the two required dates are missing!");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("The given dates do not represent a valid time interval!");
        }
        List<Message> messages = messageRepository.getReceivedMessagesFromFriendInPeriod(user.getUsername(),
                friend.getUsername(), startDate, endDate);
        generateNewMessagesFromFriendReport(user, path, friend, startDate, endDate, messages);
    }

    private void writeNewMessagesFromFriendReportHeader(UserDTO user, LocalDate startDate, LocalDate endDate, PDPageContentStream contentStream) throws IOException {
        contentStream.showText("Report - New Messages from Friend");
        writeLine(contentStream, "Current date: " + LocalDate.now().format(dateFormat));
        writeLine(contentStream, "User: " + user.getFirstName() + " " + user.getLastName() + " (" + user.getUsername() + ")");
        writeLine(contentStream, "Selected time interval: " + startDate.format(dateFormat) + " - " + endDate.format(dateFormat));
        writeLine(contentStream, "");
    }

    private void generateNewMessagesFromFriendReport(UserDTO user, String path, UserDTO friend, LocalDate startDate, LocalDate endDate, List<Message> messages) throws IOException {
        PDDocument document = new PDDocument();
        initializeDocumentAttributes(user, document);
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        initializeTextFormat(contentStream);
        int lineCounter = 6, pageCount = 1;
        contentStream.newLineAtOffset(50,750);
        writeNewMessagesFromFriendReportHeader(user, startDate, endDate, contentStream);
        writeLine(contentStream, "New messages from " + friend.getFirstName() + " " +
                friend.getLastName() + " (" + friend.getUsername() +  "): ");
        for (Message message : messages) {
            MessageDTO messageDTO = createMessageDTO(message);
            User sender = userRepository.findOne(message.getFrom());
            String recipients = message.getRecipients().stream()
                    .map(username -> userRepository.findOne(username))
                    .map(foundUser -> getDetailedNameForUser(foundUser))
                    .reduce((old, found) -> old + ", " + found).get();
            if (message.getRepliesTo() == 0 || message.getRepliesTo() == null) {
                writeLine(contentStream, tab + getDetailedNameForUser(sender)
                        + " => " + recipients + " (" + message.getSendDate().format(dateFormat) + ")");
                writeLine(contentStream, tab + "Subject: " + message.getSubject());
                int extraLines = writeMessage(contentStream, message.getMessage());
                writeLine(contentStream, "");
                lineCounter += 4 + extraLines;
            } else {
                writeLine(contentStream, tab + "(reply) " + getDetailedNameForUser(sender)
                        + " => " + recipients + " (" + message.getSendDate().format(dateFormat) + ")");
                writeLine(contentStream, tab + "Subject: " + message.getSubject());
                int extraLines = writeMessage(contentStream, message.getMessage());
                writeLine(contentStream, tab + "-".repeat(100));
                writeLine(contentStream, tab + "Original message: ");
                writeLine(contentStream, tab + messageDTO.getRepliesTo().getFrom()
                        + " => " + recipients + " (" + messageDTO.getRepliesTo().getSendDate().format(dateFormat) + ")");
                writeLine(contentStream, tab + "Subject: " + messageDTO.getRepliesTo().getSubject());
                int extraLinesReply = writeMessage(contentStream,  messageDTO.getRepliesTo().getMessage());
                writeLine(contentStream, "");
                lineCounter += 8 + extraLines + extraLinesReply;
            }
            lineCounter+=4;
            if (lineCounter >= Constants.PDF_MAX_LINE_COUNT) {
                lineCounter = 5;
                writePageNumber(contentStream, pageCount);
                pageCount++;
                contentStream.endText();
                contentStream.close();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                initializeTextFormat(contentStream);
                contentStream.newLineAtOffset(50,750);
                writeNewMessagesFromFriendReportHeader(user, startDate, endDate, contentStream);
            }
        }
        writePageNumber(contentStream, pageCount);
        contentStream.endText();
        contentStream.close();
        document.save(path);
        document.close();
    }

}
