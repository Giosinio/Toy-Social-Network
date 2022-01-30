package model.dto;

import repository.paging.Page;

import java.util.List;

public class PageDTO {
    private UserDTO userDTO;
    private long newFriendsCount;
    private List<FriendDTO> friendDTOList;
    private long newFriendRequestCount;
    private List<FriendshipRequestDTO> friendshipRequestDTOList;
    private long newMessagesCount;
    private List<MessageDTO> messageDTOPage;
    private List<EventParticipationDTO> eventParticipationDTOPage;

    /**
     * parametrized constructor for a private Page
     * @param userDTO
     * @param newFriendsCount
     * @param friendDTOList
     * @param newFriendRequestCount
     * @param friendshipRequestDTOList
     * @param newMessagesCount
     * @param messageDTOPage
     * @param eventParticipationDTOPage
     */
    public PageDTO(UserDTO userDTO,
                   long newFriendsCount,
                   List<FriendDTO> friendDTOList,
                   long newFriendRequestCount,
                   List<FriendshipRequestDTO> friendshipRequestDTOList,
                   long newMessagesCount,
                   List<MessageDTO> messageDTOPage,
                   List<EventParticipationDTO> eventParticipationDTOPage) {
        this.userDTO = userDTO;
        this.newFriendsCount = newFriendsCount;
        this.friendDTOList = friendDTOList;
        this.newFriendRequestCount = newFriendRequestCount;
        this.friendshipRequestDTOList = friendshipRequestDTOList;
        this.newMessagesCount = newMessagesCount;
        this.messageDTOPage = messageDTOPage;
        this.eventParticipationDTOPage = eventParticipationDTOPage;
    }

    /**
     * parametrized constructor for a public Page
     * @param userDTO
     * @param friendDTOList
     * @param eventParticipationDTOPage
     */
    public PageDTO(UserDTO userDTO, List<FriendDTO> friendDTOList, List<EventParticipationDTO> eventParticipationDTOPage) {
        this.userDTO = userDTO;
        this.friendDTOList = friendDTOList;
        this.eventParticipationDTOPage = eventParticipationDTOPage;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public long getNewFriendsCount() {
        return newFriendsCount;
    }

    public List<FriendDTO> getFriendDTOList() {
        return friendDTOList;
    }

    public long getNewFriendRequestCount() {
        return newFriendRequestCount;
    }

    public List<FriendshipRequestDTO> getFriendshipRequestDTOList() {
        return friendshipRequestDTOList;
    }

    public long getNewMessagesCount() {
        return newMessagesCount;
    }

    public List<MessageDTO> getMessageDTOPage() {
        return messageDTOPage;
    }

    public List<EventParticipationDTO> getEventParticipationDTOPage() {
        return eventParticipationDTOPage;
    }
}
