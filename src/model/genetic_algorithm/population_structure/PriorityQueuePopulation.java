package model.genetic_algorithm.population_structure;

import java.util.Collections;
import java.util.PriorityQueue;

public class PriorityQueuePopulation implements PopulationImplementation {
    private final int populationSize;
    private final PriorityQueue<Chromosome> population;

    public PriorityQueuePopulation(int populationSize){
        this.populationSize = populationSize;
        this.population = new PriorityQueue<>(populationSize, Collections.reverseOrder());
    }


    @Override
    public void initializeChromosomes(int imageWidth, int imageHeight){
        for (int i = 0; i < this.populationSize; i++){
            population.add(new Chromosome(imageWidth, imageHeight));
        }
    }

    @Override
    public void add(Chromosome chromosome){
        this.population.add(chromosome);
    }


}
