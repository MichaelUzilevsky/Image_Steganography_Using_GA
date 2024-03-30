package model.genetic_algorithm.fitness;

import javafx.scene.image.Image;


/**
 * Combines the Peak Signal-to-Noise Ratio (PSNR) and Structural Similarity Index (SSIM)
 * fitness functions into a single weighted fitness calculation for image steganography
 * evaluation. This class allows for a more comprehensive assessment of image quality
 * by taking into account both pixel-wise differences (PSNR) and perceptual similarities
 * (SSIM) between the original and modified images.
 */
public class WeightedFitnessCalculation implements FitnessFunction{
    private final PSNRFitnessFunction psnrFitnessFunction;
    private final SSIMFitnessFunction ssimFitnessFunction;

    // Weights for combining PSNR and SSIM scores. SSIM is given more weight due to its
    // perceptual relevance.
    private static final double SSIM_WEIGHT = 0.7;
    private static final double PSNR_WEIGHT = 0.3;

    /**
     * Constructs a new WeightedFitnessCalculation object. Initializes the
     * PSNR and SSIM fitness functions that will be used in the weighted fitness calculation.
     */
    public WeightedFitnessCalculation(){
        this.psnrFitnessFunction = new PSNRFitnessFunction();
        this.ssimFitnessFunction = new SSIMFitnessFunction();
    }

    /**
     * Calculates a weighted fitness score for an image modification using PSNR and SSIM
     * metrics. The PSNR and SSIM scores are weighted according to predefined constants
     * and combined to produce a single fitness value.
     *
     * @param originalImage The original image before any modifications.
     * @param modifiedImage The image after modifications, such as data embedding.
     * @return A weighted fitness score that represents the quality of the image
     *         modification. Higher scores indicate better quality.
     */
    @Override
    public double calculateFitness(Image originalImage, Image modifiedImage) {
        return (ssimFitnessFunction.calculateFitness(originalImage, modifiedImage) * SSIM_WEIGHT +
                psnrFitnessFunction.calculateFitness(originalImage, modifiedImage) * PSNR_WEIGHT);
    }
}
