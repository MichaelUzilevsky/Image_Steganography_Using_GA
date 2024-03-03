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

public class GeneticAlgorithm {
    private final int GENERATIONS;
    private final int POPULATION_SIZE;

    {
        GENERATIONS = 1000;
        POPULATION_SIZE = 100;
    }

    private final Image originalImage;
    private final String secretData;
    private final StringParser parser;
    private final DataManipulation dataManipulation;
    private final DataEmbedding dataEmbedding;
    private final BitArray secretDataBitArray;
    private final PopulationImplementation population;
    private final FitnessFunction fitnessFunction;
    private final SelectionStrategy selection;
    private final CrossoverStrategy crossover;

    public GeneticAlgorithm(Image originalImage, String secretData){
        this.originalImage = originalImage;
        this.secretData = secretData;

        parser = new StringParser(secretData);
        secretDataBitArray = parser.convertToBitArray();

        dataManipulation = new DataManipulation(secretDataBitArray);
        dataEmbedding = new DataEmbedding(originalImage);

        population = new ListPopulation(POPULATION_SIZE);

        fitnessFunction = new PSNRFitnessFunction();

        selection = new RankedSelection();

        crossover = new MultiPointCrossover();

        population.initializeChromosomes((int) originalImage.getWidth(), (int) originalImage.getHeight());
    }

    public Chromosome run(){
        for (int i = 0; i < GENERATIONS; i++) {

        }
        return population.getFittestChromosome();
    }
}
