package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.populations.PopulationImplementation;
import model.utils.UtilsMethods;


/**
 * Implements the ranked selection strategy for genetic algorithms.
 * This strategy includes elitism and ranks chromosomes based on their fitness for selection.
 */
public class ElitismBasedSelection implements SelectionStrategy {
    private final double ELITISM_PERCENTAGE;

    private Chromosome[] elitesChromosomes;

    public ElitismBasedSelection() {
        ELITISM_PERCENTAGE = 0.1;
    }

    /**
     * Selects the next chromosomes for the next generation, those chromosomes will later be crossover and mutated
     *
     * @param currentGeneration The current population from which to select the next generation.
     * @return A List of chromosomes selected to form the next generation.
     */
    @Override
    public Chromosome[] selectNextGenerationForCrossover(PopulationImplementation currentGeneration, int originalPopulationSize) {
        // Selection for the rest of the population
        int selectionSize = originalPopulationSize - 2 * calculateElitismSize(originalPopulationSize);

        return UtilsMethods.combineArrays(Chromosome.class, selectForCrossover(currentGeneration, selectionSize), this.elitesChromosomes);
    }

    @Override
    public Chromosome[] selectNextGenerationElitism(PopulationImplementation currentGeneration) {
        // Elitism: Take top ELITISM_PERCENTAGE% of the best chromosomes directly to the next generation
        int eliteSize = calculateElitismSize(currentGeneration.getPopulationSize());
        this.elitesChromosomes = new Chromosome[eliteSize];
        Chromosome[] elitism = new Chromosome[eliteSize];
        for (int i = 0; i < eliteSize; i++) {
            Chromosome chromosome = currentGeneration.popHighest();
            elitism[i] = chromosome;
            this.elitesChromosomes[i] = new Chromosome(chromosome);
        }
        return elitism;
    }

    private Chromosome[] selectForCrossover(PopulationImplementation currentGeneration, int selectionSize){
        Chromosome[] selected = new Chromosome[selectionSize];
        for (int i = 0; i < selectionSize; i++) {
            selected[i] = currentGeneration.getRandomChromosome();
        }
        return selected;
    }

    private int calculateElitismSize(int populationSize){
        return (int) (populationSize * ELITISM_PERCENTAGE);
    }


}
