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
    @FXML private VBox settingsComparison, settingsVisualizer;
    @FXML private ComboBox<String> comboVisualAlgo;
    @FXML private Button btnTabComparison;
    @FXML private Button btnTabVisualizer;
    @FXML private Label lblVisComp, lblVisInter;
    @FXML private TextField txtVisualCount;


    private ObservableList<SortingResult> resultsData = FXCollections.observableArrayList();
    private int[] visualArray;
    private volatile boolean isSorting = false;

    @FXML
    public void initialize() {
        comboType.setItems(FXCollections.observableArrayList("Random", "Sorted", "Inversely Sorted"));
        comboType.getSelectionModel().selectFirst();
        comboVisualAlgo.setItems(FXCollections.observableArrayList(
                "Bubble Sort",
                "Selection Sort",
                "Insertion Sort",
                "Quick Sort",
                "Merge Sort",
                "Heap Sort"
        ));
        comboVisualAlgo.getSelectionModel().selectFirst();

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
        try {
            int size = Integer.parseInt(txtSize.getText());
            String type = comboType.getValue();
            resultsData.clear();

            int[] baseData;
            if ("Sorted".equals(type)) {
                baseData = ArrayGenerator.generateSorted(size);
            } else if ("Inversely Sorted".equals(type)) {
                baseData = ArrayGenerator.generateInverselySorted(size);
            } else {
                baseData = ArrayGenerator.generateRandom(size);
            }

            resultsData.add(benchmark("Bubble Sort", baseData.clone(), BubbleSort::sort));
            resultsData.add(benchmark("Selection Sort", baseData.clone(), SelectionSort::sort));
            resultsData.add(benchmark("Insertion Sort", baseData.clone(), InsertionSort::sort));
            resultsData.add(benchmark("Merge Sort", baseData.clone(), MergeSort::sort));
            resultsData.add(benchmark("Quick Sort", baseData.clone(), QuickSort::sort));
            resultsData.add(benchmark("Heap Sort", baseData.clone(), HeapSort::sort));

        } catch (NumberFormatException e) {
            System.err.println("Invalid size input");
        }
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

    private int getVisualCount() {
        try {
            String text = txtVisualCount.getText().trim();
            if (text.isEmpty()) return 50; // Default if empty

            int count = Integer.parseInt(text);

            if (count > 100) return 100;
            if (count < 2) return 2;

            return count;
        } catch (NumberFormatException e) {
            return 50;
        }
    }

    @FXML
    private void handleStartAnimation() {
        if (isSorting) return;

        isSorting = true;
        int count = getVisualCount();
        int speed = (int) sliderSpeed.getValue();

        int delay = (int) (Math.pow(101 - speed, 1.2));

        int[] arr = ArrayGenerator.generateRandom(count);

        Thread sortingThread = new Thread(() -> {
            try {
                VisualSorter sorter = new VisualSorter(this::drawBars, delay);
                String selectedAlgo = comboVisualAlgo.getValue();

                switch (selectedAlgo) {
                    case "Bubble Sort" -> sorter.visualBubbleSort(arr);
                    case "Selection Sort" -> sorter.visualSelectionSort(arr);
                    case "Insertion Sort" -> sorter.visualInsertionSort(arr);
                    case "Quick Sort" -> sorter.visualQuickSort(arr, 0, arr.length - 1);
                    case "Merge Sort" -> sorter.visualMergeSort(arr, 0, arr.length - 1);
                    case "Heap Sort" -> sorter.visualHeapSort(arr);
                }
            } finally {
                isSorting = false;
            }
        });

        sortingThread.setDaemon(true);
        sortingThread.setName("SortingThread");
        sortingThread.start();
    }

    private void drawBars(VisualSorter.VisualStats stats) {
        lblVisComp.setText("Comparisons: " + stats.comparisons());
        lblVisInter.setText("Interchanges: " + stats.interchanges());

        paneCanvas.getChildren().clear();
        double width = paneCanvas.getWidth();
        double height = paneCanvas.getHeight();
        double barWidth = width / stats.array().length;

        for (int i = 0; i < stats.array().length; i++) {
            double barHeight = (stats.array()[i] / 500.0) * height;
            Rectangle rect = new Rectangle(i * barWidth, height - barHeight, barWidth - 1, barHeight);

            rect.setFill(i == stats.activeIndex() ? Color.RED : Color.SKYBLUE);
            paneCanvas.getChildren().add(rect);
        }
    }

    @FXML
    private void handleResetVisualizer() {

        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals("SortingThread")) {
                t.interrupt();
            }
        }

        isSorting = false;
        paneCanvas.getChildren().clear();
        lblVisComp.setText("Comparisons: 0");
        lblVisInter.setText("Interchanges: 0");
    }

    @FXML
    private void showComparisonMode() {
        toggleViews(true);
        btnTabComparison.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-border-color: #2196F3; -fx-border-width: 0 0 3 0;");
        btnTabVisualizer.setStyle("-fx-background-color: #333; -fx-text-fill: #aaa; -fx-border-width: 0;");
    }

    @FXML
    private void showVisualizerMode() {
        toggleViews(false);
        btnTabVisualizer.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-border-color: #2196F3; -fx-border-width: 0 0 3 0;");
        btnTabComparison.setStyle("-fx-background-color: #333; -fx-text-fill: #aaa; -fx-border-width: 0;");
    }

    private void toggleViews(boolean isComparison) {
        viewComparison.setVisible(isComparison);
        viewComparison.setManaged(isComparison);
        viewVisualizer.setVisible(!isComparison);
        viewVisualizer.setManaged(!isComparison);

        settingsComparison.setVisible(isComparison);
        settingsComparison.setManaged(isComparison);
        settingsVisualizer.setVisible(!isComparison);
        settingsVisualizer.setManaged(!isComparison);
    }
}
