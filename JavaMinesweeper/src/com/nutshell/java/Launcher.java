package com.nutshell.java;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int rows = 20;
            int columns = 20;
            int mines = 100;

            // Minesweeper game = new Minesweeper(rows, columns, mines);
            Window gameWindow = new Window(rows, columns, mines);
        });
    }
}
