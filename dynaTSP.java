


/**
 * Hill Climb Traveling Salesman Problem
 * 
 * @author Darcy Studdert
 * @version 20/10/23
 * 
 * Reference: Travelling Salesman Problem using Dynamic Programming.
 * Source: https://www.geeksforgeeks.org/travelling-salesman-problem-using-dynamic-programming/
 *  
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class dynaTSP {
    private static int cityCount;
    private static double[][] distances;
    private static double[][] table;    
    private static final double BIG_NUMBER = 9999999.99;
    private static List<Node> cityList;
    private static List<Integer> optimalTour = new ArrayList<>();
    private static double optimalCost = BIG_NUMBER; 
    private static int[][] backtrack;
    private static int currentStep;

    /**
     * Entry point
     * @param args  args[0] is the input file containing city coords
     * 
     */
    public static void main(String[] args) {
        readInput(args[0]);
        calculateDistances();
        initializeTable();

        long startTime = System.nanoTime();
        solveTSP();
        constructOptimalPath();

        long endTime = System.nanoTime();
        double executionTime = (double) (endTime - startTime) / 1000000000;
        System.out.println("Optimal tour cost = " + optimalCost);
        System.out.println("Total execution time: " + executionTime + " seconds");
    }

    /**
     * Given a file this program will read will populate nodes 
     * @param filename to be read
     */
    private static void readInput(String filename) {
        cityList = new ArrayList<>();
        try {
            Scanner fileScanner = new Scanner(new File(filename));
            cityCount = fileScanner.nextInt();
            fileScanner.nextLine();
            distances = new double[cityCount][cityCount];
            table = new double[cityCount][1 << cityCount];
            backtrack = new int[cityCount][1 << cityCount];

            for (int i = 0; i < cityCount; i++) {
                Arrays.fill(backtrack[i], -1);
            }

            for (int i = 0; i < cityCount; i++) {
                String line = fileScanner.nextLine();
                String[] coordData = line.split("\\s+");
                if (coordData.length >= 2) {
                    int x = Integer.parseInt(coordData[0]);
                    int y = Integer.parseInt(coordData[1]);
                    Node location = new Node(x, y);
                    location.setIdentifier(i);
                    cityList.add(location);
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
        }
    }

    /**
     * calculates pairwise distancs between all citie
     */
    private static void calculateDistances() {
        for (int i = 0; i < cityCount; i++) {
            for (int j = 0; j < cityCount; j++) {
                if (i == j) {
                    distances[i][j] = BIG_NUMBER;
                } else {
                    int deltaX = cityList.get(j).getPosX() - cityList.get(i).getPosX();
                    int deltaY = cityList.get(j).getPosY() - cityList.get(i).getPosY();
                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                    distances[i][j] = distance;
                }
            }
        }
    }

    /**
     * Initializes the dynamic programming table used to store intermediate results.
     * Initially, all distances are set to a large value @param BIG_NUMBER.
     */

    private static void initializeTable() {
        for (int i = 0; i < cityCount; i++) {
            for (int j = 0; j < (1 << cityCount); j++) {
                table[i][j] = BIG_NUMBER;
            }
        }
        table[0][1] = 0;
    }

    
    /**
     * Main method responsible for solving the Traveling Salesman Problem (TSP) using dynamic programming.
     * SolveTSP and helper methods 
     */
    private static void solveTSP() {
        for (int subset = 1; subset < (1 << cityCount); subset += 2) {
            processSubset(subset);
        }
    }

    /**
     * Processes a particular subset of cities for the TSP.
     * @param subset The current subset of cities to process.
     */
    private static void processSubset(int subset) {
        for (int currentCity = 1; currentCity < cityCount; currentCity++) {
            if (isCityInSubset(currentCity, subset)) {
                updateTableForCity(subset, currentCity);
            }
        }
    }

    /**
     * Checks if a city is part of a subset.
     * 
     * The method leverages bitwise operations. Specifically, it uses a bitwise AND operation 
     * to determine if the city (indicated by a specific bit position) is in the subset.
     * 
     * @param city The city's index.
     * @param subset The subset to check against.
     * @return true if the city is in the subset; false otherwise.
     */
    private static boolean isCityInSubset(int city, int subset) {
        return (subset & (1 << city)) != 0;  // If the bit is set, then the city is in the subset.
    }

    /**
     * Updates the DP table for a particular city in the current subset.
     * 
     * @param subset The subset being processed.
     * @param currentCity The city for which the table needs to be updated.
     */
    private static void updateTableForCity(int subset, int currentCity) {
        for (int previousCity = 0; previousCity < cityCount; previousCity++) {
            if (isCityInSubset(previousCity, subset) && canTravelBetween(previousCity, currentCity)) {
                double potentialDistance = calculatePotentialDistance(previousCity, currentCity, subset);
                updateTableIfNecessary(currentCity, subset, previousCity, potentialDistance);
            }
        }
    }

    /**
     * Checks if it's possible to travel between two cities.
     * 
     * @param city1 The starting city.
     * @param city2 The destination city.
     * @return true if they are different and can be traveled between; false otherwise.
     */
    private static boolean canTravelBetween(int city1, int city2) {
        return distances[city1][city2] != BIG_NUMBER;
    }

    /**
     * Calculates the potential distance if moving from one city to another.
     * 
     * @param fromCity The starting city.
     * @param toCity The destination city.
     * @param subset The current subset of cities.
     * @return The calculated potential distance.
     */
    private static double calculatePotentialDistance(int fromCity, int toCity, int subset) {
        return table[fromCity][subsetWithoutCity(subset, toCity)] + distances[fromCity][toCity];
    }

    /**
     * Returns the subset after removing a city.
     * 
     * This method uses a bitwise XOR operation to unset the bit corresponding to the city, 
     * effectively removing it from the subset.
     * 
     * @param subset The original subset.
     * @param city The city to be removed from the subset.
     * @return The subset without the city.
     */
    private static int subsetWithoutCity(int subset, int city) {
        return subset ^ (1 << city);  // Unset the bit corresponding to the city.
    }

    /**
     * Updates the DP table entry if the newly calculated potential distance is better than 
     * the previously recorded distance for the subset and city.
     * 
     * @param city The city for which the table entry might be updated.
     * @param subset The subset of cities.
     * @param previousCity The previous city in the tour.
     * @param potentialDistance The newly calculated distance.
     */
    private static void updateTableIfNecessary(int city, int subset, int previousCity, double potentialDistance) {
        if (potentialDistance < table[city][subset]) {
            table[city][subset] = potentialDistance;
            backtrack[city][subset] = previousCity;
        }
    }


   /**
 * Constructs the optimal path for the Traveling Salesman Problem.
 * It first identifies the optimal cost by iterating over the last column of the table (i.e., considering all cities).
 * It then backtracks from the optimal cost to construct the actual path.
 */
private static void constructOptimalPath() {
    identifyOptimalCost();
    
    if(optimalCost != BIG_NUMBER) {
        constructTourFromOptimalCost();
        System.out.println("Optimal Tour: " + optimalTour);
    }
}

/**
 * Identifies the optimal cost of the tour.
 * This is done by checking the last column of the table (representing visiting all cities) and adding the distance 
 * to return to the starting city.
 */
private static void identifyOptimalCost() {
    int allCitiesSubset = (1 << cityCount) - 1; // Represents a subset where all cities are visited.

    for (int i = 1; i < cityCount; i++) {
        if (isFeasibleTourEndingAt(i, allCitiesSubset)) {
            double tourCostIfEndingAtI = table[i][allCitiesSubset] + distances[i][0];

            if (tourCostIfEndingAtI < optimalCost) {
                optimalCost = tourCostIfEndingAtI;
                currentStep = i;
            }
        }
    }
}

    /**
     * Checks if a tour is feasible if it ends at a specific city and covers a given subset of cities.
     * 
     * @param city The city at which the tour potentially ends.
     * @param subset The subset of cities covered.
     * @return true if feasible; false otherwise.
     */
    private static boolean isFeasibleTourEndingAt(int city, int subset) {
        return table[city][subset] != BIG_NUMBER;
    }

    /**
     * Constructs the actual tour/path based on the identified optimal cost.
     * This is achieved by backtracking from the end city and using the 'backtrack' table.
     */
    private static void constructTourFromOptimalCost() {
        int allCitiesSubset = (1 << cityCount) - 1;

        while(currentStep != -1) {
            optimalTour.add(cityList.get(currentStep).getIdentifier());
            int prevCity = backtrack[currentStep][allCitiesSubset];
            
            // Removing the current city from the subset.
            allCitiesSubset = removeCityFromSubset(allCitiesSubset, currentStep);

            currentStep = prevCity;
        }

        // Adding the starting city to the beginning of the tour.
        optimalTour.add(0, 0);
    }

    /**
     * Removes a city from a given subset.
     * This is achieved using a bitwise XOR operation.
     * 
     * @param subset The original subset.
     * @param city The city to be removed.
     * @return The subset without the city.
     */
    private static int removeCityFromSubset(int subset, int city) {
        return subset ^ (1 << city);
    }
}