package model.utils;

import model.data_managers.BitArray;
import model.data_managers.image_metedate.ImageMetadata;

import java.lang.reflect.Array;

/**
 * Provides utility methods for various operations required in the context of
 * steganography and image processing. This includes methods for calculating bit requirements,
 * combining arrays, and converting between textual and bit representations.
 */
public class UtilsMethods {

    /**
     * Calculates the minimum number of bits required to represent a given integer value.
     * Handles both positive and negative integers correctly by taking into account
     * two's complement representation for negative numbers.
     *
     * @param number The integer whose bit requirement is to be calculated.
     * @return The minimum number of bits required to represent the given integer.
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

    /**
     * Calculates the maximum amount of data (in bits) that can be stored in an image,
     * excluding any header or metadata. This calculation is based on the image dimensions
     * and the bits per byte that can be replaced in each pixel's color channels.
     *
     * @param imageWidth  The width of the image in pixels.
     * @param imageHeight The height of the image in pixels.
     * @return The maximum number of bits that can be stored in the image without a header.
     */
    public static int maxDataSizeNoHeaderInBits(int imageWidth, int imageHeight){
        return (ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL * imageHeight * imageWidth);
    }

    /**
     * Calculates the maximum number of swap operations and offset size for steganography
     * based on the image dimensions. This helps in determining the range of possible
     * permutations for hiding data within an image.
     *
     * @param imageWidth  The width of the image in pixels.
     * @param imageHeight The height of the image in pixels.
     * @return The maximum number of swaps and offset size based on the image dimensions.
     */
    public static int maxNumberOfSwapsAndOffsetSize(int imageWidth, int imageHeight){
        return (ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL * imageHeight * imageWidth) / 2;
    }

    /**
     * Determines the number of swap operations that can be performed on a given amount of data.
     * This is used in the process of data manipulation for steganography, where bits of data
     * are swapped to encode secret messages.
     *
     * @param bitDataLength The length of the data in bits.
     * @return The number of swaps that can be performed on the data.
     */
    public static int numberOfSwapsForData(int bitDataLength){
        return bitDataLength / 2;
    }

    /**
     * Combines two arrays of the same type into a single array. This method is generic and
     * can work with any object type, provided that both arrays are of the same type.
     *
     * @param clazz  The class type of the arrays.
     * @param array1 The first array to combine.
     * @param array2 The second array to combine.
     * @return A new array containing all elements from both array1 and array2.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] combineArrays(Class<T> clazz, T[] array1, T[] array2) {
        T[] result = (T[]) Array.newInstance(clazz, array1.length + array2.length);
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    /**
     * Calculates the maximum length of a secret message that can be encoded into an image,
     * taking into account the image dimensions and the space required for metadata.
     *
     * @param imageWidth  The width of the image in pixels.
     * @param imageHeight The height of the image in pixels.
     * @return The maximum length of the secret message in characters.
     */
    public static int secretMessageMaxLength(int imageWidth, int imageHeight){
        return (maxDataSizeNoHeaderInBits(imageWidth, imageHeight) - ImageMetadata.getSizeInBits(imageWidth, imageHeight))
                / ConstantsClass.BITS_PER_BYTE;
    }

    /**
     * Converts a {@link BitArray} into its corresponding textual representation by interpreting
     * every 8 bits as a character. This method is useful for decoding messages hidden within images.
     *
     * @param bitArray The BitArray to convert to text.
     * @return The textual representation of the BitArray.
     * @throws IllegalArgumentException If the BitArray size is not a multiple of 8.
     */
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
