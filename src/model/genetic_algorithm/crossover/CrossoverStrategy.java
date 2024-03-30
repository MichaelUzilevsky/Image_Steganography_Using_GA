package model.genetic_algorithm.crossover;

import model.genetic_algorithm.population_structure.Chromosome;

import java.util.List;

/**
 * Defines a strategy for performing crossover operations in a genetic algorithm.
 * A crossover strategy is responsible for combining the genetic information
 * of two parent chromosomes to produce offspring chromosomes. The specific
 * method of combination depends on the implementation of this interface.
 */
public interface CrossoverStrategy {

    /**
     * Performs a crossover operation between two parent chromosomes and produces
     * a list of offspring chromosomes. The actual mechanics of the crossover depend
     * on the implementing class, and multiple offspring can be produced, hence
     * the return type is a list.
     *
     * @param parent1 The first parent chromosome involved in the crossover.
     * @param parent2 The second parent chromosome involved in the crossover.
     * @return A list of Chromosome objects representing the offspring produced
     *         from the crossover of the two parents. The number of offspring
     *         can vary depending on the crossover strategy.
     */
    List<Chromosome> crossover(Chromosome parent1, Chromosome parent2);
}
