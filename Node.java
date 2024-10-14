import java.util.List;

/**
 * Represents a node in the Traveling Salesman Problem (TSP).
 * A node can either represent a city with its coordinates, or a path through multiple cities.
 */
public class Node {

    private int posX;
    private int posY;
    private int identifier;
    private List<Integer> tourCities;

    /**
     * Constructor for defining a city location with x and y coordinates.
     *
     * @param posX X-coordinate of the city.
     * @param posY Y-coordinate of the city.
     */
    public Node(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Constructor for defining a node based on a list of cities, representing a tour.
     *
     * @param tourCities List of city identifiers representing the tour.
     */
    public Node(List<Integer> tourCities) {
        this.tourCities = tourCities;
    }

    /**
     * Gets the X-coordinate of the city.
     *
     * @return X-coordinate.
     */
    public int getPosX(){
        return posX;
    }

    /**
     * Gets the Y-coordinate of the city.
     *
     * @return Y-coordinate.
     */
    public int getPosY(){
        return posY;
    }

    /**
     * Sets the identifier for the city.
     *
     * @param identifier Unique identifier for the city.
     */
    public void setIdentifier(int identifier){
        this.identifier = identifier;
    }

    /**
     * Gets the identifier of the city.
     *
     * @return City identifier.
     */
    public int getIdentifier(){
        return identifier;
    }

    /**
     * Gets the list of city identifiers representing the tour.
     *
     * @return List of city identifiers.
     */
    public List<Integer> getTourCities() {
        return tourCities;
    }

    /**
     * Computes the total length of the path through the list of cities based on a given distance matrix.
     *
     * @param distanceMatrix 2D array representing distances between each pair of cities.
     * @return Total length of the path.
     */
    public int computePathLength(int[][] distanceMatrix) {
        int pathLength = 0;
        for (int i = 0; i < tourCities.size() - 1; i++) {
            int startCity = tourCities.get(i);
            int endCity = tourCities.get(i + 1);
            pathLength += distanceMatrix[startCity][endCity];
        }
        return pathLength;
    }

    public double distanceTo(Node other) {
        int dx = other.posX - this.posY;
        int dy = other.posY - this.posX;
        return Math.sqrt(dx * dx + dy * dy);
    }

    
}
