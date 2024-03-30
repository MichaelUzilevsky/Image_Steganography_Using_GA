package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.populations.PopulationImplementation;

/**
 * Defines the interface for selection strategies used in genetic algorithms. Selection strategies are responsible
 * for selecting chromosomes from the current population to form the next generation. The selection process can
 * be based on various criteria, such as fitness scores, to ensure that the most suitable chromosomes are carried
 * forward, thereby guiding the evolutionary process.
 */
public interface SelectionStrategy {

    /**
     * Selects a specified number of chromosomes from the current generation to form the next generation.
     * Implementations of this method should define the logic for selecting chromosomes based on the strategy
     * being implemented, such as roulette wheel selection, tournament selection, rank selection, etc.
     *
     * @param currentGeneration The current population from which to select the next generation.
     * @param amountToSelect The number of chromosomes to be selected from the current population.
     * @return An array of chromosomes selected to form the next generation.
     */
    Chromosome[] selectNextGeneration(PopulationImplementation currentGeneration, int amountToSelect);
}
