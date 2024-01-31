package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;

import java.util.Collection;

public interface SelectionStrategy {
    Collection<Chromosome> selectParents(Collection<Chromosome> population);
}
