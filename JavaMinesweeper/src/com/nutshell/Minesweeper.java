package com.nutshell;

public class Minesweeper {
    private int[][] grid;
    private boolean[][] visited;

    public Minesweeper(int rows, int columns, int mines) {
        grid = new int[rows][columns];
        visited = new boolean[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                grid[row][column] = 0;
                visited[row][column] = false;
            }
        }

        int minesLeft = mines;
        while (minesLeft > 0) {
            int row = (int) (Math.random() * grid.length);
            int column = (int) (Math.random() * grid[row].length);

            // System.out.println(row + " " + column);

            if (grid[row][column] == 0 && (row != (int) ((double) grid.length / 2) || column != (int) ((double) grid[row].length / 2 + 0.5))) {
                grid[row][column] = 1;

                minesLeft--;
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
        visited[row][column] = true;

        if (grid[row][column] != 1 && getNearMines(row, column) == 0) {
            visitAdjacentTiles(row, column);
        }
    }

    public void visitAdjacentTiles(int row, int column) {
        if (row - 1 >= 0) {
            if (column - 1 >= 0) {
                if (grid[row - 1][column - 1] != 2 && !visited[row - 1][column - 1]) {
                    visitTile(row - 1, column - 1);
                }
            }

            if (grid[row - 1][column] != 2 && !visited[row - 1][column]) {
                visitTile(row - 1, column);
            }

            if (column + 1 < grid[row].length) {
                if (grid[row - 1][column + 1] != 2 && !visited[row - 1][column + 1]) {
                    visitTile(row - 1, column + 1);
                }
            }
        }

        if (column - 1 >= 0) {
            if (grid[row][column - 1] != 2 && !visited[row][column - 1]) {
                visitTile(row, column - 1);
            }
        }

        if (column + 1 < grid[row].length) {
            if (grid[row][column + 1] != 2 && !visited[row][column + 1]) {
                visitTile(row, column + 1);
            }
        }

        if (row + 1 < grid.length) {
            if (column - 1 >= 0) {
                if (grid[row + 1][column - 1] != 2 && !visited[row + 1][column - 1]) {
                    visitTile(row + 1, column - 1);
                }
            }

            if (grid[row + 1][column] != 2 && !visited[row + 1][column]) {
                visitTile(row + 1, column);
            }

            if (column + 1 < grid[row].length) {
                if (grid[row + 1][column + 1] != 2 && !visited[row + 1][column + 1]) {
                    visitTile(row + 1, column + 1);
                }
            }
        }
    }

    public void flagTile(int row, int column) {grid[row][column] = 2;}

    public int getNearMines(int row, int column) {
        int mines = 0;

        if (row - 1 >= 0) {
            if (column - 1 >= 0) {
                if (grid[row - 1][column - 1] != 2) {
                    mines += grid[row - 1][column - 1];
                }
            }

            if (grid[row - 1][column] != 2) {
                mines += grid[row - 1][column];
            }

            if (column + 1 < grid[row].length) {
                if (grid[row - 1][column + 1] != 2) {
                    mines += grid[row - 1][column + 1];
                }
            }
        }

        if (column - 1 >= 0) {
            if (grid[row][column - 1] != 2) {
                mines += grid[row][column - 1];
            }
        }

        if (column + 1 < grid[row].length) {
            if (grid[row][column + 1] != 2) {
                mines += grid[row][column + 1];
            }
        }

        if (row + 1 < grid.length) {
            if (column - 1 >= 0) {
                if (grid[row + 1][column - 1] != 2) {
                    mines += grid[row + 1][column - 1];
                }
            }

            if (grid[row + 1][column] != 2) {
                mines += grid[row + 1][column];
            }

            if (column + 1 < grid[row].length) {
                if (grid[row + 1][column + 1] != 2) {
                    mines += grid[row + 1][column + 1];
                }
            }
        }

        return mines;
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
                            if (grid[row][column] == 2) {
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
