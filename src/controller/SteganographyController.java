package controller;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import view.SteganographyUI;

import java.io.File;

public class SteganographyController {
    private final SteganographyUI view;
    //private final GeneticAlgorithm geneticAlgorithm;

    public SteganographyController(SteganographyUI view) {
        this.view = view;
        attachActionHandlers();
        //this.geneticAlgorithm = new GeneticAlgorithm();
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

        // update the view with the result

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
