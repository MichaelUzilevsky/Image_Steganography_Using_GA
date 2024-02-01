package model.data_managers;

public class BitArray implements Cloneable{
    private byte[] bitArray;
    private final int size;

    /**
     * Constructs a BitArray.
     *
     * @param size The number of bits in the BitArray.
     */
    public BitArray(int size) {
        this.size = size;
        this.bitArray = new byte[(size + 8) / 8]; // Each byte stores 8 bits
    }

    /**
     * Sets the bit at the specified index.
     *
     * @param index The index of the bit.
     * @param value The value to set (true for 1, false for 0).
     */
    public void set(int index, boolean value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        int byteIndex = index / 8;
        int bitIndex = index % 8;
        if (value) {
            bitArray[byteIndex] |= (byte) (1 << bitIndex); // Set the bit to 1
        } else {
            bitArray[byteIndex] &= (byte) ~(1 << bitIndex); // Set the bit to 0
        }
    }

    /**
     * Sets the bits at the specified indexes from start index to start index + n -1.
     * n buts will be set.
     *
     * @param startIndex The index of the bit.
     * @param n the number of the bits forward to set
     * @param value The value to set (true for 1, false for 0).
     */
    public void set(int startIndex, int n, boolean value) {
        if (startIndex < 0 || startIndex >= size || startIndex + n - 1 >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + (startIndex + n) + "\nThe BitArray size is: " + size);
        }
        int byteIndex,  bitIndex;

        for (int i = startIndex; i < startIndex + n; i++) {
             byteIndex = i / 8;
             bitIndex = i % 8;
            if (value) {
                bitArray[byteIndex] |= (byte) (1 << bitIndex); // Set the bit to 1
            } else {
                bitArray[byteIndex] &= (byte) ~(1 << bitIndex); // Set the bit to 0
            }
        }

    }

    /**
     * Gets the value of the bit at the specified index.
     *
     * @param index The index of the bit.
     * @return The value of the bit (true for 1, false for 0).
     */
    public boolean get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        int byteIndex = index / 8;
        int bitIndex = index % 8;
        return (bitArray[byteIndex] & (1 << bitIndex)) != 0;
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
     * string representation of the bitarray
     * @return str representing the bitarray
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if(i != 0 && i % 8 == 0)
                str.append(" ");
           str.append(get(i)? "1": "0");
        }
        return str.toString();
    }

    /**
     * Creates and returns a copy (clone) of this BitArray.
     *<p></p>
     * The clone is a deep copy, meaning that it contains a copy of the internal
     * bit storage and modifications to the clone will not affect the original BitArray.
     *
     * @return A clone of this BitArray.
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
     * flips the bit at the specific index
     * @param index index in the bitArray
     */
    public void flip(int index) {
        set(index, !get(index));
    }

    /**
     * return the int representation of the bitArray
     * 1100 -> 12
     * @return the integer value of the bitArray
     */
    public int toInt() {
        int sum = 0, base = 1;
        for (int i = size() - 1; i >= 0; i--) {
            sum += (get(i)? 1 : 0) * base;
            base *= 2;
        }
        return sum;
    }

    public static void main(String[] args){
        BitArray bitArray1 = new BitArray(5);
        bitArray1.set(0,1, true);
        System.out.println(bitArray1.toInt());
        System.out.println(bitArray1);
    }
}

