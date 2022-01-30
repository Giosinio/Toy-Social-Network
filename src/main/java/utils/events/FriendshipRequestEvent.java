package utils.events;

import model.FriendshipRequest;

public class FriendshipRequestEvent implements FriendshipServiceEvent{
    private FriendshipRequest data;

    public FriendshipRequestEvent(FriendshipRequest data){
        this.data = data;
    }

    public FriendshipRequest getData(){
        return data;
    }
}
