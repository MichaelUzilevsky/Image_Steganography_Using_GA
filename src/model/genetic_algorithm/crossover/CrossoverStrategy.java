package model.genetic_algorithm.crossover;

import model.genetic_algorithm.population_structure.Chromosome;

import java.util.List;

public interface CrossoverStrategy {

    /**
     * This function cross-overs two chromosomes and returns a list representing the offsprings received from the crossover
     * A list is returned because each implementation of the crossover may result different amount of offsprings
     * @param parent1 chromosome
     * @param parent2 chromosome
     * @return list of the received offsprings
     */
    public List<Chromosome> crossover(Chromosome parent1, Chromosome parent2);
}
