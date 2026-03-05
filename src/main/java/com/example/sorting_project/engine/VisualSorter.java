package com.example.sorting_project.engine;

import javafx.application.Platform;
import java.util.function.BiConsumer;

public class VisualSorter {

    private BiConsumer<int[], Integer> updateUI;
    private int delayMs = 50;

    public VisualSorter(BiConsumer<int[], Integer> updateUI, int delayMs) {
        this.updateUI = updateUI;
        this.delayMs = delayMs;
    }

    public void visualBubbleSort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;

                    requestUpdate(array, j + 1);
                }
            }
        }
    }

    private void requestUpdate(int[] array, int currentIndex) {
        int[] copy = array.clone();
        Platform.runLater(() -> updateUI.accept(copy, currentIndex));
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}