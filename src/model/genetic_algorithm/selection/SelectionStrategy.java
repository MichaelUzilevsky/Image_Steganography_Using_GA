package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.PopulationImplementation;

import java.util.Collection;
import java.util.List;

public interface SelectionStrategy {
    Collection<Chromosome> selectNextGenerationForCrossover(PopulationImplementation currentGeneration);
    List<Chromosome> selectNextGenerationUnchanged(PopulationImplementation currentGeneration);
}
