import java.util.Random;

public class Grid {
    private Cell[][] gridCells;

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
        for (int i = 0; i < bombCount; i++) {
            Random rand = new Random();
            int x = rand.nextInt(dimensions);
            int y = rand.nextInt(dimensions);
            gridCells[x][y].setBomb(true);
        }

    }

    int getNeighbourBombs(int x, int y) {
        // method to calculate the number of neighbouring bombs for a given cell
        // This method will check the 8 surrounding cells and count how many are bombs
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue; // skip the cell itself
                int newX = x + i;
                int newY = y + j;
                // out of bound checks
                if (newX >= 0 && newX < gridCells.length && newY >= 0 && newY < gridCells[0].length) {
                    if (gridCells[newX][newY].isBomb) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
