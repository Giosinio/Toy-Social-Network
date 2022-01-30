package ui;

import business.*;
import exceptions.MessageNotFoundException;
import exceptions.NoValidRecipientsException;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;
import model.*;
import model.dto.FriendshipDTO;
import model.dto.MessageDTO;
import model.dto.UserDTO;
import utils.Constants;
import utils.OrderedTuple;
import validators.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CommonInterface {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final NetworkService networkService;
    private final MessageService messageService;
    Scanner inputScanner;

    public CommonInterface(UserService userService, FriendshipService friendshipService, NetworkService networkService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
        this.messageService = messageService;
        inputScanner = new Scanner(System.in);
    }

    private void addUser() {
        System.out.print("Username: ");
        String username = inputScanner.nextLine();
        System.out.print("Password: ");
        String password = inputScanner.nextLine();
        System.out.print("First name: ");
        String firstName = inputScanner.nextLine();
        System.out.print("Last name: ");
        String lastName = inputScanner.nextLine();
        UserDTO returnValue = userService.addUser(username, password, firstName, lastName);
        if(returnValue == null){
            System.out.println("The user was added successfully!");
        }
        else{
            System.out.println("The user with provided id already exists!");
        }
    }

    private void removeUser() {
        System.out.print("Username: ");
        String username = inputScanner.nextLine();
        User user = userService.removeUser(username);
        if(user == null){
            System.out.println("There is no user with the provided id!");
        }
        else{
            System.out.println("Removed user " + user);
        }

    }

    private void findUser(){
        System.out.print("Username: ");
        String username = inputScanner.nextLine();
        UserDTO user = userService.findUser(username);
        if(user == null){
            System.out.println("User with the given id wasn't found!");
        }
        else{
            System.out.println(user);
        }
    }

    private void updateUser(){
        System.out.print("Username: ");
        String username = inputScanner.nextLine();
        System.out.print("Password(in order to make the changes): ");
        String password = inputScanner.nextLine();
        System.out.print("New first name: ");
        String firstName = inputScanner.nextLine();
        System.out.print("New last name: ");
        String lastName = inputScanner.nextLine();
        UserDTO returnValue = userService.updateUser(username, password, firstName, lastName);
        if(returnValue == null)
            System.out.println("Update successfully!");
        else
            System.out.println("We couldn't find the user to be updated!");
    }

    private void printAllUsers() {
        Iterable<UserDTO> userDTOS = userService.getAllUsers();
        if (StreamSupport.stream(userDTOS.spliterator(), false).findAny().isEmpty()) {
            System.out.println("There are no users in our records!");
            return;
        }
        userDTOS.forEach(System.out::println);
    }

    private void addFriendship(){
        System.out.print("Username of the sender: ");
        String username1 = inputScanner.nextLine();
        System.out.print("Username of the receiver: ");
        String username2 = inputScanner.nextLine();
        LocalDate date = LocalDate.now();
        FriendshipDTO returnValue = friendshipService.addFriendship(username1, username2, date);
        if (returnValue == null){
            System.out.println("Friendship added successfully");
        }
        else{
            System.out.println("Friendship already exists!");
        }
    }

    private void removeFriendship(){
        System.out.print("First user: ");
        String username1 = inputScanner.nextLine();
        System.out.print("Second user: ");
        String username2 = inputScanner.nextLine();
        FriendshipDTO friendshipDTO = friendshipService.removeFriendship(username1, username2);
        if(friendshipDTO == null)
            System.out.println("The deletion didn't occur!");
        else
            System.out.println(friendshipDTO);
    }

    private void findFriendship(){
        System.out.print("First username: ");
        String username1 = inputScanner.nextLine();
        System.out.print("Second username: ");
        String username2 = inputScanner.nextLine();
        OrderedTuple<String> friendshipId = new OrderedTuple<>(username1, username2);
        FriendshipDTO friendshipDTO = friendshipService.findFriendship(friendshipId);
        if(friendshipDTO == null)
            System.out.println("The friendship wasn't found!");
        else
            System.out.println(friendshipDTO);
    }

    private void printAllFriendships(){
        Iterable<FriendshipDTO> friendships = friendshipService.getAllFriendships();
        if (StreamSupport.stream(friendships.spliterator(), false).findAny().isEmpty()){
            System.out.println("There are no friendships in our records!");
            return;
        }
        for(FriendshipDTO entity : friendships){
            System.out.println(entity);
        }
    }

    private void getNumberCommunities(){
        System.out.println("There are " + networkService.getNumberOfCommunities() + " communities");
    }

    private void getMostSociableCommunity(){
        int result = networkService.mostSociableCommunity();
        System.out.println("Longest chain in a community has " + result + " members in it!");
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

    private void findFriends() {
        System.out.print("Username: ");
        String username = inputScanner.nextLine();
        Iterable<FriendshipDTO> friendships = friendshipService.getFriendshipsOfUser(username);
        if (StreamSupport.stream(friendships.spliterator(), false).findAny().isEmpty()){
            System.out.println("There are no such friendships in our records!");
            return;
        }
        for(FriendshipDTO friendship : friendships){
            printFriend(username, friendship);
        }
    }

    private void findFriendsFromMonth() {
        System.out.print("Username: ");
        String username = inputScanner.nextLine();
        System.out.print("Month of year (1-12): ");
        int month = Integer.parseInt(inputScanner.nextLine());
        Iterable<FriendshipDTO> friendships = friendshipService.getFriendshipsOfUserFromMonth(username, month);
        if (StreamSupport.stream(friendships.spliterator(), false).findAny().isEmpty()){
            System.out.println("There are no such friendships in our records!");
            return;
        }
        for(FriendshipDTO friendship : friendships){
            printFriend(username, friendship);
        }
    }

    private void getAllMessages(){
        List<Message> messages = messageService.findAllMessages();
        for(Message message : messages)
            System.out.println(message);
    }

    private void findMessage(){
        System.out.print("Id: ");
        Long id = Long.parseLong(inputScanner.nextLine());
        Message foundMessage = messageService.findMessage(id);
        if(foundMessage == null)
            System.out.println("We didn't find any message with the given id!");
        else
            System.out.println(foundMessage);
    }

    private void addMessage(){
        System.out.print("From: ");
        String from = inputScanner.nextLine();
        System.out.print("Message: ");
        String message = inputScanner.nextLine();
        System.out.print("Subject: ");
        String subject = inputScanner.nextLine();
        System.out.print("To(separated by ';'): ");
        List<String> to = Arrays.stream(inputScanner.nextLine().split(";")).toList();
        LocalDateTime date = LocalDateTime.now();
        UserDTO fromUser = new UserDTO(from, null, null, null);
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
        System.out.println("From: ");
        String from = inputScanner.nextLine();
        System.out.print("Message: ");
        String message = inputScanner.nextLine();
        System.out.print("Subject: ");
        String subject = inputScanner.nextLine();
        System.out.print("Replies to: ");
        Long repliesTo = Long.parseLong(inputScanner.nextLine());
        LocalDateTime date = LocalDateTime.now();

        MessageDTO messageDTO = new MessageDTO(null, new UserDTO(from, null, null, null),
                null, message, subject, date, new MessageDTO(repliesTo, null, null, null, null, null, null));
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

    private void getConversations(){
        System.out.print("Username1: ");
        String username1 = inputScanner.nextLine();
        System.out.print("Username2: ");
        String username2 = inputScanner.nextLine();
        List<MessageDTO> conversations = messageService.getConversationsWithUser(username1, username2);
        if(conversations.size() == 0){
            System.out.println("There are no conversations between these 2 users!");
            return;
        }
        for(MessageDTO message: conversations){
            System.out.println(message);
        }
    }

    public void run() {
        welcomeMessage();
        String cmd;
        while (true) {
            System.out.print(">>>");
            cmd = inputScanner.nextLine().toLowerCase();
            try {
                switch (cmd) {
                    case "help" -> help();
                    case "add user" -> addUser();
                    case "remove user" -> removeUser();
                    case "find user" -> findUser();
                    case "update user" -> updateUser();
                    case "all users" -> printAllUsers();
                    case "add friendship" -> addFriendship();
                    case "remove friendship" -> removeFriendship();
                    case "find friendship" -> findFriendship();
                    case "all friendships" -> printAllFriendships();
                    case "no communities" -> getNumberCommunities();
                    case "most sociable community"  -> getMostSociableCommunity();
                    case "find friends" -> findFriends();
                    case "find friends from month" -> findFriendsFromMonth();
                    case "all messages" -> getAllMessages();
                    case "find message" -> findMessage();
                    case "add message" -> addMessage();
                    case "add reply message" -> replyMessage();
                    case "update message" -> updateMessage();
                    case "get conversations" -> getConversations();
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

    private void welcomeMessage(){
        System.out.println("Use command 'help' to see the menu");
    }

    private void help() {
        System.out.println("Application commands list: ");
        System.out.println("exit - turn off the application");
        System.out.println("help - print the list of commands");
        System.out.println("add user - adds a new user in the application");
        System.out.println("find user - displays the user with the given username");
        System.out.println("all users - displays all the users in the application");
        System.out.println("update user - updates an user from the application");
        System.out.println("remove user - deletes an user from application");
        System.out.println("add friendship - adds a new friendship between 2 users in application");
        System.out.println("find friendship - displays the friendship with the given usernames");
        System.out.println("all friendships - displays all friendships from the application");
        System.out.println("remove friendship - deletes a friendship from the application");
        System.out.println("no communities - displays the number of communities in the app's network");
        System.out.println("most sociable community - displays the most sociable community in the app's network");
        System.out.println("find friends - displays the friends of a user given through username");
        System.out.println("find friends from month - displays the friends of a user given through username made in a given month");
        System.out.println("all messages - displays all the messages from the application");
        System.out.println("find message - display the message with the given id");
        System.out.println("add message - send a new message to other users in application");
        System.out.println("update message - updates the content of a selected message with a new message");
        System.out.println("get conversations - gets all conversations between 2 users");
        System.out.println();
    }
}