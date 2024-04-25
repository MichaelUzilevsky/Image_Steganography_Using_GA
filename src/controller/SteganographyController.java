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

/**
 * Controls interactions between the view and the model in the Steganography application.
 * This controller handles actions for encoding, decoding, and loading images.
 */
public class SteganographyController {
    private final SteganographyUI view;

    /**
     * Initializes the controller with the application's UI view.
     *
     * @param view The SteganographyUI view instance.
     */
    public SteganographyController(SteganographyUI view) {
        this.view = view;
        attachActionHandlers();
    }

    /**
     * Attaches event handlers to UI elements for encoding, decoding, and image loading.
     */
    public void attachActionHandlers() {

        // Set the action to be performed when the encode button is pressed.
        view.setOnEncodeAction(event -> encodeAction());

        // Set the action to be performed when the decode button is pressed.
        view.setOnDecodeAction(event -> decodeAction());

        // Set the action to be performed when the load image button is pressed.
        view.setOnLoadImageAction(event -> loadImageAction());
    }

    /**
     * Encodes a secret message into an image using a genetic algorithm.
     * This method also initializes and updates the dynamic graph with the fitness score of the best chromosome.
     */
    private void encodeAction() {
        // Get the secret message from the view.
        String secretMessage = view.getSecretMessage();

        if(nullImage()){
            loadAlertMessage("Image is not included", "please add an image to proceed");
            return;
        }

        if(secretMessage.isEmpty()){
            loadAlertMessage("Message is not included", "please add a Message to proceed");
            return;
        }

        if (!testMessageSize(secretMessage)){
            loadAlertMessage("Message Size Too Large",
                    "The secret message is too large to be encoded in the selected image." +
                    "\nThe max size for this image is: " +
                    UtilsMethods.secretMessageMaxLength((int)view.getImage().getWidth(),
                                                        (int)view.getImage().getHeight())+ " chars");
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
                DynamicGraph.closeGraph();
            });
        }).start();
        
    }

    /**
     * Decodes a secret message from an image. This involves extracting embedded data,
     * reversing any manipulations, and converting the bit array back to text.
     */
    private void decodeAction() {
        if(nullImage()){
            loadAlertMessage("Image is not included", "please add image to proceed");
            return;
        }

        Image stegoImage = view.getImage();

        DataExtractor dataExtractor = new DataExtractor(stegoImage);

        ImageMetadata extracted = dataExtractor.extractMetadata();

        BitArray extractData = dataExtractor.extractData(extracted);

        if (extractData == null){
            loadAlertMessage("Image was not encoded by this algorithm", "Please try other image");
            view.clearView();
            return;
        }

        DataManipulation dataManipulation = new DataManipulation(extractData);

        int adjustedDp = adjustDpForReversal(extracted.getDataPolarity());


        BitArray originalMessage = dataManipulation.modifyBitArray(extracted.getNumberOfSwaps(),
                extracted.getOffset(),
                extracted.getDataDirection(),
                adjustedDp);

        String decodedText = UtilsMethods.convertBitArrayToItsChars(originalMessage);
        view.setText(decodedText);
    }

    /**
     * Allows the user to select an image file to load into the application.
     */
    private void loadImageAction() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(view.getPrimaryStage());

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            view.displayImage(image);
        }
    }

    /**
     * Saves the encoded image to a file chosen by the user.
     *
     * @param image The image to be saved.
     */
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

    /**
     * Checks if the secret message size is appropriate for the loaded image.
     *
     * @param message The secret message to encode.
     * @return true if the message can be encoded in the image, false otherwise.
     */
    private boolean testMessageSize(String message) {
        return message.length() <= UtilsMethods.secretMessageMaxLength((int)view.getImage().getWidth(),
                                                                        (int)view.getImage().getHeight());
    }

    /**
     * Checks if an image has been loaded into the application.
     *
     * @return true if an image is loaded, false otherwise.
     */
    private boolean nullImage(){
        return view.getImage() == null;
    }

    /**
     * Displays an alert message to the user.
     *
     * @param headerText The header text of the alert.
     * @param contentText The content text of the alert.
     */
    private void loadAlertMessage(String headerText, String contentText){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Adjusts the data polarity (dp) parameter for the reverse operation during decoding.
     *
     * @param dp The data polarity parameter to be adjusted.
     * @return The adjusted data polarity parameter.
     */
    private int adjustDpForReversal(int dp) {
        // If dp is 1 or 2, subtract it by 3 to reverse the complementing operation
        if (dp == 1 || dp == 2) {
            return 3 - dp;
        }
        // If dp is 0 or 3, leave it as it is
        return dp;
    }
}