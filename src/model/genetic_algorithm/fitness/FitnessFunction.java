package model.genetic_algorithm.fitness;

import javafx.scene.image.Image;

/**
 * Defines the interface for fitness functions used in genetic algorithms
 * to evaluate the quality of a solution. In the context of image steganography,
 * the fitness function assesses how well data has been embedded into an image
 * without significantly degrading the image's quality.
 */
public interface FitnessFunction {


    /**
     * Calculates the fitness of a modified image compared to the original image.
     * The fitness measure should reflect how effectively data has been embedded into
     * the image with minimal impact on the visual or statistical quality of the image.
     *
     * @param originalImage The original, unaltered image.
     * @param modifiedImage The image after data embedding. This is the candidate solution
     *                      whose fitness is being evaluated.
     * @return A double value representing the fitness score of the modified image. Higher
     *         values indicate better fitness (i.e., effective data embedding with minimal quality loss).
     */
    double calculateFitness(Image originalImage, Image modifiedImage);
}
