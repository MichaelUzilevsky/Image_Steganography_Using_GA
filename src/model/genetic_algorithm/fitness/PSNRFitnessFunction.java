package model.genetic_algorithm.fitness;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import model.utils.ConstantsClass;

/**
 * Implements a fitness function based on the PSNR (Peak Signal-to-Noise Ratio)
 * for evaluating the quality of steganography in colored images.
 * This class calculates the PSNR between an original and a modified image, offering a
 * measure of the distortion introduced by embedding data within the image.
 */
public class PSNRFitnessFunction implements FitnessFunction {
    private static final double FACTOR = 20;
    private static final double MAX_INTENSITY = 1;

    /**
     * Calculates the fitness of an image modification based on the PSNR between
     * the original and modified images. The PSNR is derived from the Mean Squared Error (MSE)
     * between the two images, calculated separately for each of the RGB color channels.
     *
     * <p>The PSNR is calculated using the formula:
     * PSNR = 20 * log10(MAX_I / sqrt(MSE)),
     * where MAX_I is the maximum possible pixel intensity (1.0 for images in the range [0,1]),
     * and MSE is the mean squared error between the original and modified images.</p>
     *
     * <p>The MSE for each color channel (Red, Green, Blue) is calculated as:
     * MSE_channel = sum((I_original - I_modified)^2) / (width * height),
     * where I_original and I_modified are the pixel intensities of the original
     * and modified images, respectively. The overall MSE is the average of the MSEs
     * for the three channels.</p>
     *
     * @param originalImage The original, unmodified image.
     * @param modifiedImage The image after data has been embedded.
     * @return The PSNR value indicating the fitness of the modification. A higher PSNR
     *         indicates less distortion and a better fitness for steganography purposes.
     * @throws IllegalArgumentException if the images have different dimensions.
     */
    @Override
    public double calculateFitness(Image originalImage, Image modifiedImage) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        if (width != modifiedImage.getWidth() || height != modifiedImage.getHeight()) {
            throw new IllegalArgumentException("Images must have the same dimensions");
        }

        double mseR = 0, mseG = 0, mseB = 0;
        PixelReader originalReader = originalImage.getPixelReader();
        PixelReader modifiedReader = modifiedImage.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = originalReader.getColor(x, y);
                Color modifiedColor = modifiedReader.getColor(x, y);

                mseR += Math.pow(originalColor.getRed() - modifiedColor.getRed(), 2);
                mseG += Math.pow(originalColor.getGreen() - modifiedColor.getGreen(), 2);
                mseB += Math.pow(originalColor.getBlue() - modifiedColor.getBlue(), 2);
            }
        }

        double mse = (mseR + mseG + mseB) / ConstantsClass.BYTES_IN_PIXEL / (width * height);

        if (mse == 0) {
            return Double.POSITIVE_INFINITY;
        }

        return FACTOR * Math.log10(MAX_INTENSITY / Math.sqrt(mse));
    }
}
