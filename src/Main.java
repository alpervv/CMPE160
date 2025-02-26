import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

/**
 * Finds the shortest path between two cities, prints the data
 * on the console and creates a graphical output showing the path.
 * @author Alper Vural
 * @since Date: 03/29/2024
 */
public class Main {
    /**
     * Finds the shortest path between two cities, prints the data
     * on the console and creates a graphical output showing the path.
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException{
        // Load city coordinate data
        // Each city's information is stored in one line. A line consists of the following:
        // cityName, cityXcoordinate, cityYcoordinate
        File coordinatesFile = new File("city_coordinates.txt");
        Scanner coordinateReader = new Scanner(coordinatesFile);
        ArrayList<City> cities = new ArrayList<City>(); // Cities will be stored in an arraylist since the number of cities may vary

        while (coordinateReader.hasNextLine()){
            String[] cityInfo = coordinateReader.nextLine().split(","); // Read city information (next line) and split it to its components
            String cityName = cityInfo[0]; // Name of the city
            int x = Integer.parseInt(cityInfo[1].strip()); // Convert city's x and y coordinates to integer. Strip() to get rid of whitespaces
            int y = Integer.parseInt(cityInfo[2].strip());
            cities.add(new City(cityName,x,y)); // Append new city to cities
        } // End of loading coordinate data


        // Load connection data
        // Each row in text file represents one connection. City names are seperated with a comma
        // cityName1, cityName2
        File connectionsFile = new File("city_connections.txt");
        Scanner connectionsReader = new Scanner(connectionsFile);
        ArrayList<City[]> connections = new ArrayList<City[]>(); // Arraylist of connections. A connection is an array of City objects {city1, city2}

        while (connectionsReader.hasNextLine()){
            City[] connection = new City[2]; // Initialize connection
            String[] connectionText = connectionsReader.nextLine().split(","); // Read connection information and split it to the array {city1Name, city2Name}
            connectionText[0] = connectionText[0].strip(); // Get rid of whitespaces
            connectionText[1] = connectionText[1].strip();
            // find city1 and city2 by their names
            for (City city:cities) {
                if (city.cityName.equals(connectionText[0])) // Check if cityName and text for match
                    connection[0] = city;
                else if (city.cityName.equals(connectionText[1]))
                    connection[1] = city;
            }
            connections.add(connection); // Append connection to connections
            connection[0].neighbours.add(connection[1]); // Update neighbours of each city
            connection[1].neighbours.add(connection[0]);
        } // End of loading connections data


        // Dijkstra's algorithm
        City source = takeInputSourceCity(cities); // source and target cities
        City target = takeInputTargetCity(cities);

        // Corresponds to unvisited nodes in Dijkstra's algorithm
        // Initially set to list of all cities
        ArrayList<City> unvisitedCities = new ArrayList<City>();
        for (City city:cities) // Copy cities ArrayList
            unvisitedCities.add(city);

        // Create a list of distance to source city
        // Indexes of distanceList are in synch with indexes of cities
        // For example distanceList.get(7) is the distance from source to cities.get(7)
        // Initially all distances but the source city's are set to infinity
        ArrayList<Double> distanceList = new ArrayList<Double>();
        for (int i = 0; i<cities.size(); i++) // Creates a list with all distances set to infinity
            distanceList.add(999999999999.0);
        distanceList.set(cities.indexOf(source), 0.0); // Set starting node's distance to 0

        // Stores distances to unvisited cities
        // Indexes are aligned with unvisited cities' indexes
        // Initially all cities are unvisited
        ArrayList<Double> unvisitedDistanceList = new ArrayList<Double>();
        for (int i = 0; i<cities.size(); i++) // Creates a list with all distances set to infinity
            unvisitedDistanceList.add(999999999999.0);
        unvisitedDistanceList.set(cities.indexOf(source), 0.0); // Set starting node's distance to 0

        City currentCity; // corresponds to current node in tha algorithm
        double currentMinDistance; // declare minimum distance
        while (true){
            if (unvisitedCities.size() == 0) // Exit algorithm if all cities are visited
                break;
            currentMinDistance = min(unvisitedDistanceList);
            if (currentMinDistance > 999999999998.0) // Exit algorithm if all distances to unvisited cities are infinity
                break;
            currentCity = unvisitedCities.get(unvisitedDistanceList.indexOf(currentMinDistance)); // set current node to the unvisited city with the minimum distance to source

            // Update distances to neighbour nodes
            for (City neighbour:currentCity.neighbours){
                if (unvisitedCities.contains(neighbour)) { // Only consider unvisited neighbours
                    int neighbourIndex = cities.indexOf(neighbour);
                    double assignedDistance = distanceList.get(neighbourIndex); // Distance currently assigned to neighbour
                    double distanceThroughNode = currentMinDistance + distanceBetween(currentCity, neighbour); // The distance to neighbour if we were to reach it through current city
                    if (distanceThroughNode < assignedDistance) { // Sets neighbours distance to the smaller one
                        distanceList.set(neighbourIndex, distanceThroughNode);
                        int unvisitedIndex = unvisitedCities.indexOf(neighbour); // Updates distance in unvisitedDistanceList
                        unvisitedDistanceList.set(unvisitedIndex, distanceThroughNode);
                    }
                }
            }
            unvisitedCities.remove(currentCity); // Marks current city as visited
            unvisitedDistanceList.remove(currentMinDistance); // Recall that currentCity was the distance with currentMinDistance
        }

        if (unvisitedCities.contains(target)) // If target city is not visited it can't be reached
            System.out.println("No path could be found.");
        else {
            // Trace the minimum distance path to target
            // Starting from the target node, pick its neighbour with minimum distance
            // Repeat until source city is reached
            ArrayList<City> path = new ArrayList<City>();
            currentCity = target;
            path.add(target);
            while (currentCity != source){ // Repeat until source city is reached
                City minimalNeighbour = null; // Neighbour with minimum distance
                double minDistance = 999999999999.0; // Initially set minDistance to infinity
                for (City neighbour:currentCity.neighbours){
                    int neighbourIndex = cities.indexOf(neighbour); // Index of neighbour in cities, in synch with distanceList
                    if (distanceList.get(neighbourIndex) < minDistance){ // Update if a closer neighbour is found
                        minimalNeighbour = neighbour;
                        minDistance = distanceList.get(neighbourIndex);
                    }
                }
                path.add(0,minimalNeighbour); // Insert the neighbour to beginning of the path
                currentCity = minimalNeighbour;
            } // End of Dijkstra's algorithm

            // Produce console output
            double minimalDistance = distanceList.get(cities.indexOf(target)); // Minimum distance to target city computed by the algorithm
            System.out.print("Total Distance: ");
            System.out.printf("%.2f",minimalDistance);
            System.out.print(". Path: " + source.cityName);
            for (int i = 1; i<path.size();i++)
                System.out.print(" -> " + path.get(i).cityName);

            // Start drawing
            StdDraw.setCanvasSize(2377/2,1055/2); // Adjust canvas size
            StdDraw.setXscale(0,2377); // Adjusted according to width of map.png
            StdDraw.setYscale(0,1055); // Adjusted according to heigth of map.png
            StdDraw.picture(2377/2,1055/2,"map.png",2377,1055); // Load the map
            StdDraw.enableDoubleBuffering(); // To increase performance

            for (City city: cities){ // Draw all cities on map
                drawCity(city);
            }
            for (City[] connection:connections){ // Draw connections
                drawConnection(connection[0], connection[1]);
            }
            for (City city:path) // Highlight each city on the path
                highlightCity(city);
            for (int i = 0; i < path.size()-1; i++) // Highlight each connection on the path
                highlightConnection(path.get(i),path.get(i+1));
            StdDraw.show();
            // End of drawing
        } // End of the case where a path exists
    }


    /**
     * Displays a city on canvas.
     * @param city City that wants to be displayed. Must be a City object.
     */
    public static void drawCity(City city){
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.setFont(new Font("Arial",Font.BOLD,12)); // Font used for city name
        StdDraw.filledCircle(city.x,city.y,5); // Display city centre
        StdDraw.text(city.x,city.y+15,city.cityName); // Display city name above city centre
    }

    /**
     * Draws a line between the centers of two cities
     * @param city1 First city
     * @param city2 Second city
     */
    public static void drawConnection(City city1, City city2){
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.setPenRadius(0.002); // Determines lines thickness
        StdDraw.line(city1.x,city1.y,city2.x,city2.y); // Draws line between two city centres
    }

    /**
     * Calculates distance between two cities
     */
    public static double distanceBetween(City city1, City city2){
        double x1 = city1.x;
        double y1 = city1.y;
        double x2 = city2.x;
        double y2 = city2.y;
        return Math.pow((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2),0.5); // sqrt(a^2+b^2)
    }

    /**
     * Finds minimum value in the given list
     * @return minimum value in the ArrayList
     */
    public static double min(ArrayList<Double> myList){
        double minimum = myList.get(0);
        for (int i = 1; i < myList.size(); i++) {
            if (minimum > myList.get(i))
                minimum = myList.get(i);
        }
        return minimum;
    }

    /**
     * Highlights city in blue
     */
    public static void highlightCity(City city){
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.setFont(new Font("Arial",Font.BOLD,12)); // Font used for city name
        StdDraw.filledCircle(city.x,city.y,5)   ; // Display city centre
        StdDraw.text(city.x,city.y+15,city.cityName); // Display city name above city centre
    }

    /**
     * Highlights the line between two cities.
     * Thicker line in blue.
     */
    public static void highlightConnection(City city1, City city2){
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.setPenRadius(0.01); // Determines lines thickness
        StdDraw.line(city1.x,city1.y,city2.x,city2.y); // Draws line between two city centres
    }

    /**
     * Prompts the user to enter a city name until one cityName contained by one
     * of the City objects in cities is entered.
     * @param cities An arraylist of cities that user is required to choose from
     * @return City Object the user wanted to start from
     */
    public static City takeInputSourceCity(ArrayList<City> cities){
        Scanner inputScanner = new Scanner(System.in); // Scanner to read user input on console
        ArrayList<String> cityNameList = new ArrayList<String>(); // Create an array to store city names. This will optimize the code
        for (City city:cities){ // Add all city names to cityNameArray
            cityNameList.add(city.cityName.toLowerCase()); // convert to lower case to make the program more user-friendly
        }
        boolean isValidStart; // True when a valid city name is entered, false otherwise
        System.out.print("Enter starting city: "); // Ask user to enter a city name
        String startingCityName = inputScanner.next().toLowerCase(); // name of starting city
        if (cityNameList.contains(startingCityName)) // Check if user entered a valid city name
            isValidStart = true;
        else
            isValidStart = false;
        while (!isValidStart){ // Keep asking user to enter a valid city name
            System.out.printf("City named '%s' not found. Please enter a valid city name. \n",startingCityName); // invalid city warning
            System.out.print("Enter starting city: "); // Ask user to enter a city name
            startingCityName = inputScanner.next().toLowerCase(); // name of starting city
            if (cityNameList.contains(startingCityName)) // Check if user entered a valid city name
                isValidStart = true;
        }
        int index = cityNameList.indexOf(startingCityName); // indexes of cities and cityNameList are in synch
        return cities.get(index);
    }

    /**
     * Prompts the user to enter a city name until one cityName contained by one
     * of the City objects in cities is entered.
     * @param cities An arraylist of cities that user is required to choose from
     * @return City Object the user wants to go to
     */
    public static City takeInputTargetCity(ArrayList<City> cities){
        Scanner inputScanner = new Scanner(System.in); // Scanner to read user input on console
        ArrayList<String> cityNameList = new ArrayList<String>(); // Create an array to store city names. This will optimize the code
        for (City city:cities){ // Add all city names to cityNameArray
            cityNameList.add(city.cityName.toLowerCase()); // convert to lower case to make the program more user-friendly
        }
        boolean isValidTarget; // True when a valid city name is entered, false otherwise
        System.out.print("Enter destination city: "); // Ask user to enter a city name
        String targetCityName = inputScanner.next().toLowerCase(); // name of target city
        if (cityNameList.contains(targetCityName)) // Check if user entered a valid city name
            isValidTarget = true;
        else
            isValidTarget = false;
        while (!isValidTarget){ // Keep asking user to enter a valid city name
            System.out.printf("City named '%s' not found. Please enter a valid city name. \n",targetCityName); // invalid city warning
            System.out.print("Enter destination city: "); // Ask user to enter a city name
            targetCityName = inputScanner.next().toLowerCase(); // name of starting city
            if (cityNameList.contains(targetCityName)) // Check if user entered a valid city name
                isValidTarget = true;
        }
        int index = cityNameList.indexOf(targetCityName); // indexes of cities and cityNameList are in synch
        return cities.get(index);
    }
}