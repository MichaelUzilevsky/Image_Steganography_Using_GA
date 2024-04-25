package view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/**
 * Manages a dynamic graph visualization for displaying fitness scores over generations
 * in a genetic algorithm. This class uses JavaFX to create a real-time updated line chart.
 */
public class DynamicGraph {
    private static XYChart.Series<Number, Number> series;
    private static NumberAxis xAxis;
    private static NumberAxis yAxis;
    private static Stage stage;

    /**
     * Manages a dynamic graph visualization for displaying fitness scores over generations
     * in a genetic algorithm. This class uses JavaFX to create a real-time updated line chart.
     */
    public static void initialize() {
        Platform.runLater(() -> {
            try {
                stage = new Stage();  // Create a new Stage each time
                xAxis = new NumberAxis();
                yAxis = new NumberAxis();
                series = new XYChart.Series<>();

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

    /**
     * Updates the series data in the line chart with a new data point representing
     * the fitness score for a specific generation.
     *
     * @param generation The generation number.
     * @param fitness The fitness score for the generation.
     */
    public static void updateSeries(int generation, double fitness) {
        Platform.runLater(() -> {
            series.getData().add(new XYChart.Data<>(generation, fitness));
            adjustAxesBounds();
        });
    }

    /**
     * Adjusts the bounds of the X and Y axes based on the current data points in the series.
     * This method ensures that all data points are visible within the chart by dynamically
     * adjusting the axis ranges.
     */
    private static void adjustAxesBounds() {
        // Find the min and max for both axes based on the data
        double maxX = series.getData().stream().mapToDouble(data -> data.getXValue().doubleValue()).max().orElse(0);
        double minY = series.getData().stream().mapToDouble(data -> data.getYValue().doubleValue()).min().orElse(0);
        double maxY = series.getData().stream().mapToDouble(data -> data.getYValue().doubleValue()).max().orElse(0);

        // Add padding around the data for visibility
        double paddingY = (maxY - minY) * 0.05; // 5% padding

        // Apply the adjusted bounds to the axes
        xAxis.setUpperBound(maxX + 1);
        xAxis.setLowerBound(1);
        yAxis.setLowerBound(minY - paddingY);
        yAxis.setUpperBound(maxY + paddingY);
    }

    /**
     * Closes the graph window.
     */
    public static void closeGraph() {
        Platform.runLater(() -> {
            if (stage != null) {
                stage.close();
                stage = null; // Ensure the old stage is discarded
            }
        });
    }
}
