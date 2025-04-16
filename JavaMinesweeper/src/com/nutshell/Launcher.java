package com.nutshell;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Minesweeper game = new Minesweeper(20, 20, 100);
            Window gameWindow = new Window(game);
            
            System.out.println(game + "\n");
        });
    }
}
