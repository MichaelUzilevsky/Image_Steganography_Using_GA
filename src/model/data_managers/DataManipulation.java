package model.data_managers;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.Genes;
import model.utils.UtilsMethods;

/**
 * Facilitates the manipulation of data intended for steganography embedding
 * within an image. This class provides methods for modifying a {@link BitArray}
 * according to the specifications of a genetic algorithm chromosome. Modifications
 * include swapping bits within the array and optionally complementing them to
 * achieve a desired data arrangement for steganography.
 */
public class DataManipulation {
    private final BitArray data; // the data before the manipulation

    /**
     * Constructs a DataManipulation instance with the provided data.
     *
     * @param data The {@link BitArray} to be manipulated.
     */
    public DataManipulation(BitArray data){
        this.data = data;
    }

    /**
     * This method orchestrates the manipulation of a BitArray based on the chromosome’s defined genes.
     * It performs the manipulation by determining the
     *                                                  number of swaps,
     *                                                  the offset for swapping,
     *                                                  the direction of data manipulation,
     *                                                  and how the bits are complemented (data polarity).
     * The method then calls another function to execute the manipulation with these parameters.
     *
     * @param chromosome A Chromosome object representing the genetic sequence that dictates how the data manipulation
     *                   should be carried out.
     * @return A BitArray representing the data after it has been rearranged according to the chromosome's
     *         genetic instructions.
     */
    public BitArray modifyBitArray(Chromosome chromosome) {

        int size = UtilsMethods.numberOfSwapsForData(data.size());
        int ns = chromosome.getGene(Genes.NS).toInt() % size;
        int off = chromosome.getGene(Genes.OFF).toInt() % size;
        int dd = chromosome.getGene(Genes.DD).toInt();
        int dp = chromosome.getGene(Genes.DP).toInt();

        return modifyBitArray(ns, off, dd, dp);
    }

    /**
     * This function modifies the BitArray by swapping bits within the array.
     * Swaps are made between two segments of the array, and the bits may be complemented depending on the dp value.
     * This approach is used to rearrange the data in a manner that may enhance its steganographic concealment within
     * an image.
     *
     * @param ns The number of swaps to perform within the array.
     * @param off The offset at which swapping begins in the second half of the array.
     * @param dd The direction of data manipulation (0 for left-to-right, 1 for right-to-left).
     * @param dp The data polarity determining how bits are complemented during swapping.
     * @return A {@link BitArray} that has been rearranged according to the specified manipulation parameters.
     */
    public BitArray modifyBitArray(int ns, int off,  int dd,  int dp) {

        int len = data.size();
        int mid = len / 2;
        BitArray modifiedArray = data.clone();

        for (int i = 0; i < ns; i++) {
            int firstIndex = i % mid;
            int secondIndex = calculateSecondIndex(mid, off, i, dd, len);
            swapBits(modifiedArray, firstIndex, secondIndex, dp);
        }

        return modifiedArray;
    }

    /**
     * Calculates the index in the second part of the bit array for swapping.
     * Takes into account the offset, current iteration, and data direction.
     *
     * @param mid   Midpoint of the bit array, dividing it into two equal parts.
     * @param off   Offset for the starting location of swapping in the second part.
     * @param i     Current iteration of the swapping process.
     * @param dd    Data direction for swapping in the second part.
     * @param len   Total length of the bit array.
     * @return      The calculated index in the second part of the array for swapping.
     */
    private int calculateSecondIndex(int mid, int off, int i, int dd, int len) {
        if (dd == 0) {
            return (mid + off + i) % mid + mid;
        } else {
            return len - 1 - ((off + i) % mid);
        }
    }

    /**
     * Swaps two specified bits in a BitArray and optionally complements them based on the data polarity parameter.
     * This function is a key component of the data manipulation process,
     * ensuring that the bits are correctly rearranged and potentially obfuscated for steganographic purposes.
     *
     * @param bitArray     The array where swapping and complementing will occur.
     * @param firstIndex   Index of the first bit in the array to be swapped.
     * @param secondIndex  Index of the second bit in the array to be swapped.
     * @param dp           Data polarity parameter, controlling the complementing of swapped bits.
     */
    private void swapBits(BitArray bitArray, int firstIndex, int secondIndex, int dp) {

        boolean firstBit = bitArray.get(firstIndex);
        boolean secondBit = bitArray.get(secondIndex);

        // Check if the first bit of dp is set (dp & 2), which corresponds to the second bit from the right in binary
        if ((dp & 2) != 0) {
            firstBit = !firstBit; // Complement if the second bit of dp is 1
        }
        // Check if the second bit of dp is set (dp & 1), which corresponds to the first bit from the right in binary
        if ((dp & 1) != 0) {
            secondBit = !secondBit; // Complement if the first bit of dp is 1
        }

        bitArray.set(firstIndex, secondBit);
        bitArray.set(secondIndex, firstBit);
    }

    public static void main(String[] args) {

        StringParser stringParser = new StringParser("hello");
        BitArray bitArray = stringParser.convertToBitArray();
        DataManipulation dataManipulation = new DataManipulation(bitArray);

        Chromosome chromosome = new Chromosome(0, 15, 8, 1, 3);
        BitArray modifiedArray = dataManipulation.modifyBitArray(chromosome);

        DataManipulation dataManipulation1 = new DataManipulation(modifiedArray);
        Chromosome chromosome1 = new Chromosome(0, 15, 8, 1, 3);
        BitArray back = dataManipulation1.modifyBitArray(chromosome1);

        System.out.println("Original Array: " + bitArray);
        System.out.println("Modified Array: " + modifiedArray);
        System.out.println("Modified Back Array: " + back);
    }
}