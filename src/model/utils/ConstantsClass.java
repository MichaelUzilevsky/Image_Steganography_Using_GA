package model.utils;

public class ConstantsClass {
    public static final int BITS_PER_BYTE = 8;
    public static final int BITS_REPLACED_PER_BYTE = 2;
    public static final int BYTES_IN_PIXEL = 3;
    public static final int ROUND_BITARRAY_TO = BITS_REPLACED_PER_BYTE * BYTES_IN_PIXEL;
    public static final int CHARS_PER_BYTE = BITS_PER_BYTE / BITS_REPLACED_PER_BYTE;
}
