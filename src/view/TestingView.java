package view;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.data_managers.*;
import model.data_managers.image_metedate.ImageMetadata;
import model.genetic_algorithm.population_structure.Chromosome;

public class TestingView extends Application {

    @Override
    public void start(Stage stage) {
        final int tests = 10000;
        int successCount = 0;

        for (int test = 0; test < tests; test++) {
            try {
                // Load the original image
                Image originalImage = new Image("file:/Users/michael/Downloads/mario.png");
                //
                //            // Create a writable image for modifications
                //            WritableImage modifiedImage = new WritableImage(
                //                    (int) originalImage.getWidth(),
                //                    (int) originalImage.getHeight());
                //            PixelWriter writer = modifiedImage.getPixelWriter();
                //
                //            // Copy original image to modified image
                //            for (int y = 0; y < originalImage.getHeight(); y++) {
                //                for (int x = 0; x < originalImage.getWidth(); x++) {
                //                    writer.setArgb(x, y, originalImage.getPixelReader().getArgb(x, y));
                //                }
                //            }
                //
                //            // Drawing the "X"
                //            for (int i = 0; i < modifiedImage.getWidth(); i++) {
                //                writer.setArgb(i, i, 0xFF000000);
                //                writer.setArgb(i, (int) modifiedImage.getWidth() - 1 - i, 0xFF000000);
                //            }
                //
                //            // Calculate the PSNR between the original and modified images
                //            PSNRFitnessFunction fitnessFunction = new PSNRFitnessFunction();
                //            double psnr = fitnessFunction.calculateFitness(originalImage, modifiedImage);
                //            System.out.println("PSNR: " + psnr + " dB");
                //
                //            // Display both images
                //            ImageView originalImageView = new ImageView(originalImage);
                //            ImageView modifiedImageView = new ImageView(modifiedImage);
                //            HBox root = new HBox(10, originalImageView, modifiedImageView); // 10 is the spacing between images
                //
                //            Scene scene = new Scene(root);
                //            stage.setTitle("Image Comparison");
                //            stage.setScene(scene);
                //            stage.show();

                String data = "Is It Working???";
                StringParser stringParser = new StringParser(data);
                BitArray bitArrayData = stringParser.convertToBitArray();
                //System.out.println(bitArrayData);

                DataManipulation dataManipulation = new DataManipulation(bitArrayData);
                Chromosome chromosome = new Chromosome(data.length() * 8);
                BitArray manipulated = dataManipulation.modifyBitArray(chromosome);
                //System.out.println(manipulated);


                ImageMetadata metadata = new ImageMetadata(chromosome, bitArrayData.size(),
                        (int) originalImage.getWidth(), (int) originalImage.getHeight());

                // Embed data into the image
                DataEmbedding dataEmbedding = new DataEmbedding(originalImage);
                Image stegoImage = dataEmbedding.embedData(manipulated, metadata);

                // Now extract the data
                DataExtractor dataExtractor = new DataExtractor(stegoImage);
                ImageMetadata extracted = dataExtractor.extractMetadata();
                BitArray finished = dataExtractor.extractData(extracted);
                //System.out.println(finished);

                DataManipulation dataManipulation1 = new DataManipulation(finished);
                int adjustedDp = adjustDpForReversal(extracted.getDataPolarity());


                BitArray newOne = dataManipulation1.modifyBitArray(extracted.getNumberOfSwaps(),
                        extracted.getOffset(),
                        extracted.getDataDirection(),
                        adjustedDp);

                //System.out.println(newOne);
                //.out.println(extracted.getDataPolarity());
                if (areEqual(newOne, bitArrayData)) {
                    successCount++;
                }

                //System.out.println(areEqual(newOne, bitArrayData));

            } catch (Exception e) {
                System.err.println("Error in application: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Success rate: " + successCount + "/" + tests);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static boolean areEqual(BitArray bitArray1, BitArray bitArray2) {
        // Check if the sizes are different
        if (bitArray1.size() != bitArray2.size()) {
            return false;
        }

        // Compare bits at each index
        for (int i = 0; i < bitArray1.size(); i++) {
            if (bitArray1.get(i) != bitArray2.get(i)) {
                return false; // Found a mismatch
            }
        }

        // All bits are equal
        return true;
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
