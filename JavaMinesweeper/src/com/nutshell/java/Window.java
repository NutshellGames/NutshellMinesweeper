package com.nutshell.java;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.border.Border;

public class Window {
    JFrame frame;
    JPanel panel;
    JLabel gameOverText;

    int width,height,mines;
    Minesweeper minesweeper;
    boolean gameGenerated = false;

    Color uncheckedColor = new Color(143, 213, 255);
    Color checkedColor = new Color(204, 234, 252);
    Color bombColor = Color.RED;

    // Get Images
    URL bombURL = getClass().getClassLoader().getResource("com/nutshell/resources/images/bomb.webp");
    URL boomURL = getClass().getClassLoader().getResource("com/nutshell/resources/images/boom.png");
    URL flagURL = getClass().getClassLoader().getResource("com/nutshell/resources/images/flag.png");

    ImageIcon bombImage = new ImageIcon(bombURL);
    ImageIcon boomImage = new ImageIcon(boomURL);
    ImageIcon flagImage = new ImageIcon(flagURL);

    boolean gameOver = false;
    boolean gameWon = false;

    public Window(int width, int height, int mines) {
        this.minesweeper = minesweeper;

        this.width = width;
        this.height = height;
        this.mines = mines;

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
        gameOverText.setVisible(false);

        frame.add(gameOverText);

        frame.setComponentZOrder(gameOverText, 0);
        frame.setComponentZOrder(panel, 1);

        makeGrid(width, height);

        display();
    }

    public void makeGrid(int width, int height) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.println("Tile created at " + j + ", " + i);

                final int row = i;
                final int col = j;

                JLabel tile = new JLabel();

                tile.setBackground(uncheckedColor);
                tile.setOpaque(true);

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }

                        if (SwingUtilities.isLeftMouseButton(e)) {
                            tileClicked(row, col);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            tileFlagged(row, col);
                        }
                    }
                });

                panel.add(tile);
            }
        }
    }

    public void tileClicked(int row, int column) {
        if (gameGenerated) {
            System.out.println("Tile at " + column + ", " + row + " clicked");

            if (minesweeper.getFlagged()[row][column]) {
                System.out.println("Tile Flagged");
                return;
            }

            boolean visitedAlready = minesweeper.getVisited()[row][column];

            minesweeper.visitTile(row, column);
            drawScreen();

            if (minesweeper.getTile(row, column) == 1) {
                System.out.println("Game Over");
                gameLost();
            } else {
                if (visitedAlready && (minesweeper.getNearMines(row, column) - minesweeper.getNearFlags(row, column) <= 0)) {
                    minesweeper.visitAdjacentTiles(row, column);
                    drawScreen();

                    if (minesweeper.getNearUnflaggedMines(row, column) > 0) {
                        System.out.println("Game Over");
                        gameLost();
                    }
                }
                System.out.println("Safe");
            }
        } else {
            System.out.println("Generating Game...");
            
            this.minesweeper = new Minesweeper(width, height, mines, row, column);
            gameGenerated = true;

            System.out.println("Game Generated");

            minesweeper.visitTile(row, column);
            

            drawScreen();
        }
    }

    public void tileFlagged(int row, int column) {
        System.out.println("Tile at " + column + ", " + row + " flagged");

        minesweeper.flagTile(row, column);
        drawScreen();
    }

    public void gameLost() {
        gameOver = true;

        AtomicInteger countdown = new AtomicInteger(5);

        Thread countdownThread = new Thread(() -> {
            gameOverText.setVisible(true);
            gameOverText.setOpaque(true);
            gameOverText.setBackground(Color.RED);
            gameOverText.setForeground(Color.WHITE);

            frame.setComponentZOrder(panel, 1);
            frame.setComponentZOrder(gameOverText, 1);

            while (countdown.intValue() > 0) {
                System.out.println("Application will close in " + countdown);
                countdown.decrementAndGet();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.exit(1);
                }
                    
            }

            System.exit(0);
        });

        countdownThread.start();
    }

    public void drawScreen() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.println("Drawing tile at " + i + ", " + j);
                System.out.println("Tile Visited: " + minesweeper.getVisited()[i][j] + "\n");

                JLabel tile = (JLabel) panel.getComponent(i * width + j);

                if (minesweeper.getVisited()[i][j]) {
                    switch (minesweeper.getGrid()[i][j]) {
                        case 1 -> {
                            tile.setBackground(bombColor);
                            tile.setForeground(Color.WHITE);
                            
                            // Scale the icon to fit the tile
                            Image image = boomImage.getImage();
                            Image scaledImage = image.getScaledInstance(tile.getWidth(), tile.getHeight(), Image.SCALE_SMOOTH);
                            tile.setIcon(new ImageIcon(scaledImage));
                        }
                        case 0 -> {
                            tile.setBackground(checkedColor);

                            int nearmines = minesweeper.getNearMines(i, j);
                            String text = nearmines == 0 ? "" : String.valueOf(nearmines);

                            tile.setText(text);

                            // Make a border around the tile, but only on the sides with unchecked neghbhors
                            boolean[] borderSides = new boolean[4];

                            System.out.println("Checking if " + (i - 1) + " is greater than 0: " + (i - 1 >= 0));
                            if (i - 1 >= 0 && !minesweeper.getVisited()[i - 1][j] || (minesweeper.getVisited()[i - 1][j] && minesweeper.getGrid()[i - 1][j] == 1)) {borderSides[0] = true;}
                            if (j - 1 >= 0 && !minesweeper.getVisited()[i][j - 1] || (minesweeper.getVisited()[i - 1][j] && minesweeper.getGrid()[i][j - 1] == 1)) {borderSides[1] = true;}
                            if (i + 1 < minesweeper.getWidth() && !minesweeper.getVisited()[i + 1][j] || (minesweeper.getVisited()[i - 1][j] && minesweeper.getGrid()[i + 1][j] == 1)) {borderSides[2] = true;}
                            if (j + 1 < minesweeper.getHeight() && !minesweeper.getVisited()[i][j + 1] || (minesweeper.getVisited()[i - 1][j] && minesweeper.getGrid()[i][j + 1] == 1)) {borderSides[3] = true;}

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
                    if (minesweeper.getFlagged()[i][j]) {
                        tile.setBackground(checkedColor);
                        
                        Image image = flagImage.getImage();
                        Image scaledImage = image.getScaledInstance(tile.getWidth(), tile.getHeight(), Image.SCALE_SMOOTH);
                        tile.setIcon(new ImageIcon(scaledImage));
                    } else {
                        tile.setIcon(null);
                        tile.setBackground(uncheckedColor);
                        tile.setText("");
                    }
                }
            }
        }
    }

    public void display() {frame.setVisible(true);}
}
