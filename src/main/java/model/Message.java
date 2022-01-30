package model;

import utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private final String from;
    private List<String> recipients; //id's of the users which will receive this message
    private String message;
    private String subject;
    LocalDateTime sendDate;
    private final Long repliesTo;

    /**
     * parametrized constructor of the class Message
     * @param from - String
     * @param recipients - List(String)
     * @param message - String
     * @param subject - String
     * @param sendDate - LocalDateTime
     * @param repliesTo - Long
     */
    public Message(String from, List<String> recipients, String message, String subject, LocalDateTime sendDate, Long repliesTo) {
        this.from = from;
        this.recipients = recipients;
        this.message = message;
        this.subject = subject;
        this.sendDate = sendDate;
        this.repliesTo = repliesTo;
    }

    /**
     * method that returns the content of the Message entity
     * @return - String
     */
    public String getMessage(){
        return message;
    }

    /**
     * method that changes the content of the Message entity with newMessage
     * @param newMessage - String
     */
    public void setMessage(String newMessage){
        this.message = newMessage;
    }

    /**
     * method that returns the subject of the Message entity
     * @return - String
     */
    public String getSubject(){
        return subject;
    }

    /**
     * method that changes the subject of the Message entity with newSubject
     * @param newSubject - String
     */
    public void setSubject(String newSubject){
        this.subject = newSubject;
    }

    /**
     * method that returns the username of the sender of the Message
     * @return - String
     */
    public String getFrom(){
        return from;
    }

    /**
     * method that returns a list of usernames of the receivers of the Message
     * @return - List(String)
     */
    public List<String> getRecipients(){
        return recipients;
    }


    /**
     * method that sets the list of receivers with a new one
     * @param recipients - List(String)
     */
    public void setRecipients(List<String> recipients){
        this.recipients = recipients;
    }

    /**
     * method that returns the id of the Message that current Message replies to
     * @return - Integer, if the Message replies to another existing Message
     *           null, if the Message is first of the thread/conversation
     */
    public Long getRepliesTo(){
        return repliesTo;
    }

    /**
     * method that returns the date when the Message was sent
     * @return - LocalDateTime
     */
    public LocalDateTime getSendDate(){
        return sendDate;
    }

    @Override
    public String toString(){
        return id + "->" + from + " -> " + recipients + ": " + message + " (" + sendDate.format(Constants.SENT_DATE_MESSAGE) + ")";
    }
}
