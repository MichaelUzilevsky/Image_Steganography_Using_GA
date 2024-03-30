package model.data_managers;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

public class StringParser {
    private final String text;

    public StringParser(String text) {
        this.text = text;
    }

    /**
     * get the size of the bitarray, and round it to fit the pixels in the image;
     * @return the bitarray size
     */
    private int getBitArraySize() {
        return text.length() * ConstantsClass.BITS_PER_BYTE;
    }

    /**
     * Converts a string of text into a bitarray representing its binary form.
     * Each character in the text is converted to a sequence of 8 bits (1 byte),
     * resulting in a bitarray where each bit represents a part of a character in the text.
     *
     * @return      A bitarray where each element represents a bit of the string's binary representation.
     */
    public BitArray convertToBitArray() {
        BitArray bits = new BitArray(getBitArraySize());
        int bitIndex = 0;
        for (char c : text.toCharArray()) {
            int value = c;
            for (int i = 0; i < ConstantsClass.BITS_PER_BYTE; i++) {
                boolean bit = (value & 0x80) == 0x80; // Extract the most significant bit (MSB)
                bits.set(bitIndex++, bit);
                value <<= 1; // Left shift the value to get the next bit in the next iteration
            }
        }
        return bits;
    }

    public static void main(String[] args){
        StringParser parser = new StringParser("hello");
        BitArray bits = parser.convertToBitArray();
        System.out.println(bits);
        System.out.println(UtilsMethods.convertBitArrayToItsChars(bits));

    }
}