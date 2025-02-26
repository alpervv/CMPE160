import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * A solution method for delivery man problem. Ant colony optimization
 * is inspired by ants finding a very short path to a food source.
 * @author Alper Vural, Student ID: 2023400066
 * @since Date: May 2024
 */
public class AntColony extends SolutionMethod{
    private int iterationCount; // N
    private int antsPerIteration; // M
    private double degradationConstant;
    private double alpha; // exponent of pheromone in edgeValue
    private double beta; // exponent of distance in edgeValue
    private double initialPheromone; // Initial pheromone intensity
    private double pheromonePerPath; // Q
    private double[][] pheromoneMap;
    AntColony(){} // Default constructor

    /**
     * Creates a delivery man problem to solve using ant colony optimization with default parameters.
     * @param nodes ArrayList of all nodes. Order of the nodes do not affect the solution.
     * @param sourceNode The node to start from and end at.
     */
    AntColony(ArrayList<Node> nodes, Node sourceNode) {
        super(nodes, sourceNode);
        this.iterationCount = 100;
        this.antsPerIteration = 50;
        this.degradationConstant = 0.9;
        this.alpha = 0.8;
        this.beta = 1.5;
        this.initialPheromone = 0.1;
        this.pheromonePerPath = 0.0001;

        int numNodes = nodes.size();
        // pheromoneMap is structured as follows
        // pheromoneMap[i]: Array of pheromones between Node_i and other nodes
        // for example
        // pheromoneMap[0][2]: Pheromone intensity between Node0 and Node2
        pheromoneMap = new double[numNodes][numNodes];
        for (double[] pheromoneArray: pheromoneMap) // Fill entire map with initial pheromone level
            Arrays.fill(pheromoneArray, initialPheromone);
    }

    /**
     * Creates a delivery man problem to solve using ant colony optimization with custom parameters.
     * @param nodes ArrayList of all nodes. Order of the nodes do not affect the solution.
     * @param sourceNode The node to start from and end at.
     * @param iterationCount number of iterations
     * @param antsPerIteration number of ants created per each iteration
     * @param degradationConstant at the end of each iteration, pheromone levels are
     * gradually decreased by degradation constant. Degradation constant should be between 0 and 1.
     * @param alpha exponent of pheromone in edgeValue
     * @param beta exponent of distance in edgeValue
     * @param initialPheromone initial level of pheromone on a path. Value must be different from 0.
     * @param pheromonePerPath After each ant traversal, pheromone intensity
     * of an edge in the cycle is updated by the amount pheromonePerPath/(length of the path)
     */
    AntColony(ArrayList<Node> nodes, Node sourceNode, int iterationCount, int antsPerIteration, double degradationConstant, double alpha, double beta, double initialPheromone, double pheromonePerPath) {
        super(nodes, sourceNode);
        this.iterationCount = iterationCount;
        this.antsPerIteration = antsPerIteration;
        this.degradationConstant = degradationConstant;
        this.alpha = alpha;
        this.beta = beta;
        this.initialPheromone = initialPheromone;
        this.pheromonePerPath = pheromonePerPath;

        int numNodes = nodes.size();
        // pheromoneMap is structured as follows
        // pheromoneMap[i]: Array of pheromones between Node_i and other nodes
        // for example
        // pheromoneMap[0][2]: Pheromone intensity between Node0 and Node2
        pheromoneMap = new double[numNodes][numNodes];
        for (double[] pheromoneArray: pheromoneMap) // Fill entire map with initial pheromone level
            Arrays.fill(pheromoneArray, initialPheromone);
    }

    /**
     * Solves problem and updates shortestDistance and shortestPath accordingly.
     * Calling getDistance and getPath methods before calling solveProblem
     * will not return solutions to problem.
     */
    public void solveProblem(){
        ArrayList<Node> bestPath = null; // Shortest path is stored here
        double bestDistance = Double.MAX_VALUE; // Shortest distance is stored here. Initially set to a large value.
        Random randomGenerator = new Random();
        for (int n = 0; n<iterationCount; n++) { // Repeating iterations
            for (int m = 0; m < antsPerIteration; m++) { // Repeating traversing ants
                Node antStartNode = nodes.get(randomGenerator.nextInt(0, nodes.size())); // randomly choose ant's starting position
                Node currentNode = antStartNode; // Initially set currentNode to ant's starting node
                ArrayList<Node> unvisitedNodes = (ArrayList<Node>) nodes.clone(); // Initially all nodes but ant's starting node are unvisited
                unvisitedNodes.remove(antStartNode);
                ArrayList<Node> antPath = new ArrayList<Node>(); // The path ant follows is stored here
                antPath.add(antStartNode); // Add ant's starting node to path

                while (unvisitedNodes.size() > 0) { // Probabilistically choose next node from unvisited nodes until all nodes are visited
                    currentNode = probabilisticChoose(currentNode, unvisitedNodes);
                    antPath.add(currentNode);
                    unvisitedNodes.remove(currentNode);
                }
                antPath.add(antStartNode); // Return back to starting node

                // Update pheromone intensity on the path
                double intensityIncrease = pheromonePerPath / findPathLength(antPath); // intensity increase in each edge
                for (int i = 0; i < antPath.size() - 1; i++) {
                    Node node1 = antPath.get(i);
                    Node node2 = antPath.get(i + 1);
                    int index1 = nodes.indexOf(node1); // Indexes of node1 and node2 in nodes datafield is required
                    int index2 = nodes.indexOf(node2); // since pheromoneMap is in sync with these indexes
                    pheromoneMap[index1][index2] += intensityIncrease;
                    pheromoneMap[index2][index1] += intensityIncrease;
                }
                // Update the shortest path
                if (findPathLength(antPath) < bestDistance) { // If a shorter path is found, store it instead
                    bestPath = antPath;
                    bestDistance = findPathLength(bestPath);
                }
            } // end of traversing ants

            // Degrade all pheromone levels
            for (double[] pheromoneArray:pheromoneMap)
                for (int i = 0; i<pheromoneArray.length; i++)
                    pheromoneArray[i] *= degradationConstant; // Multiply by degradation constant
        } // end of all iterations

        this.shortestDistance = bestDistance;
        this.shortestPath = bestPath;

        shortestPath.removeLast();
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
     * @return A probabilistically chosen node according to pheromone levels and distances between nodes.
     */
    private Node probabilisticChoose(Node currentNode, ArrayList<Node> unvisitedNodes){
        double[] probabilityArray = new double[unvisitedNodes.size()]; // Array to store probabilities for nodes to be chosen as next
        for (int i = 0; i<unvisitedNodes.size(); i++){ // Calculate probabilities.
            // probabilitiyArray's indexes are updated in synch with unvisitedNodes indexes
            // For exapmle, probabilitiyArray[5] is the probability to choose unvisitedNodes[5]
            Node unvisitedNode = unvisitedNodes.get(i);
            int currentIndex = nodes.indexOf(currentNode); // Indexes of current and unvisited node in the nodes datafield
            int unvisitedIndex = nodes.indexOf(unvisitedNode);
            double pheromone = pheromoneMap[currentIndex][unvisitedIndex]; // Pheromone intensity between nodes
            double distance = distanceBetween(unvisitedNode, currentNode);
            probabilityArray[i] = Math.pow(pheromone, alpha)/Math.pow(distance, beta); // Update probability
        }
        double totalProb = 0; // Calculate the sum of probabilities in probabilityArray
        for (double prob: probabilityArray)
            totalProb += prob;

        // Choose an element randomly
        double randomSum = Math.random()*totalProb; // random number between 0 and sum of probabilities
        double currentSum = 0;
        for (int i = 0; i<unvisitedNodes.size(); i++){ // Add probabilities until currentSum exceeds randomSum.
            currentSum += probabilityArray[i];
            if (currentSum > randomSum)
                return unvisitedNodes.get(i); // return probabilistically chosen node
        }
        return unvisitedNodes.get(0);
    }

    private static double distanceBetween(Node node1, Node node2){
        return Math.pow((node1.x-node2.x)*(node1.x-node2.x)+(node1.y-node2.y)*(node1.y-node2.y),0.5); // sqrt((x1-x2)^2+(y1-y2)^2)
    }

    /**
     * @return The length of the path
     */
    private static double findPathLength(ArrayList<Node> path){
        double pathLength = 0;
        for (int i = 0; i < path.size()-1; i++){ // sum up distances between neighbouring nodes
            pathLength += distanceBetween(path.get(i), path.get(i+1));
        }
        return pathLength;
    }

    /**
     * @return pheromoneMap
     */
    public double[][] getPheromoneMap(){
        return pheromoneMap;
    }


}
