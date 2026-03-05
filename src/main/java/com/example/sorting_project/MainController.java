package com.example.sorting_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.example.sorting_project.engine.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;
import java.util.Arrays;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

public class MainController {

    @FXML private TableColumn<SortingResult, String> colName;
    @FXML private TableColumn<SortingResult, Double> colAvg;
    @FXML private TableColumn<SortingResult, Double> colMin;
    @FXML private TableColumn<SortingResult, Double> colMax;
    @FXML private TableColumn<SortingResult, Integer> colComp;
    @FXML private TableColumn<SortingResult, Integer> colInt;
    @FXML private TextField txtSize;
    @FXML private ComboBox<String> comboType;
    @FXML private TableView<SortingResult> tableResults;
    @FXML private Pane paneCanvas;
    @FXML private VBox viewComparison, viewVisualizer;
    @FXML private Slider sliderSpeed;


    private ObservableList<SortingResult> resultsData = FXCollections.observableArrayList();
    private int[] visualArray;

    @FXML
    public void initialize() {
        // the ComboBox
        comboType.setItems(FXCollections.observableArrayList("Random", "Sorted", "Inversely Sorted"));
        comboType.getSelectionModel().selectFirst();

        colName.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        colAvg.setCellValueFactory(new PropertyValueFactory<>("avgRuntime"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("minRuntime"));
        colMax.setCellValueFactory(new PropertyValueFactory<>("maxRuntime"));
        colComp.setCellValueFactory(new PropertyValueFactory<>("comparisons"));
        colInt.setCellValueFactory(new PropertyValueFactory<>("interchanges"));

        tableResults.setItems(resultsData);
    }

    @FXML
    private void handleRunComparison() {
        resultsData.clear();
        int size = Integer.parseInt(txtSize.getText());
        int[] baseArray = ArrayGenerator.generateRandom(size, size * 10); // Or based on the ComboBox

        resultsData.add(benchmark("Bubble Sort", baseArray, BubbleSort::sort));
        resultsData.add(benchmark("Selection Sort", baseArray, SelectionSort::sort));
        resultsData.add(benchmark("Insertion Sort", baseArray, InsertionSort::sort));
        resultsData.add(benchmark("Merge Sort", baseArray, MergeSort::sort));
        resultsData.add(benchmark("Quick Sort", baseArray, QuickSort::sort));
        resultsData.add(benchmark("Heap Sort", baseArray, HeapSort::sort));
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                int[] data = FileParser.parseFile(selectedFile.getAbsolutePath());
                resultsData.clear();

                SingleRunResult raw = QuickSort.sort(data.clone());

                double timeMs = raw.getRuntimeNs() / 1_000_000.0;
                SortingResult tableRow = new SortingResult(
                        "Quick Sort (File)",
                        timeMs, // Avg
                        timeMs, // Min
                        timeMs, // Max
                        raw.getComparisons(),
                        raw.getInterchanges()
                );

                resultsData.add(tableRow);
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }
    }

    private SortingResult benchmark(String name, int[] originalArray, java.util.function.Function<int[], SingleRunResult> sortFunc) {
        int iterations = 10;
        long[] runs = new long[iterations];
        int comps = 0;
        int inters = 0;

        for (int i = 0; i < iterations; i++) {
            int[] copy = originalArray.clone();
            SingleRunResult raw = sortFunc.apply(copy);
            runs[i] = raw.getRuntimeNs();
            comps = raw.getComparisons();
            inters = raw.getInterchanges();
        }

        double avgMs = (java.util.Arrays.stream(runs).average().orElse(0)) / 1_000_000.0;
        long minNs = java.util.Arrays.stream(runs).min().orElse(0);
        long maxNs = java.util.Arrays.stream(runs).max().orElse(0);

        return new SortingResult(name, avgMs, minNs / 1_000_000.0, maxNs / 1_000_000.0, comps, inters);
    }

    @FXML
    private void handleSwitchMode() {
        boolean isComparisonVisible = viewComparison.isVisible();
        viewComparison.setVisible(!isComparisonVisible);
        viewVisualizer.setVisible(isComparisonVisible);
    }

    private void drawArray(int[] arr, int highlightedIndex1, int highlightedIndex2) {
        Platform.runLater(() -> {
            paneCanvas.getChildren().clear();
            double canvasWidth = paneCanvas.getWidth();
            double canvasHeight = paneCanvas.getHeight();
            double barWidth = canvasWidth / arr.length;

            for (int i = 0; i < arr.length; i++) {
                double barHeight = (arr[i] / (double) Arrays.stream(arr).max().getAsInt()) * canvasHeight;
                Rectangle rect = new Rectangle(i * barWidth, canvasHeight - barHeight, barWidth - 1, barHeight);

                // Highlight the elements currently being compared/swapped
                if (i == highlightedIndex1 || i == highlightedIndex2) {
                    rect.setFill(Color.RED);
                } else {
                    rect.setFill(Color.SKYBLUE);
                }
                paneCanvas.getChildren().add(rect);
            }
        });
    }

    @FXML
    private void handleStartAnimation() {
        int[] arr = ArrayGenerator.generateRandom(50, 100); // Small size for visibility
        int speed = (int) sliderSpeed.getValue();

        new Thread(() -> {
            VisualSorter sorter = new VisualSorter(this::drawBars, 101 - speed);
            sorter.visualBubbleSort(arr);
        }).start();
    }

    private void drawBars(int[] arr, int activeIndex) {
        paneCanvas.getChildren().clear();
        double width = paneCanvas.getWidth();
        double height = paneCanvas.getHeight();
        double barWidth = width / arr.length;

        for (int i = 0; i < arr.length; i++) {
            double barHeight = (arr[i] / 100.0) * height; // Scale based on max value
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(
                    i * barWidth, height - barHeight, barWidth - 1, barHeight
            );
            rect.setFill(i == activeIndex ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.SKYBLUE);
            paneCanvas.getChildren().add(rect);
        }
    }

    public void handleResetVisualizer(ActionEvent actionEvent) {
    }
}
