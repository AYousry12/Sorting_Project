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
    @FXML private ComboBox<String> comboVisualSource;
    @FXML private Button btnSelectFileVisual;
    @FXML private Label lblFileStatus;
    @FXML private CheckBox chkBubble, chkSelection, chkInsertion, chkQuick, chkMerge, chkHeap;
    @FXML private Label lblFileStatusComparison;


    private ObservableList<SortingResult> resultsData = FXCollections.observableArrayList();
    private int[] visualArray;
    private int[] loadedFileData = null;
    private volatile boolean isSorting = false;

    @FXML
    public void initialize() {
        // 1. Define the shared list of states
        ObservableList<String> states = FXCollections.observableArrayList(
                "Random", "Sorted", "Inversely Sorted", "From File"
        );

        // 2. Setup Comparison Tab
        if (comboType != null) {
            comboType.setItems(states);
            comboType.getSelectionModel().selectFirst();
        }

        // 3. Setup Visualizer Tab - Check for null to prevent the crash
        if (comboVisualSource != null) {
            comboVisualSource.setItems(states);
            comboVisualSource.getSelectionModel().selectFirst();

            // Wiring the File button visibility
            comboVisualSource.valueProperty().addListener((obs, oldVal, newVal) -> {
                boolean isFile = "From File".equals(newVal);
                if (btnSelectFileVisual != null) {
                    btnSelectFileVisual.setVisible(isFile);
                    btnSelectFileVisual.setManaged(isFile);
                }
            });
        }

        // 4. Setup Algorithm Dropdown
        if (comboVisualAlgo != null) {
            comboVisualAlgo.setItems(FXCollections.observableArrayList(
                    "Bubble Sort", "Selection Sort", "Insertion Sort",
                    "Quick Sort", "Merge Sort", "Heap Sort"
            ));
            comboVisualAlgo.getSelectionModel().selectFirst();
        }

        // 5. Setup Table Columns
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
        int[] baseData;
        String type = comboType.getValue();

        if ("From File".equals(type)) {
            if (loadedFileData == null) {
                lblFileStatusComparison.setText("Error: Load a file first!");
                return;
            }
            baseData = loadedFileData;
            txtSize.setText(String.valueOf(baseData.length));
        } else {
            int size = Integer.parseInt(txtSize.getText());
            if ("Sorted".equals(type)) baseData = ArrayGenerator.generateSorted(size);
            else if ("Inversely Sorted".equals(type)) baseData = ArrayGenerator.generateInverselySorted(size);
            else baseData = ArrayGenerator.generateRandom(size);
        }

        resultsData.clear();

        if (chkBubble.isSelected()) resultsData.add(benchmark("Bubble Sort", baseData.clone(), BubbleSort::sort));
        if (chkSelection.isSelected()) resultsData.add(benchmark("Selection Sort", baseData.clone(), SelectionSort::sort));
        if (chkInsertion.isSelected()) resultsData.add(benchmark("Insertion Sort", baseData.clone(), InsertionSort::sort));
        if (chkMerge.isSelected()) resultsData.add(benchmark("Merge Sort", baseData.clone(), MergeSort::sort));
        if (chkQuick.isSelected()) resultsData.add(benchmark("Quick Sort", baseData.clone(), QuickSort::sort));
        if (chkHeap.isSelected()) resultsData.add(benchmark("Heap Sort", baseData.clone(), HeapSort::sort));
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv", "*.dat"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                this.loadedFileData = FileParser.parseFile(file.getAbsolutePath());
                String msg = "Loaded: " + file.getName() + " (" + loadedFileData.length + " items)";

                if (lblFileStatus != null) lblFileStatus.setText(msg);
                if (lblFileStatusComparison != null) lblFileStatusComparison.setText(msg);

                System.out.println("File loaded successfully.");
            } catch (Exception e) {
                String errorMsg = "Error: Could not read file.";
                if (lblFileStatus != null) lblFileStatus.setText(errorMsg);
                if (lblFileStatusComparison != null) lblFileStatusComparison.setText(errorMsg);
                e.printStackTrace();
            }
        }
    }

    private SortingResult benchmark(String name, int[] originalArray, java.util.function.Function<int[], SingleRunResult> sortFunc) {
        int iterations = 10;
        long[] runs = new long[iterations];
        int finalComps = 0;
        int finalInters = 0;

        for (int i = 0; i < iterations; i++) {
            int[] copy = originalArray.clone();
            SingleRunResult raw = sortFunc.apply(copy);
            runs[i] = raw.getRuntimeNs();

            finalComps = raw.getComparisons();
            finalInters = raw.getInterchanges();
        }

        double avgMs = (Arrays.stream(runs).average().orElse(0)) / 1_000_000.0;
        long minNs = Arrays.stream(runs).min().orElse(0);
        long maxNs = Arrays.stream(runs).max().orElse(0);

        return new SortingResult(name, avgMs, minNs / 1_000_000.0, maxNs / 1_000_000.0, finalComps, finalInters);
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
        // 1. Double-click protection
        if (isSorting) return;

        String source = comboVisualSource.getValue();
        int count = getVisualCount();
        int[] tempArr;

        // 2. Logic for choosing the array state
        if ("Sorted".equals(source)) {
            tempArr = ArrayGenerator.generateSorted(count);
        } else if ("Inversely Sorted".equals(source)) {
            tempArr = ArrayGenerator.generateInverselySorted(count);
        } else if ("From File".equals(source)) {
            if (loadedFileData == null) {
                lblFileStatus.setText("Error: No file loaded!");
                return;
            }
            int actualCount = Math.min(loadedFileData.length, 100);
            tempArr = Arrays.copyOf(loadedFileData, actualCount);
            lblFileStatus.setText("Visualizing File Data (" + actualCount + " elements)");
        } else {
            // Enforce the 500 scaling here if your generator doesn't do it automatically
            tempArr = ArrayGenerator.generateRandom(count);
        }

        // 3. Lock the data for the thread
        final int[] finalArr = tempArr;
        isSorting = true;

        // Draw the initial state before sorting starts
        drawBars(new VisualSorter.VisualStats(finalArr, -1, 0, 0));

        Thread sortingThread = new Thread(() -> {
            try {
                // Calculate delay: 100 speed = 1ms, 1 speed = ~250ms
                int speedValue = (int) sliderSpeed.getValue();
                int delay = (int) (Math.pow(101 - speedValue, 1.2));

                VisualSorter sorter = new VisualSorter(this::drawBars, delay);
                String selectedAlgo = comboVisualAlgo.getValue();

                // Run the algorithm
                switch (selectedAlgo) {
                    case "Bubble Sort" -> sorter.visualBubbleSort(finalArr);
                    case "Selection Sort" -> sorter.visualSelectionSort(finalArr);
                    case "Insertion Sort" -> sorter.visualInsertionSort(finalArr);
                    case "Quick Sort" -> sorter.visualQuickSort(finalArr, 0, finalArr.length - 1);
                    case "Merge Sort" -> sorter.visualMergeSort(finalArr, 0, finalArr.length - 1);
                    case "Heap Sort" -> sorter.visualHeapSort(finalArr);
                    default -> System.out.println("Unknown Algorithm Selected");
                }
            } catch (Exception e) {
                // Catches the 'Sorting Interrupted' signal from the Reset button
                System.out.println("Sorting thread terminated safely.");
            } finally {
                // 4. THE FIX: Always reset the flag so the button works again
                isSorting = false;
                Platform.runLater(() -> {
                    if (lblFileStatus != null && !"From File".equals(source)) {
                        lblFileStatus.setText("Sorting Complete");
                    }
                });
            }
        });

        sortingThread.setDaemon(true);
        sortingThread.setName("SortingThread");
        sortingThread.start();
    }

    private void drawBars(VisualSorter.VisualStats stats) {
        // We wrap the label updates and the drawing in runLater to keep them in sync
        Platform.runLater(() -> {
            // Update the counters using the data passed from VisualSorter
            lblVisComp.setText("Comparisons: " + stats.comparisons());
            lblVisInter.setText("Interchanges: " + stats.interchanges());

            paneCanvas.getChildren().clear();
            double width = paneCanvas.getWidth();
            double height = paneCanvas.getHeight();
            int[] currentArr = stats.array();
            double barWidth = width / currentArr.length;

            for (int i = 0; i < currentArr.length; i++) {

                double barHeight = (currentArr[i] / 500.0) * height;

                Rectangle rect = new Rectangle(i * barWidth, height - barHeight, barWidth - 1, barHeight);

                if (i == stats.activeIndex()) {
                    rect.setFill(Color.WHITE);
                } else {
                    rect.setFill(Color.web("#2196F3"));
                }
                paneCanvas.getChildren().add(rect);
            }
        });
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
