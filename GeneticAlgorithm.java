/**
 * (CS-310 / Program 4 - Genetic Algorithms) GeneticAlgorithm Class
 * Class that takes input matrix and conducts inner processing of a genetic algorithm, i.e. crossover(), mutate()
 * @author Anthony Norderhaug
 * @date 12/9/2020
 */

import java.util.*;


public class GeneticAlgorithm {
    String lineBreak = "\nX------------------------------------------------------X\n";
    private List<List<Integer>> adjMatrix;
    private List<String> labels;
    private Random randGen = new Random(310); // SEED FOR RANDOM GENERATOR HERE!
    private int generationNumber;
    private int mutationCount = 0;

    /**
     * Constructor initializing class-level adjMatrix and labels
     *
     * @param adjMatrix                                             input matrix for pathing
     * @param labels                                                input labels for naming vertices
     */
    public GeneticAlgorithm(List<List<Integer>> adjMatrix, List<String> labels) {
        this.adjMatrix = adjMatrix;
        this.labels = labels;
    }

    /**
     * shortestPath() generates initial population and performs sets of crossing over, mutating, and printing info for
     * specified # of generations. Derives path of most fitness/shortest length and prints info.
     * NOTE: Due to being Genetic Algorithm, there is chance NOT to be produce shortestPath, but a near-optimal one.
     *
     * @param populationSize                                        user-specified population size
     * @param generations                                           user-specified generations
     */
    public void shortestPath(int populationSize, int generations) {
        List<Integer> shortestPath;
        List<String> shortestPathLabels = new ArrayList<>();

        // generating first population (random) and giving info
        List<List<Integer>> genPopulation = genInitPop(populationSize);
        popInfo(genPopulation);

        // processing through generations, crossing over, mutating, and printing info
        while (generationNumber < generations) {
            genPopulation = crossover(genPopulation);
            mutate(genPopulation);
            popInfo(genPopulation);
        }

        // deriving labels associated with shortestPath's Integer values
        shortestPath = genPopulation.get(0);
        for (Integer number: shortestPath) {
            shortestPathLabels.add(labels.get(number));
        }

        System.out.println(lineBreak + "\nSHORTEST PATH SUMMARY\n");
        System.out.println("Shortest path's sequence of nodes\n\t" + printSequence(shortestPath)
                + "\n\t" + printSequence(shortestPathLabels));
        System.out.println("| Shortest Path's length: " + distance(shortestPath));
    }

    /**
     * printSequence() prints input List into readable format
     *
     * @param inputList                                             input List
     * @param <E>                                                   type parameter, to work with Strings and Integers
     * @return                                                      String containing formatted List contents
     */
    private <E> String printSequence(List<E> inputList) {
        String output = "";

        for (int i = 0; i < inputList.size(); i++) {
            if (i == inputList.size()-1) {
                output += inputList.get(i) + "";
            } else {
                output += (inputList.get(i) + ", ");
            }
        }
        return output;
    }

    /**
     * genInitPop() creates population based on user-specified size. For each element, genrandomSequence() is called.
     * Initializes generation number with 1.
     *
     * @param size                                                  user-specified population size
     * @return                                                      randomly generated population, List
     */
    private List<List<Integer>> genInitPop(int size) {
        List<List<Integer>> population = new ArrayList<>(size);
        generationNumber = 1;

        for (int i = 0; i < size; i++) {
            population.add(genRandomSequence());
        }

        return population;
    }

    /**
     * genRandomSequence() uses cityAvailability to generate List of available indices and apply them onto List
     * curSequence. After each application, index is removed from availability, leading to no dupes and normalized size.
     *
     * @return                                                      randomly generated sequence, List
     */
    private List<Integer> genRandomSequence() {
        List<Integer> curSequence = new ArrayList<>();
        List<Integer> cities = cityAvailability();
        int derivedIndex;

        while (!cities.isEmpty()) {
            derivedIndex = randGen.nextInt(cities.size());

            curSequence.add(cities.get(derivedIndex));
            cities.remove(derivedIndex);
        }

        return curSequence;
    }

    /**
     * cityAvailability returns List containing all indices of adjMatrix's nodes.
     *
     * @return                                                      available indices, List
     */
    private List<Integer> cityAvailability() {
        List<Integer> availableCities = new ArrayList<>(adjMatrix.size());

        for (int i = 0; i < adjMatrix.size(); i++) {
            availableCities.add(i);
        }
        return availableCities;
    }

    /**
     * length() calculates total distance based on edge length of every vertex of the input List. (Less distance is more fit)
     *
     * @param sequence                                              input List being evaluated for fitness
     * @return                                                      int representing total path distance
     */
    private int distance(List<Integer> sequence) {
        int fitnessSum = 0;

        for (int i = 0; i < sequence.size()-1; i++) {
            fitnessSum += adjMatrix.get(sequence.get(i)).get(sequence.get(i+1));
        }

        return fitnessSum;
    }

    /**
     * sortByFitness sorts input List population with an insertion sort. Objects of lesser distance are deemed more fit
     * and move to front of list.
     *
     * @param population                                            input population being sorted
     */
    private void sortByFitness(List<List<Integer>> population) {
        int prevIndex;
        List<Integer> curSequence;

        for (int i = 1; i < population.size(); i++) {
            curSequence = population.get(i);
            prevIndex = i - 1;

            while (prevIndex >= 0 && distance(population.get(prevIndex)) > distance(curSequence)) {
                population.set(prevIndex+1, population.get(prevIndex));
                prevIndex--;
            }
            population.set(prevIndex+1, curSequence);
        }
    }

    /**
     * fitnessData() calculates average distance of all paths in population. Stores info in String as well as distance
     * of most fit path
     *
     * @param population                                            population being analyzed, List
     * @return                                                      String containing info
     */
    private String fitnessData(List<List<Integer>> population) {
        double averageDistance = 0.0;

        for (int i = 0; i < population.size(); i++) {
            averageDistance += distance(population.get(i));
        }
        averageDistance /= population.size();

        return "| Distance of most Fit Path: \t" + distance(population.get(0)) +
                "\n| Average Path Length: \t" + averageDistance;
    }

    /**
     * crossOver() identifies sequences in pop. to breed together based on relative fitness. int randomIndex and HashSet
     * existingChildren used to avoid sequences crossing over with self and identical children. Updates generationNumber.
     *
     * @param population                                            input population to be crossed over
     * @return                                                      new population as a result
     */
    private List<List<Integer>> crossover(List<List<Integer>> population) {
        List<List<Integer>> newPopulation = new ArrayList<>();
        List<Integer> newChild;
        HashSet<List<Integer>> existingChildren = new HashSet<>();
        int valueCap = Math.max(population.size()/3, 1); // setting initial crossover frequency for most fit path
        int startingIndex = 0;
        int randomIndex;

        newPopulation.add(population.get(0)); // cloning most fit sequence to next generation
        System.out.println("\n* Crossover process beginning...");
        while (newPopulation.size() != population.size()) {
            for (int i = 0; i < valueCap; i++) {
                do {
                    do {
                        randomIndex = randGen.nextInt(population.size());
                    } while (randomIndex == startingIndex);

                    newChild = breed(population.get(startingIndex), population.get(randomIndex));
                } while (existingChildren.contains(newChild));

                existingChildren.add(newChild);
                newPopulation.add(newChild);
            }
            // decreasing crossover frequency for next sequence, update occurs if frequency is over 1
            if (valueCap > 1) {
                valueCap = Math.max(valueCap / 2, 1);
            }
            startingIndex++;
        }
        generationNumber++;
        existingChildren.clear();

        return newPopulation;
    }

    /**
     * breed() breeds together sequences in random samples of 0-3 and traverses from each parent back and forth until
     * child sequence is full. first parent to breed is also randomized. In case of duplicates, fixDuplicates() called.
     *
     * @param parentA                                               parent of produced child sequence
     * @param parentB                                               parent of produced child sequence
     * @return                                                      child sequence
     */
    private List<Integer> breed(List<Integer> parentA, List<Integer> parentB) {
        List<Integer> child = new ArrayList<>(parentA.size());
        List<Integer> activeParent;

        int parentSelector, startingIndex, valueCap;
        startingIndex = 0;
        parentSelector = randGen.nextInt(2);

        while (child.size() != parentA.size()) {
            if (parentSelector % 2 == 1) {
                activeParent = parentA;
            } else {
                activeParent = parentB;
            }
            valueCap = Math.min(parentA.size() - child.size(), randGen.nextInt(4));

            for (int i = valueCap; i > 0 ; i--) {
                child.add(activeParent.get(startingIndex++));
            }
            parentSelector++;
        }
        fixDuplicates(child);

        return child;
    }

    /**
     * fixDuplicates() initializes HashSet with originally available city indices and verifies position within input
     * sequence. HashSet removes index once located and since every sequence should have each index only once, HashSet
     * failing to contain an index from sequence means it has occured more than once! Dupe's index is stored and later
     * replaced with indices remaining in the availability HashSet.
     *
     * @param sequence                                              input List being processed for dupes
     */
    private void fixDuplicates(List<Integer> sequence) {
        HashSet<Integer> cities = new HashSet<>(cityAvailability());
        List<Integer> duplicateIndices = new ArrayList<>();
        int curCity;

        for (int i = 0; i < sequence.size(); i++) {
            curCity = sequence.get(i);

            if (cities.contains(curCity)) {
                cities.remove(curCity);
            } else {
                duplicateIndices.add(i);
            }
        }

        for (Integer city : cities) {
            sequence.set(duplicateIndices.get(0), city);
            duplicateIndices.remove(0);
        }
    }

    /**
     * mutate(), based off percentage of 100 / pop.size(), swaps random indices within a population's sequences. Has
     * potential occur multiple times or not at all.
     *
     * @param population                                            input population to be mutated
     */
    private void mutate(List<List<Integer>> population) {
        double mutationRate = 100.0 / (population.size()); // Canvas: "100 values => 1% , 10 values => 10%"

        for (int i = 0; i < population.size(); i++) {
            if (randGen.nextInt(100) <= mutationRate) {
                System.out.println("! A mutation has occurred for Generation #" + generationNumber + "!");
                mutationCount++;
                int sequenceSize = population.get(i).size();

                Collections.swap(population.get(i), randGen.nextInt(sequenceSize/2),
                        randGen.nextInt(sequenceSize/2) + sequenceSize/2);
            }
        }
    }

    /**
     * popInfo() prints information about population's current state such as generation number, current sequences sorted
     * most fit to least, fitness data, and the count of mutation that have occurred.
     *
     * @param population                                            population having info outputted
     */
    private void popInfo(List<List<Integer>> population) {
        System.out.println(lineBreak);
        System.out.println("CURRENT GENERATION: #" + generationNumber + "\n");

        sortByFitness(population);
        System.out.println("Sequences (sorted from most fit to least fit):");
        for (int i = 0; i < population.size(); i++) {

            System.out.print((i+1) + ")\t\t");
            for (int j = 0; j < adjMatrix.size(); j++) {
                if (j == adjMatrix.size()-1) {
                    System.out.println(population.get(i).get(j));
                } else {
                    System.out.print(population.get(i).get(j) + ", ");
                }
            }
        }
        System.out.println("\n" + fitnessData(population));
        System.out.println("| Mutation Count : \t" + mutationCount);
    }
}