package model.data_managers.image_metedate;

import model.data_managers.BitArray;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

import java.util.Map;

public class MetadataSerializer {
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

    public static ImageMetadata deserialize(BitArray bitArray, int imageWidth, int imageHeight){
        int offset = 0;
        int totalSize = bitArray.size();

        int dd_size = UtilsMethods.bitsNeeded(ConstantsClass.DATA_DIRECTION_SIZE);
        int dp_size = UtilsMethods.bitsNeeded(ConstantsClass.DATA_POLARITY_SIZE);
        int ns_off_size = (totalSize - dd_size - dp_size - 1) / 3;
        int data_size =  ns_off_size + 1;


        BitArray temp = bitArray.get(offset, data_size);
        int dataLength = temp.toInt();
        offset += data_size;

        temp = bitArray.get(offset, ns_off_size);
        int numberOfSwaps = temp.toInt();
        offset += ns_off_size;

        temp = bitArray.get(offset, ns_off_size);
        int offsetValue = temp.toInt();
        offset += ns_off_size;

         temp = bitArray.get(offset, dd_size);
        int dataDirection = temp.toInt();
        offset += dd_size;

        temp = bitArray.get(offset, dp_size);
        int dataPolarity = temp.toInt();

        return new ImageMetadata(dataLength, numberOfSwaps, offsetValue, dataDirection, dataPolarity, imageWidth, imageHeight);

    }

    // return the size of the bitarray
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
