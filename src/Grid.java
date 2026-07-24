import java.util.Random;
import java.util.Arrays;

public class Grid implements GameBoard {
    // Reveal result codes
    public static final int BOMB = -1;
    public static final int SAFE = 1;
    public static final int ALREADY_HANDLED = 2;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_WHITE = "\u001B[37m";
    // 256-color orange. Falls back to terminal default if unsupported.
    private static final String ANSI_ORANGE = "\u001B[38;5;208m";

    private Cell[][] gridCells;
    int hiddenCellsCount;

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
        }

        // Set neighbor bomb counts for all cells
        for (int i = 0; i < gridCells.length; i++) {
            for (int j = 0; j < gridCells[0].length; j++) {
                if (!gridCells[i][j].isBomb) {
                    int count = 0;
                    int[][] neighbourCoords = getNeighbourCoords(i, j);
                    for (int[] coord : neighbourCoords) {
                        if (gridCells[coord[0]][coord[1]].isBomb) {
                            count++;
                        }
                    }
                    gridCells[i][j].setNeighbourBombs(count);
                }
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
        return Arrays.copyOf(coords, index);
    }

    @Override
    public int revealCell(int x, int y) {
        // method to reveal the cell at the given coordinates
        if (x < 0 || x >= gridCells.length || y < 0 || y >= gridCells[0].length) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }

        Cell cell = gridCells[x][y];

        // Ignore repeated reveals and flagged cells.
        if (cell.isRevealed || cell.isFlagged) {
            return ALREADY_HANDLED;
        }

        cell.isRevealed = true;

        if (cell.isBomb) {
            return BOMB;
        }

        // Count down once per newly revealed safe cell.
        this.hiddenCellsCount--;

        if (cell.neighbourBombs == 0) {
            // If the cell is safe and has no neighbouring bombs, reveal neighbours
            // recursively.
            int[][] neighbourCoords = getNeighbourCoords(x, y);
            for (int[] coord : neighbourCoords) {
                int neighbourX = coord[0];
                int neighbourY = coord[1];
                if (!gridCells[neighbourX][neighbourY].isBomb) {
                    revealCell(neighbourX, neighbourY);
                }
            }
        }

        return SAFE;
    }

    @Override
    public void flagCell(int x, int y) {
        // method to flag the cell at the given coordinates
        if (x < 0 || x >= gridCells.length || y < 0 || y >= gridCells[0].length) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }

        Cell cell = gridCells[x][y];
        cell.flag();
    }

    @Override
    public void display() {
        // method to display the grid in the console
        int rows = gridCells.length;
        int cols = gridCells[0].length;
        int rowLabelWidth = String.valueOf(rows - 1).length();
        int colLabelWidth = String.valueOf(cols - 1).length();
        int cellWidth = Math.max(1, colLabelWidth);

        StringBuilder horizontalBar = new StringBuilder("+");
        for (int i = 0; i < cols; i++) {
            horizontalBar.append("-").append("-".repeat(cellWidth)).append("-+");
        }

        // Print column indices above the board.
        StringBuilder colHeader = new StringBuilder(" ".repeat(rowLabelWidth + 2));
        for (int j = 0; j < cols; j++) {
            colHeader.append(" ").append(String.format("%" + cellWidth + "d", j)).append("  ");
        }
        System.out.println(colHeader);

        for (int i = 0; i < rows; i++) {
            System.out.println(" ".repeat(rowLabelWidth + 1) + horizontalBar);
            StringBuilder row = new StringBuilder("|");

            for (int j = 0; j < cols; j++) {
                String symbol;
                if (gridCells[i][j].isFlagged) {
                    symbol = "F"; // Display "F" for flagged cells
                } else if (gridCells[i][j].isRevealed) {
                    if (gridCells[i][j].isBomb) {
                        symbol = "*";
                    } else if (gridCells[i][j].neighbourBombs > 0) {
                        symbol = String.valueOf(gridCells[i][j].neighbourBombs);
                    } else {
                        // if the cell is revealed and has no neighbouring bombs, display ""
                        symbol = " ";
                    }
                } else {
                    // if the cell is not revealed, display "X"
                    symbol = "X";
                }

                String paddedSymbol = String.format("%" + cellWidth + "s", symbol);
                row.append(" ")
                        .append(getColorForSymbol(symbol))
                        .append(paddedSymbol)
                        .append(ANSI_RESET)
                        .append(" |");
            }
            System.out.println(String.format("%" + rowLabelWidth + "d ", i) + row);
        }
        System.out.println(" ".repeat(rowLabelWidth + 1) + horizontalBar);
    }

    @Override
    public int getDisplayLineCount() {
        int rows = gridCells.length;
        // 1 column-header line + one horizontal bar per row + one row line per row +
        // final bar.
        return (2 * rows) + 2;
    }

    private String getColorForSymbol(String symbol) {
        if ("F".equals(symbol)) {
            return ANSI_RED;
        }
        if ("*".equals(symbol)) {
            return ANSI_RED;
        }
        if ("1".equals(symbol)) {
            return ANSI_GREEN;
        }
        if ("2".equals(symbol)) {
            return ANSI_YELLOW;
        }
        if ("X".equals(symbol)) {
            return ANSI_WHITE;
        }

        // 3 or more revealed neighbouring bombs.
        try {
            int value = Integer.parseInt(symbol.trim());
            if (value >= 3) {
                return ANSI_ORANGE;
            }
        } catch (NumberFormatException ex) {
            // Non-numeric symbols (such as blank) use default color.
        }

        return ANSI_RESET;
    }

    @Override
    public boolean isWon() {
        return hiddenCellsCount == 0;
    }
}
