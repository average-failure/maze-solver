package solver;

import java.util.PriorityQueue;
import java.util.Queue;

import generator.MazeGenerator.Maze;
import generator.MazeGenerator.Tile;
import main.App;

public class MazeSolver {

    private class Position {

        final int row;
        final int column;
        Position previous;
        int f;
        int g;
        final int h;

        Position(final int row, final int column, final int g, final Position previous) {
            this.row = row;
            this.column = column;
            this.g = g;
            this.previous = previous;
            h = manhattanDistance(maze.endRow(), maze.endColumn(), row, column);
            f = g + h;
        }

        private static int manhattanDistance(final int x1, final int y1, final int x2, final int y2) {
            return Math.abs(x2 - x1) + Math.abs(y2 - y1);
        }
    }

    private final Maze maze;
    private final Position[][] solution;

    public MazeSolver(final Maze maze) {
        this.maze = maze;
        solution = new Position[maze.rows()][maze.columns()];
    }

    public Maze solve() {
        final Queue<Position> frontier = new PriorityQueue<>((a, b) -> a.f - b.f);
        frontier.add(new Position(maze.startRow(), maze.startColumn(), 0, null));

        int count = 0;
        while (!frontier.isEmpty()) {
            final Position current = frontier.remove();
            if (current.row == maze.endRow() && current.column == maze.endColumn())
                break;

            maze.grid()[current.row][current.column] = Tile.CHECKED;

            checkNeighbours(frontier, current);

            if (count++ % 2 == 0)
                main.App.print(maze.grid(), 20); // For animation
        }

        tracePath();

        return maze;
    }

    private void checkNeighbours(final Queue<Position> frontier, final Position current) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                final int nextRow = current.row + i;
                final int nextColumn = current.column + j;

                if (i == 0 && j == 0 || i != 0 && j != 0 || !isValid(nextRow, nextColumn)
                        || isBlocked(nextRow, nextColumn))
                    continue;
                if (solution[nextRow] == null)
                    solution[nextRow] = new Position[maze.columns()];

                final Position nextPosition = solution[nextRow][nextColumn];
                final Position newNextPosition = new Position(nextRow, nextColumn, current.g + 1, current);
                if (nextPosition == null || nextPosition.f > newNextPosition.f) {
                    solution[nextRow][nextColumn] = newNextPosition;
                    frontier.add(newNextPosition);
                }
            }
        }
    }

    private boolean isValid(final int row, final int column) {
        return row >= 0 && row < maze.rows() && column >= 0 && column < maze.columns();
    }

    private boolean isBlocked(final int row, final int column) {
        final Tile tile = maze.grid()[row][column];
        return !(tile == Tile.PATH || tile == Tile.END);
    }

    private void tracePath() {
        maze.grid()[maze.endRow()][maze.endColumn()] = Tile.END;

        Position current = solution[maze.endRow()][maze.endColumn()].previous;
        while (current != null) {
            maze.grid()[current.row][current.column] = Tile.SOLUTION;
            current = current.previous;

            App.print(maze.grid(), 20); // For animation
        }

        maze.grid()[maze.startRow()][maze.startColumn()] = Tile.START;
    }
}
