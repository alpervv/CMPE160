import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        // Enter the coordinate data file here
        File inputFile = new File("input05.txt"); // Open the file of coordinate data
        Scanner inputScanner = new Scanner(inputFile);
        // Each coordinate pair will be stored in a node object
        ArrayList<Node> nodes = new ArrayList<Node>();
        while(inputScanner.hasNextLine()){
            String line = inputScanner.nextLine(); // Data for one node. In the format: x,y
            String[] parts = line.split(","); // Separate x and y coordinates
            double x = Double.parseDouble(parts[0]); // Convert coordinates to double.
            double y = Double.parseDouble(parts[1]);
            nodes.add(new Node(x,y)); // Append new node to the list storing nodes
        }

        int chosenMethod = 2; // Choose algorithm to use. 1: Brute-Force, 2: Ant Colony Optimization
        int mapToDraw = 1; // Choose map to draw. 1: Shortest path (for both algorithms), 2: Pheromone intensity map (only for ant colony)

        if (chosenMethod == 1) {
            int startNodeIndex = 0;
            Node startNode = nodes.get(startNodeIndex);
            BruteForce problem = new BruteForce(nodes, startNode); // Create a BruteForce object to solve the problem.
            long startTime = System.currentTimeMillis(); // Start the timer
            problem.solveProblem(); // Brute-force algorithm takes effect with this method.
            long endTime = System.currentTimeMillis(); // End the timer
            long duration = (endTime - startTime); // Calculate the time it takes to solve the problem
            System.out.println("Method: Brute-Force Method");
            System.out.printf("Shortest Distance: %.5f", problem.getShortestDistance());
            System.out.println();
            var shortestPath = problem.getShortestPath();
            int[] pathIndexes = new int[nodes.size()+1];
            for (int i=0; i<shortestPath.size();i++){
                pathIndexes[i] = nodes.indexOf(shortestPath.get(i))+1;
            }

            System.out.println("Shortest Path: " + Arrays.toString(pathIndexes));

            System.out.println("Time it takes to find the shortest path: " + duration/1000.0 + " seconds");

            drawPath(nodes, problem.getShortestPath());
        }

        if (chosenMethod == 2){
            int iterationCount = 200; // N
            int antsPerIteration = 80; // M
            double degradationConstant = 0.8;
            double alpha = 1.2; // exponent of pheromone in edgeValue
            double beta = 1.8; // exponent of distance in edgeValue
            double initialPheromone = 0.07; // Initial pheromone intensity
            double pheromonePerPath = 0.001; // Q
            AntColony problem = new AntColony(nodes, nodes.get(0), iterationCount, antsPerIteration, degradationConstant, alpha, beta, initialPheromone, pheromonePerPath);
            //AntColony problem = new AntColony(nodes, nodes.get(0));
            long startTime = System.currentTimeMillis(); // Start the timer
            problem.solveProblem();
            long endTime = System.currentTimeMillis(); // End the timer
            long duration = (endTime - startTime); // Calculate the time it takes to solve the problem

            System.out.println("Method: Ant Colony Optimization");
            System.out.printf("Shortest Distance: %.5f", problem.getShortestDistance());
            System.out.println();
            var shortestPath = problem.getShortestPath();
            int[] pathIndexes = new int[nodes.size()+1];
            for (int i=0; i<shortestPath.size();i++){
                pathIndexes[i] = nodes.indexOf(shortestPath.get(i))+1;
            }
            System.out.println("Shortest Path: " + Arrays.toString(pathIndexes));

            System.out.println("Time it takes to find the shortest path: " + duration/1000.0 + " seconds");

            if (mapToDraw == 1)
                drawPath(nodes, problem.getShortestPath());
            if (mapToDraw == 2)
                drawPheromoneMap(nodes, problem.getPheromoneMap());
        }
    }

    private static void drawPath(ArrayList<Node> nodes, ArrayList<Node> path){
        StdDraw.setCanvasSize(660,660);
        StdDraw.setXscale(0.0,1.0);
        StdDraw.setYscale(0.0,1.0);
        StdDraw.enableDoubleBuffering();

        // Draw the lines
        for (int i = 0; i<path.size()-1; i++){
            Node node1 = path.get(i);
            Node node2 = path.get(i+1);
            StdDraw.setPenRadius(0.005);
            StdDraw.line(node1.x, node1.y, node2.x, node2.y);
        }

        // Draw migros
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        Node migros = path.getFirst();
        StdDraw.filledCircle(migros.x, migros.y, 0.015);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 10));
        StdDraw.text(migros.x, migros.y, ""+(1));

        // Drawing nodes
        for (int i=1; i<nodes.size(); i++){
            Node node = path.get(i);
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.filledCircle(node.x, node.y, 0.015);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 10));
            StdDraw.text(node.x, node.y, ""+(nodes.indexOf(node)+1));
        }
        StdDraw.show();
    }

    private static void drawPheromoneMap(ArrayList<Node> nodes, double[][] pheromoneMap){
        StdDraw.setCanvasSize(660,660);
        StdDraw.setXscale(0.0,1.0);
        StdDraw.setYscale(0.0,1.0);
        StdDraw.enableDoubleBuffering();

        // Draw the lines
        double maxPheromone = 0;
        for (double[] pheromoneArray:pheromoneMap) // Find the maximum pheromone intensity on the map
            for (double pheromone: pheromoneArray)
                if (pheromone > maxPheromone)
                    maxPheromone = pheromone;

        for (int i = 1; i<nodes.size(); i++)
            for (int j = 0; j<i; j++){
                Node node1 = nodes.get(i);
                Node node2 = nodes.get(j);
                double pheromoneLevel = pheromoneMap[i][j];
                StdDraw.setPenRadius(0.005*pheromoneLevel/maxPheromone);
                StdDraw.line(node1.x, node1.y, node2.x, node2.y);
            }



        // Drawing nodes
        for (int i=0; i<nodes.size(); i++){
            Node node = nodes.get(i);
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.filledCircle(node.x, node.y, 0.015);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 10));
            StdDraw.text(node.x, node.y, ""+(i+1));
        }
        StdDraw.show();
    }
}