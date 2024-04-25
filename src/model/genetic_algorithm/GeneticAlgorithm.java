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
import model.genetic_algorithm.selection.ElitismSelection;
import model.genetic_algorithm.selection.RouletteWheelSelection;
import model.genetic_algorithm.selection.SelectionStrategy;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;
import view.DynamicGraph;

import java.util.List;

/**
 * This class implements a genetic algorithm for optimizing the embedding of secret data into an image
 * using steganography. The algorithm iterates through a number of generations, each time selecting chromosomes
 * (candidate solutions) based on their fitness, performing crossover and mutation to produce new chromosomes,
 * and then evaluating the fitness of the resulting population. The fitness function evaluates how well the
 * secret data has been embedded into the image, with the goal of minimizing the impact on the image's visual
 * quality while ensuring the data's integrity.
 * The genetic algorithm utilizes several components:
 * - A {@link DataManipulation} instance to apply genetic operations on the secret data.
 * - A {@link DataEmbedding} instance to embed the secret data into the image.
 * - A {@link PopulationImplementation} instance to manage the population of chromosomes.
 * - A {@link FitnessFunction} instance to calculate the fitness of each chromosome.
 * - A {@link SelectionStrategy} instance for selecting chromosomes for the next generation.
 * - A {@link CrossoverStrategy} instance to crossover pairs of chromosomes and produce offspring.
 */
public class GeneticAlgorithm {

    // Fields defining the parameters of the genetic algorithm
    private final int GENERATIONS;
    private final int POPULATION_SIZE;
    private final double MUTATION_RATE;
    private final double CROSSOVER_RATE;
    private final double ELITISM_PERCENTAGE;

    {
        GENERATIONS = 15;
        POPULATION_SIZE = 20;
        MUTATION_RATE = 0.1;
        CROSSOVER_RATE = 0.9;
        ELITISM_PERCENTAGE = 0.1;
    }

    private final Image originalImage;
    private final DataManipulation dataManipulation;
    private final DataEmbedding dataEmbedding;
    private final PopulationImplementation population;
    private final FitnessFunction fitnessFunction;
    private final SelectionStrategy selection;
    private final SelectionStrategy elitismSelection;
    private final CrossoverStrategy crossover;
    private final BitArray secretDataBitArray;

    /**
     * Constructs a GeneticAlgorithm instance for a given image and secret data.
     *
     * @param originalImage The image into which the secret data is to be embedded.
     * @param secretData The secret data to be embedded into the image.
     */
    public GeneticAlgorithm(Image originalImage, String secretData){
        this.originalImage = originalImage;

        StringParser parser = new StringParser(secretData);
        this.secretDataBitArray = parser.convertToBitArray();

        dataManipulation = new DataManipulation(secretDataBitArray);
        dataEmbedding = new DataEmbedding(originalImage);

        population = new PriorityQueuePopulation(POPULATION_SIZE);

        fitnessFunction = new PSNRFitnessFunction();

        selection = new RouletteWheelSelection();

        elitismSelection = new ElitismSelection();

        crossover = new MultiPointCrossoverGeneSplit();

        population.initializeChromosomes(secretData.length() * ConstantsClass.BITS_PER_BYTE);
    }

    /**
     * Executes the genetic algorithm, iterating through generations and applying genetic operations
     * to optimize the embedding of the secret data into the image. The method returns the image with
     * the secret data embedded as a result of the optimization process.
     *
     * @return The image with the secret data optimally embedded.
     */
    public Image run(){
        int elitismSize = (int) (population.getPopulationSize() * ELITISM_PERCENTAGE);
        int selectionSize = population.getPopulationSize() - elitismSize;

        for (int i = 1; i <= GENERATIONS; i++) {
            System.out.println("generation "+ i+" population Size " + population.getPopulationSize());

            // Evaluate fitness of the current generation
            evaluatePopulationFitness();
 
            System.out.println("the Fittest in this generation is " + population.viewFittest());

            // Update the dynamic graph with the fitness score of the fittest chromosome
            DynamicGraph.updateSeries(i, population.viewFittest().getFitnessScore());


            // Selection
            // Elitism
            Chromosome[] elitismSelected = elitismSelection.selectNextGeneration(population, elitismSize);
            // selected for crossover
            Chromosome[] selectedForCrossover = selection.selectNextGeneration(population, selectionSize);


            // Crossover
            Chromosome[] afterCrossover = performCrossover(selectedForCrossover);

            // Mutation
            performMutation(afterCrossover);

            // New population
            Chromosome[] newPopulation = UtilsMethods.combineArrays(Chromosome.class, elitismSelected, afterCrossover);

            population.setPopulation(newPopulation);
        }
        // fittest Chromosome
        Chromosome fittestChromosome = population.viewFittest();
        return embedIntoTheImage(fittestChromosome);
    }

    /**
     * Embeds the manipulated secret data into the original image based on a given chromosome.
     * The chromosome dictates how the data manipulation is to be performed before embedding.
     *
     * @param chromosome The chromosome encoding the data manipulation strategy.
     * @return An image with the secret data embedded according to the chromosome's strategy.
     */
    private Image embedIntoTheImage(Chromosome chromosome){
        BitArray manipulated = dataManipulation.modifyBitArray(chromosome);

        ImageMetadata metadata = new ImageMetadata(chromosome, secretDataBitArray.size(),
                (int) originalImage.getWidth(), (int) originalImage.getHeight());

        // Embed data into the image
        return dataEmbedding.embedData(manipulated, metadata);
    }

    /**
     * Performs mutation on a set of chromosomes after crossover. Each chromosome has a chance
     * equal to the mutation rate of undergoing a mutation, which alters its genes randomly.
     *
     * @param afterCrossover The array of chromosomes to be potentially mutated.
     */
    private void performMutation(Chromosome[] afterCrossover) {
        for (Chromosome chromosome : afterCrossover){
            if (Math.random() <= MUTATION_RATE){
                chromosome.mutateChromosome();
            }
        }
    }

    /**
     * Performs crossover on a set of selected chromosomes to produce offspring. The method pairs
     * chromosomes and applies a crossover strategy to each pair, generating new chromosomes (offspring).
     * Mixing genetic material between pairs of chromosomes according to the crossover strategy.
     *
     * @param selectedForCrossover The array of chromosomes selected for crossover.
     * @return An array of chromosomes resulting from the crossover process.
     */
    private Chromosome[] performCrossover(Chromosome[] selectedForCrossover) {
        Chromosome[] offsprings = new Chromosome[selectedForCrossover.length];
        int arrIndex = 0;

        // Pair up selected chromosomes for crossover. pairs them sequentially.
        for (int i = 0; i < selectedForCrossover.length - 1; i += 2) {
            Chromosome parent1 = selectedForCrossover[i];
            Chromosome parent2 = selectedForCrossover[i + 1];

            if(Math.random() <= CROSSOVER_RATE){
                // Apply crossover strategy to generate two offspring
                List<Chromosome> children = crossover.crossover(parent1, parent2);

                offsprings[arrIndex++] = (children.get(0));
                offsprings[arrIndex++] = (children.get(1));
            }
            else {
                offsprings[arrIndex++] = (parent1);
                offsprings[arrIndex++] = (parent2);
            }

        }
        if(offsprings[selectedForCrossover.length-1] == null){
            offsprings[selectedForCrossover.length-1] = selectedForCrossover[selectedForCrossover.length / 2];
        }

        return offsprings;
    }

    /**
     * Evaluates the fitness of each chromosome in the population. This method updates each chromosome's
     * fitness score based on how well it meets the objective of embedding secret data into an image.
     */
    private void evaluatePopulationFitness(){
        // finding the best fitness value for each chromosome
        for (Chromosome chromosome : population.getPopulation())
            findBestFitnessForChromosome(chromosome);

        population.updateStructure();
    }

    /**
     * Finds and sets the best flexible gene value for a given chromosome based on fitness evaluation.
     * This method iterates through all possible combinations of the flexible gene, selects the one
     * that results in the highest fitness, and updates the chromosome accordingly.
     * Optimizes a single chromosome by testing different configurations of its flexible genes,
     * setting the chromosome's genes to the configuration that results in the highest fitness.
     *
     * @param chromosome The chromosome to optimize.
     */
    private void findBestFitnessForChromosome(Chromosome chromosome){
        int bestFlexibleGeneValue = -1;
        double bestFitness = -1;
        double fitness = 0;
        Image modifiedImage;
        
        for (int i = 0; i < ConstantsClass.POSSIBLE_COMBINATIONS_AMOUNT_FOR_FLEXIBLE_GENE ; i++) {
            chromosome.setIndexesForGenes(i);

            modifiedImage = embedIntoTheImage(chromosome);

            fitness = fitnessFunction.calculateFitness(originalImage, modifiedImage);

            if (fitness > bestFitness){
                bestFitness = fitness;
                bestFlexibleGeneValue = i;
            }
        }
        chromosome.setIndexesForGenes(bestFlexibleGeneValue);
        chromosome.setFitnessScore(fitness);
    }
}
