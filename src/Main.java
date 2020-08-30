
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;


public class Main {
    static boolean noFail = true;
    public static void main(String[] args) {
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

        int counter = 0;

            int[][] firstValues = getRandomSolution();
            Solution firstNode = new Solution(firstValues);
            while (counter < 1000) {
                while (Main.noFail) {
                    climb(firstNode);
                }
                Main.noFail = true;
                counter++;
                firstNode = new Solution(getRandomSolution());
                System.out.println("Plateau reached, selecting new random starting node");
            }

            System.out.println("1000 failed attempts have been made, halting program.");
    }
    
    static void printSolution(Solution node) {
        String renter,depart,dest,make,colour,statement;
        for (Car c :
                node.allCars) {
            if (c.renterMatches(0) || c.destMatches(4)) {
                renter = c.getRenterText();
                depart = c.getDepartText();
                dest = c.getDestText();
                make = c.getMakeText();
                colour = c.getColourText();
                statement = "The " + renter + " departed at " + depart + " to " + dest + " in a " + colour + " " + make;
                System.out.println(statement);
            }
        }
    }

    static void climb(Solution node) {
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
        while (remainingSwaps.size()>0){

        int[] randomSwap = remainingSwaps.get(rand.nextInt(remainingSwaps.size()));

        int[] firstValue = {randomSwap[0],
                            randomSwap[2],
                            (int)node.allCars[randomSwap[0]].allDetails[randomSwap[2]].get(0)};

        int[] secondValue = {randomSwap[1],
                            randomSwap[2],
                            (int)node.allCars[randomSwap[1]].allDetails[randomSwap[2]].get(0)};

        int startScore = scoreSolution(node);
        node.allCars[firstValue[0]].allDetails[firstValue[1]].set(0,secondValue[2]);
        node.allCars[secondValue[0]].allDetails[secondValue[1]].set(0,firstValue[2]);
        int endScore = scoreSolution(node);

        if (endScore == 15) {
            System.out.println("Solution Found");
            printSolution(node);
            exit(10);
        }

        if (endScore > startScore) {
            break;
        } else {

            node.allCars[firstValue[0]].allDetails[firstValue[1]].set(0, firstValue[2]);
            node.allCars[secondValue[0]].allDetails[secondValue[1]].set(0, secondValue[2]);
            remainingSwaps.remove(randomSwap);
        }
        }
        if (remainingSwaps.size() == 0) Main.noFail = false;


    }

    static int[][] getRandomSolution(){
        int[][] randomNode = new int[5][5];
        List<List<Integer>> remainingValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            remainingValues.add(new ArrayList<>(Arrays.asList(0,1,2,3,4)));
        }

        Random rand = new Random();
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {

                int index = rand.nextInt(remainingValues.get(j).size());
                randomNode[i][j] = remainingValues.get(j).get(index);
                remainingValues.get(j).remove(index);

            }
        }
        return randomNode;
    }





    static int scoreSolution(Solution candidate) {

        int score = 0;
        int tempScore = 0;
        // The Toyota Camry was hired at 6:00am by a British couple.
        for (Car testCar:
             candidate.allCars) {
            if (testCar.possibleDepart.contains(1) && // Departs at 6am
                    testCar.possibleRenters.contains(1) && // Rented by British
                    testCar.possibleMakes.contains(1)) { // Make is Toyota
                // System.out.println("T1");
                score++;
            }// else {System.out.println("F1");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The car in the middle had a black colour
        if (candidate.carThr.possibleColours.contains(2)) { // Colour is black
            // System.out.println("T2");
            score++;
        }//  else {System.out.println("F2");}

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The Hyundai Accent left the depot at 9:00am.
        for (Car testCar:
                candidate.allCars) {
            if (testCar.possibleDepart.contains(4) && // Departs at 9am
                    testCar.possibleMakes.contains(3)) { // Make is Hyundai
                // System.out.println("T3");
                score++;
            }//  else {System.out.println("F3");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The Holden Barina with a blue colour was to the left of the car that carries the British couple.
        for (int i = 0; i < 4; i++) {
            // Car is blue, car to the right was rented by British
            if (candidate.allCars[i].possibleColours.contains(0) && candidate.allCars[i+1].possibleRenters.contains(1)){
                // System.out.println("T4");
                score++;
            }//  else {System.out.println("F4");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // To the right of the car hired by a French lady was the car going to Gold Coast.
        for (int i = 0; i < 4; i++) {
            // Car was rented by French, car to the right is going to GC
            if (candidate.allCars[i].rentersContains(2) && candidate.allCars[i+1].destContains(3)){
                // System.out.println("T5");
                score++;
            }//  else {System.out.println("F5");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The Nissan X-Trail was heading for Sydney.
        for (Car testCar :
            candidate.allCars) {
            if (testCar.makesContains(2) && // Make is Nissan
            testCar.destContains(2)){ // Dest is Sydney
                // System.out.println("T6");
                score++;
            }//  else {System.out.println("F6");}

        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // To the right of the car carrying a Chinese businessman was the car with a green colour.
        for (int i = 0; i < 4; i++) {
            // car was rented by Chinese, car to the right was green
            if (candidate.allCars[i].rentersContains(3) && candidate.allCars[i+1].colourContains(4)){
                // System.out.println("T7");
                score++;
            }//  else {System.out.println("F7");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The car going to Newcastle left at 5:00am.
        for (Car testCar :
                candidate.allCars) {
            if (testCar.destContains(0) && // Destination is Newcastle
                    testCar.departContains(0)){ // Departure time is 5am
                // System.out.println("T8");
                score++;
            }//  else {System.out.println("F8");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The Honda Civic left at 7:00am and was on the right of the car heading for Gold Coast.
        for (int i = 0; i < 4; i++) {
            if ((candidate.allCars[i].destContains(3)) && // Car was going to GC
                    // Car to the right was Honda and departed at 7am
                    (candidate.allCars[i+1].makesContains(4) && candidate.allCars[i+1].departContains(2))){
                // System.out.println("T9");
                score++;
            }//  else {System.out.println("F9");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The car with a red colour was going to Tamworth.
        for (Car testCar :
                candidate.allCars) {
            if (testCar.colourContains(1) && // Colour is red
                    testCar.destContains(1)){ // Dest is Tamworth
                // System.out.println("T10");
                score++;
            }//  else {System.out.println("F10");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // To the left of the car that left at 7:00am was the car with a white colour.
        for (int i = 0; i < 4; i++) {
            // Car was white, car to the right departed at 7am
            if (candidate.allCars[i].colourContains(3) && candidate.allCars[i+1].departContains(2)){
                // System.out.println("T11");
                score++;
            }//  else {System.out.println("F11");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The last car was hired by an Indian man.
        if (candidate.carFiv.rentersContains(4)){ // Rented by Indian
            // System.out.println("T12");
            score++;
        }//  else {System.out.println("F12");}

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The car with a black colour left at 8:00am.
        for (Car testCar :
                candidate.allCars) {
            if (testCar.colourContains(2) && // Car is black
                    testCar.departContains(3)){ // Departure time is 8am
                // System.out.println("T13");
                score++;
            }//  else {System.out.println("F13");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The car carrying an Indian man was to the right of the car hired by a Chinese businessman.
        for (int i = 0; i < 4; i++) {
            // car was rented by Chinese, car to the right was rented by Indian
            if (candidate.allCars[i].rentersContains(3) && candidate.allCars[i+1].rentersContains(4)){
                // System.out.println("T14");
                score++;
            }//  else {System.out.println("F14");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        // The car heading for Tamworth left at 6:00am.
        for (Car testCar :
                candidate.allCars) {
            if (testCar.destContains(1) && // Destination is Tamworth
                    testCar.departContains(1)){ // Departure time is 6am
                // System.out.println("T15");
                score++;
            }//  else {System.out.println("F15");}
        }

        // if (tempScore == 0) { return -1; } else {score = score + tempScore; tempScore = 0;}

        return score;
    }


}

class Solution {

    Car carOne;
    Car carTwo;
    Car carThr;
    Car carFou;
    Car carFiv;
    Car[] allCars = new Car[5];

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
}

class Car {
    // Variables that hold the remaining possible choices for this particular car
    // If only one choice remains the car becomes 'set' to that choice but it is still called a 'possible' choice
    // The named values corresponding to each number are commented after the declaration in order
    int renter,depart,dest,make,colour;
    List<Integer> possibleColours = new ArrayList<>(Arrays.asList(0,1,2,3,4)); // Blue, Red, Black, White, Green
    List<Integer> possibleRenters = new ArrayList<>(Arrays.asList(0,1,2,3,4)); // Canadian, British, French, Chinese, Indian
    List<Integer> possibleMakes = new ArrayList<>(Arrays.asList(0,1,2,3,4)); // Holden, Toyota, Nissan, Hyundai, Honda
    List<Integer> possibleDest = new ArrayList<>(Arrays.asList(0,1,2,3,4)); // Newcastle, Tamworth, Sydney, Gold Coast, Port Macquarie
    List<Integer> possibleDepart = new ArrayList<>(Arrays.asList(0,1,2,3,4)); // 5am, 6am, 7am, 8am, 9am
    List[] allDetails = {possibleRenters, possibleDepart, possibleDest, possibleMakes, possibleColours};

    // Set details of cars on initialisation using a list
    Car(int[] values) {
        setPossibleRenters(values[0]);
        setPossibleDepart(values[1]);
        setPossibleDest(values[2]);
        setPossibleMakes(values[3]);
        setPossibleColours(values[4]);
        allDetails[0] = possibleRenters;
        allDetails[1] = possibleDepart;
        allDetails[2] = possibleDest;
        allDetails[3] = possibleMakes;
        allDetails[4] = possibleColours;
    }



    Boolean colourContains(Integer value) {
        return possibleColours.contains(value);
    }

    Boolean rentersContains(Integer value) {
        return possibleRenters.contains(value);
    }

    Boolean makesContains(Integer value) {
        return possibleMakes.contains(value);
    }

    Boolean destContains(Integer value) {
        return possibleDest.contains(value);
    }

    Boolean departContains(Integer value) {
        return possibleDepart.contains(value);
    }

    Boolean colourMatches(Integer value) { return (possibleColours.size() == 1) && (possibleColours.get(0).equals(value)); }

    Boolean renterMatches(Integer value) { return (possibleRenters.size() == 1) && (possibleRenters.get(0).equals(value)); }

    Boolean makeMatches(Integer value) {
        return (possibleMakes.size() == 1) && (possibleMakes.get(0).equals(value));
    }

    Boolean destMatches(Integer value) {
        return (possibleDest.size() == 1) && (possibleDest.get(0).equals(value));
    }

    Boolean departMatches(Integer value) {
        return (possibleDepart.size() == 1) && (possibleDepart.get(0).equals(value));
    }

    void setPossibleColours(int value){
        possibleColours = new ArrayList<>(Arrays.asList((value)));
    }

    void setPossibleMakes(int value){
        possibleMakes = new ArrayList<>(Arrays.asList((value)));
    }

    void setPossibleDest(int value){
        possibleDest = new ArrayList<>(Arrays.asList((value)));
    }

    void setPossibleRenters(int value){
        possibleRenters = new ArrayList<>(Arrays.asList((value)));
    }

    void setPossibleDepart(int value){
        possibleDepart = new ArrayList<>(Arrays.asList((value)));
    }



    String getRenterText(){
        if (renterMatches(0)) return "Canadian couple";
        if (renterMatches(1)) return "British couple";
        if (renterMatches(2)) return "French lady";
        if (renterMatches(3)) return "Chinese businessman";
        if (renterMatches(4)) return "Indian man";
        return "renter error";
    }

    String getDepartText(){
        if (departMatches(0)) return "5am";
        if (departMatches(1)) return "6am";
        if (departMatches(2)) return "7am";
        if (departMatches(3)) return "8am";
        if (departMatches(4)) return "9am";
        return "depart error";
    }

    String getDestText(){
        if (destMatches(0)) return "Newcastle";
        if (destMatches(1)) return "Tamworth";
        if (destMatches(2)) return "Sydney";
        if (destMatches(3)) return "Gold Coast";
        if (destMatches(4)) return "Port Macquarie";
        return "dest error";
    }

    String getColourText(){
        if (colourMatches(0)) return "Blue";
        if (colourMatches(1)) return "Red";
        if (colourMatches(2)) return "Black";
        if (colourMatches(3)) return "White";
        if (colourMatches(4)) return "Green";
        return "colour error";
    }

    String getMakeText(){
        if (makeMatches(0)) return "Holden Barina";
        if (makeMatches(1)) return "Toyota Camry";
        if (makeMatches(2)) return "Nissan X-Trail";
        if (makeMatches(3)) return "Hyundai Accent";
        if (makeMatches(4)) return "Honda Civic";
        return "make error";
    }




}

