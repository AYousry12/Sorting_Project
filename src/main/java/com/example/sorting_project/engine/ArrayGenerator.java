package com.example.sorting_project.engine;

import java.util.Random;

public class ArrayGenerator {
    private static final int MAX_VAL = 500;
    private static final Random random = new Random();

    public static int[] generateRandom(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(MAX_VAL) + 1;
        }
        return arr;
    }

    public static int[] generateSorted(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = (int) (((double)(i + 1) / size) * MAX_VAL);
        }
        return arr;
    }

    public static int[] generateInverselySorted(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = (int) (((double)(size - i) / size) * MAX_VAL);
        }
        return arr;
    }
}