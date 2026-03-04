package com.example.sorting_project.engine;

public class SortingResult {
    public String algorithmName;
    public long runtime;        // in nanoseconds
    public int comparisons;
    public int interchanges;


    public SortingResult(String name, long runtime, int comparisons, int interchanges) {
        this.algorithmName = name;
        this.runtime = runtime;
        this.comparisons = comparisons;
        this.interchanges = interchanges;
    }
}