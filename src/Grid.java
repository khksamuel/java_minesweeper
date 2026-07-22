import java.util.Random;

public class Grid {
    private Cell[][] gridCells;
    public int hiddenCellsCount;

    Grid(int dimensions) {
        // constructor to initialize the grid with the specified dimensions (assuming a
        // square grid)
        // The grid will be represented as a 2D array of cell objects

        // blank 2D array of cell objects
        this.gridCells = new Cell[dimensions][dimensions];
        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++) {
                gridCells[i][j] = new Cell();
            }
        }

        // assign bombs 10% of the cells
        int bombCount = (int) (dimensions * dimensions * 0.1);
        Random rand = new Random();
        for (int i = 0; i < bombCount; i++) {
            int x = rand.nextInt(dimensions);
            int y = rand.nextInt(dimensions);

            // Keep sampling until we find a cell that does not already contain a bomb.
            while (gridCells[x][y].isBomb) {
                x = rand.nextInt(dimensions);
                y = rand.nextInt(dimensions);
            }
            gridCells[x][y].setBomb(true);
            gridCells[x][y].setAlive(false);

            // setting the neighbourBombs count for the neighbouring cells
            int[][] neighbourCoords = getNeighbourCoords(x, y);
            for (int[] coord : neighbourCoords) {
                int neighbourX = coord[0];
                int neighbourY = coord[1];
                gridCells[neighbourX][neighbourY]
                        .setNeighbourBombs(gridCells[neighbourX][neighbourY].neighbourBombs + 1);
            }
        }
        this.hiddenCellsCount = dimensions * dimensions - bombCount;
    }

    int[][] getNeighbourCoords(int x, int y) {
        // method to get the coordinates of neighbouring cells for a given cell
        // This method will return a 2D array of coordinates for the 8 surrounding cells
        int[][] coords = new int[8][2];
        int index = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue; // skip the cell itself
                int newX = x + i;
                int newY = y + j;
                // out of bound checks
                if (newX >= 0 && newX < gridCells.length && newY >= 0 && newY < gridCells[0].length) {
                    coords[index][0] = newX;
                    coords[index][1] = newY;
                    index++;
                }
            }
        }
        return coords;
    }

    int revealCell(int x, int y) {
        // method to reveal the cell at the given coordinates
        if (x < 0 || x >= gridCells.length || y < 0 || y >= gridCells[0].length) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }

        gridCells[x][y].isRevealed = true;
        // Returns -1 for bomb, 1 for alive, 0 for dead
        int result = gridCells[x][y].reveal();
        if (result == 1 && gridCells[x][y].neighbourBombs == 0) {
            // If the cell is alive and has no neighbouring bombs, reveal its neighbours
            int[][] neighbourCoords = getNeighbourCoords(x, y);
            for (int[] coord : neighbourCoords) {
                int neighbourX = coord[0];
                int neighbourY = coord[1];
                if (!gridCells[neighbourX][neighbourY].isRevealed) {
                    // recursively reveal the neighbouring cell (including its neighbours if it has
                    // no neighbouring bombs)
                    revealCell(neighbourX, neighbourY);
                    this.hiddenCellsCount--;
                }
            }
        }
        this.hiddenCellsCount--;
        return result;
    }
}
