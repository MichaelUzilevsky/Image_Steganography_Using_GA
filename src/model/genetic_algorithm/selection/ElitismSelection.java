package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;

import java.util.Collection;

/**
 * This type of selection is based on the Elitism principle.
 * Elitism ensures that the best-performing individuals from one generation are carried over to the next generation.
 * In my implementation of this selection algorithm,
 * I want to add some randomality to the process of creating new offsprings.
 * Therefor I decided to divide the new generations as follows
 *                                                              40% -> the best chromosomes of last generation
 *                                                              60% -> crossover of the best 60% of the last generation,
 *                                                                      the 1st will cross over with the 60th
 *                                                                      the 2nd with the 59th, and so on.
 *                                                                      this will create a diverse new generation
 * <p>
 *                                                              10% -> of the crossover-ed new offsprings,
 *                                                                      will get mutated
 * <p>
 *                                                          this numbers may change base on the algorithms' performance
 */
public class ElitismSelection implements SelectionStrategy {
    @Override
    public Collection<Chromosome> selectParents(Collection<Chromosome> population) {
        return null;
    }
}
