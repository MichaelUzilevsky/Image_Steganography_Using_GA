package model.data_managers.image_metedate;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.Genes;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

import java.util.*;

public class ImageMetadata implements Iterable<Map.Entry<Integer, Integer>>{
    private final int dataLength;
    private final int numberOfSwaps;
    private final int offset;
    private final int dataDirection;
    private final int dataPolarity;
    private final int imageWidth;
    private final int imageHeight;


    public ImageMetadata(int dataLength, int numberOfSwaps, int offset, int dataDirection, int dataPolarity, int imageWidth, int imageHeight) {
        this.dataLength = dataLength;
        this.numberOfSwaps = numberOfSwaps;
        this.offset = offset;
        this.dataDirection = dataDirection;
        this.dataPolarity = dataPolarity;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

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

    // how many bits needed to store this number
    public static int getSizeInBits(int imageWidth, int imageHeight){
        return UtilsMethods.bitsNeeded(UtilsMethods.maxDataSizeNoHeaderInBits(imageWidth, imageHeight)) +
                2 * UtilsMethods.bitsNeeded(UtilsMethods.maxNumberOfSwapsAndOffsetSize(imageWidth, imageHeight)) +
                UtilsMethods.bitsNeeded(ConstantsClass.DATA_POLARITY_SIZE) +
                UtilsMethods.bitsNeeded(ConstantsClass.DATA_DIRECTION_SIZE);
    }

    public int getSizeInBits(){
        return UtilsMethods.bitsNeeded(UtilsMethods.maxDataSizeNoHeaderInBits(imageWidth, imageHeight)) +
                2 * UtilsMethods.bitsNeeded(UtilsMethods.maxNumberOfSwapsAndOffsetSize(imageWidth, imageHeight)) +
                UtilsMethods.bitsNeeded(ConstantsClass.DATA_POLARITY_SIZE) +
                UtilsMethods.bitsNeeded(ConstantsClass.DATA_DIRECTION_SIZE);
    }

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
