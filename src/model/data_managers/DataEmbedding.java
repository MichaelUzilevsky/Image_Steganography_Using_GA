package model.data_managers;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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
    public WritableImage embedData(BitArray data) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage writableImage = new WritableImage(originalImage.getPixelReader(), width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // Calculate the number of pixels to embed data into
        int pixelsToEmbed = data.size() / (ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL);

        for(int pixelIndex = 0; pixelIndex < pixelsToEmbed; pixelIndex++) {
            int dataIndex = pixelIndex * ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL;

            int row = pixelIndex / width;
            int col = pixelIndex % width;


            if (row < height && col < width) {
                int redData = (data.get(dataIndex) ? 1 : 0) | (data.get(dataIndex + 1) ? 2 : 0);
                int greenData = (data.get(dataIndex + 2) ? 1 : 0) | (data.get(dataIndex + 3) ? 2 : 0);
                int blueData = (data.get(dataIndex + 4) ? 1 : 0) | (data.get(dataIndex + 5) ? 2 : 0);

                Color color = originalImage.getPixelReader().getColor(row, col);

                // Embedding data into the color components
                int red = ((int) (color.getRed() * 255) & 0xFC) | redData;
                int green = ((int) (color.getGreen() * 255) & 0xFC) | greenData;
                int blue = ((int) (color.getBlue() * 255) & 0xFC) | blueData;

                Color newColor = Color.rgb(red, green, blue, 1.0);
                pixelWriter.setColor(row, col, newColor);
            }
        }

        return writableImage;
    }
}
