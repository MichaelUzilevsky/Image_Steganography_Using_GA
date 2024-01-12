package model;

import java.util.BitSet;

public class StringToBitArray {
    private StringToBitArray() {}

    /**
     * Converts a string of text into a bitarray representing its binary form.
     * Each character in the text is converted to a sequence of 8 bits (1 byte),
     * resulting in a bitarray where each bit represents a part of a character in the text.
     *
     * @param text  The string to be converted into binary.
     * @return      A bitarray where each element represents a bit of the string's binary representation.
     */
    public static BitSet convertToBitArray(String text) {
        BitSet bits = new BitSet();
        int bitIndex = 0;
        for (char c : text.toCharArray()) {
            int value = c;
            for (int i = 0; i < 8; i++) {
                boolean bit = (value & 0x80) == 0x80; // Extract the most significant bit (MSB)
                bits.set(bitIndex++, bit);
                value <<= 1; // Left shift the value to get the next bit in the next iteration
            }
        }
        return bits;
    }


    /**
     * Formats the bitarray of bits into a readable string,
     * where each bit is represented as '1' or '0'. Bits are grouped in sets of 8
     * (representing a byte) and separated by tabs for better readability.
     *
     * @param bits  The bitarray of bits to be formatted.
     * @return      A StringBuilder containing the formatted string representation of the bit array.
     */
    public static StringBuilder StringRepresentation(BitSet bits, String betweenBytes){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < bits.length() ; i++) {
            if(i != 0 && i % 8 == 0)
                str.append(betweenBytes);

            str.append(bits.get(i) ? "1" : "0");
        }
        return str;
    }
}