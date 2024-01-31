package model.data_manegers;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.BitSet;

public class DataEmbedding {
    private final Image originalImage;
    private final int BITS_PER_PIXEL = 2;
    private final int BYTES_IN_COLOR = 3;

    public DataEmbedding(Image image) {
        this.originalImage = image;
    }

    public WritableImage embedData(BitSet data) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage writableImage = new WritableImage(originalImage.getPixelReader(), width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();


        for(int i = 0; i < data.length(); i+=6){
            // Extract bits for each color component from BitSet
            int redData = (data.get(i) ? 1 : 0) | (data.get(i + 1) ? 2 : 0);
            int greenData = (data.get(i + 2) ? 1 : 0) | (data.get(i + 3) ? 2 : 0);
            int blueData = (data.get(i + 4) ? 1 : 0) | (data.get(i + 5) ? 2 : 0);

            int row = i / width, col = i % width;
            Color color = originalImage.getPixelReader().getColor(row, col);

            // Embedding data into the color components
            int red = ((int) (color.getRed() * 255) & 0xFC) | redData;
            int green = ((int) (color.getGreen() * 255) & 0xFC) | greenData;
            int blue = ((int) (color.getBlue() * 255) & 0xFC) | blueData;

            Color newColor = Color.rgb(red, green, blue, 1.0);
            pixelWriter.setColor(row, col, newColor);
        }



        return writableImage;
    }
}
