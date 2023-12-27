package generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MazeGenerator {

    public enum Tile {
        PATH, WALL, START, END, CHECKED, SOLUTION
    }

    private class Position {

        int row;
        int column;
        Position previous;

        Position(final int x, final int y, final Position previous) {
            this.row = x;
            this.column = y;
            this.previous = previous;
        }

        Position getNext() {
            if (previous.row < row) {
                return new Position(row + 1, column, this);
            } else if (previous.row > row) {
                return new Position(row - 1, column, this);
            } else if (previous.column < column) {
                return new Position(row, column + 1, this);
            } else if (previous.column > column) {
                return new Position(row, column - 1, this);
            }
            throw new IllegalArgumentException("Previous position must not be same as current position");
        }
    }

    public record Maze(Tile[][] grid, int rows, int columns, int startRow, int startColumn, int endRow,
            int endColumn) {}

    private static final Random random = new Random();

    private final Tile[][] grid;
    private final int rows;
    private final int columns;

    public MazeGenerator(final int rows, final int columns) {
        if (rows < 3 || columns < 3)
            throw new IllegalArgumentException("Grid must be at least 3x3");
        this.rows = rows - 2;
        this.columns = columns - 2;
        this.grid = new Tile[this.rows][this.columns];
        for (final Tile[] row : grid)
            Arrays.fill(row, Tile.WALL);
    }

    public Maze generate() {
        final Position start = new Position(1, 1, null);
        grid[start.row][start.column] = Tile.START;

        final ArrayList<Position> frontier = new ArrayList<>();
        frontier.addAll(getAdjacentElements(start));

        int count = 0;
        while (!frontier.isEmpty()) {
            final Position current = frontier.remove(random.nextInt(frontier.size()));
            final Position next = current.getNext();
            if (!isValid(next.row, next.column) || grid[next.row][next.column] != Tile.WALL)
                continue;
            grid[current.row][current.column] = Tile.PATH;
            grid[next.row][next.column] = Tile.PATH;
            frontier.addAll(getAdjacentElements(next));
            if (count++ % 4 == 0)
                main.App.print(grid, 20); // For animation
        }

        int endRow = rows - 1;
        int endColumn = columns - 1;
        while (grid[endRow][endColumn] != Tile.PATH) {
            endRow--;
            endColumn--;
        }
        grid[endRow][endColumn] = Tile.END;

        final int trueRows = rows + 2;
        final int trueColumns = columns + 2;

        final Tile[][] maze = new Tile[trueRows][trueColumns];
        for (int i = 0; i < trueRows; i++) {
            for (int j = 0; j < trueColumns; j++) {
                if (i == 0 || i == rows + 1 || j == 0 || j == columns + 1)
                    maze[i][j] = Tile.WALL;
                else
                    maze[i][j] = grid[i - 1][j - 1];
            }
        }

        return new Maze(maze, trueRows, trueColumns, start.row + 1, start.column + 1, endRow + 1, endColumn + 1);
    }

    private ArrayList<Position> getAdjacentElements(final Position position) {
        final ArrayList<Position> adjacents = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0 || i != 0 && j != 0 || !isValid(position.row + i, position.column + j)
                        || grid[position.row + i][position.column + j] != Tile.WALL)
                    continue;
                adjacents.add(new Position(position.row + i, position.column + j, position));
            }
        }

        return adjacents;
    }

    private boolean isValid(final int row, final int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }
}
