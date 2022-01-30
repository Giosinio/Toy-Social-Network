package model.dto;

import utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {
    private final Long id;
    private final UserDTO from;
    private final List<UserDTO> to;
    private final String message;
    private final String subject;
    private final LocalDateTime sendDate;
    private final MessageDTO repliesTo;

    public MessageDTO(Long id, UserDTO from, List<UserDTO> to, String message, String subject, LocalDateTime sendDate, MessageDTO repliesTo) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.subject = subject;
        this.sendDate = sendDate;
        this.repliesTo = repliesTo;
    }

    public Long getId(){
        return id;
    }

    public UserDTO getFrom() {
        return from;
    }

    public List<UserDTO> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject(){
        return subject;
    }

    public LocalDateTime getSendDate() {
        return sendDate;
    }

    public MessageDTO getRepliesTo() {
        return repliesTo;
    }

    @Override
    public String toString(){
        String output = from + ": " + message + " (" + sendDate.format(Constants.SENT_DATE_MESSAGE) + ")";
        if(getRepliesTo() != null)
            output += "\nReplies to: " + getRepliesTo().getMessage();
        return output;
    }
}
