import java.io.IOException;
import java.util.*;
import static java.lang.System.exit;

public class Main {
    static boolean noFail = true;
    public static void main(String[] args) {
        printIntro();

        // Attempt counter
        int counter = 0;
        // First node is randomly generated
        Solution firstNode = new Solution(getRandomSolution());
        while (counter < 1000) {
            // Plateaus tracked using a global flag
            while (Main.noFail) {
                // Success is handled inside the climb function
                climb(firstNode);
            }
            // On plateau reset flag, increment counter, generate new random node
            Main.noFail = true;
            counter++;
            firstNode = new Solution(getRandomSolution());
            System.out.println("Plateau reached, selecting new random starting node - Attempt count: "+counter);
        }
        // Counter has hit 1000, attempt has failed
        System.out.println("1000 failed attempts have been made, halting program.");
    }

    /**
     * The main function used for scoring a node.
     * The score represents the number of unbroken rules.
     * If all 15 rules are unbroken the node is the correct solution to the puzzle.
     * A node with a higher score is considered closer to being correct for the purposes of hill climbing
     *
     * @param candidate The node to be scored
     * @return int number of unbroken rules
     */
    static int scoreSolution(Solution candidate) {

        int score = 0;

        // The Toyota Camry was hired at 6:00am by a British couple.
        for (Car testCar: candidate.allCars) {
            if (testCar.departsAt("6am") && testCar.rentedBy("British") && testCar.makeIs("Toyota")) score++;
        }

        // The car in the middle had a black colour
        if (candidate.carThr.colourIs("Black")) score++;

        // The Hyundai Accent left the depot at 9:00am.
        for (Car testCar: candidate.allCars) {
            if (testCar.departsAt("9am") && testCar.makeIs("Hyundai")) score++;
        }

        // The Holden Barina with a blue colour was to the left of the car that carries the British couple.
        for (int i = 0; i < 4; i++) {
            // Car is blue, car to the right was rented by British
            if (candidate.allCars[i].colourIs("Blue") && candidate.allCars[i+1].rentedBy("British"))score++;
        }

        // To the right of the car hired by a French lady was the car going to Gold Coast.
        for (int i = 0; i < 4; i++) {
            // Car was rented by French, car to the right is going to GC
            if (candidate.allCars[i].rentedBy("French") && candidate.allCars[i+1].goingTo("Gold Coast")) score++;
        }

        // The Nissan X-Trail was heading for Sydney.
        for (Car testCar : candidate.allCars) {
            if (testCar.makeIs("Nissan") && testCar.goingTo("Sydney")) score++;
        }

        // To the right of the car carrying a Chinese businessman was the car with a green colour.
        for (int i = 0; i < 4; i++) {
            // car was rented by Chinese, car to the right was green
            if (candidate.allCars[i].rentedBy("Chinese")&& candidate.allCars[i+1].colourIs("Green")) score++;
        }

        // The car going to Newcastle left at 5:00am.
        for (Car testCar : candidate.allCars) {
            if (testCar.goingTo("Newcastle") && testCar.departsAt("5am")) score++;
        }

        // The Honda Civic left at 7:00am and was on the right of the car heading for Gold Coast.
        for (int i = 0; i < 4; i++) {
            if ((candidate.allCars[i].goingTo("Gold Coast")) && // Car was going to GC
                    // Car to the right was Honda and departed at 7am
                    (candidate.allCars[i+1].makeIs("Honda") && candidate.allCars[i+1].departsAt("7am"))){
                score++;
            }
        }

        // The car with a red colour was going to Tamworth.
        for (Car testCar : candidate.allCars) {
            if (testCar.colourIs("Red") && testCar.goingTo("Tamworth")) score++;
        }

        // To the left of the car that left at 7:00am was the car with a white colour.
        for (int i = 0; i < 4; i++) {
            // Car was white, car to the right departed at 7am
            if (candidate.allCars[i].colourIs("White") && candidate.allCars[i+1].departsAt("7am")) score++;
        }

        // The last car was hired by an Indian man.
        if (candidate.carFiv.rentedBy("Indian")) score++;

        // The car with a black colour left at 8:00am.
        for (Car testCar : candidate.allCars) {
            if (testCar.colourIs("Black") && testCar.departsAt("8am")) score++;
        }

        // The car carrying an Indian man was to the right of the car hired by a Chinese businessman.
        for (int i = 0; i < 4; i++) {
            // car was rented by Chinese, car to the right was rented by Indian
            if (candidate.allCars[i].rentedBy("Chinese") && candidate.allCars[i+1].rentedBy("Indian")) score++;
        }

        // The car heading for Tamworth left at 6:00am.
        for (Car testCar :
                candidate.allCars) {
            if (testCar.goingTo("Tamworth") && testCar.departsAt("6am")) score++;
        }

        return score;
    }

    /**
     * Primary hill climbing function
     *
     * @param node Starting node to attempt to climb from
     */
    static void climb(Solution node) {

        // Generate a list of car/detail information to swap to make neighbours
        Random rand = new Random();
        int[][] SWAPS = {{0,1},{0,2},{0,3},{0,4},{1,2},{1,3},{1,4},{2,3},{2,4},{3,4}};
        List<int[]> remainingSwaps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int[] s :
                    SWAPS) {
                int[] swap = {s[0],s[1],i};
                remainingSwaps.add(swap);
            }
        }

        // Loop over every possible swap that can be made in a random order
        while (remainingSwaps.size()>0){
            // Find random swap
            int[] randomSwap = remainingSwaps.get(rand.nextInt(remainingSwaps.size()));

            // Record starting score, execute swap, record new score
            int startScore = scoreSolution(node);
            node.executeSwap(randomSwap[0],randomSwap[1],randomSwap[2]);
            int endScore = scoreSolution(node);

            // A score of 15 is the correct solution
            if (endScore == 15) {
                System.out.println("Solution Found");
                printSolution(node);
                exit(10);
            }

            // Higher endScore is improvement, break the inner loop and start again with the improved node
            if (endScore > startScore) {
                break;
                // If there is no improvement undo the swap and test the next one
            } else {
                node.executeSwap(randomSwap[1],randomSwap[0],randomSwap[2]);
                remainingSwaps.remove(randomSwap);
            }
        }
        // All swaps have been attempted and no improvement was found, this node is a plateau
        if (remainingSwaps.size() == 0) Main.noFail = false;
    }

    /**
     * Formats the contents of the soloution to be printed to console in a format that answers the original question
     *
     * @param node Solution to be printed
     */
    static void printSolution(Solution node) {
        String renter,depart,dest,make,colour,statement;
        for (Car c :
                node.allCars) {
            // Only the two cars relevant to the question will be printed, but all their details will be
            if (c.rentedBy("Canadian") || c.goingTo("Port Macquarie")) {
                renter = c.getRenterText();
                make = c.getMakeText();
                depart = c.intToString("depart",c.depart);
                colour = c.intToString("colour",c.colour);
                dest = c.intToString("dest",c.dest);
                statement = "The " + renter + " departed at " + depart + " to " + dest + " in a " + colour + " " + make;
                System.out.println(statement);
            }
        }
    }

    /**
     * Function that prints the introduction text
     */
    static void printIntro(){
        // Opening command line information
        System.out.println("COSC350 Programming Assignment - Andrew McIntosh ID 220176382");
        System.out.println("");
        System.out.println("Which car was going to Port Macquarie? Which car was hired by a Canadian couple?");
        System.out.println("");
        System.out.println("This program seeks to answer those questions using the information provided by");
        System.out.println("implementing a First Choice Hill Climbing search algorithm. The algorithm considers a node");
        System.out.println("to be a complete set of five cars with valid assignments of renter, departure time,");
        System.out.println("destination, make/model and colour. Valid meaning exactly one car has each possible");
        System.out.println("assignment in each category i.e. exactly one car of each colour. A valid starting node is");
        System.out.println("randomly generated and scored based on how many of the 'rules' provided it does not violate.");
        System.out.println("If it does not violate any of the rules it is taken to be the correct full solution.");
        System.out.println("Otherwise a list of neighbors is generated. A neighbouring node is a node which has one");
        System.out.println("'detail' (e.g. just the destination) of two cars swapped. This preserves the validity of the");
        System.out.println("node. In a random order each neighbour is scored. If one is found which violates fewer");
        System.out.println("rules it is considered an improvement, and selected as the current node. This is climbing");
        System.out.println("the hill and is repeated until a node which violates no rules is found.");
        System.out.println("");
        System.out.println("The algorithm also uses a random restart element. If all neighbours are scored and none ");
        System.out.println("improve the current node a plateau has been reached and a new random starting node is ");
        System.out.println("selected.");
        System.out.println("");
        System.out.println("The algorithm will stop itself after 1000 unsuccessful attempts however this should not occur");
        System.out.println("");
        System.out.println("Press \"ENTER\" to start the algorithm...");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a nested array of ints in the format used by the Solution constructor to make random nodes
     *
     * @return int[][] with random values which represent a valid node
     */
    static int[][] getRandomSolution(){
        // Array in the format used by the Solution constructor
        int[][] randomNode = new int[5][5];

        // All unused values are tracked
        List<List<Integer>> remainingValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            remainingValues.add(new ArrayList<>(Arrays.asList(0,1,2,3,4)));
        }

        Random rand = new Random();
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                // An unused value is assigned to the holding array then removed from the list of unused values
                int index = rand.nextInt(remainingValues.get(j).size());
                randomNode[i][j] = remainingValues.get(j).get(index);
                remainingValues.get(j).remove(index);
            }
        }
        return randomNode;
    }
}

/**
 * A node that represents a 'solution' to the puzzle.
 * These are intended to always be valid, i.e. only one car can be blue at a time. However they need not be correct.
 */
class Solution {

    Car carOne;
    Car carTwo;
    Car carThr;
    Car carFou;
    Car carFiv;
    Car[] allCars = new Car[5];

    /**
     * Solution constructor
     *
     * @param allValues A nested array of ints where each inside array is the input for the Car constructor
     */
    Solution(int[][] allValues){
        carOne = new Car(allValues[0]);
        carTwo = new Car(allValues[1]);
        carThr = new Car(allValues[2]);
        carFou = new Car(allValues[3]);
        carFiv = new Car(allValues[4]);
        allCars[0] = carOne;
        allCars[1] = carTwo;
        allCars[2] = carThr;
        allCars[3] = carFou;
        allCars[4] = carFiv;
    }

    /**
     * Method to swap the values of one detail between two cars
     *
     * @param firstCar First car to have the detail swapped
     * @param secondCar Second car to have the detail swapped
     * @param field integer detail (not value, integer version of detail)
     */
    void executeSwap(int firstCar, int secondCar, int field){
        // Store the values to be swapped
        int firstValue = allCars[firstCar].allDetails[field];
        int secondValue = allCars[secondCar].allDetails[field];

        // Set new values
        allCars[firstCar].allDetails[field] = secondValue;
        allCars[secondCar].allDetails[field] = firstValue;

        // Update named versions of detail fields
        for (Car c:
             allCars) {
           c.renter = c.allDetails[0];
            c.depart = c.allDetails[1];
            c.dest = c.allDetails[2];
            c.make = c.allDetails[3];
            c.colour = c.allDetails[4];
        }

    }
}

/**
 * Individual car. Only used inside Solutions
 */
class Car {

    int renter,depart,dest,make,colour;
    int[] allDetails = new int[5];
    static String[] renterStr = {"Canadian", "British", "French", "Chinese", "Indian"};
    static String[] departStr = {"5am", "6am", "7am", "8am", "9am"};
    static String[] destStr = {"Newcastle", "Tamworth", "Sydney", "Gold Coast", "Port Macquarie"};
    static String[] makeStr = {"Holden", "Toyota", "Nissan", "Hyundai", "Honda"};
    static String[] colourStr = {"Blue", "Red", "Black", "White", "Green"};

    /**
     * Car constructor
     *
     * @param values An array of ints that represents the value of each detail field
     */
    Car(int[] values) {
        allDetails[0] = values[0];
        allDetails[1] = values[1];
        allDetails[2] = values[2];
        allDetails[3] = values[3];
        allDetails[4] = values[4];
        renter = allDetails[0];
        depart = allDetails[1];
        dest = allDetails[2];
        make = allDetails[3];
        colour = allDetails[4];
    }

    /**
     * The following five functions test if the current value of a field matches a value using strings.
     * The purpose is for readability of the score function
     */
    Boolean departsAt(String value){
        return this.depart == stringToInt("depart", value);
    }

    Boolean rentedBy(String value){
        return this.renter == stringToInt("renter", value);
    }

    Boolean makeIs(String value){
        return this.make == stringToInt("make", value);
    }

    Boolean colourIs(String value){
        return this.colour == stringToInt("colour", value);
    }

    Boolean goingTo(String value){
        return this.dest == stringToInt("dest", value);
    }

    /**
     * Function to get the string representation of an integer value of a detail field
     *
     * @param field The name of the field
     * @param value Integer value of the field
     * @return String value of the integer value
     */
    String intToString(String field, int value){

        switch (field){
            case "renter": return renterStr[value];
            case "depart": return departStr[value];
            case "dest": return destStr[value];
            case "make": return makeStr[value];
            case "colour": return colourStr[value];
        }
        System.out.println("Internal error (intToString)");
        exit(1);
        return "";
    }

    /**
     * Function to get the integer representation of the name of a field value
     *
     * @param field The name of the field
     * @param value String value of the fiel
     * @return Integer value of the string value
     */
    int stringToInt(String field, String value){
        switch (field) {
            case "renter":
                return getIndexOfString(value, renterStr);
            case "depart":
                return getIndexOfString(value, departStr);
            case "dest":
                return getIndexOfString(value, destStr);
            case "make":
                return getIndexOfString(value, makeStr);
            case "colour":
                return getIndexOfString(value, colourStr);
        }
        System.out.println("Internal error (stringToInt)");
        exit(1);
        return 0;
    }

    /**
     * Get the index of a string in an array of strings
     *
     * @param value string being searched for
     * @param arr array to be searched
     * @return integer index of the value in arr
     */
    int getIndexOfString(String value, String[] arr){
        for (int i = 0; i <= 4; i++) {
            if (arr[i].equals(value)) return i;
        }
        System.out.println("Internal error (getIndexOfString)");
        exit(1);
        return 0;
    }

    /**
     * The following two functions get the fully formatted renter and make strings for outputting the final solution.
     */
    String getRenterText(){
        switch (renter) {
            case 0:
                return "Canadian couple";
            case 1:
                return "British couple";
            case 2:
                return "French lady";
            case 3:
                return "Chinese businessman";
            case 4:
                return "Indian man";
        }
        return "renter error";
    }

    String getMakeText(){
        switch (make) {
            case 0:
                return "Holden Barina";
            case 1:
                return "Toyota Camry";
            case 2:
                return "Nissan X-Trail";
            case 3:
                return "Hyundai Accent";
            case 4:
                return "Honda Civic";
        }
        return "make error";
    }




}

