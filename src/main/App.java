package main;

import generator.MazeGenerator;
import generator.MazeGenerator.Tile;
import solver.MazeSolver;

public class App {

    private static int previousGridSize;

    public static void main(final String[] args) {
        print(new MazeSolver(new MazeGenerator(75, 75).generate()).solve().grid(), 0);
    }

    public static void print(final Tile[][] grid, final long delay) {
        if (previousGridSize > 0)
            System.out.print("\033[" + previousGridSize + "F\033[0J");
        previousGridSize = grid.length;

        for (final Tile[] row : grid) {
            for (final Tile tile : row) {
                switch (tile) {
                    case PATH -> System.out.print("  ");
                    case WALL -> System.out.print("██");
                    case CHECKED -> System.out.print("░░");
                    case SOLUTION -> System.out.print("▓▓");
                    case START -> System.out.print("ST");
                    case END -> System.out.print("ED");
                    default -> throw new IllegalArgumentException("Unknown tile: " + tile);
                }
            }
            System.out.println();
        }

        try {
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
