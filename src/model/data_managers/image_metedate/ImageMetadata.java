package model.data_managers.image_metedate;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.Genes;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

import java.util.*;

/**
 * Encapsulates metadata information about an image used for steganography. This
 * includes details required for embedding data into the image, such as data length,
 * number of swaps, offset, data direction, data polarity, and the image dimensions.
 */
public class ImageMetadata implements Iterable<Map.Entry<Integer, Integer>>{
    private final int dataLength;
    private final int numberOfSwaps;
    private final int offset;
    private final int dataDirection;
    private final int dataPolarity;
    private final int imageWidth;
    private final int imageHeight;


    /**
     * Constructs an ImageMetadata object with specified parameters.
     *
     * @param dataLength The length of the data to be embedded.
     * @param numberOfSwaps The number of swaps to perform on the data.
     * @param offset The offset for starting the swapping in the data array.
     * @param dataDirection The direction for data manipulation (0 for left-to-right, 1 for right-to-left).
     * @param dataPolarity The data polarity, determining bit complementing behavior.
     * @param imageWidth The width of the image in pixels.
     * @param imageHeight The height of the image in pixels.
     */
    public ImageMetadata(int dataLength, int numberOfSwaps, int offset, int dataDirection, int dataPolarity, int imageWidth, int imageHeight) {
        this.dataLength = dataLength;
        this.numberOfSwaps = numberOfSwaps;
        this.offset = offset;
        this.dataDirection = dataDirection;
        this.dataPolarity = dataPolarity;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    /**
     * Constructs an ImageMetadata object from a Chromosome and image dimensions.
     *
     * @param chromosome The Chromosome containing the genetic information for embedding.
     * @param dataLength The length of the data to be embedded.
     * @param imageWidth The width of the image in pixels.
     * @param imageHeight The height of the image in pixels.
     */
    public ImageMetadata(Chromosome chromosome, int dataLength, int imageWidth, int imageHeight  ){
        this.dataLength = dataLength;

        int size = UtilsMethods.numberOfSwapsForData(dataLength);

        this.numberOfSwaps = chromosome.getGene(Genes.NS).toInt() % size;
        this.offset =  chromosome.getGene(Genes.OFF).toInt() % size;
        this.dataDirection =  chromosome.getGene(Genes.DD).toInt();
        this.dataPolarity =  chromosome.getGene(Genes.DP).toInt();
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public int getDataLength() {
        return dataLength;
    }

    public int getNumberOfSwaps() {
        return numberOfSwaps;
    }

    public int getOffset() {
        return offset;
    }

    public int getDataDirection() {
        return dataDirection;
    }

    public int getDataPolarity() {
        return dataPolarity;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Calculates the total number of bits required to store this metadata based on
     * the image dimensions. This calculation is critical for both embedding and
     * extracting processes to correctly allocate or retrieve the metadata from
     * the steganographic image.
     *
     * @param imageWidth The width of the image in pixels.
     * @param imageHeight The height of the image in pixels.
     * @return The size of the metadata in bits.
     */
    public static int getSizeInBits(int imageWidth, int imageHeight){
        return UtilsMethods.bitsNeeded(UtilsMethods.maxDataSizeNoHeaderInBits(imageWidth, imageHeight)) +
                2 * UtilsMethods.bitsNeeded(UtilsMethods.maxNumberOfSwapsAndOffsetSize(imageWidth, imageHeight)) +
                UtilsMethods.bitsNeeded(ConstantsClass.DATA_POLARITY_SIZE) +
                UtilsMethods.bitsNeeded(ConstantsClass.DATA_DIRECTION_SIZE);
    }

    /**
     * Calculates the total number of bits required to store this instance's metadata.
     *
     * @return The size of the metadata in bits.
     */
    public int getSizeInBits(){
        return UtilsMethods.bitsNeeded(UtilsMethods.maxDataSizeNoHeaderInBits(imageWidth, imageHeight)) +
                2 * UtilsMethods.bitsNeeded(UtilsMethods.maxNumberOfSwapsAndOffsetSize(imageWidth, imageHeight)) +
                UtilsMethods.bitsNeeded(ConstantsClass.DATA_POLARITY_SIZE) +
                UtilsMethods.bitsNeeded(ConstantsClass.DATA_DIRECTION_SIZE);
    }

    /**
     * Provides an iterator over the metadata entries, facilitating the traversal
     * and manipulation of the metadata parameters. Each entry maps a parameter value
     * to its respective description or identifier.
     *
     * @return An iterator over the metadata entries.
     */
    @Override
    public Iterator<Map.Entry<Integer, Integer>> iterator() {
        List<Map.Entry<Integer, Integer>> parameters = Arrays.asList(
                new AbstractMap.SimpleEntry<>(dataLength, UtilsMethods.maxDataSizeNoHeaderInBits(imageWidth, imageHeight)),
                new AbstractMap.SimpleEntry<>(numberOfSwaps, UtilsMethods.maxNumberOfSwapsAndOffsetSize(imageWidth, imageHeight)),
                new AbstractMap.SimpleEntry<>(offset, UtilsMethods.maxNumberOfSwapsAndOffsetSize(imageWidth, imageHeight)),
                new AbstractMap.SimpleEntry<>(dataDirection, ConstantsClass.DATA_DIRECTION_SIZE),
                new AbstractMap.SimpleEntry<>(dataPolarity, ConstantsClass.DATA_POLARITY_SIZE)
        );
        return parameters.iterator();
    }
}