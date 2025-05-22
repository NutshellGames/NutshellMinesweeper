package com.nutshell.java;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class TimeSaver {
    private static final String fileLocation = "Highscores.json";
    private static File saveFile = new File(fileLocation);
    private static HashMap<String, HashMap<Integer, Integer>> saveData = new HashMap<>(); // Map<gridSize, Map<mines, time>>

    static String getFileLocation() {
        return fileLocation;
    }

    static File getSaveFile() {
        return saveFile;
    }

    static HashMap<String, HashMap<Integer, Integer>> getSaveData() {
        return saveData;
    }

    static void setSaveData(HashMap<String, HashMap<Integer, Integer>> saveData) {
        TimeSaver.saveData = saveData;
    }

    static void addSaveData(String gridSize, int mines, int time) {
        if (saveData.containsKey(gridSize)) {
            HashMap<Integer, Integer> minesData = saveData.get(gridSize);
            if (minesData.containsKey(mines)) {
                int previousTime = minesData.get(mines);
                if (time < previousTime) {
                    minesData.put(mines, time);
                }
            } else {
                minesData.put(mines, time);
            }
        } else {
            HashMap<Integer, Integer> minesData = new HashMap<>();
            minesData.put(mines, time);
            saveData.put(gridSize, minesData);
        }
    }

    static void removeSaveData(String gridSize, int mines) {
        saveData.get(gridSize).remove(mines);
    }

    static void clearSaveData() {
        saveData.clear();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    static void saveDataToFile() {
        String jsonString = "[{\n";

        for (String gridSize : saveData.keySet()) {
            jsonString += "\t\"" + gridSize + "\": {\n";
            HashMap<Integer, Integer> minesData = saveData.get(gridSize);

            for (Integer mines : minesData.keySet()) {
                jsonString += "\t\t\"" + mines + "\": " + minesData.get(mines) + ",\n";
            }

            jsonString = jsonString.substring(0, jsonString.length() - 2); // Remove the last comma
            jsonString += "\n\t},\n";
        }

        jsonString = jsonString.substring(0, jsonString.length() - 2); // Remove the last comma

        jsonString += "\n}]";

        // Save the json to the file
        try (FileWriter fileWriter = new FileWriter(saveFile)) {
            saveFile.delete(); // Delete the file if it exists
            saveFile.createNewFile(); // Create a new file

            fileWriter.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadDataFromFile() {
        try (FileReader fileReader = new FileReader(saveFile)) {
            String jsonString = "";
            int character;

            while ((character = fileReader.read()) != -1) {
                jsonString += (char) character;
            }

            // Parse the JSON string
            String[] lines = jsonString.split("\n");

            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":");
                    String gridSize = parts[0].trim().replaceAll("\"", "");
                    String[] minesData = new String[2];

                    minesData[0] = parts[0];
                    minesData[1] = parts[1];

                    minesData = parts[1].trim().replaceAll("[{}]", "").split(",");

                    HashMap<Integer, Integer> minesMap = new HashMap<>();

                    for (String mineData : minesData) {
                        String[] mineParts = mineData.trim().split(":");
                        if (mineParts.length != 2) {
                            continue; // Skip invalid data
                        }

                        int mines = Integer.MAX_VALUE; // Default mines if not specified
                        if (!mineParts[0].trim().replaceAll("\"", "").equals("")) {
                            mines = Integer.parseInt(mineParts[0].trim().replaceAll("\"", ""));
                        }

                        int time = Integer.MAX_VALUE; // Default time if not specified
                        if (!mineParts[1].trim().equals("")) {
                            time = Integer.parseInt(mineParts[1].trim());
                        }
                        
                        minesMap.put(mines, time);

                        System.out.println(minesMap);
                    }

                    saveData.put(gridSize, minesMap);
                }
            }

            System.out.println("Data loaded from file: " + saveFile.getAbsolutePath());
            System.out.println("Save data: " + saveData);
        } catch (IOException e) {
            // Handle the exception
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }
}
