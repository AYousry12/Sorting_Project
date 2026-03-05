package com.example.sorting_project.engine;

public class HeapSort {
    private static int comparisons = 0;
    private static int interchanges = 0;

    public static SingleRunResult sort(int[] array) {
        comparisons = 0;
        interchanges = 0;
        int n = array.length;
        long startTime = System.nanoTime();

        // Build heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i);
        }

        // extract roots one by one to the end of the array
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            interchanges++;
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;

            // call max heapify to correct the heap
            heapify(array, i, 0);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        return new SingleRunResult(duration, comparisons, interchanges);
    }

    private static void heapify(int[] array, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n) {
            comparisons++;
            if (array[l] > array[largest]) {
                largest = l;
            }
        }

        if (r < n) {
            comparisons++;
            if (array[r] > array[largest]) {
                largest = r;
            }
        }

        if (largest != i) {
            interchanges++;
            int temp = array[i];
            array[i] = array[largest];
            array[largest] = temp;

            heapify(array, n, largest);
        }
    }

}