package com.nutshell;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;

public final class Window {
    JFrame frame;
    JPanel panel;
    JLabel gameOverText;

    int width,height;
    Minesweeper minesweeper;
    Color uncheckedColor = new Color(143, 213, 255);
    Color checkedColor = new Color(204, 234, 252);

    boolean gameOver = false;

    public Window(Minesweeper minesweeper) {
        this.minesweeper = minesweeper;

        width = minesweeper.getWidth();
        height = minesweeper.getHeight();

        frame = new JFrame();
        frame.setTitle("Minesweeper");

        frame.setSize(800, 800);
        frame.setResizable(false);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new GridLayout(width, height));

        gameOverText = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverText.setFont(new Font("Arial", Font.BOLD, 30));
        gameOverText.setOpaque(false);
        gameOverText.setVisible(true);
        frame.add(gameOverText);

        makeGrid(width, height);

        display();
    }

    public void makeGrid(int width, int height) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int row = i;
                final int col = j;

                JLabel tile = new JLabel();

                tile.setBackground(uncheckedColor);
                tile.setOpaque(true);

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (minesweeper.getVisited()[row][col] || gameOver) {
                            return;
                        }

                        if (SwingUtilities.isLeftMouseButton(e)) {
                            tileClicked(row, col);

                            if (minesweeper.getTile(row, col) == 1) {
                                tile.setBackground(Color.RED);
                            } else {
                                drawScreen();
                            }

                            System.out.println("Left Click");
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            tileFlagged(row, col);

                            System.out.println("Right Click");
                        }
                    }
                });

                panel.add(tile);
            }
        }
    }

    public void tileClicked(int row, int column) {
        System.out.println("Tile at " + column + ", " + row + " clicked");

        minesweeper.visitTile(row, column);

        if (minesweeper.getTile(row, column) == 1) {
            System.out.println("Game Over");
            gameOver = true;

            try {
                int countdown = 5;

                while (countdown > 0) {
                    System.out.println("Application will close in " + countdown);
                    countdown--;
                    Thread.sleep(1000);
                }

                System.exit(0);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
                System.exit(1);
            }
        } else {
            System.out.println("Safe");
        }
    }

    public void tileFlagged(int row, int column) {
        System.out.println("Tile at " + column + ", " + row + " flagged");

        minesweeper.flagTile(row, column);
    }

    public void drawScreen() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                JLabel tile = (JLabel) panel.getComponent(i * width + j);

                if (minesweeper.getVisited()[i][j]) {
                    switch (minesweeper.getGrid()[i][j]) {
                        case 1 -> {
                            tile.setBackground(Color.RED);
                            tile.setText("X");
                        }
                        case 0 -> {
                            tile.setBackground(checkedColor);

                            int nearmines = minesweeper.getNearMines(i, j);
                            String text = nearmines == 0 ? "" : String.valueOf(nearmines);

                            tile.setText(text);

                            // Make a border around the tile, but only on the sides with unchecked neghbhors
                            boolean[] borderSides = new boolean[4];

                            if (i - 1 >= 0 && !minesweeper.getVisited()[i - 1][j]) {borderSides[0] = true;}
                            if (j - 1 >= 0 && !minesweeper.getVisited()[i][j - 1]) {borderSides[1] = true;}
                            if (i + 1 < minesweeper.getWidth() && !minesweeper.getVisited()[i + 1][j]) {borderSides[2] = true;}
                            if (j + 1 < minesweeper.getHeight() && !minesweeper.getVisited()[i][j + 1]) {borderSides[3] = true;}

                            Border border1 = BorderFactory.createCompoundBorder(
                                borderSides[0] ? BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK) : BorderFactory.createEmptyBorder(),
                                borderSides[1] ? BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK) : BorderFactory.createEmptyBorder()
                            );  
                            
                            Border border2 = BorderFactory.createCompoundBorder(
                                borderSides[2] ? BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK) : BorderFactory.createEmptyBorder(),
                                borderSides[3] ? BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK) : BorderFactory.createEmptyBorder()
                            );  
                            
                            Border border = BorderFactory.createCompoundBorder(border1, border2);

                            tile.setBorder(border);
                        }
                    }
                    
                } else {
                    tile.setBackground(uncheckedColor);
                }
            }
        }
    }

    public void display() {frame.setVisible(true);}
}
