package com.example.sorting_project.engine;

public class SelectionSort {
    public static SortingResult sort(int[] array) {
        int length = array.length;
        int comparisons = 0;
        int interchanges = 0;
        long startTime = System.nanoTime();

        for (int i = 0; i < length - 1; i++) {
            int min_index = i;
            for (int j = i+1 ; j < length; j++) {
                comparisons++;
               if (array[j] < array[min_index]) {
                   min_index = j ;
               }
            }
            if (min_index != i) {
                interchanges++;
                int temp = array[i];
                array[i] = array[min_index];
                array[min_index] = temp;
            }
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        return new SortingResult("Selection Sort", duration, comparisons, interchanges);
    }
}
