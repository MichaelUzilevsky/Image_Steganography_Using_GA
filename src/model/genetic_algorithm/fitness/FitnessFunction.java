package model.genetic_algorithm.fitness;

import javafx.scene.image.Image;
import model.genetic_algorithm.population_structure.Chromosome;

public interface FitnessFunction {
    double calculateFitness(Chromosome chromosome, Image image);
}
