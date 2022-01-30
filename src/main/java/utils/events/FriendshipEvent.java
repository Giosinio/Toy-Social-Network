package utils.events;

import model.Friendship;

public class FriendshipEvent implements FriendshipServiceEvent{
    Friendship data;
    ChangeEventType type;

    public FriendshipEvent(Friendship data, ChangeEventType type){
        this.data = data;
        this.type = type;
    }
}
