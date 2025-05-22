package com.nutshell.java;

import java.util.ArrayList;

public class Minesweeper {
    private int[][] grid;
    private boolean[][] visited;
    private boolean[][] flagged;
    private int minesLeft;

    public Minesweeper(int rows, int columns, int mines, int startingRow, int startingColumn) {
        System.out.println(rows + " " + columns + " " + mines + " " + startingRow + " " + startingColumn);

        grid = new int[rows][columns];
        visited = new boolean[rows][columns];
        flagged = new boolean[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                grid[row][column] = 0;
                visited[row][column] = false;
                flagged[row][column] = false;
            }
        }

        int plantCount = mines;
        while (plantCount > 0) {
            int row = (int) (Math.random() * grid.length);
            int column = (int) (Math.random() * grid[row].length);

            // System.out.println(row + " " + column);
            int rowDiff = Math.abs(row - startingRow);
            int columnDiff = Math.abs(column - startingColumn);

            boolean tooClose = (rowDiff <= 1 && columnDiff <= 1);

            if (grid[row][column] == 0 && !tooClose) {
                grid[row][column] = 1;

                plantCount--;
            }
        }
    }

    public int getWidth() {return grid.length;}

    public int getHeight() {return grid[0].length;}

    public int[][] getGrid() {return grid;}

    public void setGrid(int[][] grid) {this.grid = grid;}

    public int getTile(int row, int column) {return grid[row][column];}

    public boolean[][] getVisited() {return visited;}

    public void setVisited(boolean[][] visited) {this.visited = visited;}

    public void visitTile(int row, int column) {
        System.out.println("Visiting tile " + row + ", " + column);

        visited[row][column] = true;

        if (grid[row][column] != 1 && getNearMines(row, column) == 0) {
            visitAdjacentTiles(row, column);
        }
    }

    public void visitAdjacentTiles(int row, int column) {
        ArrayList<int[]> adjacentTiles = getAdjacentTiles(row, column);

        for (int[] tile : adjacentTiles) {
            int tileRow = tile[0];
            int tileColumn = tile[1];

            if (!visited[tileRow][tileColumn] && !flagged[tileRow][tileColumn]) {
                visited[tileRow][tileColumn] = true;

                if (getNearMines(tileRow, tileColumn) == 0) {
                    visitAdjacentTiles(tileRow, tileColumn);
                }
            }
        }
    }

    public ArrayList<int[]> getAdjacentTiles(int row, int column) {
        ArrayList<int[]> adjacentTiles = new ArrayList<>();

        if (row - 1 >= 0) {
            if (column - 1 >= 0) {
                adjacentTiles.add(new int[]{row - 1, column - 1});
            }

            adjacentTiles.add(new int[]{row - 1, column});

            if (column + 1 < grid[row].length) {
                adjacentTiles.add(new int[]{row - 1, column + 1});
            }
        }

        if (column - 1 >= 0) {
            adjacentTiles.add(new int[]{row, column - 1});
        }

        if (column + 1 < grid[row].length) {
            adjacentTiles.add(new int[]{row, column + 1});
        }

        if (row + 1 < grid.length) {
            if (column - 1 >= 0) {
                adjacentTiles.add(new int[]{row + 1, column - 1});
            }

            adjacentTiles.add(new int[]{row + 1, column});

            if (column + 1 < grid[row].length) {
                adjacentTiles.add(new int[]{row + 1, column + 1});
            }
        }

        return adjacentTiles;
    }

    public boolean[][] getFlagged() {return flagged;}

    public void flagTile(int row, int column) {flagged[row][column] = true;}

    public void unflagTile(int row, int column) {flagged[row][column] = false;}

    public int getMinesLeft() {return minesLeft;}

    public int getNearMines(int row, int column) {
        int mines = 0;

        ArrayList<int[]> adjacentTiles = getAdjacentTiles(row, column);

        for (int[] tile : adjacentTiles) {
            int tileRow = tile[0];
            int tileColumn = tile[1];

            if (grid[tileRow][tileColumn] == 1) {
                mines++;
            }
        }

        return mines;
    }

    public int getNearFlags(int row, int column) {
        int flags = 0;

        ArrayList<int[]> adjacentTiles = getAdjacentTiles(row, column);

        for (int[] tile : adjacentTiles) {
            int tileRow = tile[0];
            int tileColumn = tile[1];

            if (flagged[tileRow][tileColumn]) {
                flags++;
            }
        }

        return flags;
    }

    public int getNearUnflaggedMines(int row, int column) {
        int mines = 0;

        ArrayList<int[]> adjacentTiles = getAdjacentTiles(row, column);

        for (int[] tile : adjacentTiles) {
            int tileRow = tile[0];
            int tileColumn = tile[1];

            if (grid[tileRow][tileColumn] == 1 && !flagged[tileRow][tileColumn]) {
                mines++;
            }
        }

        return mines;
    }

    public int totalMinesLeft() {
        int totalMines = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                if (grid[row][column] == 1 && !flagged[row][column]) {
                    totalMines++;
                }
            }
        }

        return totalMines;
    }

    public int totalFlags() {
        int totalFlags = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                if (flagged[row][column]) {
                    totalFlags++;
                }
            }
        }

        return totalFlags;
    }

    public int totalVisited() {
        int totalVisited = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                if (visited[row][column]) {
                    totalVisited++;
                }
            }
        }

        return totalVisited;
    }

    public String toString(int type) {
        StringBuilder result = new StringBuilder();

        switch (type) {
            case 0:
                for (int row = 0; row < grid.length; row++) {
                    for (int column = 0; column < grid[row].length; column++) {
                        result.append(grid[row][column]).append("  ");
                    }
                    result.append("\n");
                }
                break;
            case 1:
                for (int row = 0; row < grid.length; row++) {
                    for (int column = 0; column < grid.length; column++) {
                        switch (grid[row][column]) {
                            case 0 -> result.append(getNearMines(row, column)).append("  ");
                            case 1 -> result.append("-1 ");
                            default -> result.append("9  ");
                        }

                    }
                    result.append("\n");
                }
                break;
            case 2:
                for (int row = 0; row < grid.length; row++) {
                    for (int column = 0; column < grid.length; column++) {
                        if (visited[row][column]) {
                            if (grid[row][column] == 0) {
                                result.append(getNearMines(row, column)).append("  ");
                            } else {
                                result.append("-1 ");
                            }
                        } else {
                            if (flagged[row][column]) {
                                result.append("9  ");
                            } else {
                                result.append("X  ");
                            }
                        }
                    }
                    result.append("\n");
                }
                break;
            default:
                break;
        }

        return result.toString();
    }

    public String toString() {return toString(0);}
}
