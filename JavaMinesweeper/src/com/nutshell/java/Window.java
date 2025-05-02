package com.nutshell.java;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.concurrent.atomic.*;
import javax.swing.*;
import javax.swing.border.Border;

public class Window {
    JFrame frame;
    JPanel panel;
    JLabel gameOverText;
    JLabel infoText;

    int width,height,mines;
    Minesweeper minesweeper;
    boolean gameGenerated = false;

    Color uncheckedColor = new Color(143, 213, 255);
    Color checkedColor = new Color(204, 234, 252);
    Color bombColor = Color.RED;

    Color correctFlagColor = new Color(0, 255, 0);
    Color incorrectFlagColor = new Color(255, 0, 0);

    // Get Images
    URL bombURL = getClass().getClassLoader().getResource("com/nutshell/resources/images/bomb.png");
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
        
        panel.setLayout(new GridLayout(width, height));

        gameOverText = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverText.setFont(new Font("Arial", Font.BOLD, 30));
        gameOverText.setOpaque(false);
        gameOverText.setVisible(false);

        infoText = new JLabel("0:00 | Flags: 0", SwingConstants.CENTER);
        infoText.setFont(new Font("Arial", Font.BOLD, 20));
        infoText.setOpaque(false);
        infoText.setVisible(true);

        
        frame.add(gameOverText, BorderLayout.SOUTH);
        frame.add(infoText, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        makeGrid(width, height);

        display();
    }

    public void startTimer() {
        AtomicInteger time = new AtomicInteger(0);

        // Start the timer
        Thread timerThread = new Thread(() -> {
            while (!gameOver) {
                try {
                    Thread.sleep(1000);
                    
                    time.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        timerThread.start();
        
        Thread updateThread = new Thread(() -> {
            while (!gameOver) {
                int minutes = time.get() / 60;
                int seconds = time.get() % 60;

                infoText.setText(String.format("%02d:%02d | Flags: %d", minutes, seconds, (mines - minesweeper.totalFlags())));
            }
            
        });
    
        updateThread.start();
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
                gameFinished(false);
            } else {
                if (visitedAlready && (minesweeper.getNearMines(row, column) - minesweeper.getNearFlags(row, column) <= 0)) {
                    minesweeper.visitAdjacentTiles(row, column);
                    drawScreen();

                    if (minesweeper.getNearUnflaggedMines(row, column) > 0) {
                        System.out.println("Game Over");
                        gameFinished(false);
                    }
                }
            }

            if (minesweeper.totalVisited() == (width * height) - mines && !gameOver) {
                System.out.println("Game Won");

                if (minesweeper.totalFlags() != mines) {
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            if (minesweeper.getGrid()[i][j] == 1 && !minesweeper.getFlagged()[i][j]) {
                                minesweeper.flagTile(i, j);
                            }
                        }
                    }
                }

                gameFinished(true);
            }
        } else {
            this.minesweeper = new Minesweeper(width, height, mines, row, column);
            gameGenerated = true;

            minesweeper.visitTile(row, column);
            
            drawScreen();
            startTimer();
        }
    }

    public void tileFlagged(int row, int column) {
        if (minesweeper.getVisited()[row][column]) {
            return;
        }

        if (minesweeper.getFlagged()[row][column]) {
            minesweeper.unflagTile(row, column);
        } else {
            minesweeper.flagTile(row, column);
        }
        
        drawScreen();
    }

    public void gameFinished(boolean won) {
        gameOver = true;
        gameWon = won;

        drawScreen();

        AtomicInteger countdown = new AtomicInteger(5);

        gameOverText.setVisible(true);
        gameOverText.setOpaque(true);
        gameOverText.setBackground(Color.RED);

        if (won) {
            gameOverText.setText("You won!");
            gameOverText.setBackground(Color.GREEN);
        } else {
            gameOverText.setText("Game Over!");
        }

        gameOverText.setForeground(Color.WHITE);
    }

    public void drawScreen() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
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
                            tile.setForeground(Color.BLACK);
                            tile.setFont(new Font("Arial", Font.BOLD, (tile.getWidth() / 2) - 5)); // Set font size based on tile width
                            tile.setHorizontalAlignment(SwingConstants.CENTER);
                            tile.setVerticalAlignment(SwingConstants.CENTER);

                            // Make a border around the tile, but only on the sides with unchecked neghbhors
                            boolean[] borderSides = new boolean[4];

                            if (i - 1 >= 0) { if (!minesweeper.getVisited()[i - 1][j] || (minesweeper.getVisited()[i - 1][j] && minesweeper.getGrid()[i - 1][j] == 1)) {borderSides[0] = true;}}
                            if (j - 1 >= 0) { if (!minesweeper.getVisited()[i][j - 1] || (minesweeper.getVisited()[i][j - 1] && minesweeper.getGrid()[i][j - 1] == 1)) {borderSides[1] = true;}}
                            if (i + 1 < minesweeper.getWidth()) {if (!minesweeper.getVisited()[i + 1][j] || (minesweeper.getVisited()[i + 1][j] && minesweeper.getGrid()[i + 1][j] == 1)) {borderSides[2] = true;}}
                            if (j + 1 < minesweeper.getHeight()) { if (!minesweeper.getVisited()[i][j + 1] || (minesweeper.getVisited()[i][j + 1] && minesweeper.getGrid()[i][j + 1] == 1)) {borderSides[3] = true;}}

                            Border border1 = BorderFactory.createCompoundBorder(
                                borderSides[0] ? BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLUE) : BorderFactory.createEmptyBorder(),
                                borderSides[1] ? BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLUE) : BorderFactory.createEmptyBorder()
                            );

                            Border border2 = BorderFactory.createCompoundBorder(
                                borderSides[2] ? BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLUE) : BorderFactory.createEmptyBorder(),
                                borderSides[3] ? BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLUE) : BorderFactory.createEmptyBorder()
                            );

                            Border border = BorderFactory.createCompoundBorder(border1, border2);

                            tile.setBorder(border);
                        }
                    }
                    
                } else {
                    if (minesweeper.getFlagged()[i][j]) {
                        if (gameOver && !gameWon) {
                            if (minesweeper.getGrid()[i][j] == 1) {
                                tile.setBackground(correctFlagColor);
                            } else {
                                tile.setBackground(incorrectFlagColor);
                            }
                        } else {
                            tile.setBackground(checkedColor);
                        }
                        
                        Image image = flagImage.getImage();
                        Image scaledImage = image.getScaledInstance(tile.getWidth(), tile.getHeight(), Image.SCALE_SMOOTH);
                        tile.setIcon(new ImageIcon(scaledImage));
                    } else if (minesweeper.getGrid()[i][j] == 1 && gameOver && !gameWon) {
                        tile.setBackground(Color.RED);

                        Image image = bombImage.getImage();
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
