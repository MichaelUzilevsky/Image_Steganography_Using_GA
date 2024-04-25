package model.genetic_algorithm.population_structure.populations;

import model.genetic_algorithm.population_structure.Chromosome;

/**
 * A population management class that utilizes a priority queue to maintain chromosomes in a genetic algorithm.
 * This implementation provides an efficient way to access the fittest chromosomes and supports operations
 * such as insertion, retrieval, and removal based on fitness scores.
 */
public class PriorityQueuePopulation implements PopulationImplementation {
    private int populationSize;
    private final FixedSizePriorityQueue<Chromosome> population;

    /**
     * Constructs a new instance of PriorityQueuePopulation with a specified size.
     *
     * @param populationSize The maximum number of chromosomes this population can hold.
     */
    public PriorityQueuePopulation(int populationSize){
        this.populationSize = populationSize;
        this.population = new FixedSizePriorityQueue<>(Chromosome.class, populationSize);
    }

    /**
     * Sets the population to a new array of chromosomes. The existing population is replaced.
     *
     * @param population An array of Chromosome objects to be used as the new population.
     */
    @Override
    public void setPopulation(Chromosome[] population) {
        this.population.buildMaxHeap(population);
        this.populationSize = population.length;
    }

    /**
     * Initializes chromosomes in the population with random or predefined values for their genes.
     *
     * @param dataSizeInBits The size of the data (in bits) that each chromosome represents.
     */
    @Override
    public void initializeChromosomes(int dataSizeInBits){
        Chromosome[] arr = new Chromosome[this.populationSize];
        for (int i = 0; i < this.populationSize; i++){
            arr[i] = new Chromosome(dataSizeInBits);
        }
        setPopulation(arr);
    }

    /**
     * Retrieves the size of the current population.
     *
     * @return The number of chromosomes in the population.
     */
    @Override
    public int getPopulationSize() {
        return this.populationSize;
    }

    /**
     * Retrieves the fittest chromosome from the population based on its fitness score.
     *
     * @return The chromosome with the highest fitness score.
     */
    @Override
    public Chromosome getFittestChromosome() {
       return population.getHighest();
    }

    /**
     * Retrieves the entire population as an array of chromosomes.
     *
     * @return An array of all chromosomes in the population.
     */
    @Override
    public Chromosome[] getPopulation() {
        return population.getElements();
    }


    /**
     * Selects and returns a random chromosome from the population.
     * This method is essential for introducing randomness into the genetic operations,
     * such as in some crossover and mutation techniques,
     * where random chromosomes need to be selected for genetic diversity.
     *
     * @return A randomly selected chromosome.
     */
    @Override
    public Chromosome getRandomChromosome() {
        return population.extractRandomInRange(0, population.size());
    }

    /**
     * Removes and returns the chromosome with the highest fitness score from the population.
     *
     * @return The chromosome with the highest fitness score.
     */
    @Override
    public Chromosome popHighest() {
       Chromosome max = population.extractMax();
        if (max != null){
            populationSize--;
            return max;
        }
        return null;
    }

    /**
     * Updates the internal structure of the population, which may involve reorganizing the priority queue.
     */
    @Override
    public void updateStructure() {
        setPopulation(this.population.getElements());
    }

    @Override
    public void printSorted() {
        population.printHeap();
    }

    /**
     * Provides a view (not removal) of the fittest chromosome without affecting the population.
     *
     * @return The chromosome with the highest fitness score.
     */
    @Override
    public Chromosome viewFittest() {
        return population.getHighest();
    }

    /**
     * Adds a new chromosome to the population.
     * This method is critical for maintaining the population size after genetic operations
     * like crossover and mutation have been applied.
     * It ensures that all chromosomes are kept sorted according to their fitness scores,
     * thanks to the underlying priority queue mechanism.
     *
     * @param chromosome The Chromosome to be inserted.
     */
    @Override
    public void insert(Chromosome chromosome) {
        population.insert(chromosome);
    }
}