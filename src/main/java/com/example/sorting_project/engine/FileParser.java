package com.example.sorting_project.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileParser {

    public static int[] parseFile(String filePath) throws IOException {
        List<Integer> integerList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split by comma, but use a regex to ignore surrounding whitespace
                String[] parts = line.split(",");

                for (String part : parts) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        try {
                            integerList.add(Integer.parseInt(trimmed));
                        } catch (NumberFormatException e) {
                            System.err.println("Skipping invalid number: " + trimmed);
                        }
                    }
                }
            }
        }

        // Convert the dynamic list to an array
        return integerList.stream().mapToInt(i -> i).toArray();
    }
}