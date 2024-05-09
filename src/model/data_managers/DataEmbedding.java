package model.data_managers;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import model.data_managers.image_metedate.ImageMetadata;
import model.data_managers.image_metedate.MetadataSerializer;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

/**
 * Manages the process of embedding data into an image. This class supports embedding arbitrary data,
 * represented as a {@link BitArray}, into an image by subtly altering the least significant bits (LSBs)
 * of the image pixels' color components, making the changes visually imperceptible.
 */
public class DataEmbedding {
    private final Image originalImage;

    /**
     * Initializes a new instance of DataEmbedding for a specific image.
     *
     * @param image The image into which data will be embedded.
     */
    public DataEmbedding(Image image) {
        this.originalImage = image;
    }

    /**
     * Embeds the provided data and metadata into the original image.
     * This method serializes the metadata, applies necessary padding, combines the metadata and data,
     * and then embeds the combined BitArray into the original image's pixels. The embedding modifies the LSBs
     * of each color component in each pixel to store the binary data, preserving the overall appearance of the image.
     *
     * @param data The data to embed into the image, represented as a {@link BitArray}.
     * @param metadata The metadata associated with the data, which is necessary for correctly extracting the data.
     * @return A new {@link WritableImage} with the data and metadata embedded within it.
     */
    public WritableImage embedData(BitArray data, ImageMetadata metadata) {
        // Prepare combined data for embedding
        BitArray combinedData = prepareDataForEmbedding(data, metadata);

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage writableImage = new WritableImage(originalImage.getPixelReader(), width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int bitsForOnePixel = ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL;
        int pixelsToEmbed = combinedData.size() / bitsForOnePixel;

        // Embed combinedData into the image
        for (int pixelIndex = 0; pixelIndex < pixelsToEmbed; pixelIndex++) {
            int dataIndex = pixelIndex * bitsForOnePixel;
            int pixelY = pixelIndex / width;
            int pixelX = pixelIndex % width;

            if (pixelX < width && pixelY < height) {
                int[] colorData = extractBitsForPixel(combinedData, dataIndex);

                // Update the pixel color with embedded bits
                updatePixelColor(pixelWriter, pixelX, pixelY, colorData);
            }
        }
        return writableImage;
    }

    /**
     * Prepares the data, metadata, and signature for embedding by serializing,
     * padding, and combining them into a single BitArray.
     *
     * @param data The data to be embedded.
     * @param metadata The metadata associated with the data.
     * @return A combined BitArray ready for embedding into the image.
     */
    private BitArray prepareDataForEmbedding(BitArray data, ImageMetadata metadata) {
        // Serialize metadata to BitArray
        BitArray metadataBitArray = MetadataSerializer.serialize(metadata);

        // Apply padding to metadata, data, and signature
        BitArray paddedMetadata = addPaddingToBitArray(metadataBitArray);
        BitArray paddedData = addPaddingToBitArray(data);
        BitArray paddedSignature = addPaddingToBitArray(new StringParser(ConstantsClass.ENCODING_PASSKEY).convertToBitArray());

        // Combine padded metadata, data, and signature into a new BitArray
        return combineBitArrays(paddedMetadata, paddedData, paddedSignature);
    }

    /**
     * Extracts bits for a pixel from the combined data.
     *
     * @param combinedData The combined BitArray of metadata, data, and signature.
     * @param dataIndex The starting index in the combined BitArray for the pixel.
     * @return An array of color data with the extracted bits embedded.
     */
    private int[] extractBitsForPixel(BitArray combinedData, int dataIndex) {
        int[] colorData = new int[ConstantsClass.BYTES_IN_PIXEL];
        // getting the data from the bitarray for 1 pixel
        for (int colorIndex = 0; colorIndex < ConstantsClass.BYTES_IN_PIXEL; colorIndex++) {
            for (int bitIndex = 0; bitIndex < ConstantsClass.BITS_REPLACED_PER_BYTE; bitIndex++) {
                if (combinedData.get(dataIndex + colorIndex * ConstantsClass.BITS_REPLACED_PER_BYTE + bitIndex)) {
                    colorData[colorIndex] |= (1 << bitIndex);
                }
            }
        }
        return colorData;
    }

    /**
     * Updates the color of a pixel in the image.
     *
     * @param pixelWriter The PixelWriter to update the pixel.
     * @param pixelX The X coordinate of the pixel.
     * @param pixelY The Y coordinate of the pixel.
     * @param colorData The array of color data with embedded bits.
     */
    private void updatePixelColor(PixelWriter pixelWriter, int pixelX, int pixelY, int[] colorData) {
        Color originalColor = originalImage.getPixelReader().getColor(pixelX, pixelY);
        // setting the color of the pixel of the image
        int red = embedBitsIntoColor((int) (originalColor.getRed() * 255), colorData[0]);
        int green = embedBitsIntoColor((int) (originalColor.getGreen() * 255), colorData[1]);
        int blue = embedBitsIntoColor((int) (originalColor.getBlue() * 255), colorData[2]);

        Color newColor = Color.rgb(red, green, blue, 1.0);
        pixelWriter.setColor(pixelX, pixelY, newColor);
    }

    /**
     * Embeds bits into a color component.
     *
     * @param originalColorComponent The original color component value.
     * @param bitsToEmbed The bits to embed into the color component.
     * @return The new color component value with the bits embedded.
     */
    private int embedBitsIntoColor(int originalColorComponent, int bitsToEmbed) {
        return (originalColorComponent & (0xFF << ConstantsClass.BITS_REPLACED_PER_BYTE)) | bitsToEmbed;
    }

    public BitArray addPaddingToBitArray(BitArray bitArray){
        int padding = UtilsMethods.calculatePadding(bitArray.size());
        BitArray temp = new BitArray(bitArray.size() + padding);
        temp.set(0, bitArray);
        return temp;
    }

    public BitArray combineBitArrays(BitArray... arrays){
        int sum = 0;
        for (BitArray bitArray : arrays){
            sum += bitArray.size();
        }
        BitArray total = new BitArray(sum);
        int offset = 0;
        for (BitArray bitArray : arrays){
            total.set(offset, bitArray);
            offset += bitArray.size();
        }
        return total;
    }
}
