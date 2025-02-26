import java.util.ArrayList;

/**
 * Superclass of solution algorithms for delivery man problem.
 * Given a complete directed graph G = (V, A), the delivery man problem
 * consists of determining a Hamiltonian circuit minimizing the sum
 * of distances (along the circuit) from a given vertex v,
 * to every vertex of V, including v itself.
 * @author Alper Vural
 * @since Date: May 2024
 */
public abstract class SolutionMethod {
    protected ArrayList<Node> nodes; // List of nodes
    protected Node sourceNode; // Starting node
    protected double shortestDistance; // Shortest computed distance for given list of nodes
    protected ArrayList<Node> shortestPath; // The path giving the shortest distance

    SolutionMethod(){
        this.nodes = null;
    }

    /**
     * Creates a delivery man problem to solve
     * @param nodes ArrayList of all nodes. Order of the nodes do not affect the solution.
     * @param sourceNode The node to start from and end at.
     */
    SolutionMethod(ArrayList<Node> nodes, Node sourceNode){
        this.nodes = nodes;
        this.sourceNode = sourceNode;
    }

    /**
     * Solves problem and updates shortestDistance and shortestPath accordingly.
     * Calling getDistance and getPath methods before calling solveProblem
     * will not return solutions to problem.
     */
    public abstract void solveProblem();

    public double getShortestDistance(){
        return shortestDistance;
    }

    public ArrayList<Node> getShortestPath(){
        return shortestPath;
    }


}
