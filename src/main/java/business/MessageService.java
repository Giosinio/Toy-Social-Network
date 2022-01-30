package business;

import exceptions.MessageNotFoundException;
import exceptions.NoValidRecipientsException;
import exceptions.UserNotFoundException;
import model.Message;
import model.User;
import model.dto.MessageDTO;
import model.dto.UserDTO;
import repository.PagingMessageRepository;
import repository.Repository;
import repository.paging.Page;
import repository.paging.PageableImplementation;
import utils.events.ChangeEventType;
import utils.events.MessageEvent;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.ValidationException;
import validators.Validator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MessageService implements Observable<MessageEvent> {
    private final Repository<String, User> userRepository;
    private final Validator<String, User> userValidator;
    private final PagingMessageRepository messageRepository;
    private final Validator<Long, Message> messageValidator;
    private final List<Observer<MessageEvent>> observerList;

    public MessageService(Repository<String, User> userRepository, Validator<String, User> userValidator, PagingMessageRepository messageRepository, Validator<Long, Message> messageValidator){
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.messageRepository = messageRepository;
        this.messageValidator = messageValidator;
        observerList = new ArrayList<>();
    }

    /**
     * method that returns a list of all the messages from the database
     * @return - List(Messages)
     */
    public List<Message> findAllMessages(){
        return messageRepository.findAll();
    }

    /**
     * method that finds the Message entity with the given id
     * @param id - Long
     * @return - Message
     */
    public Message findMessage(Long id){
        messageValidator.validateID(id);
        return messageRepository.findOne(id);
    }

    /**
     * method that receives a MessageDTO object, creates a new Message entity from MessageDTO, validates it, and if it's valid, stores
     * the Message into repository
     * @param messageDTO - MessageDTO entity
     * @return - null, if the message was stored successfully(always)
     * @throws ValidationException - if the attributes are invalid
     */
    public Message addMessage(MessageDTO messageDTO) throws ValidationException {
        Message newMessage = new Message(messageDTO.getFrom().getUsername(), null, messageDTO.getMessage(), messageDTO.getSubject(), messageDTO.getSendDate(), null);
        List<String> validUsers = messageDTO.getTo().stream()
                .map(UserDTO::getUsername)
                .filter(x -> userRepository.findOne(x) != null)
                .collect(Collectors.toList());
        newMessage.setRecipients(validUsers);
        messageValidator.validateEntity(newMessage);
        if(messageDTO.getFrom() == null )
            throw new UserNotFoundException("Sender wasn't found in our records!");
        if(newMessage.getRecipients().size() == 0)
            throw new NoValidRecipientsException("There isn't any valid recipient!");

        Message result =  messageRepository.save(newMessage);
        if(result == null)
            notifyAll(new MessageEvent(createMessageDTO(newMessage), ChangeEventType.ADD, MessageEvent.MessageType.SENT));
            if(newMessage.getRecipients().contains(newMessage.getFrom())){
                notifyAll(new MessageEvent(createMessageDTO(newMessage), ChangeEventType.ADD, MessageEvent.MessageType.RECEIVED));
            }
        return result;
    }

    /**
     * method that adds a new Message entity in the Repository which replies to an existing Message entity
     * @param messageDTO - MessageDTO
     * @return - null, if the Message was sent successfully
     *           Message entity, otherwise
     * @throws ValidationException - if the given input in invalid
     */
    public Message addReplyMessage(MessageDTO messageDTO) throws ValidationException{
        Message repliedMessage = messageRepository.findOne(messageDTO.getRepliesTo().getId());
        if(repliedMessage == null){
            throw new MessageNotFoundException("Message that you want to reply to doesn't exist!");
        }
        Message newMessage = new Message(messageDTO.getFrom().getUsername(), List.of(repliedMessage.getFrom()), messageDTO.getMessage(), messageDTO.getSubject(), messageDTO.getSendDate(), repliedMessage.getId());
        messageValidator.validateEntity(newMessage);
        Message result = messageRepository.save(newMessage);
        if(result == null){
            notifyAll(new MessageEvent(createMessageDTO(newMessage), ChangeEventType.ADD, MessageEvent.MessageType.SENT));
            if(repliedMessage.getFrom().equals(newMessage.getFrom())){ //if the sender of the new message is the same with the sender of the replied message
                notifyAll(new MessageEvent(createMessageDTO(newMessage), ChangeEventType.ADD, MessageEvent.MessageType.RECEIVED));
            }
        }
        return result;
    }

    /**
     * method that updates the Message with a given id with a new content
     * @param messageDTO - MessageDTO
     * @return null, if the update succeeded
     *         Message, otherwise
     */
    public Message updateMessage(MessageDTO messageDTO){
        Message updateMessage = new Message(null, null, messageDTO.getMessage(), messageDTO.getSubject(), messageDTO.getSendDate(), null);
        updateMessage.setId(messageDTO.getId());
        messageValidator.validateEntity(updateMessage);
        return messageRepository.update(updateMessage);
    }

    /**
     *  method that returns a list of all the conversations between 2 users
     * @param username1 - String(id of the User)
     * @param username2 - String(id of the User)
     * @return - List(MessageDTO)
     */
    public List<MessageDTO> getConversationsWithUser(String username1, String username2){
        userValidator.validateID(username1);
        userValidator.validateID(username2);
        User user1 = userRepository.findOne(username1);
        User user2 = userRepository.findOne(username2);
        if(user1 == null || user2 == null)
            throw new UserNotFoundException("One of the users doesn't exists!");

        return messageRepository.getConversationsBetweenUsers(username1, username2).stream().map(this::createMessageDTO).collect(Collectors.toList());
    }

    /**
     * method that returns a list of all Message entities for a certain user
     * @param username - String
     * @return - List(MessageDTO)
     */
    public List<MessageDTO> getReceivedMessagesForUser(String username){
        userValidator.validateID(username);
        User user = userRepository.findOne(username);
        if(user == null)
            throw new UserNotFoundException("The user doesn't exist!");

        List<Message> messageList = messageRepository.getReceivedMessagesForUser(username);
        return messageList.stream().map(this::createMessageDTO).toList();
    }

    public List<MessageDTO> getSentMessagesForUser(String username){
        userValidator.validateID(username);
        User user = userRepository.findOne(username);
        if(user == null)
            throw new UserNotFoundException("The user doesn't exist!");

        List<Message> messageList = messageRepository.getSentMessagesForUser(username);
        return messageList.stream().map(this::createMessageDTO).toList();
    }

    /**
     * returns the count of newly received Message entities for a certain user
     * @param username String representing the ID of the User
     * @return long representing the count of Messages received by a User after their last login date
     * @throws ValidationException if the username is invalid
     * @throws UserNotFoundException if the User cannot be found
     */
    public long getNewMessagesCount(String username){
        userValidator.validateID(username);
        User user = userRepository.findOne(username);
        if(user == null)
            throw new UserNotFoundException("The user doesn't exist!");

        Predicate<Message> isNew = message -> message.getSendDate().isAfter(LocalDateTime.from(user.getLastLoginAt().atStartOfDay()));

        List<Message> messageList = messageRepository.getReceivedMessagesForUser(username);
        return messageList.stream()
                .filter(isNew)
                .count();
    }

    public MessageDTO createMessageDTO(Message message){
        if(message == null)
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

    /**
     * method that receives a MessageDTO entity that contains a message which will be sent to the sender
     * of the replied message and to all the receivers of that message
     * @param messageDTO - MessageDTO
     * @return - null, if the message was sent successfully
     *           MessageDTO, otherwise
     */
    public Message replyAll(MessageDTO messageDTO){
        Message newMessage = new Message(messageDTO.getFrom().getUsername(), null, messageDTO.getMessage(), messageDTO.getSubject(), messageDTO.getSendDate(), messageDTO.getRepliesTo().getId());
        messageValidator.validateEntity(newMessage);
        userValidator.validateID(messageDTO.getFrom().getUsername());
        if(userRepository.findOne(newMessage.getFrom()) == null)
            throw new UserNotFoundException("The sender of the message wasn't found!");
        Message repliedMessage = messageRepository.findOne(newMessage.getRepliesTo());
        if(repliedMessage == null)
            throw new MessageNotFoundException("The message that you want to reply doesn't exist!");
        List<String> to = new ArrayList<>();
        to.add(repliedMessage.getFrom());
        to.addAll(repliedMessage.getRecipients());
        to.remove(messageDTO.getFrom().getUsername());
        Set<String> uniqueRecipients = new HashSet<>(to);
        newMessage.setRecipients(uniqueRecipients.stream().toList());
        Message result = messageRepository.save(newMessage);
        if(result == null){
            notifyAll(new MessageEvent(createMessageDTO(newMessage), ChangeEventType.ADD, MessageEvent.MessageType.SENT));
            if(repliedMessage.getFrom().equals(newMessage.getFrom()) || repliedMessage.getRecipients().contains(newMessage.getFrom())){
                notifyAll(new MessageEvent(createMessageDTO(newMessage), ChangeEventType.ADD, MessageEvent.MessageType.RECEIVED));
            }
        }
        return result;
    }

    @Override
    public void addObserver(Observer<MessageEvent> observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer<MessageEvent> observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyAll(MessageEvent event) {
        observerList.forEach(x -> x.update(event));
    }

    private int pageSize;

    public void setPageSize(int pageSize){
        this.pageSize = pageSize;
    }

    /**
     * method that returns the content from the specified page for Inbox submenu
     * @param username - String(id)
     * @param page - int
     * @return - List(MessageDTO)
     */
    public List<MessageDTO> getInboxPage(String username, int page){
        Page<Message> messagePage = messageRepository.getReceivedMessagesForUser(username, new PageableImplementation(page-1, pageSize));
        return messagePage.getContent().map(this::createMessageDTO).toList();
    }

    /**
     * method that returns the content from the specified page for Sent Mails submenu
     * @param username - String(id)
     * @param page - int
     * @return - List(MessageDTO)
     */
    public List<MessageDTO> getSentMessagesPage(String username, int page){
        Page<Message> messagePage = messageRepository.getSentMessagesForUser(username, new PageableImplementation(page-1, pageSize));
        return messagePage.getContent().map(this::createMessageDTO).toList();
    }

    /**
     * method that returns the number of pages of Inbox submenu for a given User
     * @param username - String(id)
     * @return - int
     */
    public int getNoPagesReceivedMessages(String username){
        int noMessages = messageRepository.getNoReceivedMessagesForUser(username);
        if(noMessages  % pageSize == 0)
            return noMessages / pageSize;
        return 1 + (noMessages / pageSize);
    }

    /**
     * method that returns the number of pages of Sent Mails submenu for a given User
     * @param username - String
     * @return - int
     */
    public int getNoPagesSentMessages(String username){
        int noMessages = messageRepository.getNoSentMessagesForUser(username);
        if(noMessages  % pageSize == 0)
            return noMessages / pageSize;
        return 1 + (noMessages / pageSize);
    }

    /**
     * method that returns a list of all the Messages that belong to the thread of the given MessageDTO
     * @param messageDTO - MessageDTO
     * @return - List of MessageDTO
     */
    public List<MessageDTO> getTheRestOfTheThread(MessageDTO messageDTO){
        List<MessageDTO> threadList = new ArrayList<>();
        Message currentMessage = messageRepository.findOne(messageDTO.getId());
        Message replyCurrentMessage = messageRepository.findOne(currentMessage.getRepliesTo());
        MessageDTO currentMessageDTO = createMessageDTO(replyCurrentMessage);
        while(currentMessageDTO != null){
            threadList.add(currentMessageDTO);
            if(currentMessageDTO.getRepliesTo() == null)
                currentMessageDTO = null;
            else
                currentMessageDTO = createMessageDTO(messageRepository.findOne(currentMessageDTO.getRepliesTo().getId()));
        }
        return threadList;
    }
}
