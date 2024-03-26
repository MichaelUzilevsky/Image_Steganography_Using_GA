package model.genetic_algorithm;

import javafx.scene.image.Image;
import model.data_managers.BitArray;
import model.data_managers.DataEmbedding;
import model.data_managers.DataManipulation;
import model.data_managers.StringParser;
import model.data_managers.image_metedate.ImageMetadata;
import model.genetic_algorithm.crossover.CrossoverStrategy;
import model.genetic_algorithm.crossover.MultiPointCrossoverGeneSplit;
import model.genetic_algorithm.fitness.FitnessFunction;
import model.genetic_algorithm.fitness.PSNRFitnessFunction;
import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.populations.PopulationImplementation;
import model.genetic_algorithm.population_structure.populations.PriorityQueuePopulation;
import model.genetic_algorithm.selection.ElitismBasedSelection;
import model.genetic_algorithm.selection.SelectionStrategy;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;
import view.DynamicGraph;

import java.util.List;

public class GeneticAlgorithm {
    private final int GENERATIONS;
    private final int POPULATION_SIZE;
    private final double MUTATION_RATE;

    {
        GENERATIONS = 15;
        POPULATION_SIZE = 20;
        MUTATION_RATE = 0.3;
    }

    private final Image originalImage;
    private final DataManipulation dataManipulation;
    private final DataEmbedding dataEmbedding;
    private final PopulationImplementation population;
    private final FitnessFunction fitnessFunction;
    private final SelectionStrategy selection;
    private final CrossoverStrategy crossover;
    private final BitArray secretDataBitArray;

    public GeneticAlgorithm(Image originalImage, String secretData){
        this.originalImage = originalImage;

        StringParser parser = new StringParser(secretData);
        this.secretDataBitArray = parser.convertToBitArray();

        dataManipulation = new DataManipulation(secretDataBitArray);
        dataEmbedding = new DataEmbedding(originalImage);

        population = new PriorityQueuePopulation(POPULATION_SIZE);

        fitnessFunction = new PSNRFitnessFunction();

        selection = new ElitismBasedSelection();

        crossover = new MultiPointCrossoverGeneSplit();

        population.initializeChromosomes(secretData.length() * ConstantsClass.BITS_PER_BYTE);
    }


    public Image run(){
        for (int i = 0; i < GENERATIONS; i++) {
            System.out.println("generation "+ i+" population Size " + population.getPopulationSize());

            // Evaluate fitness of the current generation
            evaluatePopulationFitness();

            System.out.println("the Fittest in this generation is " + population.viewFittest());

            // Update the dynamic graph with the fitness score of the fittest chromosome
            DynamicGraph.updateSeries(i, population.viewFittest().getFitnessScore());


            // Selection
            // Elitism
            Chromosome[] selectedUnchanged = selection.selectNextGenerationElitism(population);
                // selected for crossover
            Chromosome[] selectedForCrossover = selection.selectNextGenerationForCrossover(population, POPULATION_SIZE);


            // Crossover
            Chromosome[] afterCrossover = performCrossover(selectedForCrossover);

            // Mutation
            performMutation(afterCrossover);

            // New population
            Chromosome[] newPopulation = UtilsMethods.combineArrays(Chromosome.class, selectedUnchanged, afterCrossover);

            population.setPopulation(newPopulation);
        }
        // fittest Chromosome
        Chromosome fittestChromosome = population.viewFittest();
        return embedIntoTheImage(fittestChromosome);
    }

    private Image embedIntoTheImage(Chromosome chromosome){
        BitArray manipulated = dataManipulation.modifyBitArray(chromosome);

        ImageMetadata metadata = new ImageMetadata(chromosome, secretDataBitArray.size(),
                (int) originalImage.getWidth(), (int) originalImage.getHeight());

        // Embed data into the image
        return dataEmbedding.embedData(manipulated, metadata);
    }

    private void performMutation(Chromosome[] afterCrossover) {
        for (Chromosome chromosome : afterCrossover){
            if (Math.random() <= MUTATION_RATE){
                chromosome.mutateChromosome();
            }
        }
    }

    private Chromosome[] performCrossover(Chromosome[] selectedForCrossover) {
        Chromosome[] offsprings = new Chromosome[selectedForCrossover.length];
        int arrIndex = 0;

        // Pair up selected chromosomes for crossover. pairs them sequentially.
        for (int i = 0; i < selectedForCrossover.length - 1; i += 2) {
            Chromosome parent1 = selectedForCrossover[i];
            Chromosome parent2 = selectedForCrossover[i + 1];

            // Apply crossover strategy to generate two offspring
            List<Chromosome> children = crossover.crossover(parent1, parent2);

            offsprings[arrIndex++] = (children.get(0));
            offsprings[arrIndex++] = (children.get(1));
        }

        return offsprings;
    }

    private void evaluatePopulationFitness(){
        // finding the best fitness value for each chromosome
        for (Chromosome chromosome : population.getPopulation())
            findBestFitnessForChromosome(chromosome);

        population.updateStructure();
    }

    private void findBestFitnessForChromosome(Chromosome chromosome){
        int bestFlexibleGeneValue = -1;
        double bestFitness = -1;
        for (int i = 0; i < ConstantsClass.POSSIBLE_COMBINATIONS_AMOUNT_FOR_FLEXIBLE_GENE ; i++) {
            chromosome.setIndexesForGenes(i);

            Image modifiedImage = embedIntoTheImage(chromosome);

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
