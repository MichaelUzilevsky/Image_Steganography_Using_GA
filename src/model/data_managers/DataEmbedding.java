package model.data_managers;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import model.data_managers.image_metedate.ImageMetadata;
import model.data_managers.image_metedate.MetadataSerializer;
import model.utils.ConstantsClass;

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
    public WritableImage embedData(BitArray data,  ImageMetadata metadata) {
        // Serialize metadata to BitArray
        BitArray metadataBitArray = MetadataSerializer.serialize(metadata);

        // Calculate padding for the metadata BitArray
        int metadataPaddingSize =
                (ConstantsClass.ROUND_BITARRAY_TO - (metadataBitArray.size() % ConstantsClass.ROUND_BITARRAY_TO)) %
                        ConstantsClass.ROUND_BITARRAY_TO;

        // Apply padding to metadata
        BitArray paddedMetadata = new BitArray(metadataBitArray.size() + metadataPaddingSize);
        paddedMetadata.set(0, metadataBitArray);

        // Calculate padding for the data BitArray
        int dataPaddingSize =
                (ConstantsClass.ROUND_BITARRAY_TO - (data.size() % ConstantsClass.ROUND_BITARRAY_TO)) %
                        ConstantsClass.ROUND_BITARRAY_TO;

        // Apply padding to data
        BitArray paddedData = new BitArray(data.size() + dataPaddingSize);
        paddedData.set(0, data);

        // Combine padded metadata and data into a new BitArray
        BitArray combinedData = new BitArray(paddedMetadata.size() + paddedData.size());
        combinedData.set(0, paddedMetadata);
        combinedData.set(paddedMetadata.size(), paddedData);

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage writableImage = new WritableImage(originalImage.getPixelReader(), width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int bitsForOnePixel = ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL;
        int pixelsToEmbed = combinedData.size() / bitsForOnePixel;

        // Embed combinedData into the image
        for (int pixelIndex = 0; pixelIndex < pixelsToEmbed; pixelIndex++) {
            int dataIndex = pixelIndex * bitsForOnePixel;
            int y = pixelIndex / width;
            int x = pixelIndex % width;

            if (x < width && y < height) {
                int[] colorData = new int[ConstantsClass.BYTES_IN_PIXEL];
                for (int colorIndex = 0; colorIndex < ConstantsClass.BYTES_IN_PIXEL; colorIndex++) {
                    for (int bitIndex = 0; bitIndex < ConstantsClass.BITS_REPLACED_PER_BYTE; bitIndex++) {
                        if (combinedData.get(dataIndex + colorIndex * ConstantsClass.BITS_REPLACED_PER_BYTE + bitIndex)) {
                            colorData[colorIndex] |= (1 << bitIndex);
                        }
                    }
                }

                Color originalColor = originalImage.getPixelReader().getColor(x, y);
                int red = ((int) (originalColor.getRed() * 255) & (0xFF << ConstantsClass.BITS_REPLACED_PER_BYTE)) | colorData[0];
                int green = ((int) (originalColor.getGreen() * 255) & (0xFF << ConstantsClass.BITS_REPLACED_PER_BYTE)) | colorData[1];
                int blue = ((int) (originalColor.getBlue() * 255) & (0xFF << ConstantsClass.BITS_REPLACED_PER_BYTE)) | colorData[2];

                Color newColor = Color.rgb(red, green, blue, 1.0);
                pixelWriter.setColor(x, y, newColor);
            }
        }

        return writableImage;
    }
}
