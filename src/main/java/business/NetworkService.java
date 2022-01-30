package business;

import model.Friendship;
import model.Graph;
import utils.OrderedTuple;
import model.User;
import repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class NetworkService {
    private Graph<String> network;
    private final Repository<String, User> userRepository;
    private final Repository<OrderedTuple<String>, Friendship> friendshipRepository;

    public NetworkService(Repository<String, User> userRepository, Repository<OrderedTuple<String>, Friendship> friendshipRepository){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    private void initialiseNetwork(){
        List<String> userIDs = new ArrayList<>();
        for(User user: userRepository.findAll())
            userIDs.add(user.getId());
        List<OrderedTuple<String>> friendshipIDs = new ArrayList<>();
        for(Friendship friendship: friendshipRepository.findAll())
            friendshipIDs.add(friendship.getId());
        network = new Graph<>(userIDs, friendshipIDs);
    }

    /**
     * method that uses Graph entity to return the number of communities from the social graph
     * @return - int
     */
    public int getNumberOfCommunities(){
        initialiseNetwork();
        return network.getNumberOfConnectedComponents();
    }

    /**
     * method that uses Graph entity to return the longest elementary chain from the social graph
     * @return - int
     */
    public int mostSociableCommunity(){
        initialiseNetwork();
        return network.getLargestComponentDiameter();
    }
}
