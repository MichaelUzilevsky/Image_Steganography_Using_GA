package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.PopulationImplementation;

import java.util.*;

/**
 * Implements the ranked selection strategy for genetic algorithms.
 * This strategy includes elitism and ranks chromosomes based on their fitness for selection.
 */
public class RankedSelection implements SelectionStrategy {
    private static final Random random = new Random();
    private final double ELITISM_PERCENTAGE;

    public RankedSelection() {
        ELITISM_PERCENTAGE = 0.15;
    }

    /**
     * Selects the next chromosomes for the next generation, those chromosomes will later be crossover and mutated
     * This method incorporates elitism by directly carrying over a percentage of the top chromosomes.
     *
     * @param currentGeneration The current population from which to select the next generation.
     * @return A List of chromosomes selected to form the next generation.
     */
    @Override
    public List<Chromosome> selectNextGeneration(PopulationImplementation currentGeneration) {
        List<Chromosome> nextGeneration = new ArrayList<>();
        List<Chromosome> sortedCurrentGen = new ArrayList<>(currentGeneration.getPopulation());
        sortedCurrentGen.sort(Collections.reverseOrder()); // high fitness at the top


        // Elitism: Take top ELITISM_PERCENTAGE% of the best chromosomes directly to the next generation
        int eliteSize = (int) (currentGeneration.getPopulationSize() * ELITISM_PERCENTAGE);
        for (int i = 0; i < eliteSize; i++) {
            nextGeneration.add(sortedCurrentGen.get(i));
        }

        // Selection for the rest of the population
        int selectionSize = currentGeneration.getPopulationSize() - eliteSize;
        Collection<Chromosome> selectedChromosomes = selectForCrossover(currentGeneration, selectionSize);
        nextGeneration.addAll(selectedChromosomes);

        return nextGeneration;

    }


    /**
     * Selects a specified number of chromosomes for crossover based on ranked selection.
     *
     * @param currentGeneration The current generation of chromosomes.
     * @param selectionSize    The number of chromosomes to select for crossover.
     * @return A List of chromosomes selected for crossover.
     */
    private List<Chromosome> selectForCrossover(PopulationImplementation currentGeneration, int selectionSize){

        currentGeneration.getPopulation().sort(Collections.reverseOrder());
        int totalPopulation = currentGeneration.getPopulationSize();
        double totalRankSum = calculateTotalRankSum(totalPopulation);
        List<Chromosome> selectedChromosomes = new ArrayList<>();

        // Calculates selection probability for each rank
        List<Double> cumulativeProbabilities = getCumulativeProbabilities(totalPopulation, totalRankSum);

        // Perform selection based on cumulative probabilities
        for (int i = 0; i < selectionSize; i++) {
            double randomValue = random.nextDouble();
            for (int j = 0; j < cumulativeProbabilities.size(); j++) {
                if (randomValue <= cumulativeProbabilities.get(j)) {
                    selectedChromosomes.add(currentGeneration.get(j));
                    break;
                }
            }
        }

        return selectedChromosomes;

    }


    /**
     * Calculates cumulative probabilities for selection based on ranks.
     *
     * @param totalPopulation The total number of chromosomes in the population.
     * @param totalRankSum    The sum of all ranks in the population.
     * @return A list of cumulative probabilities.
     */
    private List<Double> getCumulativeProbabilities(int totalPopulation, double totalRankSum) {
        List<Double> cumulativeProbabilities = new ArrayList<>();
        double cumulative = 0.0;
        for (int rank = 1; rank <= totalPopulation; rank++) {
            double probability = rank / totalRankSum;
            cumulative += probability;
            cumulativeProbabilities.add(cumulative);
        }
        return cumulativeProbabilities;
    }


    /**
     * Calculates the total rank sum for the population, used in determining selection probabilities.
     *
     * @param populationSize The size of the population.
     * @return The total sum of ranks.
     */
    private double calculateTotalRankSum(int populationSize) {
        return populationSize * (populationSize + 1) / 2.0; // Sum of 1 to N
    }

}
