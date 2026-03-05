package com.example.sorting_project.engine;

public class SingleRunResult {
    private final long runtimeNs; // Raw nanoseconds
    private final int comparisons;
    private final int interchanges;

    public SingleRunResult(long runtimeNs, int comparisons, int interchanges) {
        this.runtimeNs = runtimeNs;
        this.comparisons = comparisons;
        this.interchanges = interchanges;
    }

    public long getRuntimeNs() { return runtimeNs; }
    public int getComparisons() { return comparisons; }
    public int getInterchanges() { return interchanges; }
}