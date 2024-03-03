package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.PopulationImplementation;

import java.util.Collection;

public interface SelectionStrategy {
    Collection<Chromosome> selectNextGeneration(PopulationImplementation currentGeneration);
}
