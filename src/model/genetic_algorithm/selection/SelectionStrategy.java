package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.populations.PopulationImplementation;

public interface SelectionStrategy {
    Chromosome[] selectNextGenerationForCrossover(PopulationImplementation currentGeneration, int originalPopulationSize);
    Chromosome[] selectNextGenerationElitism(PopulationImplementation currentGeneration);
}
