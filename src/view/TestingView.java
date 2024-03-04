package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.genetic_algorithm.fitness.PSNRFitnessFunction;

public class TestingView extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Load the original image
            Image originalImage = new Image("file:/Users/michael/Downloads/mario.png");

            // Create a writable image for modifications
            WritableImage modifiedImage = new WritableImage(
                    (int) originalImage.getWidth(),
                    (int) originalImage.getHeight());
            PixelWriter writer = modifiedImage.getPixelWriter();

            // Copy original image to modified image
            for (int y = 0; y < originalImage.getHeight(); y++) {
                for (int x = 0; x < originalImage.getWidth(); x++) {
                    writer.setArgb(x, y, originalImage.getPixelReader().getArgb(x, y));
                }
            }

            // Drawing the "X"
            for (int i = 0; i < modifiedImage.getWidth(); i++) {
                writer.setArgb(i, i, 0xFF000000);
                writer.setArgb(i, (int) modifiedImage.getWidth() - 1 - i, 0xFF000000);
            }

            // Calculate the PSNR between the original and modified images
            PSNRFitnessFunction fitnessFunction = new PSNRFitnessFunction();
            double psnr = fitnessFunction.calculateFitness(originalImage, modifiedImage);
            System.out.println("PSNR: " + psnr + " dB");

            // Display both images
            ImageView originalImageView = new ImageView(originalImage);
            ImageView modifiedImageView = new ImageView(modifiedImage);
            HBox root = new HBox(10, originalImageView, modifiedImageView); // 10 is the spacing between images

            Scene scene = new Scene(root);
            stage.setTitle("Image Comparison");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error in application: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
