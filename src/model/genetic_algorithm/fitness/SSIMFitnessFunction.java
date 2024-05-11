package model.genetic_algorithm.fitness;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * A fitness function implementation based on the Structural Similarity Index (SSIM).
 * SSIM is used to measure the similarity between two images. It is considered to be
 * more perceptually relevant than traditional metrics like Mean Squared Error (MSE)
 * or Peak Signal-to-Noise Ratio (PSNR), especially in the context of image processing
 * and steganography.
 * This class computes the SSIM index for blocks of an image, providing a detailed
 * assessment of the changes made to the image's visual quality due to data embedding.
 */
public class SSIMFitnessFunction implements FitnessFunction{
    public static final int BLOCK_SIZE = 8;

    // Constants for converting RGB to grayscale luminance
    public static final double RED_WEIGHT = 0.299;
    public static final double GREEN_WEIGHT = 0.587;
    public static final double BLUE_WEIGHT = 0.114;

    // Constants for SSIM calculation
    public static final double k1 = 0.01;
    public static final double k2 = 0.03;
    public static final double L = 255;

    public static final double FACTOR = 2;

    /**
     * Calculates the fitness of a modified image using the Structural Similarity Index.
     * The SSIM value is averaged over blocks of the image to account for local pattern
     * changes and provides a more nuanced view of image quality than global metrics.
     *
     * @param originalImage The original image before data embedding.
     * @param modifiedImage The image after data has been embedded.
     * @return The average SSIM value over all blocks of the image. Values range from
     *         -1 (completely different) to 1 (identical), where higher values indicate
     *         less perceptual difference between the original and modified images.
     */
    @Override
    public double calculateFitness(Image originalImage, Image modifiedImage) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        if (width != (int) modifiedImage.getWidth() || height != (int) modifiedImage.getHeight()) {
            throw new IllegalArgumentException("Images must have the same dimensions.");
        }

        double totalSSIM = 0.0;
        int count = 0;

        for (int i = 0; i < width; i += BLOCK_SIZE) {
            for (int j = 0; j < height; j += BLOCK_SIZE) {
                double mean1 = calculateMeanIntensityForBlock(originalImage, i, j);
                double mean2 = calculateMeanIntensityForBlock(modifiedImage, i, j);

                double variance1 = calculateVarianceForBlock(originalImage, i, j, mean1);
                double variance2 = calculateVarianceForBlock(modifiedImage, i, j, mean2);

                double covariance = calculateCovarianceForTwoBlocks(originalImage, modifiedImage, i, j, mean1, mean2);

                double ssim = calculateSSIM(mean1, mean2, variance1, variance2, covariance);
                totalSSIM += ssim;
                count++;
            }
        }

        return count > 0 ? totalSSIM / count : 0;
    }

    // Private helper methods for SSIM calculation:
    // calculateMeanIntensityForBlock,
    // calculateVarianceForBlock,
    // calculateCovarianceForTwoBlocks,
    // calculateSSIM

    private double calculateMeanIntensityForBlock(Image image, int startingRow, int startingCol) {
        double sumIntensity = 0;
        int totalPixels = SSIMFitnessFunction.BLOCK_SIZE * SSIMFitnessFunction.BLOCK_SIZE;
        PixelReader reader = image.getPixelReader();

        for (int i = startingRow; i < startingRow + SSIMFitnessFunction.BLOCK_SIZE && i < image.getHeight(); i++) {
            for (int j = startingCol; j < startingCol + SSIMFitnessFunction.BLOCK_SIZE && j < image.getWidth(); j++) {
                Color color = reader.getColor(j, i);

                // Calculate the luminance using the weighted sum of the RGB components
                double intensity = RED_WEIGHT * color.getRed() +
                        GREEN_WEIGHT * color.getGreen() +
                        BLUE_WEIGHT * color.getBlue();
                sumIntensity += intensity;
            }
        }

        return sumIntensity / totalPixels;
    }

    private double calculateVarianceForBlock(Image image, int startingRow, int startingCol, double mean){
        double sumCovariance = 0;
        int totalPixels = SSIMFitnessFunction.BLOCK_SIZE * SSIMFitnessFunction.BLOCK_SIZE;
        PixelReader reader = image.getPixelReader();

        for (int i = startingRow; i < startingRow + SSIMFitnessFunction.BLOCK_SIZE && i < image.getHeight(); i++) {
            for (int j = startingCol; j < startingCol + SSIMFitnessFunction.BLOCK_SIZE && j < image.getWidth(); j++) {
                Color color = reader.getColor(j, i);

                // Calculate the luminance using the weighted sum of the RGB components
                double intensity = RED_WEIGHT * color.getRed() +
                        GREEN_WEIGHT * color.getGreen() +
                        BLUE_WEIGHT * color.getBlue();
                sumCovariance += (intensity - mean) * (intensity - mean);
            }
        }

        return sumCovariance / (totalPixels - 1);
    }

    private double calculateCovarianceForTwoBlocks(Image image1, Image image2, int startingRow, int startingCol, double mean1, double mean2){
        double sumCovariance = 0;
        int totalPixels = SSIMFitnessFunction.BLOCK_SIZE * SSIMFitnessFunction.BLOCK_SIZE;
        PixelReader reader1 = image1.getPixelReader();
        PixelReader reader2 = image2.getPixelReader();

        for (int i = startingRow; i < startingRow + SSIMFitnessFunction.BLOCK_SIZE && i < image1.getHeight(); i++) {
            for (int j = startingCol; j < startingCol + SSIMFitnessFunction.BLOCK_SIZE && j < image1.getWidth(); j++) {
                Color color1 = reader1.getColor(j, i);
                Color color2 = reader2.getColor(j, i);

                double intensity1 = RED_WEIGHT * color1.getRed() +
                        GREEN_WEIGHT * color1.getGreen() +
                        BLUE_WEIGHT * color1.getBlue();

                double intensity2 = RED_WEIGHT * color2.getRed() +
                        GREEN_WEIGHT * color2.getGreen() +
                        BLUE_WEIGHT * color2.getBlue();


                sumCovariance += (intensity1 - mean1) * (intensity2 - mean2);
            }
        }

        return sumCovariance / (totalPixels - 1);
    }

    private double calculateSSIM(double mean1, double mean2, double variance1, double variance2, double covariance){

        final double C1 = (k1 * L) * (k1 * L);
        final double C2 = (k2 * L) * (k2 * L);

        double numerator = (FACTOR * mean1 * mean2 + C1) * (FACTOR * covariance + C2);
        double denominator = (mean1 * mean1 + mean2 * mean2 + C1) * (variance1 + variance2 + C2);

        return numerator / denominator;
    }
}
