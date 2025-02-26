import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A solution method for delivery man problem. Bruteforce method tries
 * all permutations for given set of nodes to find the optimal solution.
 * @author Alper Vural
 * @since Date: May 2024
 */
public class BruteForce extends SolutionMethod{
    private Node[] tempShortestPath = null; // This datafield stores shortest path as an Array. This is done for optimization concerns.
    BruteForce(){} // Default constructor

    /**
     * Creates a delivery man problem to solve using bruteforce method
     * @param nodes ArrayList of all nodes. Order of the nodes do not affect the solution.
     * @param sourceNode The node to start from and end at.
     */
    BruteForce(ArrayList<Node> nodes, Node sourceNode) {
        super(nodes, sourceNode);
    }

    /**
     * Solves problem and updates shortestDistance and shortestPath accordingly.
     * Calling getDistance and getPath methods before calling solveProblem
     * will not return solutions to problem. This method may take long time
     * to terminate.
     */
    public void solveProblem(){
        // Shortest distance is initially set to a large value.
        // Because algorithm depends on checking path lengths and choosing the one with smaller length.
        shortestDistance = Double.MAX_VALUE;
        Node[] nodesAsArray = nodes.toArray(new Node[nodes.size()]); // Store nodes in an array for optimization purposes
        permuteAndTry(nodesAsArray, 0); // This function algorithmically tries all permutations and updates shortestPath and shortestDistance
        shortestPath = new ArrayList<Node>();
        // permuteAndTry method stores shortest path in an Array (for optimization), we convert it to an ArrayList.
        for (int i = 0; i<tempShortestPath.length; i++){
            shortestPath.add(tempShortestPath[i]);
        }

        // Shift shortestPath so that first node becomes the first element
        ArrayList<Node> tempNodes = (ArrayList<Node>)shortestPath.clone(); // create a copy of shortestPath as it will be updated
        int shiftBy = shortestPath.indexOf(sourceNode);
        int N = shortestPath.size();
        if (shiftBy != 0)
            for (int i=0; i<N; i++){
                shortestPath.set((i-shiftBy+N) % N,tempNodes.get(i));
            }
        // For optimization purposes, paths don't include their ending nodes, but we know it is the same as starting node
        shortestPath.add(sourceNode); // Append ending node
    }

    /**
     * This method algorithmically permutes through all permutations of given array of nodes
     * For each permutation: length of the path is calculated,
     * shortestPath and ShortestDistance data-fields are updated.
     * @param nodeArray The array of nodes to permute over
     * @param k Set this parameter to 0 in order to find all permutations
     */
    private void permuteAndTry(Node[] nodeArray, int k) {
        if (k == nodeArray.length) { // k
            double distance = findPathLength(nodeArray);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                tempShortestPath = nodeArray.clone();
            }
        } else { // Algorithm to find next permutation
            for (int i = k; i < nodeArray.length; i++) {
                Node temp = nodeArray[i];
                nodeArray[i] = nodeArray[k];
                nodeArray[k] = temp;
                permuteAndTry(nodeArray, k + 1);
                temp = nodeArray[k];
                nodeArray[k] = nodeArray[i];
                nodeArray[i] = temp;
            }
        }
    }


    /**
     * @return pythagorean distance between two nodes
     */
    private static double distanceBetween(Node node1, Node node2){
        double xDif = node1.x-node2.x;
        double yDif = node1.y-node2.y;
        return Math.pow(xDif*xDif + yDif*yDif, 0.5); // sqrt((x1-x2)^2+(y1-y2)^2)
    }

    /**
     * Given an ArrayList of nodes, calculate the distance of the path that follows the nodes sequentially
     * and ends at the starting node. It is assumed that the ArrayList does not contain the starting node at the end
     * @param path An ArrayList of nodes making the path. If it doesn't have starting node at the end,
     * distance is still calculated as we go back to starting node.
     */
    private static double findPathLength(ArrayList<Node> path){
        double pathLength = 0;
        for (int i = 0; i < path.size()-1; i++){ // sum up distances between neighbouring nodes
            pathLength += distanceBetween(path.get(i), path.get(i+1));
        }
        pathLength += distanceBetween(path.getFirst(), path.getLast()); // Connect the path to form a loop
        return pathLength;
    }

    /**
     * Given an Array of nodes, calculate the distance of the path that follows the nodes sequentially
     * and ends at the starting node.
     * @param path An Array of nodes making the path. If it doesn't have starting node at the end,
     * distance is still calculated as we go back to starting node.
     */
    private static double findPathLength(Node[] path){
        double pathLength = 0;
        for (int i = 0; i < path.length-1; i++){ // sum up distances between neighbouring nodes
            pathLength += distanceBetween(path[i], path[i+1]);
        }
        pathLength += distanceBetween(path[0], path[path.length-1]); // Connect the path to form a loop
        return pathLength;
    }
}

