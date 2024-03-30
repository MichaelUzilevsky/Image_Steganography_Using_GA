package model.genetic_algorithm.population_structure.populations;

import model.genetic_algorithm.population_structure.Chromosome;


public interface PopulationImplementation {

    /**
     * Sets the entire population of chromosomes.
     *
     * @param population Array of Chromosome objects representing the population.
     */
    void setPopulation(Chromosome[] population);

    /**
     * Initializes the chromosomes in the population with a given data size in bits.
     * This can include random generation or setting initial conditions.
     *
     * @param dataSizeInBits The size of data each chromosome represents, in bits.
     */
    void initializeChromosomes(int dataSizeInBits);

    /**
     * Gets the size of the population.
     *
     * @return The number of chromosomes in the population.
     */
    int getPopulationSize();

    /**
     * Gets the size of the population.
     *
     * @return The number of chromosomes in the population.
     */
    Chromosome getFittestChromosome();

    /**
     * Gets the size of the population.
     *
     * @return The number of chromosomes in the population.
     */
    Chromosome[] getPopulation();

    /**
     * Selects and returns a random chromosome from the population.
     *
     * @return A randomly selected Chromosome.
     */
    Chromosome getRandomChromosome();

    /**
     * Selects and returns a random chromosome from the population.
     *
     * @return A randomly selected Chromosome.
     */
    Chromosome popHighest();

    /**
     * Updates the internal structure of the population, if necessary.
     * This could involve sorting based on fitness scores or any other maintenance tasks.
     */
    void updateStructure();

    /**
     * Prints the chromosomes of the population in sorted order based on their fitness scores.
     */
    void printSorted();

    /**
     * Provides a view (not removal) of the fittest chromosome without affecting the population.
     *
     * @return The chromosome with the highest fitness score.
     */
    Chromosome viewFittest();

    /**
     * Inserts a new chromosome into the population.
     *
     * @param chromosome The Chromosome to be inserted.
     */
    void insert(Chromosome chromosome);
}
