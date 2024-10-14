/**
 * Hill Climb Traveling Salesman Problem
 * 
 * @author Darcy Studdert
 * @version 20/10/23
 * 
 */

import java.io.*;
import java.util.*;

/**
 * HillClimbTSP is a class for solving the Traveling Salesman Problem (TSP)
 * using a hill climbing algorithm with specified parameters.
 */
public class climbTSP {

    private int numNodes;
    private double[][] coordinates; // Store x and y coordinates
    private int maxIterations;
    private int plateauLimit;
        private static int[][] distances;


    /**
     * Initializes a new instance of the HillClimbTSP class.
     *
     * @param numNodes      The number of nodes in the TSP problem.
     * @param coordinates   The coordinates of the nodes 
     * @param maxIterations The maximum number of iterations for the hill climbing algorithm.
     * @param plateauLimit  The limit for consecutive iterations without improvement before restarting.
     */
    public climbTSP(int numNodes, double[][] coordinates, int maxIterations, int plateauLimit) {
        this.numNodes = numNodes;
        this.coordinates = coordinates;
        this.maxIterations = maxIterations;
        this.plateauLimit = plateauLimit;
    }

    /**
     * Main method to run the TSP solver with command-line arguments.
     *
     * @param args takes in Input file max iterations and pleatau limit
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java HillClimbTSP <input_file> <Max Iterations> <Plateau Limit>");
            return;
        }

        String inputFileName = args[0];
        int maxIterations = Integer.parseInt(args[1]);
        int plateauLimit = Integer.parseInt(args[2]);

        try {
            Scanner scanner = new Scanner(new File(inputFileName));

            // Read the number of nodes
            int numNodes = scanner.nextInt();
            double[][] coordinates = new double[numNodes][2]; // Store x and y coordinates

            for (int i = 0; i < numNodes; i++) {
                int x = scanner.nextInt();
                int y = scanner.nextInt();
                coordinates[i][0] = x;
                coordinates[i][1] = y;
            }

            climbTSP tspSolver = new climbTSP(numNodes, coordinates, maxIterations, plateauLimit);

            long startTime = System.nanoTime();
            List<Integer> bestSolution = tspSolver.solve();
            long endTime = System.nanoTime();
            double durationMs = (endTime - startTime) / 1e6;

            double bestCost = tspSolver.calculateTourLength(bestSolution);

            System.out.println("Best TSP Tour: " + bestSolution);
            System.out.println("Total Tour Length: " + bestCost);
            System.out.println("Time Taken: " + durationMs + " ms");

            scanner.close();

        } catch (FileNotFoundException e) {
            System.err.println("Error reading the input file: " + e.getMessage());
        }
    }

    /**
     * Solves the TSP problem using a hill climbing algorithm.
     *
     * @return The best TSP tour found by the algorithm.
     */
    private List<Integer> solve() {
        List<Integer> currentSolution = generateRandomSolution();
        List<Integer> bestSolution = new ArrayList<>(currentSolution);
        double currentCost = calculateTourLength(currentSolution);
        double bestCost = currentCost;

        int plateauSize = 0; // Counter for consecutive iterations without improvement
        int iteration = 0;

        while (iteration < maxIterations) {
            List<Integer> neighborSolution = generateNeighborSolution(currentSolution);
            double neighborCost = calculateTourLength(neighborSolution);

            if (neighborCost < currentCost) {
                currentSolution = neighborSolution;
                currentCost = neighborCost;
                plateauSize = 0; // Reset plateau size counter because there was an improvement

                if (currentCost < bestCost) {
                    bestSolution = new ArrayList<>(currentSolution);
                    bestCost = currentCost;
                }
            } else {
                plateauSize++;

                if (plateauSize >= plateauLimit) {
                    // Stop the search because no improvement is seen for a reasonably large number of consecutive iterations
                    break;
                }
            }

            iteration++;
        }

        return bestSolution;
    }

    /**
     * Generates a neighboring solution by swapping two nodes in the current solution.
     *
     * @param currentSolution The current TSP tour.
     * @return A neighboring TSP tour.
     */
    
    private List<Integer> generateNeighborSolution(List<Integer> currentSolution) {
        List<Integer> neighborSolution = new ArrayList<>(currentSolution);
        Random rand = new Random();
        int i, j;
        do {
            i = rand.nextInt(numNodes - 2) + 1;
            j = rand.nextInt(numNodes - 2) + 1;
        } while (i == j);
        Collections.swap(neighborSolution, i, j);
        return neighborSolution;
    }

    /**
     * Generates a random initial solution by shuffling node indinces.
     *
     * @return A random initial TSP tour.
     */
    private List<Integer> generateRandomSolution() {
        // Generate a random perm
        List<Integer> randomSolution = new ArrayList<>();
        for (int i = 1; i < numNodes; i++) {
            randomSolution.add(i);
        }
        Random rand = new Random();
        for (int i = numNodes - 2; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            if (i != j) {
                int temp = randomSolution.get(i);
                randomSolution.set(i, randomSolution.get(j));
                randomSolution.set(j, temp);
            }
        }
        // Insert the starting node 0
        randomSolution.add(0, 0);
        randomSolution.add(0);
        return randomSolution;
    }

    /**
     * Calculates the total length of a TSP tour based on the given distances and tour order.
     *
     * @param tour The order in which cites are visited in the tour.
     * @return The total length olf the tour.
     */
    private double calculateTourLength(List<Integer> tour) {
        double totalLength = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            int city1 = tour.get(i);
            int city2 = tour.get(i + 1);
            // Use the Euclidean distance between two cities as the edge weight
            double x1 = coordinates[city1][0];
            double y1 = coordinates[city1][1];
            double x2 = coordinates[city2][0];
            double y2 = coordinates[city2][1];
            double distance = calculateDistance(x1, y1, x2, y2);
            totalLength += distance;
        }
        return totalLength;
    }

    /**
     * calc distance
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The distance between the two points.
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    
}
