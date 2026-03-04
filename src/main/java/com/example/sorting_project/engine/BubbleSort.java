package com.example.sorting_project.engine;

public class BubbleSort {

    public static SortingResult sort(int[] array) {
        int length = array.length;
        int comparisons = 0;
        int interchanges = 0;
        long startTime = System.nanoTime();

        for (int i = 0; i < length - 1; i++) {
            for (int j = 0; j < length - i - 1; j++) {
                comparisons++;
                if (array[j] > array[j + 1]) {
                    interchanges++;
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        return new SortingResult("Bubble Sort", duration, comparisons, interchanges);
    }
}