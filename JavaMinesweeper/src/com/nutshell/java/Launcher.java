package com.nutshell.java;

import javax.swing.*;

public class Launcher {
    private final String saveFilePath = "Highscores.json";
    private int[][] saves;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int rows = 5;
            int columns = 5;
            int mines = 5; // Maximum number of mines is (rows * columns - 9)

            if (rows * columns - 9 < mines) {
                JOptionPane.showMessageDialog(null, "The number of mines cannot exceed (rows * columns - 9).");
                throw new IllegalArgumentException("Invalid number of mines: " + mines);
            }

            Window gameWindow = new Window(rows, columns, mines);
        });
    }

    public void saveGame() {
        
    }
}
