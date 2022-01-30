package ui;

import business.FriendshipService;
import business.MessageService;
import business.UserService;
import exceptions.*;
import model.Message;
import utils.OrderedTuple;
import model.User;
import model.dto.FriendshipDTO;
import model.dto.FriendshipRequestDTO;
import model.dto.MessageDTO;
import model.dto.UserDTO;
import utils.Constants;
import validators.ValidationException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserInterface {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final Scanner inputScanner;
    private UserDTO loggedUser;

    public UserInterface(UserService userService, FriendshipService friendshipService, MessageService messageService){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.inputScanner = new Scanner(System.in);
    }

    private UserDTO login(){
        System.out.println("!!!Login prompt!!!");
        System.out.println("Introduce your credentials!");
        while(true){
            System.out.print("Username: ");
            String username = inputScanner.nextLine();
            System.out.print("Password: ");
            String password = inputScanner.nextLine();
            UserDTO loggedUser = userService.findUser(username);
            if(loggedUser == null){
                System.out.println("The given username wasn't found!");
            }
            else if(!userService.checkPassword(username, password)){
                System.out.println("Incorrect password!");
            }
            else{
                System.out.println("Login successful!");
                return loggedUser;
            }
            System.out.println("Try your credentials again!");
        }
    }

    private void help() {
        System.out.println("Application commands list: ");
        System.out.println("exit - turn off the application");
        System.out.println("help - print the list of commands");
        System.out.println("all users - displays all the users in the application");
        System.out.println("add friendship - adds a new friendship between 2 users in application");
        System.out.println("find friendship - displays the friendship with the given usernames");
        System.out.println("remove friendship - deletes a friendship from the application");
        System.out.println("find friends - displays the friends of a user given through username");
        System.out.println("find friends from month - displays the friends of a user given through username made in a given month");
        System.out.println("send friendship request - sends a friendship request to another user");
        System.out.println("find friendship requests - shows all pending friendship requests from other users");
        System.out.println("accept friendship request - accepts a friendship request from another user");
        System.out.println("reject friendship request - rejects a friendship request from another user");
        System.out.println("cancel friendship request - cancels a friendship request to another user");
        System.out.println("find sent friendship requests - shows all sent friendship requests");
        System.out.println("add message - sends a new message");
        System.out.println("update message - updates an existing message");
        System.out.println("get conversations - displays all the conversations with a given user");
        System.out.println("get all conversations - displays all your conversations");
        System.out.println();
    }

    private void printAllUsers() {
        Iterable<UserDTO> userDTOS = userService.getAllUsers();
        if (StreamSupport.stream(userDTOS.spliterator(), false).findAny().isEmpty()) {
            System.out.println("There are no users in our records!");
            return;
        }
        userDTOS.forEach(System.out::println);
    }

    private void updateAccount(){
        System.out.print("Password (in order to make the changes): ");
        String password = inputScanner.nextLine();
        System.out.print("New first name: ");
        String firstName = inputScanner.nextLine();
        System.out.print("New last name: ");
        String lastName = inputScanner.nextLine();
        UserDTO returnValue = userService.updateUser(loggedUser.getUsername(), password, firstName, lastName);
        if(returnValue == null){
            loggedUser = userService.findUser(loggedUser.getUsername()); //we bring the updated info to UI
            System.out.println("Update successfully!");
        }
        else
            System.out.println("We couldn't find the user to be updated!");
    }

    private void removeFriendship(){
        System.out.print("Remove your friendship relationship with: ");
        String friendUsername = inputScanner.nextLine();
        FriendshipDTO friendshipDTO = friendshipService.removeFriendship(loggedUser.getUsername(), friendUsername);
        if(friendshipDTO == null)
            System.out.println("The deletion didn't occur!");
        else
            System.out.println("Friendship relationship removed!");
    }

    private void findFriendship(){
        System.out.print("Friend username: ");
        String friendUsername = inputScanner.nextLine();
        OrderedTuple<String> friendshipId = new OrderedTuple<>(loggedUser.getUsername(), friendUsername);
        FriendshipDTO friendshipDTO = friendshipService.findFriendship(friendshipId);
        if(friendshipDTO == null)
            System.out.println("You are not friend with " + friendUsername);
        else
            System.out.println("You are friend with " + friendUsername + " since " + friendshipDTO.getDate());
    }

    private void printFriend(String username, FriendshipDTO friendship) {
        String format = "%s | %s | %s";
        UserDTO friend = friendship.getFirstUser();
        if (friend.getUsername().equals(username))
            friend = friendship.getSecondUser();
        String lastName = friend.getLastName();
        String firstName = friend.getFirstName();
        String date = friendship.getDate().format(Constants.DISPLAY_DATE_FORMATTER);
        String result = String.format(format, lastName, firstName, date);
        System.out.println(result);
    }

    private void printFriendshipRequestDTO(FriendshipRequestDTO friendshipRequestDTO) {
        UserDTO fromUserDTO = friendshipRequestDTO.getFromUserDTO();
        UserDTO toUserDTO = friendshipRequestDTO.getToUserDTO();
        String date = friendshipRequestDTO.getCreated().format(Constants.DISPLAY_DATE_FORMATTER);
        System.out.println(fromUserDTO + " sent " + toUserDTO + " a friendship request on " + date + ".");
    }

    private void findFriends() {
        Iterable<FriendshipDTO> friendships = friendshipService.getFriendshipsOfUser(loggedUser.getUsername());
        if (StreamSupport.stream(friendships.spliterator(), false).findAny().isEmpty()){
            System.out.println("You don't have any friends right now!");
            return;
        }
        for(FriendshipDTO friendship : friendships){
            printFriend(loggedUser.getUsername(), friendship);
        }
    }

    private void findFriendsFromMonth() {
        System.out.print("Month of year (1-12): ");
        int month = Integer.parseInt(inputScanner.nextLine());
        Iterable<FriendshipDTO> friendships = friendshipService.getFriendshipsOfUserFromMonth(loggedUser.getUsername(), month);
        if (StreamSupport.stream(friendships.spliterator(), false).findAny().isEmpty()){
            System.out.println("There are no such friendships in our records!");
            return;
        }
        for(FriendshipDTO friendship : friendships){
            printFriend(loggedUser.getUsername(), friendship);
        }
    }

    private void sendFriendshipRequest() {
        System.out.print("Username of receiver: ");
        String toUsername = inputScanner.nextLine();
        try {
            friendshipService.sendFriendshipRequest(loggedUser.getUsername(), toUsername);
            System.out.println("Friendship request sent successfully!");
        } catch (FriendshipRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void findFriendshipRequests() {
        Iterable<FriendshipRequestDTO> friendshipRequests = friendshipService.getFriendshipRequestsToUser(loggedUser.getUsername());
        if (StreamSupport.stream(friendshipRequests.spliterator(), false).findAny().isEmpty()){
            System.out.println("You don't have any pending friendship requests right now!");
            return;
        }
        for(FriendshipRequestDTO friendshipRequest : friendshipRequests){
            printFriendshipRequestDTO(friendshipRequest);
        }
    }

    private void acceptFriendshipRequest() {
        System.out.print("Username of sender: ");
        String fromUsername = inputScanner.nextLine();
        try {
            friendshipService.acceptFriendshipRequest(loggedUser.getUsername(), fromUsername);
            System.out.println("Friendship request accepted!");
        } catch (FriendshipRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void rejectFriendshipRequest() {
        System.out.print("Username of sender: ");
        String fromUsername = inputScanner.nextLine();
        try {
            friendshipService.rejectFriendshipRequest(loggedUser.getUsername(), fromUsername);
            System.out.println("Friendship request rejected!");
        } catch (FriendshipRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cancelFriendshipRequest() {
        System.out.print("Username of receiver: ");
        String toUsername = inputScanner.nextLine();
        try {
            friendshipService.cancelFriendshipRequest(loggedUser.getUsername(), toUsername);
            System.out.println("Friendship request cancelled!");
        } catch (FriendshipRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void findSentFriendshipRequests() {
        Iterable<FriendshipRequestDTO> friendshipRequests = friendshipService.getFriendshipRequestsFromUser(loggedUser.getUsername());
        if (StreamSupport.stream(friendshipRequests.spliterator(), false).findAny().isEmpty()) {
            System.out.println("You don't have any sent friendship requests right now!");
            return;
        }
        for (FriendshipRequestDTO friendshipRequest : friendshipRequests) {
            printFriendshipRequestDTO(friendshipRequest);
        }
    }

    private void addMessage(){
        System.out.print("Message: ");
        String message = inputScanner.nextLine();
        System.out.print("Subject: ");
        String subject = inputScanner.nextLine();
        System.out.print("To(separated by ';'): ");
        List<String> to = Arrays.stream(inputScanner.nextLine().split(";")).toList();
        LocalDateTime date = LocalDateTime.now();
        UserDTO fromUser = loggedUser;
        List<UserDTO> toUser = to.stream()
                .map(username -> new UserDTO(username, null, null, null))
                .collect(Collectors.toList());
        Message result = messageService.addMessage(new MessageDTO(null, fromUser, toUser, message, subject, date, null));
        if(result == null){
            System.out.println("Message sent successfully!");
            return;
        }
        System.out.println("The message wasn't sent");
    }

    private void replyMessage(){
        System.out.print("Message: ");
        String message = inputScanner.nextLine();
        System.out.print("Subject: ");
        String subject = inputScanner.nextLine();
        System.out.print("Replies to: ");
        Long repliesTo = Long.parseLong(inputScanner.nextLine());
        LocalDateTime date = LocalDateTime.now();

        MessageDTO messageDTO = new MessageDTO(null, loggedUser, null, message, subject, date, new MessageDTO(repliesTo, null, null, null, null,null, null));
        Message result = messageService.addReplyMessage(messageDTO);
        if(result == null){
            System.out.println("The reply was sent successfully!");
            return;
        }
        System.out.println("The reply wasn't sent");
    }

    private void updateMessage(){
        System.out.print("ID of the message to be updated: ");
        Long id = Long.parseLong(inputScanner.nextLine());
        System.out.print("New message: ");
        String message = inputScanner.nextLine();
        System.out.print("New subject: ");
        String subject = inputScanner.nextLine();
        LocalDateTime date = LocalDateTime.now();
        Message result = messageService.updateMessage(new MessageDTO(id, null, null, message, subject, date, null));
        if(result == null)
            System.out.println("Update successfully!");
        else
            System.out.println("There isn't any message with the given id!");
    }

    private void getConversationsWithUser(){
        System.out.print("See conversations with: ");
        String username = inputScanner.nextLine();
        List<MessageDTO> conversations = messageService.getConversationsWithUser(loggedUser.getUsername(), username);
        if(conversations.size() == 0){
            System.out.println("You don't have any messages with the provided user!" );
            return;
        }
         for(MessageDTO message: conversations){
                System.out.println(message);
         }
    }

    private void replyAll(){
        System.out.print("Message: ");
        String message = inputScanner.nextLine();
        System.out.print("Subject: ");
        String subject = inputScanner.nextLine();
        System.out.print("Reply all to message: ");
        Long repliesTo = Long.parseLong(inputScanner.nextLine());
        LocalDateTime sendDate = LocalDateTime.now();
        MessageDTO messageDTO = new MessageDTO(null, loggedUser, null, message, subject, sendDate, new MessageDTO(repliesTo, null, null, null, null, null, null));
        if(messageService.replyAll(messageDTO) == null){
            System.out.println("Messages were sent successfully!");
            return;
        }
        System.out.println("None of the messages were sent!");
    }

    private void welcomeMessage(){
        System.out.println("Use command 'help' to see the menu");
    }

    public void run(){
        this.loggedUser = login();
        welcomeMessage();
        String cmd;
        while(true){
            System.out.print(">>>");
            cmd = inputScanner.nextLine().toLowerCase().strip();
            try {
                switch (cmd) {
                    case "help" -> help();
                    case "all users" -> printAllUsers(); //to see which users I can add as friend
                    case "update account" -> updateAccount();
                    case "remove friendship" -> removeFriendship();
                    case "find friendship" -> findFriendship();
                    case "find friends" -> findFriends();
                    case "find friends from month" -> findFriendsFromMonth();
                    case "send friendship request" -> sendFriendshipRequest();
                    case "find friendship requests" -> findFriendshipRequests();
                    case "accept friendship request" -> acceptFriendshipRequest();
                    case "reject friendship request" -> rejectFriendshipRequest();
                    case "cancel friendship request" -> cancelFriendshipRequest();
                    case "find sent friendship requests" -> findSentFriendshipRequests();
                    case "add message" -> addMessage();
                    case "add reply message" -> replyMessage();
                    case "reply all" -> replyAll();
                    case "update message" -> updateMessage();
                    case "get conversations" -> getConversationsWithUser();
                    case "exit" -> {
                        inputScanner.close();
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid command!");
                }
            }
            catch (ValidationException | UserNotFoundException | WrongPasswordException | NoValidRecipientsException | MessageNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
            catch (NumberFormatException exception){
                System.out.println("Invalid data type provided!");
            }
        }
    }
}