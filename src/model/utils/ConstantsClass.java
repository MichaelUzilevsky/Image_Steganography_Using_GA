package model.utils;

public class ConstantsClass {
    // constants for data embedding
    public static final int BITS_PER_BYTE = 8;
    public static final int BITS_REPLACED_PER_BYTE = 3;
    public static final int BYTES_IN_PIXEL = 3;
    public static final int ROUND_BITARRAY_TO = BITS_REPLACED_PER_BYTE * BYTES_IN_PIXEL;


    // fixed sizes of genes
    public static final int POSSIBLE_COMBINATIONS_AMOUNT_FOR_FLEXIBLE_GENE = 24; // 4!
    public static final int FLEXIBLE_GENE_SIZE = 5; // 4! = 24 -> 11000 MAX 5 BITS
    public static final int DATA_DIRECTION_SIZE = 1; // 0 OR 1
    public static final int DATA_POLARITY_SIZE = 2; // 00 01 10 11
    public static String ENCODING_PASSKEY = "Encoded by Michael Uzilevsky";
}
