/**
 * (CS-310 / Program 4 - Genetic Algorithms) Driver Class
 * Class that applies genetic algorithm onto an input matrix for specified # of sequences and generations
 * @author Anthony Norderhaug
 * @date 12/9/2020
 */

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;


public class Driver {
    List<String> labels = new ArrayList<>();
    List<List<Integer>> adjMatrix;
    Random randGen = new Random(310); // CS310 SPECIFIED SEED

    public static void main(String[] args) {
        new Driver(args[0]);
    }

    /**
     * Driver() instance identifying input file's validity and prompting for sequences & generations. Creates
     * GeneticAlgorithm object and applies public method shortestPath()
     */
    Driver(String filename) {
        Scanner inputS = new Scanner(System.in);
        String input;
        int sequenceCount, generations;
        adjMatrix = readGraph(filename); // initializes class adjMatrix and labels with file specified by filename

        // checking if data was able to be pulled, if null, an error occurred and program exits
        if (adjMatrix != null) {
            System.out.println("File \"" + filename + "\" found.");
            matrixInfo();
        } else {
            System.out.println("File \"" + filename + "\" could not be found or opened with current perms.");
            System.exit(0);
        }

        // prompting user for desired # of sequences, defaults to 10 if input is invalid
        System.out.println("\n? Enter the number of initial sequences to generate.\n" +
                "(Based on CS310's \"matrix\" file in Canvas, recommended default value is 25)\n");
        try {
            input = inputS.nextLine();
            if (input.equals("") || Integer.parseInt(input) < 2) {
                throw new NumberFormatException();
            } else {
                sequenceCount = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Defaulting to 25.");
            sequenceCount = 25;
        }

        // prompting user for desired # of generations, defaults to 50 if input is invalid
        System.out.println("\n? Enter the number of generations to process.\n" +
                "(Based on CS310's \"matrix\" file in Canvas, recommended default value is 20)\n");
        try {
            input = inputS.nextLine();
            if (input.equals("") || Integer.parseInt(input) < 1) {
                throw new NumberFormatException();
            } else {
                generations = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Defaulting to 20.");
            generations = 20;
        }

        new GeneticAlgorithm(adjMatrix, labels).shortestPath(sequenceCount, generations);
    }

    /**
     * readGraph() reads and splits each line of specified file. First element is added List labels and the
     * following are added to List curSequence. After completely iterating, a new ArrayList is added to adjMatrix
     * containing curSequence's contents.
     *
     * @param filename                                              program argument, specifies filename
     */
    public List<List<Integer>> readGraph(String filename) {
        try {
            List<List<Integer>> adjMatrix = new ArrayList<>();
            ArrayList<Integer> curSequence = new ArrayList<>();
            Scanner input = new Scanner(new FileInputStream(filename));
            List<String> splitInput;

            while (input.hasNextLine()) {
                splitInput = new ArrayList<>(Arrays.asList(input.nextLine().split(",")));
                labels.add(splitInput.get(0));
                splitInput.remove(0);

                while (!splitInput.isEmpty()) {
                    curSequence.add(Integer.parseInt(splitInput.get(0)));
                    splitInput.remove(0);
                }
                adjMatrix.add(new ArrayList<>(curSequence.subList(0, curSequence.size())));
                curSequence.clear();
            }

            return adjMatrix;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * matrixinfo() prints info about matrix when initially read. Includes input nodes and example connections.
     */
    public void matrixInfo() {
        int randomIndex;

        System.out.println("\nNumber of Input Nodes: " + adjMatrix.size() + "\n");
        System.out.println("3 examples of existing connections:");
        for (int i = 0; i < 3; i++) {
            randomIndex = randGen.nextInt(adjMatrix.size());

            System.out.print("\t" + labels.get(randomIndex) + ":    ");
            for (int j = 0; j < adjMatrix.size(); j++) {
                if (j == adjMatrix.size()-1) {
                    System.out.println(adjMatrix.get(randomIndex).get(j));
                } else {
                    System.out.print(adjMatrix.get(randomIndex).get(j) + ", ");
                }
            }
        }
    }

}
