package model.genetic_algorithm.population_structure.populations;

import model.genetic_algorithm.population_structure.Chromosome;


public interface PopulationImplementation {

    void setPopulation(Chromosome[] population);

    void initializeChromosomes(int dataSizeInBits);

    int getPopulationSize();

    Chromosome getFittestChromosome();

    Chromosome[] getPopulation();

    Chromosome getRandomChromosome();

    Chromosome popHighest();

    void updateStructure();

    void printSorted();

    Chromosome viewFittest();
}
