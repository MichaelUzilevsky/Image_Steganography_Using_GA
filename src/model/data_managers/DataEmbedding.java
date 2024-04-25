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
        // Serialize metadata to BitArray
        BitArray metadataBitArray = MetadataSerializer.serialize(metadata);

        // Apply padding to metadata
        BitArray paddedMetadata = addPaddingToBitArray(metadataBitArray);

        // Apply padding to data
        BitArray paddedData = addPaddingToBitArray(data);

        // apply padding to signature
        StringParser parser = new StringParser(ConstantsClass.ENCODING_PASSKEY);
        BitArray temp = parser.convertToBitArray();
        BitArray paddedSignature = addPaddingToBitArray(temp);

        // Combine padded metadata and data and signature into a new BitArray
        BitArray combinedData = combineBitArrays(paddedMetadata, paddedData, paddedSignature);

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
                int[] colorData = new int[ConstantsClass.BYTES_IN_PIXEL];
                // getting the data from the bitarray for 1 pixel
                for (int colorIndex = 0; colorIndex < ConstantsClass.BYTES_IN_PIXEL; colorIndex++) {
                    for (int bitIndex = 0; bitIndex < ConstantsClass.BITS_REPLACED_PER_BYTE; bitIndex++) {
                        if (combinedData.get(dataIndex + colorIndex * ConstantsClass.BITS_REPLACED_PER_BYTE + bitIndex)) {
                            colorData[colorIndex] |= (1 << bitIndex);
                        }
                    }
                }

                // setting the color of the pixel of the image
                Color originalColor = originalImage.getPixelReader().getColor(pixelX, pixelY);
                int red = ((int) (originalColor.getRed() * 255) & (0xFF << ConstantsClass.BITS_REPLACED_PER_BYTE)) | colorData[0];
                int green = ((int) (originalColor.getGreen() * 255) & (0xFF << ConstantsClass.BITS_REPLACED_PER_BYTE)) | colorData[1];
                int blue = ((int) (originalColor.getBlue() * 255) & (0xFF << ConstantsClass.BITS_REPLACED_PER_BYTE)) | colorData[2];

                Color newColor = Color.rgb(red, green, blue, 1.0);
                pixelWriter.setColor(pixelX, pixelY, newColor);
            }
        }

        return writableImage;
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
