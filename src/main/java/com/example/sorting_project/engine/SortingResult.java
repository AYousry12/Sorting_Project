package com.example.sorting_project.engine;

public class SortingResult {
    public String algorithmName;
    public double avgRuntime; // in milliseconds
    public double minRuntime;
    public double maxRuntime;    public int comparisons;
    public int interchanges;


    public SortingResult(String name, double avg, double min, double max, int comp, int inter) {
        this.algorithmName = name;
        this.avgRuntime = avg;
        this.minRuntime = min;
        this.maxRuntime = max;
        this.comparisons = comp;
        this.interchanges = inter;
    }

    public String getAlgorithmName() { return algorithmName; }
    public double getAvgRuntime() { return avgRuntime; }
    public double getMinRuntime() { return minRuntime; }
    public double getMaxRuntime() { return maxRuntime; }
    public int getComparisons() { return comparisons; }
    public int getInterchanges() { return interchanges; }
}