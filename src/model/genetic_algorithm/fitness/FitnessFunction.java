package model.genetic_algorithm.fitness;

import javafx.scene.image.Image;

public interface FitnessFunction {
    double calculateFitness(Image originalImage, Image modifedImage);
}
