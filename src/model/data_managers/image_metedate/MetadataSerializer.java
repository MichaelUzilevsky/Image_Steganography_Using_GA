package model.data_managers.image_metedate;

import model.data_managers.BitArray;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

import java.util.Map;

/**
 * Provides functionality for serializing and deserializing {@link ImageMetadata}
 * into a {@link BitArray}. This allows metadata to be embedded into an image for
 * steganographic purposes and later extracted for data retrieval.
 */
public class MetadataSerializer {

    /**
     * Serializes the given {@link ImageMetadata} into a {@link BitArray}.
     *
     * @param metadata The image metadata to be serialized.
     * @return A {@link BitArray} containing the serialized metadata.
     */
    public static BitArray serialize(ImageMetadata metadata){
        BitArray bitArray = new BitArray(metadata.getSizeInBits());

        int offset = 0;

        for (Map.Entry<Integer, Integer> entry : metadata){
            int len = UtilsMethods.bitsNeeded(entry.getValue());
            insetToBitArray(entry.getKey(), offset, len, bitArray);
            offset += len;
        }
        return bitArray;
    }

    /**
     * Deserializes a {@link BitArray} back into an {@link ImageMetadata} object.
     *
     * @param bitArray The bit array containing serialized image metadata.
     * @param imageWidth The width of the image for which the metadata is intended.
     * @param imageHeight The height of the image for which the metadata is intended.
     * @return An {@link ImageMetadata} object constructed from the provided bit array.
     */
    public static ImageMetadata deserialize(BitArray bitArray, int imageWidth, int imageHeight){
        int offset = 0;
        int totalSize = bitArray.size();

        int dd_size = UtilsMethods.bitsNeeded(ConstantsClass.DATA_DIRECTION_SIZE);
        int dp_size = UtilsMethods.bitsNeeded(ConstantsClass.DATA_POLARITY_SIZE);
        int ns_off_size = (totalSize - dd_size - dp_size - 1) / 3;
        int data_size =  ns_off_size + 1;

        int dataLength = retrieveFromBitArray(bitArray, offset, data_size);
        offset += data_size;

        int numberOfSwaps = retrieveFromBitArray(bitArray, offset, ns_off_size);
        offset += ns_off_size;

        int offsetValue = retrieveFromBitArray(bitArray, offset, ns_off_size);
        offset += ns_off_size;

        int dataDirection = retrieveFromBitArray(bitArray, offset, dd_size);
        offset += dd_size;

        int dataPolarity = retrieveFromBitArray(bitArray, offset, dp_size);

        return new ImageMetadata(dataLength, numberOfSwaps, offsetValue, dataDirection, dataPolarity, imageWidth, imageHeight);

    }

    /**
     * returns the int representation of the value in the bitarray in a range
     * @param bitArray given bitarray
     * @param startingPos starting pos to read from
     * @param size size of the data to read
     * @return the int representation of the value between this range
     */
    private static int retrieveFromBitArray(BitArray bitArray, int startingPos, int size){
        BitArray temp = bitArray.get(startingPos, size);
        return temp.toInt();
    }

    /**
     * Inserts a value into the provided {@link BitArray} at the specified starting index.
     *
     * @param valueToInsert The integer value to be inserted into the bit array.
     * @param startingIndex The starting index in the bit array where the value begins.
     * @param len The length in bits of the value to insert.
     * @param bitArray The {@link BitArray} into which the value is inserted.
     */
    private static void insetToBitArray(int valueToInsert, int startingIndex, int len, BitArray bitArray){
        BitArray temp;
        temp = new BitArray(UtilsMethods.bitsNeeded(valueToInsert));
        temp.modifyBitArrayByNumber(valueToInsert);
        bitArray.set(startingIndex + len - temp.size(), temp);
    }

    public static void main(String[] args) {
        // Create an ImageMetadata object with some sample values
        ImageMetadata originalMetadata = new ImageMetadata(4, 3, 15, 1, 0, 16, 16);

        // Serialize the ImageMetadata object to a BitArray
        BitArray serializedData = MetadataSerializer.serialize(originalMetadata);

        // Deserialize the BitArray back into an ImageMetadata object
        ImageMetadata deserializedMetadata = MetadataSerializer.deserialize(serializedData, 16, 16);

        // Print original and deserialized metadata for comparison
        System.out.println("Original Metadata:");
        printMetadata(originalMetadata);

        System.out.println("\nDeserialized Metadata:");
        printMetadata(deserializedMetadata);
    }

    /**
     * Prints the metadata properties to the console for comparison or debugging purposes.
     *
     * @param metadata The {@link ImageMetadata} object whose properties are to be printed.
     */
    private static void printMetadata(ImageMetadata metadata) {
        System.out.println("Data Length: " + metadata.getDataLength());
        System.out.println("Number of Swaps: " + metadata.getNumberOfSwaps());
        System.out.println("Offset: " + metadata.getOffset());
        System.out.println("Data Direction: " + metadata.getDataDirection());
        System.out.println("Data Polarity: " + metadata.getDataPolarity());
        System.out.println("Image Width: " + metadata.getImageWidth());
        System.out.println("Image Height: " + metadata.getImageHeight());
    }
}
