package com.example.sorting_project.engine;

public class QuickSort {
    private static int comparisons = 0;
    private static int interchanges = 0;

    public static SortingResult sort(int[] array) {
        comparisons = 0;
        interchanges = 0;
        long startTime = System.nanoTime();

        quickSort(array, 0, array.length - 1);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        return new SortingResult("Quick Sort", duration, comparisons, interchanges);
    }

    private static void quickSort(int[] array, int low, int high) {
        if (low < high) {
            // partitionIndex is the index where the pivot is now in the right place
            int SplitIndex = partition(array, low, high);

            // Recursively sort before and after split
            quickSort(array, low, SplitIndex - 1);
            quickSort(array, SplitIndex + 1, high);
        }
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[high]; // i`ll use last element as the pivot
        int i = (low - 1); // starting Index of smaller elements

        for (int j = low; j < high; j++) {
            comparisons++;
            if (array[j] <= pivot) {
                i++;
                // Swap element at the bound and current smaller element
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                interchanges++;
            }
        }

        // Swap the pivot with the element at i+1
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        interchanges++; // correct pivot location

        return i + 1;
    }
}