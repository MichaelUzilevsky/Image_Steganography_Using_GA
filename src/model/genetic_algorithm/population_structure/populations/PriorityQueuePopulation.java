package model.genetic_algorithm.population_structure.populations;

import model.genetic_algorithm.population_structure.Chromosome;

public class PriorityQueuePopulation implements PopulationImplementation {
    private int populationSize;
    private final FixedSizePriorityQueue<Chromosome> population;

    public PriorityQueuePopulation(int populationSize){
        this.populationSize = populationSize;
        this.population = new FixedSizePriorityQueue<>(Chromosome.class, populationSize);
    }

    @Override
    public void setPopulation(Chromosome[] population) {
        this.population.buildMaxHeap(population);
        this.populationSize = population.length;
    }

    @Override
    public void initializeChromosomes(int dataSizeInBits){
        Chromosome[] arr = new Chromosome[this.populationSize];
        for (int i = 0; i < this.populationSize; i++){
            arr[i] = new Chromosome(dataSizeInBits);
        }
        setPopulation(arr);
    }


    @Override
    public int getPopulationSize() {
        return this.populationSize;
    }

    @Override
    public Chromosome getFittestChromosome() {
       return population.getHighest();
    }

    @Override
    public Chromosome[] getPopulation() {
        return population.getElements();
    }

    @Override
    public Chromosome getRandomChromosome() {
        return population.extractRandomInRange(0, population.size());
    }

    @Override
    public Chromosome popHighest() {
       Chromosome max = population.extractMax();
        if (max != null){
            populationSize--;
            return max;
        }
        return null;
    }

    @Override
    public void updateStructure() {
        setPopulation(this.population.getElements());
    }

    @Override
    public void printSorted() {
        population.printHeap();
    }

    @Override
    public Chromosome viewFittest() {
        return population.getHighest();
    }

}
