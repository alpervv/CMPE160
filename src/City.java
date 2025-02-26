import java.util.ArrayList;

/**
 * Contains the following information of one city:
 * name of the city, x and y coordinates,
 * neighbours of the city (cities with which this city has a connection).
 * @author Alper Vural
 * @since  Date: 03/29/2024
 */
public class City {
    public String cityName;
    public int x;
    public int y;

    public ArrayList<City> neighbours = new ArrayList<City>(); // Cities with which current city has a connection

    public City(String cityName, int x, int y){
        this.cityName = cityName;
        this.x = x;
        this.y = y;
    }
}
