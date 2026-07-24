import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class GridTest {
    @Test
    void getNeighbourCoordsCenterHasEightNeighbours() {
        Grid grid = new Grid(3);

        int[][] neighbours = grid.getNeighbourCoords(1, 1);

        assertEquals(8, neighbours.length);

        Set<String> actual = toCoordSet(neighbours);
        Set<String> expected = Set.of(
                "0,0", "0,1", "0,2",
                "1,0", "1,2",
                "2,0", "2,1", "2,2");
        assertEquals(expected, actual);
    }

    @Test
    void getNeighbourCoordsCornerHasThreeNeighbours() {
        Grid grid = new Grid(3);

        int[][] neighbours = grid.getNeighbourCoords(0, 0);

        assertEquals(3, neighbours.length);
        Set<String> actual = toCoordSet(neighbours);
        Set<String> expected = Set.of("0,1", "1,0", "1,1");
        assertEquals(expected, actual);
    }

    @Test
    void revealCellThrowsForOutOfBoundsCoordinates() {
        Grid grid = new Grid(3);

        assertThrows(IllegalArgumentException.class, () -> grid.revealCell(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> grid.revealCell(0, -1));
        assertThrows(IllegalArgumentException.class, () -> grid.revealCell(3, 0));
        assertThrows(IllegalArgumentException.class, () -> grid.revealCell(0, 3));
    }

    @Test
    void flagCellThrowsForOutOfBoundsCoordinates() {
        Grid grid = new Grid(3);

        assertThrows(IllegalArgumentException.class, () -> grid.flagCell(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> grid.flagCell(0, -1));
        assertThrows(IllegalArgumentException.class, () -> grid.flagCell(3, 0));
        assertThrows(IllegalArgumentException.class, () -> grid.flagCell(0, 3));
    }

    @Test
    void revealCellOnBombReturnsGameOverCode() throws Exception {
        Grid grid = deterministicGridWithBombAt(1, 1, 3);

        int result = grid.revealCell(1, 1);

        assertEquals(Grid.BOMB, result);
    }

    @Test
    void revealCellRecursivelyRevealsConnectedZeroCells() {
        Grid grid = new Grid(3);

        int result = grid.revealCell(1, 1);

        assertEquals(Grid.SAFE, result);
        assertEquals(0, grid.hiddenCellsCount);
    }

    @Test
    void revealCellReturnsAlreadyHandledCodeForFlaggedOrRevealedCell() {
        Grid grid = new Grid(3);

        grid.flagCell(1, 1);
        assertEquals(Grid.ALREADY_HANDLED, grid.revealCell(1, 1));

        grid.flagCell(1, 1);
        assertEquals(Grid.SAFE, grid.revealCell(1, 1));
        assertEquals(Grid.ALREADY_HANDLED, grid.revealCell(1, 1));
    }

    @Test
    void flagCellTogglesFlagState() throws Exception {
        Grid grid = new Grid(3);
        Cell[][] cells = readCells(grid);

        assertFalse(cells[1][1].isFlagged);

        grid.flagCell(1, 1);
        assertTrue(cells[1][1].isFlagged);

        grid.flagCell(1, 1);
        assertFalse(cells[1][1].isFlagged);
    }

    private static Grid deterministicGridWithBombAt(int bombX, int bombY, int size) throws Exception {
        Grid grid = new Grid(size);

        Cell[][] cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }

        cells[bombX][bombY].setBomb(true);

        int[][] neighbours = new int[][] {
                { bombX - 1, bombY - 1 }, { bombX - 1, bombY }, { bombX - 1, bombY + 1 },
                { bombX, bombY - 1 }, { bombX, bombY + 1 },
                { bombX + 1, bombY - 1 }, { bombX + 1, bombY }, { bombX + 1, bombY + 1 }
        };

        for (int[] n : neighbours) {
            int x = n[0];
            int y = n[1];
            if (x >= 0 && x < size && y >= 0 && y < size) {
                cells[x][y].setNeighbourBombs(cells[x][y].neighbourBombs + 1);
            }
        }

        Field cellsField = Grid.class.getDeclaredField("gridCells");
        cellsField.setAccessible(true);
        cellsField.set(grid, cells);

        grid.hiddenCellsCount = (size * size) - 1;
        return grid;
    }

    private static Cell[][] readCells(Grid grid) throws Exception {
        Field cellsField = Grid.class.getDeclaredField("gridCells");
        cellsField.setAccessible(true);
        return (Cell[][]) cellsField.get(grid);
    }

    private static Set<String> toCoordSet(int[][] coords) {
        Set<String> coordSet = new HashSet<>();
        for (int[] c : coords) {
            coordSet.add(c[0] + "," + c[1]);
        }
        return coordSet;
    }
}
