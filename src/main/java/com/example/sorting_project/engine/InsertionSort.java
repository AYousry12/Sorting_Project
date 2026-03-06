package com.example.sorting_project.engine;

public class InsertionSort {

    public static SingleRunResult sort(int[] array) {
        int length = array.length;
        int comparisons = 0;
        int interchanges = 0;
        long startTime = System.nanoTime();

        for (int i = 1; i < length; i++) { // Start at 1 as first element is considered sorted
            int current_value = array[i];
            int j = i - 1;

            while (j >= 0) {
                comparisons++;
                if (array[j] > current_value) {
                    array[j + 1] = array[j];
                    interchanges++;
                    j--;
                } else {
                    break;
                }
            }

            array[j + 1] = current_value;
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        return new SingleRunResult(duration, comparisons, interchanges);
    }
}