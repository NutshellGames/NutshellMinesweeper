package com.nutshell.java;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Launcher {
    private final String saveFilePath = "Highscores.json";
    private int[][] saves;
    public static void main(String[] args) {
        // TimeSaver.addSaveData("10 x 10", 25, 117);
        // TimeSaver.addSaveData("10 x 10", 25, 98);
        // TimeSaver.addSaveData("10 x 5", 12, 73);
        // TimeSaver.addSaveData("10 x 15", 35, 242);
        // TimeSaver.addSaveData("10 x 10", 25, 143);

        // TimeSaver.saveDataToFile();

        TimeSaver.loadDataFromFile();

        SwingUtilities.invokeLater(() -> {
            int rows = 20;
            int columns = 20;
            int mines = 100; // Maximum number of mines is (rows * columns - 9)

            if (rows * columns - 9 < mines) {
                JOptionPane.showMessageDialog(null, "The number of mines cannot exceed (rows * columns - 9).");
                throw new IllegalArgumentException("Invalid number of mines: " + mines);
            }

            Window gameWindow = new Window(rows, columns, mines);
        });
    }
}
