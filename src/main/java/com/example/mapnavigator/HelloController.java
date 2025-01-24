package com.example.mapnavigator;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import java.util.*;

public class HelloController {
    @FXML
    private ComboBox<String> sourceComboBox;
    @FXML
    private ComboBox<String> destinationComboBox;
    @FXML
    private TextField newSourceField;
    @FXML
    private TextField newDestinationField;
    @FXML
    private TextField newWeightField;
    @FXML
    private Label resultLabel;
    @FXML
    private Canvas graphCanvas;
    private final Graph graph = new Graph();
    private List<String> shortestPath = new ArrayList<>();
    private List<List<String>> allPaths = new ArrayList<>();
    @FXML
    private void initialize() {
        refreshComboBoxes();
    }
    @FXML
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    protected void onFindPathClick() {
        String source = sourceComboBox.getValue();
        String destination = destinationComboBox.getValue();
        if (source == null || destination == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please select both source and destination.");
            return;
        }
        allPaths = graph.findAllPaths(source, destination);
        shortestPath = graph.dijkstraShortestPath(source, destination);
        if (allPaths.isEmpty()) {
            resultLabel.setText("No path found between " + source + " and " + destination);
        } else {
            StringBuilder resultBuilder = new StringBuilder("All possible paths:\n");
            for (List<String> path : allPaths) {
                resultBuilder.append(String.join(" -> ", path)).append("\n");
            }
            resultBuilder.append("\nShortest path: ").append(String.join(" -> ", shortestPath));
            int totalDistance = calculateTotalDistance(shortestPath);
            resultBuilder.append(" (Total distance: ").append(totalDistance).append(" km)");
            resultLabel.setText(resultBuilder.toString());
            drawGraph();
        }
    }
    private int calculateTotalDistance(List<String> path) {
        int totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            totalDistance += graph.getEdges(current).get(next);
        }
        return totalDistance;
    }
    @FXML
    protected void onAddEdgeClick() {
        String source = newSourceField.getText();
        String destination = newDestinationField.getText();
        int weight;
        try {
            weight = Integer.parseInt(newWeightField.getText());
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid weight.");
            return;
        }
        if (source.isEmpty() || destination.isEmpty()) {
            resultLabel.setText("Please enter both source and destination.");
            return;
        }
        graph.addEdge(source, destination, weight);
        refreshComboBoxes();
        resultLabel.setText("Edge added: " + source + " -> " + destination + " (" + weight + " km)");
        drawGraph();
    }
    @FXML
    protected void onDeleteEdgeClick() {
        String source = newSourceField.getText();
        String destination = newDestinationField.getText();
        if (source.isEmpty() || destination.isEmpty()) {
            resultLabel.setText("Please enter both source and destination.");
            return;
        }
        if (graph.deleteEdge(source, destination)) {
            resultLabel.setText("Edge deleted: " + source + " -> " + destination);
            refreshComboBoxes();
            drawGraph();
        } else {
            resultLabel.setText("Edge does not exist.");
        }
    }
    private void drawGraph() {
        Platform.runLater(() -> {
            GraphicsContext gc = graphCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
            double width = graphCanvas.getWidth();
            double height = graphCanvas.getHeight();
            Map<String, Double[]> positions = calculateNodePositions(width, height);
            gc.setLineWidth(2);
            for (String city : graph.getNodes()) {
                for (Map.Entry<String, Integer> neighbor : graph.getEdges(city).entrySet()) {
                    Double[] cityPos = positions.get(city);
                    Double[] neighborPos = positions.get(neighbor.getKey());
                    gc.setStroke(Color.BLACK);
                    if (isEdgeInShortestPath(city, neighbor.getKey())) {
                        gc.setStroke(Color.RED);
                    }
                    gc.strokeLine(cityPos[0], cityPos[1], neighborPos[0], neighborPos[1]);
                    double midX = (cityPos[0] + neighborPos[0]) / 2;
                    double midY = (cityPos[1] + neighborPos[1]) / 2;
                    gc.setFill(Color.BLACK);
                    gc.fillText(neighbor.getValue() + " km", midX, midY);
                }
            }
            for (Map.Entry<String, Double[]> entry : positions.entrySet()) {
                Double[] pos = entry.getValue();
                gc.setFill(Color.BLACK);
                gc.fillOval(pos[0] - 5, pos[1] - 5, 10, 10);
                gc.fillText(entry.getKey(), pos[0] + 5, pos[1] - 5);
            }
        });
    }
    private Map<String, Double[]> calculateNodePositions(double width, double height) {
        Map<String, Double[]> positions = new HashMap<>();
        int index = 0;
        for (String city : graph.getNodes()) {
            double angle = 2 * Math.PI * index / graph.getNodes().size();
            double x = width / 2 + (width / 3) * Math.cos(angle);
            double y = height / 2 + (height / 3) * Math.sin(angle);
            positions.put(city, new Double[]{x, y});
            index++;
        }
        return positions;
    }
    private boolean isEdgeInShortestPath(String source, String destination) {
        if (shortestPath == null || shortestPath.isEmpty()) {
            return false;
        }

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            if (shortestPath.get(i).equals(source) && shortestPath.get(i + 1).equals(destination)) {
                return true;
            }
        }
        return false;
    }
    private void refreshComboBoxes() {
        Platform.runLater(() -> {
            sourceComboBox.setItems(FXCollections.observableArrayList(graph.getNodes()));
            destinationComboBox.setItems(FXCollections.observableArrayList(graph.getNodes()));
        });
    }
}
