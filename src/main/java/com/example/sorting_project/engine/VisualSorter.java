package com.example.sorting_project.engine;

import javafx.application.Platform;
import java.util.function.Consumer;

public class VisualSorter {

    private Consumer<VisualStats> updateUI;
    private int comparisons = 0;
    private int interchanges = 0;
    private int delayMs;

    public VisualSorter(Consumer<VisualStats> updateUI, int delayMs) {
        this.updateUI = updateUI;
        this.delayMs = delayMs;
    }

    public record VisualStats(int[] array, int activeIndex, int comparisons, int interchanges) {
    }

    private volatile boolean paused = false;

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (!paused) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    private void requestUpdate(int[] array, int currentIndex) {
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Sorting Interrupted");
        }

        synchronized (this) {
            while (paused) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        VisualStats stats = new VisualStats(array.clone(), currentIndex, this.comparisons, this.interchanges);
        updateUI.accept(stats);

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sorting Interrupted");
        }
    }

    public void visualBubbleSort(int[] array) {
        comparisons = 0;
        interchanges = 0;
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                comparisons++;
                if (array[j] > array[j + 1]) {
                    interchanges++;
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    requestUpdate(array, j + 1);
                }
            }
        }
    }

    public void visualSelectionSort(int[] array) {
        comparisons = 0;
        interchanges = 0;
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            int min_idx = i;
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                if (array[j] < array[min_idx]) {
                    min_idx = j;
                }
            }
            if (min_idx != i) {
                interchanges++;
                int temp = array[min_idx];
                array[min_idx] = array[i];
                array[i] = temp;
            }
            requestUpdate(array, i);
        }
    }

    public void visualInsertionSort(int[] array) {
        comparisons = 0;
        interchanges = 0;
        int n = array.length;
        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0) {
                comparisons++;
                if (array[j] > key) {
                    array[j + 1] = array[j];
                    interchanges++;
                    j = j - 1;
                    requestUpdate(array, j + 1);
                } else {
                    break;
                }
            }
            array[j + 1] = key;
            requestUpdate(array, i);
        }
    }

    public void visualQuickSort(int[] array, int low, int high) {
        if (low == 0 && high == array.length - 1) {
            comparisons = 0;
            interchanges = 0;
        }
        if (low < high) {
            int pi = partition(array, low, high);
            visualQuickSort(array, low, pi - 1);
            visualQuickSort(array, pi + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            comparisons++;
            if (array[j] < pivot) {
                i++;
                interchanges++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                requestUpdate(array, j);
            }
        }
        interchanges++;
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        requestUpdate(array, i + 1);
        return i + 1;
    }

    public void visualHeapSort(int[] array) {
        comparisons = 0;
        interchanges = 0;
        int n = array.length;
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(array, n, i);

        for (int i = n - 1; i > 0; i--) {
            interchanges++;
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            requestUpdate(array, i);
            heapify(array, i, 0);
        }
    }

    private void heapify(int[] array, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n) {
            comparisons++;
            if (array[l] > array[largest]) largest = l;
        }
        if (r < n) {
            comparisons++;
            if (array[r] > array[largest]) largest = r;
        }

        if (largest != i) {
            interchanges++;
            int swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;
            requestUpdate(array, largest);
            heapify(array, n, largest);
        }
    }

    public void visualMergeSort(int[] array, int l, int r) {
        if (l == 0 && r == array.length - 1) {
            comparisons = 0;
            interchanges = 0;
        }
        if (l < r) {
            int m = l + (r - l) / 2;
            visualMergeSort(array, l, m);
            visualMergeSort(array, m + 1, r);
            merge(array, l, m, r);
        }
    }

    private void merge(int[] array, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;
        int L[] = new int[n1];
        int R[] = new int[n2];
        for (int i = 0; i < n1; ++i) L[i] = array[l + i];
        for (int j = 0; j < n2; ++j) R[j] = array[m + 1 + j];

        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            comparisons++;
            if (L[i] <= R[j]) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            interchanges++;
            requestUpdate(array, k);
            k++;
        }
        while (i < n1) {
            array[k] = L[i];
            interchanges++;
            i++;
            k++;
            requestUpdate(array, k);
        }
        while (j < n2) {
            array[k] = R[j];
            interchanges++;
            j++;
            k++;
            requestUpdate(array, k);
        }
    }
}