package model.utils;

public class BitsUtils {

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

    public static void main(String[] args) {
        int testNumber = 12345;
        System.out.println("Number of bits needed: " + bitsNeeded(testNumber));
    }
}
