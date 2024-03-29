package view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class DynamicGraph {
    private static final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private static final NumberAxis xAxis = new NumberAxis();
    private static final NumberAxis yAxis = new NumberAxis();
    private static final Stage stage = new Stage();

    public static void initialize() {
        Platform.runLater(() -> {
            try {
                xAxis.setLabel("Generation");
                yAxis.setLabel("Fitness Score");

                // Optionally, disable auto-ranging to manually manage axis range
                xAxis.setAutoRanging(false);
                yAxis.setAutoRanging(false);

                final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Fitness Score Over Generations");
                series.setName("Fitness Growth");
                lineChart.getData().add(series);

                Scene scene = new Scene(lineChart, 800, 600); // Adjusted size for focus
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void updateSeries(int generation, double fitness) {
        Platform.runLater(() -> {
            series.getData().add(new XYChart.Data<>(generation, fitness));
            adjustAxesBounds();
        });
    }

    private static void adjustAxesBounds() {
        // Find the min and max for both axes based on the data
        double maxX = series.getData().stream().mapToDouble(data -> data.getXValue().doubleValue()).max().orElse(0);
        double minY = series.getData().stream().mapToDouble(data -> data.getYValue().doubleValue()).min().orElse(0);
        double maxY = series.getData().stream().mapToDouble(data -> data.getYValue().doubleValue()).max().orElse(0);

        // Add padding around the data for visibility
        double paddingY = (maxY - minY) * 0.05; // 5% padding

        // Apply the adjusted bounds to the axes
        xAxis.setUpperBound(maxX + 1);
        yAxis.setLowerBound(minY - paddingY);
        yAxis.setUpperBound(maxY + paddingY);
    }
}
