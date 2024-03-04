package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import model.genetic_algorithm.GeneticAlgorithm;
import view.SteganographyUI;
import javax.imageio.ImageIO;

import java.io.File;

public class SteganographyController {
    private final SteganographyUI view;

    public SteganographyController(SteganographyUI view) {
        this.view = view;
        attachActionHandlers();
    }

    public void attachActionHandlers() {

        // Set the action to be performed when the encode button is pressed.
        view.setOnEncodeAction(event -> encodeAction());

        // Set the action to be performed when the decode button is pressed.
        view.setOnDecodeAction(event -> decodeAction());

        // Set the action to be performed when the load image button is pressed.
        view.setOnLoadImageAction(event -> loadImageAction());
    }

    private void encodeAction() {
        // Get the secret message from the view.
        String secretMessage = view.getSecretMessage();

        // Encode the secret message using the model
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(view.getImage(), secretMessage);

        Image modifiedImage = geneticAlgorithm.run();

        saveImageToFile(modifiedImage);

        view.clearView();
    }

    private void saveImageToFile(Image image) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Encoded Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        // Prompt the user to determine the file save location
        File file = fileChooser.showSaveDialog(view.getPrimaryStage());

        if (file != null) {
            try {
                // Create a new WritableImage of the same width and height as the original Image
                WritableImage writableImage = new WritableImage(
                        (int) image.getWidth(),
                        (int) image.getHeight());

                // Get the PixelReader from the original Image
                PixelReader pixelReader = image.getPixelReader();

                // Get the PixelWriter from the WritableImage
                PixelWriter pixelWriter = writableImage.getPixelWriter();

                // Copy the pixels from the original Image to the WritableImage
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        pixelWriter.setArgb(x, y, pixelReader.getArgb(x, y));
                    }
                }

                // Use ImageIO to write the WritableImage to a file
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception here
            }
        }
    }

    private void decodeAction() {
        // Logic for decoding the secret message.

    }

    private void loadImageAction() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(view.getPrimaryStage());

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            view.displayImage(image);
        }
    }
}