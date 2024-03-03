package model.genetic_algorithm.population_structure;

import java.util.*;

public class ListPopulation implements PopulationImplementation {
    private final int populationSize;
    private final List<Chromosome> population;

    public ListPopulation(int populationSize){
        this.populationSize = populationSize;
        this.population = new ArrayList<>(populationSize);
    }


    @Override
    public void initializeChromosomes(int imageWidth, int imageHeight){
        for (int i = 0; i < this.populationSize; i++){
            population.add(new Chromosome(imageWidth, imageHeight));
        }
    }

    @Override
    public Chromosome get(int index) {
        return population.get(index);
    }

    @Override
    public int getPopulationSize() {
        return this.populationSize;
    }

    @Override
    public Chromosome getFittestChromosome() {
        Chromosome highestFitness = population.get(0);
        for (Chromosome chromosome : population){
            if (chromosome.getFitnessScore() > highestFitness.getFitnessScore())
                highestFitness = chromosome;
        }
        return highestFitness;
    }

    @Override
    public void add(Chromosome chromosome){
        this.population.add(chromosome);
    }

    @Override
    public List<Chromosome> getPopulation() {
        return this.population;
    }


}
