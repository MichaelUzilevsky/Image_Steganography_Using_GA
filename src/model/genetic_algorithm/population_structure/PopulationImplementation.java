package model.genetic_algorithm.population_structure;

import java.util.List;

public interface PopulationImplementation {

    /**
     * adds chromosome to the population
     * @param chromosome not null
     */
    void add(Chromosome chromosome);

    List<Chromosome> getPopulation();

    /**
     * first initialize a complete random population of chromosomes
     * @param imageWidth image width
     * @param imageHeight image height
     */
    void initializeChromosomes(int imageWidth, int imageHeight);

    Chromosome get(int index);

    int getPopulationSize();

    Chromosome getFittestChromosome();
}
