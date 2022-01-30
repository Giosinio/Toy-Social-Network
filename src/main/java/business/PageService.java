package business;

import exceptions.UserNotFoundException;
import model.FriendshipRequest;
import model.User;
import model.dto.*;
import repository.FriendshipRepository;
import repository.PagingEventParticipationRepository;
import repository.PagingMessageRepository;
import repository.Repository;
import repository.paging.Page;
import utils.Tuple;

import java.util.List;
import java.util.stream.StreamSupport;

public class PageService {

    private final Repository<String, User> userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final EventParticipationService eventParticipationService;

    public PageService(Repository<String, User> userRepository,
                       FriendshipRepository friendshipRepository,
                       FriendshipService friendshipService,
                       MessageService messageService,
                       EventParticipationService eventParticipationService) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.eventParticipationService = eventParticipationService;
    }

    /**
     * returns a PageDTO object containing information of interest for the User
     * @param username String - represents the ID of the user
     * @return PageDTO object containing the information
     */
    public PageDTO getUserPage(String username) {
        User user = userRepository.findOne(username);
        if (user == null) {
            throw new UserNotFoundException("The given user was not found!");
        }

        UserDTO userDTO = UserService.createUserDTO(user);
        long newFriendsCount = friendshipRepository.getNewFriendsCount(username);
        List<FriendDTO> friendDTOList =
                StreamSupport.stream(friendshipService.getFriendsOfUser(username).spliterator(), false)
                .toList();
        long newFriendRequestsCount = friendshipService.getNewFriendshipRequestCount(username);
        List<FriendshipRequestDTO> friendshipRequestDTOList =
                StreamSupport.stream(friendshipService.getFriendshipRequestsToUser(username).spliterator(), false)
                .toList();
        long newMessagesCount = messageService.getNewMessagesCount(username);
        List<MessageDTO> messageDTOPage = messageService.getInboxPage(username, 1);
        List<EventParticipationDTO> eventParticipationDTOPage = eventParticipationService.getPageEventsUserParticipatesIn(username, 0);

        PageDTO pageDTO = new PageDTO(
                userDTO,
                newFriendsCount,
                friendDTOList,
                newFriendRequestsCount,
                friendshipRequestDTOList,
                newMessagesCount,
                messageDTOPage,
                eventParticipationDTOPage
        );

        return pageDTO;
    }
}
