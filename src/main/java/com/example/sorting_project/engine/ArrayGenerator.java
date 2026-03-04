package com.example.sorting_project.engine;

import java.util.Random;

public class ArrayGenerator {

    // the random generator has a max value that the random number won`t exceed
    public static int[] generateRandom(int size, int maxValue) {
        int[] arr = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(maxValue + 1);
        }
        return arr;
    }

    // The sorted generator has a start value to start counting from
    public static int[] generateSorted(int size, int startValue) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = startValue + i;
        }
        return arr;
    }

    // the inversely sorted generator has a max value to start decreasing from it
    public static int[] generateInverselySorted(int size, int startValue) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = startValue - i;
        }
        return arr;
    }
}