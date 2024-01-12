import java.util.BitSet;
import java.util.TreeMap;

public class DataManipulation {
    private DataManipulation(){}

    /**
     * Modifies a bit array based on the given parameters of a steganographic algorithm.
     * It performs swapping and optional complementing of bits within the array.
     *
     * @param bitArray  bit array representing secret data.
     * @param ns        Number of swaps to perform.
     * @param off       Offset for starting the swapping in the second part of the array.
     * @param dd        Data direction (0 for left-to-right, 1 for right-to-left in the second part).
     * @param dp        Data polarity (00, 01, 10, 11) determining the complementing behavior.
     * @return          The modified boolean array representing rearranged secret data.
     */
    public static BitSet modifyBitArray(BitSet bitArray, int ns, int off, int dd, String dp) {
        int len = bitArray.length();
        int mid = len / 2;
        BitSet modifiedArray = (BitSet) bitArray.clone();

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
    private static int calculateSecondIndex(int mid, int off, int i, int dd, int len) {
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
    private static void swapBits(BitSet bitArray, int firstIndex, int secondIndex, String dp) {
        boolean firstBit = bitArray.get(firstIndex);
        boolean secondBit = bitArray.get(secondIndex);

        if (dp.charAt(0) == '0') {
            firstBit = !firstBit;
        }
        if (dp.charAt(1) == '0') {
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
    public static BitSet boolToBitSet(boolean[] bits, int offset, int length) {
        BitSet bitset = new BitSet(length - offset);
        for (int i = offset; i < length; i++)
            bitset.set(i - offset, bits[i]);

        return bitset;
    }


    public static void main(String[] args) {
        boolean[] boolArray = {true, false, true, true, true, false, true,
                            false, false, false, true, false, true, false};

        BitSet bitArray = boolToBitSet(boolArray, 0, boolArray.length);

        BitSet modifiedArray = modifyBitArray(bitArray, 4, 3, 1, "11");

        System.out.println("Original Array: " + StringToBitArray.StringRepresentation(bitArray, ""));
        System.out.println("Modified Array: " + StringToBitArray.StringRepresentation(modifiedArray, ""));
    }

}
