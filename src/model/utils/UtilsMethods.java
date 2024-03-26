package model.utils;


import model.data_managers.BitArray;
import model.data_managers.image_metedate.ImageMetadata;

import java.lang.reflect.Array;

public class UtilsMethods {

    /**
     * Calculates the number of bits needed to represent a given integer.
     *
     * @param number The integer to evaluate.
     * @return The number of bits needed to represent the number.
     */
    public static int bitsNeeded(int number) {
        // Special case for zero, as log(0) is undefined.
        if (number == 0) {
            return 1;
        }
        // For negative numbers, convert to positive equivalent due to two's complement.
        if (number < 0) {
            // Edge case for Integer.MIN_VALUE
            if (number == Integer.MIN_VALUE) {
                return 32;
            }
            number = -number;
        }
        // Calculate the number of bits (log2(number) + 1).
        return (int) (Math.log(number) / Math.log(2)) + 1;
    }

    // number not bytes needed to store it
    public static int maxDataSizeNoHeaderInBits(int imageWidth, int imageHeight){
        return (ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL * imageHeight * imageWidth);
    }
    // number not bytes needed to store it
    public static int maxNumberOfSwapsAndOffsetSize(int imageWidth, int imageHeight){
        return (ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL * imageHeight * imageWidth) / 2;
    }

    public static int numberOfSwapsForData(int bitDataLength){
        return bitDataLength / 2;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] combineArrays(Class<T> clazz, T[] array1, T[] array2) {
        T[] result = (T[]) Array.newInstance(clazz, array1.length + array2.length);
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    public static int secretMessageMaxLength(int imageWidth, int imageHeight){
        return (maxDataSizeNoHeaderInBits(imageWidth, imageHeight) - ImageMetadata.getSizeInBits(imageWidth, imageHeight))
                / ConstantsClass.BITS_PER_BYTE;
    }

    public static String convertBitArrayToItsChars(BitArray bitArray){
        StringBuilder text = new StringBuilder();
        int size = bitArray.size();

        // Ensure the BitArray length is a multiple of BITS_PER_BYTE
        if (size % ConstantsClass.BITS_PER_BYTE != 0) {
            throw new IllegalArgumentException("BitArray size must be a multiple of " + ConstantsClass.BITS_PER_BYTE);
        }

        for (int bitIndex = 0; bitIndex < size; bitIndex += ConstantsClass.BITS_PER_BYTE) {
            int value = 0;
            for (int i = 0; i < ConstantsClass.BITS_PER_BYTE; i++) {
                value <<= 1;
                if (bitArray.get(bitIndex + i)) {
                    value |= 1;
                }
            }
            char c = (char) value;
            text.append(c);
        }
        return text.toString();
    }
}
