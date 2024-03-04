package model.genetic_algorithm.population_structure;

import java.util.List;

public interface PopulationImplementation {

    List<Chromosome> getPopulation();

    void setPopulation(List<Chromosome> population);

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
