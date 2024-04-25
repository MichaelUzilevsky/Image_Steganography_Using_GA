package model.data_managers;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.data_managers.image_metedate.ImageMetadata;
import model.data_managers.image_metedate.MetadataSerializer;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

/**
 * Handles the extraction of embedded data and metadata from an image that has been
 * used as a carrier in steganography. This class is capable of retrieving both
 * the hidden data and the metadata required for correctly interpreting the data
 * from an image's pixel values.
 */
public class DataExtractor {
    private final Image stegoImage;

    /**
     * Initializes a new DataExtractor with a specified steganographic image.
     *
     * @param stegoImage The image from which data and metadata are to be extracted.
     */
    public DataExtractor(Image stegoImage) {
        this.stegoImage = stegoImage;
    }

    /**
     * Extracts bits from the image based on a specified number of bits.
     * This method sequentially retrieves bits embedded in the least significant bits
     * of the image's color components.
     *
     * @param totalBitsToExtract The total number of bits to be extracted from the image.
     * @return A {@link BitArray} containing the extracted bits.
     */
    private BitArray extractBitsFromImage(int totalBitsToExtract) {
        int width = (int) stegoImage.getWidth();
        int height = (int) stegoImage.getHeight();
        BitArray extractedBits = new BitArray(totalBitsToExtract);

        int extractedBitCount = 0;
        for (int y = 0; y < height && extractedBitCount < totalBitsToExtract; y++) {
            for (int x = 0; x < width && extractedBitCount < totalBitsToExtract; x++) {
                Color color = stegoImage.getPixelReader().getColor(x, y);
                // Extracting bits from each color component, in the order red, green, blue
                int[] colors = {(int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255)};

                for (int colorIndex = 0; colorIndex < ConstantsClass.BYTES_IN_PIXEL; colorIndex++) {
                    for (int bitIndex = 0; bitIndex < ConstantsClass.BITS_REPLACED_PER_BYTE; bitIndex++) {
                        // Mask the LSBs of the color component to extract the bit
                        boolean bit = (colors[colorIndex] & (1 << bitIndex)) != 0;
                        if (extractedBitCount < totalBitsToExtract) {
                            extractedBits.set(extractedBitCount++, bit);
                        }
                    }
                }
            }
        }

        return extractedBits;
    }

    /**
     * Extracts and deserializes the metadata from the image.
     * The metadata size is determined based on the image dimensions and is
     * used to extract the corresponding bits from the image, which are then
     * deserialized to reconstruct the metadata.
     *
     * @return An {@link ImageMetadata} instance containing the extracted metadata.
     */
    public ImageMetadata extractMetadata() {
        int metadataSize = calculateMetadataSize(); // Calculate the size of metadata based on image dimensions
        BitArray metadataBits = extractBitsFromImage(metadataSize);
        return MetadataSerializer.deserialize(metadataBits, (int) stegoImage.getWidth(), (int) stegoImage.getHeight());
    }

    /**
     * Calculates the size of the metadata based on the dimensions of the image.
     *
     * @return The size of the metadata in bits.
     */
    private int calculateMetadataSize() {
        return ImageMetadata.getSizeInBits((int)stegoImage.getWidth(), (int)stegoImage.getHeight());
    }

    /**
     * Extracts the data portion from the image. This method calculates
     * the total number of bits to extract, which includes the metadata size, padding,
     * and the actual data length specified in the metadata. The method then retrieves
     * the data bits, starting immediately after the metadata and its padding.
     *
     * @param metadata The {@link ImageMetadata} instance containing metadata
     *                 information, such as the length of the data to be extracted.
     * @return A {@link BitArray} containing the extracted data.
     */
    public BitArray extractData(ImageMetadata metadata) {
        int metaDataSize = calculateMetadataSize();
        int metadataSizeWithPadding = metaDataSize + UtilsMethods.calculatePadding(metaDataSize);

        // Calculate the total bits to extract, which  come from metadata
        int totalBitsToExtract = calculateTotalBitsToExtract(metadata); // no signature
        int signatureSize = ConstantsClass.ENCODING_PASSKEY.length() * ConstantsClass.BITS_PER_BYTE;

        BitArray allBits = extractBitsFromImage(totalBitsToExtract + signatureSize);

        // Extract just the data, starting after the metadata and its padding
        int dataSize = metadata.getDataLength();  //the data size without padding
        int paddedDataSize = dataSize + UtilsMethods.calculatePadding(dataSize);

        // validate signature
        BitArray signature = new BitArray(signatureSize);

        for (int i = 0; i < signatureSize; i++) {
            boolean bit = allBits.get(metadataSizeWithPadding + paddedDataSize + i); // Start right after metadata and data and its padding
            signature.set(i, bit);
        }

        if (!UtilsMethods.convertBitArrayToItsChars(signature).equals(ConstantsClass.ENCODING_PASSKEY)){
            return null;
        }

        // get the data
        BitArray dataBits = new BitArray(dataSize);

        for (int i = 0; i < dataSize; i++) {
            boolean bit = allBits.get(metadataSizeWithPadding + i); // Start right after metadata and its padding
            dataBits.set(i, bit);
        }

        return dataBits;
    }

    /**
     * Calculates the total number of bits to extract from the steganographic image,
     * which includes the metadata, padding, and the actual data length.
     *
     * @param metadata The {@link ImageMetadata} used to determine the data length.
     * @return The total number of bits to extract, accounting for metadata, data,
     *         and their respective padding.
     */
    private int calculateTotalBitsToExtract(ImageMetadata metadata) {

        return calculateMetadataSize() +
                (ConstantsClass.ROUND_BITARRAY_TO - (calculateMetadataSize() % ConstantsClass.ROUND_BITARRAY_TO)) %
                ConstantsClass.ROUND_BITARRAY_TO +
                metadata.getDataLength() +
                (ConstantsClass.ROUND_BITARRAY_TO - (metadata.getDataLength() % ConstantsClass.ROUND_BITARRAY_TO)) %
                        ConstantsClass.ROUND_BITARRAY_TO;
    }
}