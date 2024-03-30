package model.data_managers;

import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

/**
 * Represents a binary array where each element can be either 0 or 1.
 * This class provides methods to manipulate bits within a byte array,
 * allowing for efficient storage and manipulation of binary data.
 */
public class BitArray implements Cloneable{
    private byte[] bitArray;
    private final int size;

    /**
     * Initializes a new BitArray of the specified size.
     *
     * @param size The size of the BitArray in bits.
     */
    public BitArray(int size) {
        this.size = size;
        this.bitArray = new byte[(size + ConstantsClass.BITS_PER_BYTE) / ConstantsClass.BITS_PER_BYTE];
    }

    /**
     * Sets the value of the bit at a specified index.
     *
     * @param index The index of the bit to set.
     * @param value The new value of the bit (true for 1, false for 0).
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public void set(int index, boolean value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        int byteIndex = index / ConstantsClass.BITS_PER_BYTE;
        int bitIndex = index % ConstantsClass.BITS_PER_BYTE;
        if (value) {
            bitArray[byteIndex] |= (byte) (1 << bitIndex); // Set the bit to 1
        } else {
            bitArray[byteIndex] &= (byte) ~(1 << bitIndex); // Set the bit to 0
        }
    }

    /**
     * Sets a range of bits to the specified value starting from a given index.
     *
     * @param startIndex The starting index where bits should be set.
     * @param n The number of bits to set.
     * @param value The value to set for the specified range of bits (true for 1, false for 0).
     * @throws IndexOutOfBoundsException if the range specified is out of bounds.
     */
    public void set(int startIndex, int n, boolean value) {
        if (startIndex < 0 || startIndex >= size || startIndex + n - 1 >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + (startIndex + n) + "\nThe BitArray size is: " + size);
        }
        int byteIndex,  bitIndex;

        for (int i = startIndex; i < startIndex + n; i++) {
             byteIndex = i / ConstantsClass.BITS_PER_BYTE;
             bitIndex = i % ConstantsClass.BITS_PER_BYTE;
            if (value) {
                bitArray[byteIndex] |= (byte) (1 << bitIndex); // Set the bit to 1
            } else {
                bitArray[byteIndex] &= (byte) ~(1 << bitIndex); // Set the bit to 0
            }
        }

    }

    /**
     * Sets a sequence of bits starting from a specified index using the values from another BitArray.
     *
     * @param startIndex The starting index in this BitArray.
     * @param values The BitArray containing the values to be copied.
     * @throws IndexOutOfBoundsException if copying would result in access of data outside array bounds.
     */
    public void set(int startIndex, BitArray values){
        if(startIndex + values.size > size)
            throw new IndexOutOfBoundsException("Index out of bounds: " + (startIndex + values.size()) + "\nThe BitArray size is: " + size);

        int valuesIndex = 0;
        for (int i = startIndex; i < startIndex + values.size; i++) {
            set(i, values.get(valuesIndex++));
        }
    }


    /**
     * Gets the value of a bit at a specified index.
     *
     * @param index The index of the bit to get.
     * @return The value of the bit at the specified index (true for 1, false for 0).
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public boolean get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        int byteIndex = index / ConstantsClass.BITS_PER_BYTE;
        int bitIndex = index % ConstantsClass.BITS_PER_BYTE;
        return (bitArray[byteIndex] & (1 << bitIndex)) != 0;
    }

    /**
     * Extracts a sequence of bits as a new BitArray.
     *
     * @param startingIndex The starting index of the sequence to extract.
     * @param length The number of bits in the sequence.
     * @return A new BitArray containing the specified sequence of bits.
     * @throws IndexOutOfBoundsException if the specified range is out of bounds.
     */
    public BitArray get(int startingIndex, int length){
        if (startingIndex < 0 || startingIndex >= size || startingIndex + length > size) {
            throw new IndexOutOfBoundsException("Invalid starting index or length");
        }

        BitArray result = new BitArray(length);
        for (int i = 0; i < length; i++) {
            boolean bitValue = get(startingIndex + i);
            result.set(i, bitValue);
        }
        return result;
    }

    /**
     * Returns the size of the BitArray.
     *
     * @return The number of bits in the BitArray.
     */
    public int size() {
        return size;
    }

    /**
     * Provides a string representation of the BitArray.
     *
     * @return A string showing the binary representation of the BitArray.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if(i != 0 && i % ConstantsClass.BITS_PER_BYTE == 0)
                str.append(" ");
           str.append(get(i)? "1": "0");
        }
        return str.toString();
    }

    /**
     * Creates and returns a copy of this BitArray.
     *
     * @return A clone of this BitArray instance.
     */
    @Override
    public BitArray clone() {
        try {
            BitArray cloned = (BitArray) super.clone();
            cloned.bitArray = bitArray.clone(); // Deep copy of the array
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can never happen
        }
    }

    /**
     * Flips the value of the bit at the specified index (0 becomes 1, and 1 becomes 0).
     *
     * @param index The index of the bit to flip.
     */
    public void flip(int index) {
        set(index, !get(index));
    }

    /**
     * Converts the BitArray to an integer value.
     *
     * @return The integer value represented by the BitArray.
     */
    public int toInt() {
        int sum = 0, base = 1;
        for (int i = size() - 1; i >= 0; i--) {
            sum += (get(i)? 1 : 0) * base;
            base *= 2;
        }
        return sum;
    }

    /**
     * Modifies the BitArray based on the integer value provided.
     *
     * @param number The integer value to set the BitArray to represent.
     * @throws IllegalArgumentException if the number cannot be represented by the size of the BitArray.
     */
    public void modifyBitArrayByNumber(int number) {
        // Check if the number can fit in the BitArray
        int bitsNeeded = UtilsMethods.bitsNeeded(number);
        if (bitsNeeded > size) {
            throw new IllegalArgumentException("Number is too large to insert into this BitArray.");
        }

        // Clear the BitArray before setting new values
        for (int i = 0; i < size; i++) {
            set(i, false);
        }


        for (int i = 0; i < size; i++) {
            boolean value = ((number >> (size - 1 - i)) & 1) == 1;
            set(i, value);
        }

    }

    public static void main(String[] args){
        BitArray bitArray1 = new BitArray(5);
        bitArray1.set(0,1, true);
        System.out.println(bitArray1.toInt());
        System.out.println(bitArray1);
        bitArray1.modifyBitArrayByNumber(12);
        System.out.println(bitArray1);
    }
}