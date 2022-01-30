package model;

import utils.MyVariable;
import utils.OrderedTuple;

import java.util.*;

/**
 * Class for Graph entity
 * @param <E> - type of the vertices stored in Graph entity
 */
public class Graph<E extends Comparable<E>> {
    private final Map<E, List<E>> adjacentLists;

    public Graph(Iterable<E> vertices, List<OrderedTuple<E>> edges){
        adjacentLists = new HashMap<>();
        for(E vertex : vertices)
            adjacentLists.put(vertex, new ArrayList<>());
        for(OrderedTuple<E> edge : edges){
            adjacentLists.get(edge.getFirst()).add(edge.getSecond());
            adjacentLists.get(edge.getSecond()).add(edge.getFirst());
        }
    }

    /**
     * method that calculates the number of connected components in our Graph
     * @return - int
     */
    public int getNumberOfConnectedComponents(){
        Set<E> consumedVertices = new HashSet<>();
        int result = 0;
        for (E vertex : adjacentLists.keySet()){
            if(!consumedVertices.contains(vertex)){
                result += 1;
                DFS(vertex, consumedVertices);
            }
        }
        return result;
    }

    /**
     * method that executes DFS algorithm on our Graph
     * @param currentVertex - the current vertex in DFS search
     * @param consumedVertices - the vertices that were already covered by the DFS search
     */
    private void DFS(E currentVertex, Set<E> consumedVertices){
        consumedVertices.add(currentVertex);
        for (E adjacentVertex : adjacentLists.get(currentVertex)){//currentUser.getFriendList()){
            if(!(consumedVertices.contains(adjacentVertex)))
                DFS(adjacentVertex, consumedVertices);
        }
    }

    /**
     * method that returns the longest elementary chain in our Graph
     * @return - int
     */
    public int getLargestComponentDiameter(){
        Set<E> mainConsumedVertices = new HashSet<>();
        int globalMax = 0;
        for(E vertex : adjacentLists.keySet()){
            if(!mainConsumedVertices.contains(vertex)){
                Set<E> consumedVertices = new HashSet<>();
                DFS(vertex, consumedVertices);
                int localMax = getConnectedComponentDiameter(consumedVertices);
                if(localMax > globalMax)
                    globalMax = localMax;
                mainConsumedVertices.addAll(consumedVertices);
            }
        }
        return globalMax;
    }

    /**
     * method that applies backtracking strategy to calculate the longest chain in the current connected component
     * @param bktList - solution list
     * @param maxim - wrapper variable which stores the longest chain
     */
    private void backtracking(List<E> bktList, MyVariable<Integer> maxim){
        if(bktList.size() > maxim.getValue())
            maxim.setValue(bktList.size());
        for(E adjacentVertex : adjacentLists.get(bktList.get(bktList.size()-1))){//bktList.get(bktList.size()-1).getFriendList()){
            if(!bktList.contains(adjacentVertex)){
                bktList.add(adjacentVertex);
                backtracking(bktList, maxim);
                bktList.remove(adjacentVertex);
            }
        }
    }

    /**
     * method that calculates the longest chain in our current Graph entity, using backtracking method from every vertex
     * as a start point
     * @return - int(the longest chain in the current community)
     */
    private int getConnectedComponentDiameter(Iterable<E> connectedComponent){
        int globalMax = 0;
        for(E vertex : connectedComponent){
            List<E> bktList = new ArrayList<>();
            bktList.add(vertex);
            MyVariable<Integer> max = new MyVariable<>(0);
            backtracking(bktList, max);
            if(max.getValue() > globalMax)
                globalMax = max.getValue();
        }
        return globalMax;
    }
}
