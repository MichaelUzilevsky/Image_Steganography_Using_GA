package controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import model.data_managers.BitArray;
import model.data_managers.DataExtractor;
import model.data_managers.DataManipulation;
import model.data_managers.image_metedate.ImageMetadata;
import model.genetic_algorithm.GeneticAlgorithm;
import model.utils.UtilsMethods;
import view.DynamicGraph;
import view.SteganographyUI;
import javax.imageio.ImageIO;
import javafx.scene.control.Alert.AlertType;
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

        if (!testMessageSize(secretMessage)){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Message Size Too Large");
            alert.setContentText("The secret message is too large to be encoded in the selected image." +
                    "\nThe max size for this image is: " +
                    UtilsMethods.secretMessageMaxLength((int)view.getImage().getWidth(),
                    (int)view.getImage().getHeight())+ " chars");

            alert.showAndWait();
            return;
        }

        // Initialize the Dynamic Graph
        DynamicGraph.initialize();

        new Thread(() -> {
            // Encode the secret message using the model
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(view.getImage(), secretMessage);

            // get the modified image, after the genetic algorithm
            Image modifiedImage = geneticAlgorithm.run();

            Platform.runLater(() -> {
                saveImageToFile(modifiedImage);
                view.clearView();
            });
        }).start();
        
    }


    private void decodeAction() {

        Image stegoImage = view.getImage();

        DataExtractor dataExtractor = new DataExtractor(stegoImage);

        ImageMetadata extracted = dataExtractor.extractMetadata();

        BitArray extractData = dataExtractor.extractData(extracted);

        DataManipulation dataManipulation = new DataManipulation(extractData);

        int adjustedDp = adjustDpForReversal(extracted.getDataPolarity());


        BitArray originalMessage = dataManipulation.modifyBitArray(extracted.getNumberOfSwaps(),
                extracted.getOffset(),
                extracted.getDataDirection(),
                adjustedDp);

        String decodedText = UtilsMethods.convertBitArrayToItsChars(originalMessage);
        view.setText(decodedText);
    }
    private void loadImageAction() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(view.getPrimaryStage());

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            view.displayImage(image);
        }
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
            }
        }
    }

    private boolean testMessageSize(String message) {
        return message.length() <= UtilsMethods.secretMessageMaxLength((int)view.getImage().getWidth(),
                                                                        (int)view.getImage().getHeight());
    }

    private int adjustDpForReversal(int dp) {
        // If dp is 1 or 2, subtract it by 3 to reverse the complementing operation
        if (dp == 1 || dp == 2) {
            return 3 - dp;
        }
        // If dp is 0 or 3, leave it as it is
        return dp;
    }
}