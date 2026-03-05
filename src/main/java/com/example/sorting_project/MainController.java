package com.example.sorting_project;

import javafx.fxml.FXML;
import com.example.sorting_project.engine.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;
import javafx.stage.FileChooser;


public class MainController {

    @FXML private TextField txtSize;
    @FXML private ComboBox<String> comboType;
    @FXML private TableView<SortingResult> tableResults;
    @FXML private TableColumn<SortingResult, String> colName;
    @FXML private TableColumn<SortingResult, Double> colAvg;
    @FXML private TableColumn<SortingResult, Long> colMin;
    @FXML private TableColumn<SortingResult, Long> colMax;
    @FXML private TableColumn<SortingResult, Integer> colComp;
    @FXML private TableColumn<SortingResult, Integer> colInt;

    private ObservableList<SortingResult> resultsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // the ComboBox
        comboType.setItems(FXCollections.observableArrayList("Random", "Sorted", "Inversely Sorted"));
        comboType.getSelectionModel().selectFirst();

        // Linking Table Columns to SortingResult fields
        colName.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        colAvg.setCellValueFactory(new PropertyValueFactory<>("runtime"));
        colComp.setCellValueFactory(new PropertyValueFactory<>("comparisons"));
        colInt.setCellValueFactory(new PropertyValueFactory<>("interchanges"));

        // TODO: Min/Max need the multi-run logic , to be done
        tableResults.setItems(resultsData);
    }

    @FXML
    private void handleRunComparison() {
        resultsData.clear();
        int size = Integer.parseInt(txtSize.getText());
        String type = comboType.getValue();

        // Generate the array based on user choices
        int[] baseArray;
        if (type.equals("Sorted")) baseArray = ArrayGenerator.generateSorted(size, 0);
        else if (type.equals("Inversely Sorted")) baseArray = ArrayGenerator.generateInverselySorted(size, size);
        else baseArray = ArrayGenerator.generateRandom(size, size * 10);

        // Run all 6 algorithms and add to table
        resultsData.add(BubbleSort.sort(baseArray.clone()));
        resultsData.add(SelectionSort.sort(baseArray.clone()));
        resultsData.add(InsertionSort.sort(baseArray.clone()));
        resultsData.add(MergeSort.sort(baseArray.clone()));
        resultsData.add(QuickSort.sort(baseArray.clone()));
        resultsData.add(HeapSort.sort(baseArray.clone()));
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
                resultsData.add(QuickSort.sort(data.clone())); // Test with one for now
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSwitchMode() {
        System.out.println("Switching to Visualization Mode...");
        // the scene swap logic here
    }
}
