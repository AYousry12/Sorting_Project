package com.example.sorting_project.engine;

public class MergeSort {
    private static int comparisons = 0;
    private static int interchanges = 0;

    public static SortingResult sort(int[] array) {
        comparisons = 0;
        interchanges = 0;
        long startTime = System.nanoTime();

        mergeSort(array, 0, array.length - 1);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        return new SortingResult("Merge Sort", duration, comparisons, interchanges);
    }

    private static void mergeSort(int[] array, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);

            // Merge the sorted halves
            merge(array, left, mid, right);
        }
    }

    private static void merge(int[] array, int left, int mid, int right) {
        int Leftsize = mid - left + 1;
        int Rightsize = right - mid;

        int[] L = new int[Leftsize];
        int[] R = new int[Rightsize];

        for (int i = 0; i < Leftsize; i++){
            L[i] = array[left + i];
            interchanges++;
        }

        for (int i = 0; i < Rightsize; i++) {
            R[i] = array[mid + 1 + i];
            interchanges++;
        }

        int i = 0, j = 0;
        int current_index = left;

        while (i < Leftsize && j < Rightsize) {
            comparisons++;
            if (L[i] <= R[j]) {
                array[current_index] = L[i];
                i++;
            } else {
                array[current_index] = R[j];
                j++;
            }
            interchanges++;
            current_index++;
        }

        //Copy remaining elements of L[]
        while (i < Leftsize) {
            array[current_index] = L[i];
            i++;
            current_index++;
            interchanges++;
        }

        //Copy remaining elements of R[]
        while (j < Rightsize) {
            array[current_index] = R[j];
            j++;
            current_index++;
            interchanges++;
        }
    }


}