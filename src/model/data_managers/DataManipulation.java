package model.data_managers;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.Genes;

public class DataManipulation {
    private final BitArray data;

    public DataManipulation(BitArray data){
        this.data = data;
    }

    /**
     * Modifies a bit array based on the given parameters of a steganographic algorithm.
     * It performs swapping and optional complementing of bits within the array.
     *
     * @param chromosome representing the chromosome, witch based on his genes the data will change.
     * ns        Number of swaps to perform.
     * off       Offset for starting the swapping in the second part of the array.
     * dd        Data direction (0 for left-to-right, 1 for right-to-left in the second part).
     * dp        Data polarity (00, 01, 10, 11) determining the complementing behavior.
     * @return          The modified boolean array representing rearranged secret data.
     */
    public BitArray modifyBitArray(Chromosome chromosome) {

        int ns = chromosome.getGene(Genes.NS).toInt();
        int off = chromosome.getGene(Genes.OFF).toInt();
        int dd = chromosome.getGene(Genes.DD).toInt();
        int dp = chromosome.getGene(Genes.DP).toInt();

        int len = data.size();
        int mid = len / 2;
        BitArray modifiedArray = (BitArray) data.clone();

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
     * Swaps two bits in the provided boolean array and optionally complements them based on the dp parameter.
     *
     * @param bitArray     The array where swapping and complementing will occur.
     * @param firstIndex   Index of the first bit in the array to be swapped.
     * @param secondIndex  Index of the second bit in the array to be swapped.
     * @param dp           Data polarity parameter, controlling the complementing of swapped bits.
     */
    private void swapBits(BitArray bitArray, int firstIndex, int secondIndex, int dp) {
        String dpStr = Integer.toBinaryString(dp);
        boolean firstBit = bitArray.get(firstIndex);
        boolean secondBit = bitArray.get(secondIndex);

        if (dpStr.charAt(0) == '0') {
            firstBit = !firstBit;
        }
        if (dpStr.charAt(1) == '0') {
            secondBit = !secondBit;
        }

        bitArray.set(firstIndex, secondBit);
        bitArray.set(secondIndex, firstBit);
    }


    /**
     * Converts a boolean array into a BitSet
     * @param bits Boolean array to convert.
     * @param offset Array index to start reading from.
     * @param length Number of bits to convert.
     * @return bitset representation of the boolean[]
     */
    public static BitArray boolToBitSet(boolean[] bits, int offset, int length) {
        BitArray bitset = new BitArray(length - offset);
        for (int i = offset; i < length; i++)
            bitset.set(i - offset, bits[i]);

        return bitset;
    }


    public static void main(String[] args) {
        boolean[] boolArray = {false, false, true, false, true, false,
                true, false, true, false, true, true};


        BitArray bitArray = boolToBitSet(boolArray, 0, boolArray.length);

        DataManipulation dataManipulation = new DataManipulation(bitArray);

        Chromosome chromosome = new Chromosome(4,4);
        BitArray modifiedArray = dataManipulation.modifyBitArray(chromosome);

        System.out.println("Original Array: " + bitArray);
        System.out.println("Modified Array: " + modifiedArray);
    }

}
