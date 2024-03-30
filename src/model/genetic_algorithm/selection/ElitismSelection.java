package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.populations.PopulationImplementation;

/**
 * Implements the elitism selection strategy for a genetic algorithm. This strategy ensures that
 * the top-performing chromosomes, as measured by their fitness, are carried over to the next
 * generation without modification. It's a way to ensure that the best genetic material is not lost.
 */
public class ElitismSelection implements SelectionStrategy {

    /**
     * Selects a specified number of the fittest chromosomes from the current generation and
     * carries them over to the next generation. Each selected chromosome is cloned and inserted
     * back into the population to maintain the population size.
     *
     * @param currentGeneration The current population from which to select the elite chromosomes.
     * @param amountToSelect The number of elite chromosomes to be selected and carried over to the next generation.
     * @return An array of chromosomes representing the selected elite members of the current generation.
     */
    @Override
    public Chromosome[] selectNextGeneration(PopulationImplementation currentGeneration, int amountToSelect) {
        Chromosome[] elitism = new Chromosome[amountToSelect];
        // Extract the top-performing chromosomes based on their fitness
        for (int i = 0; i < amountToSelect; i++) {
            elitism[i] = currentGeneration.popHighest();
        }

        // Re-insert the selected chromosomes back into the population to preserve genetic diversity
        for (Chromosome chromosome : elitism) {
            currentGeneration.insert(new Chromosome(chromosome));
        }
        return elitism;
    }
}
