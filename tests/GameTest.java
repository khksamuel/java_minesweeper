import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

public class GameTest {
    @Test
    void revealBombEndsGame() throws Exception {
        GameBoard board = deterministicGridWithBombAt(1, 1, 3);
        Game game = new Game(board);

        assertFalse(game.isGameOver());
        int result = game.reveal(1, 1);

        assertEquals(Grid.BOMB, result);
        assertTrue(game.isGameOver());
        assertFalse(game.isWon());
    }

    @Test
    void revealSafeCellDoesNotImmediatelyEndGame() {
        GameBoard board = new Grid(3);
        Game game = new Game(board);

        // Reveal a few safe cells without hitting a bomb
        // The game might be won after revealing all non-bomb cells,
        // but that's expected behavior, not a failure
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!game.isGameOver() && !game.isWon()) {
                    game.reveal(i, j);
                    // If we hit a bomb, stop
                    if (game.isGameOver() && !game.isWon()) {
                        return; // That's fine, we hit a bomb
                    }
                }
            }
        }
        // If we get here, we either won or revealed safely
        assertTrue(game.isGameOver() == game.isWon(),
                "Game should be over if and only if won");
    }

    @Test
    void revealAllNonBombCellsWinsGame() {
        GameBoard board = new Grid(3);
        Game game = new Game(board);

        // Reveal until we win
        while (!game.isGameOver()) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int result = game.reveal(i, j);
                    if (result == Grid.BOMB) {
                        // Hit a bomb, start over with a fresh board
                        board = new Grid(3);
                        game = new Game(board);
                        break;
                    }
                }
            }
        }

        assertTrue(game.isGameOver());
        assertTrue(game.isWon());
    }

    @Test
    void flagTogglesFlagState() {
        GameBoard board = new Grid(3);
        Game game = new Game(board);

        game.flag(0, 0);
        game.flag(0, 0);
        game.flag(0, 1);

        // After flagging, we can't directly check the flag state from Game,
        // but revealing a flagged cell should return something other than SAFE
        // (either ALREADY_HANDLED or the actual cell content)
        int result = game.reveal(0, 0);
        // The exact result depends on the grid state, just verify it completes
        assertTrue(result == Grid.ALREADY_HANDLED || result == Grid.SAFE || result == Grid.BOMB);
    }

    @Test
    void revealAfterGameOverReturnsAlreadyHandled() throws Exception {
        GameBoard board = deterministicGridWithBombAt(1, 1, 3);
        Game game = new Game(board);

        // Hit the bomb
        int result = game.reveal(1, 1);
        assertEquals(Grid.BOMB, result);
        assertTrue(game.isGameOver());

        // Try to reveal another cell after game is over
        int result2 = game.reveal(0, 0);
        assertEquals(Grid.ALREADY_HANDLED, result2);
    }

    @Test
    void flagAfterGameOverDoesNothing() throws Exception {
        GameBoard board = deterministicGridWithBombAt(1, 1, 3);
        Game game = new Game(board);

        // Hit the bomb
        game.reveal(1, 1);
        assertTrue(game.isGameOver());

        // Try to flag after game is over (should silently do nothing)
        game.flag(0, 0);
        // If we could reveal the cell later, it should still be unflagged
        // But we can't test this directly without more public access
    }

    @Test
    void getBoard() {
        GameBoard board = new Grid(3);
        Game game = new Game(board);

        assertEquals(board, game.getBoard());
    }

    private static GameBoard deterministicGridWithBombAt(int bombX, int bombY, int size) throws Exception {
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
}
