package view;

import controller.SteganographyController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

public class SteganographyUI extends Application{
    private SteganographyController controller;
    private TextArea secretMessageArea;
    private ImageView imageView;
    private Consumer<ActionEvent> onEncodeAction;
    private Consumer<ActionEvent> onDecodeAction;
    private Consumer<ActionEvent> onLoadImageAction;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {

        this.primaryStage = stage;
        this.controller = new SteganographyController(this);

        controller.attachActionHandlers();

        stage.setTitle("Image Steganography");

        // Main layout container
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        // Initialize UI components
        secretMessageArea = new TextArea();
        imageView = new ImageView();
        Button loadImageButton = new Button("Load Image");
        Button encodeButton = new Button("Encode");
        Button decodeButton = new Button("Decode");

        setUpLayout(layout, loadImageButton, encodeButton, decodeButton);
        attachEventHandlers(loadImageButton, encodeButton, decodeButton);

        // Create and set the scene
        Scene scene = new Scene(layout, 1050, 675);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/design/style.css")).toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
    private void setUpLayout(VBox layout, Button loadImageButton, Button encodeButton, Button decodeButton) {
        // Configure and add components to layout
        secretMessageArea.setPromptText("Secret Message");
        secretMessageArea.setWrapText(true);
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        // Set IDs for styling
        loadImageButton.setId("loadImageButton");
        encodeButton.setId("encodeButton");
        decodeButton.setId("decodeButton");
        secretMessageArea.setId("secretMessageArea");
        imageView.setId("imageView");

        encodeButton.setMaxWidth(Double.MAX_VALUE);
        decodeButton.setMaxWidth(Double.MAX_VALUE);

        HBox buttonBox = new HBox(10, encodeButton, decodeButton);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(encodeButton, Priority.ALWAYS);
        HBox.setHgrow(decodeButton, Priority.ALWAYS);

        layout.getChildren().addAll(imageView, loadImageButton, secretMessageArea, buttonBox);
    }

    private void attachEventHandlers(Button loadImageButton, Button encodeButton, Button decodeButton) {
        loadImageButton.setOnAction(event -> {
            if (onLoadImageAction != null) {
                onLoadImageAction.accept(event);
            }
        });

        encodeButton.setOnAction(event -> {
            if (onEncodeAction != null) {
                onEncodeAction.accept(event);
            }
        });

        decodeButton.setOnAction(event -> {
            if (onDecodeAction != null) {
                onDecodeAction.accept(event);
            }
        });
    }

    public void setOnEncodeAction(Consumer<ActionEvent> onEncodeAction) {
        this.onEncodeAction = onEncodeAction;
    }

    public void setOnDecodeAction(Consumer<ActionEvent> onDecodeAction) {
        this.onDecodeAction = onDecodeAction;
    }

    public void setOnLoadImageAction(Consumer<ActionEvent> onLoadImageAction) {
        this.onLoadImageAction = onLoadImageAction;
    }

    public String getSecretMessage() {
        return secretMessageArea.getText();
    }

    public void displayImage(Image image) {
        imageView.setImage(image);
    }


    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

}

