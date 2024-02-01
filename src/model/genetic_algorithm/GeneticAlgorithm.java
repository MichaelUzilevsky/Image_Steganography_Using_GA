package model.genetic_algorithm;

import javafx.scene.image.Image;
import model.data_managers.DataManipulation;
import model.data_managers.StringParser;
import model.genetic_algorithm.crossover.CrossoverStrategy;
import model.genetic_algorithm.crossover.MultiPointCrossover;
import model.genetic_algorithm.fitness.FitnessFunction;
import model.genetic_algorithm.fitness.PSNRFitnessFunction;
import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.PopulationImplementation;
import model.genetic_algorithm.population_structure.PriorityQueuePopulation;
import model.genetic_algorithm.selection.ElitismSelection;
import model.genetic_algorithm.selection.SelectionStrategy;

import java.util.BitSet;

public class GeneticAlgorithm {
    private final int GENERATIONS = 1000;
    private final int POPULATION_SIZE = 100;
    private Image originalImage;
    private String secretData;
    private StringParser parser;
    private BitSet secretDataBitArray;
    private DataManipulation dataManipulation;
    private PopulationImplementation population;
    private FitnessFunction fitnessFunction;
    private SelectionStrategy selection;
    private CrossoverStrategy crossover;

    public GeneticAlgorithm(){

    }
    public GeneticAlgorithm(Image originalImage, String secretData){
        this.originalImage = originalImage;

        this.secretData = secretData;

        parser = new StringParser(secretData);
        //secretDataBitArray = parser.convertToBitArray();

        //dataManipulation = new DataManipulation(secretDataBitArray);

        population = new PriorityQueuePopulation(POPULATION_SIZE);

        fitnessFunction = new PSNRFitnessFunction();

        selection = new ElitismSelection();

        crossover = new MultiPointCrossover();

        population.initializeChromosomes((int) originalImage.getWidth(), (int) originalImage.getHeight());
    }

    public Chromosome run(){
        return null;
    }

    public Chromosome getBestChromosome() {
        return  null;
    }

    public Image bestEmbedding(){
        return  null;
    }

    public void setOriginalImage(Image originalImage) {
        this.originalImage = originalImage;
    }

    public void setSecretData(String secretData) {
        this.secretData = secretData;
    }
}
