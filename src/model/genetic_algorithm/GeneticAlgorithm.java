package model.genetic_algorithm;

import javafx.scene.image.Image;
import model.data_managers.BitArray;
import model.data_managers.DataEmbedding;
import model.data_managers.DataManipulation;
import model.data_managers.StringParser;
import model.genetic_algorithm.crossover.CrossoverStrategy;
import model.genetic_algorithm.crossover.MultiPointCrossover;
import model.genetic_algorithm.fitness.FitnessFunction;
import model.genetic_algorithm.fitness.PSNRFitnessFunction;
import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.PopulationImplementation;
import model.genetic_algorithm.population_structure.ListPopulation;
import model.genetic_algorithm.selection.RankedSelection;
import model.genetic_algorithm.selection.SelectionStrategy;

import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithm {
    private final int GENERATIONS;
    private final int POPULATION_SIZE;
    private final double MUTATION_RATE;

    {
        GENERATIONS = 1000;
        POPULATION_SIZE = 100;
        MUTATION_RATE = 0.1;
    }

    private final Image originalImage;
    private final DataManipulation dataManipulation;
    private final DataEmbedding dataEmbedding;
    private final PopulationImplementation population;
    private final FitnessFunction fitnessFunction;
    private final SelectionStrategy selection;
    private final CrossoverStrategy crossover;

    public GeneticAlgorithm(Image originalImage, String secretData){
        this.originalImage = originalImage;

        StringParser parser = new StringParser(secretData);
        BitArray secretDataBitArray = parser.convertToBitArray();

        dataManipulation = new DataManipulation(secretDataBitArray);
        dataEmbedding = new DataEmbedding(originalImage);

        population = new ListPopulation(POPULATION_SIZE);

        fitnessFunction = new PSNRFitnessFunction();

        selection = new RankedSelection();

        crossover = new MultiPointCrossover();

        population.initializeChromosomes((int) originalImage.getWidth(), (int) originalImage.getHeight());
    }


    public Image run(){
        for (int i = 0; i < GENERATIONS; i++) {

            // Evaluate fitness of the current generation
            evaluatePopulationFitness();

            // Selection
                // selected for crossover
            List<Chromosome> selectedForCrossover =
                    new ArrayList<>(selection.selectNextGenerationForCrossover(population));
                // Elitism
            List<Chromosome> selectedUnchanged =
                    new ArrayList<>(selection.selectNextGenerationUnchanged(population));

            // Crossover
            List<Chromosome> afterCrossover = performCrossover(selectedForCrossover);

            // Mutation
            performMutation(afterCrossover);

            // New population
            selectedUnchanged.addAll(afterCrossover);

            population.setPopulation(selectedUnchanged);
        }
        return dataEmbedding.embedData(dataManipulation.modifyBitArray(population.getFittestChromosome()));
    }



    private void performMutation(List<Chromosome> afterCrossover) {
        for (Chromosome chromosome : afterCrossover){
            if (Math.random() <= MUTATION_RATE){
                chromosome.mutateChromosome();
            }
        }
    }

    private List<Chromosome> performCrossover(List<Chromosome> selectedForCrossover) {
        List<Chromosome> offsprings = new ArrayList<>();

        // Pair up selected chromosomes for crossover. This example pairs them sequentially.
        for (int i = 0; i < selectedForCrossover.size() - 1; i += 2) {
            Chromosome parent1 = selectedForCrossover.get(i);
            Chromosome parent2 = selectedForCrossover.get(i + 1);

            // Apply crossover strategy to generate two offspring
            List<Chromosome> children = crossover.crossover(parent1, parent2);

            offsprings.add(children.get(0));
            offsprings.add(children.get(1));
        }

        return offsprings;
    }

    private void evaluatePopulationFitness(){
        // finding the best fitness value for each chromosome
        for (Chromosome chromosome : population.getPopulation())
            findBestFitnessForChromosome(chromosome);
    }

    private void findBestFitnessForChromosome(Chromosome chromosome){
        int bestFlexibleGeneValue = -1;
        double bestFitness = -1;
        for (int i = 0; i <Chromosome.POSSIBLE_COMBINATIONS_AMOUNT_FOR_FLEXIBLE_GENE ; i++) {
            chromosome.setIndexesForGenes(i);

            BitArray modifiedBitArray = dataManipulation.modifyBitArray(chromosome);
            Image modifiedImage = dataEmbedding.embedData(modifiedBitArray);

            double fitness = fitnessFunction.calculateFitness(originalImage, modifiedImage);

            if (fitness > bestFitness){
                bestFitness = fitness;
                bestFlexibleGeneValue = i;
            }
        }
        chromosome.setIndexesForGenes(bestFlexibleGeneValue);
        chromosome.setFitnessScore(bestFitness);
    }
}
