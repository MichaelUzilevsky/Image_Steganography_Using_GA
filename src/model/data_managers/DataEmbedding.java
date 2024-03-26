package model.data_managers;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import model.data_managers.image_metedate.ImageMetadata;
import model.data_managers.image_metedate.MetadataSerializer;
import model.utils.ConstantsClass;

/**
 * Manages the embedding of data into an image.
 * This class provides functionality to embed arbitrary data represented as a BitArray into an image,
 * by altering the least significant bits of the image pixels' color components.
 */
public class DataEmbedding {
    private final Image originalImage;

    /**
     * Constructs a new DataEmbedding instance for a given image.
     *
     * @param image The image into which data will be embedded.
     */
    public DataEmbedding(Image image) {
        this.originalImage = image;
    }

    /**
     * Embeds the provided data into the image.
     *
     * @param data The data to embed into the image, represented as a BitArray.
     * @return A new Image with the data embedded.
     */
    public WritableImage embedDataNotUsed(BitArray data) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage writableImage = new WritableImage(originalImage.getPixelReader(), width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // Calculate the number of pixels to embed data into
        int pixelsToEmbed = data.size() / (ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL);

        for(int pixelIndex = 0; pixelIndex < pixelsToEmbed; pixelIndex++) {
            int dataIndex = pixelIndex * ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL;

            int y = pixelIndex / width; // y-coordinate (rows)
            int x = pixelIndex % width; // x-coordinate (columns)


            if (x < width && y < height) {
                int redData = (data.get(dataIndex) ? 1 : 0) | (data.get(dataIndex + 1) ? 2 : 0);
                int greenData = (data.get(dataIndex + 2) ? 1 : 0) | (data.get(dataIndex + 3) ? 2 : 0);
                int blueData = (data.get(dataIndex + 4) ? 1 : 0) | (data.get(dataIndex + 5) ? 2 : 0);

                Color color = originalImage.getPixelReader().getColor(x, y);

                // Embedding data into the color components
                int red = ((int) (color.getRed() * 255) & 0xFC) | redData;
                int green = ((int) (color.getGreen() * 255) & 0xFC) | greenData;
                int blue = ((int) (color.getBlue() * 255) & 0xFC) | blueData;

                Color newColor = Color.rgb(red, green, blue, 1.0);
                pixelWriter.setColor(x, y, newColor);
            }
        }

        return writableImage;
    }

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
