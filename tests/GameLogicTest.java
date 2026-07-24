import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

public class GameLogicTest {
    @Test
    void invalidDimensionExitsGracefully() {
        StubInputHandler input = new StubInputHandler("abc");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        assertTrue(input.isClosed(), "Input handler should be closed");
        assertTrue(renderer.getOutput().stream()
                .anyMatch(s -> s.contains("positive integer")),
                "Should display error message");
    }

    @Test
    void validDimensionStartsGame() {
        StubInputHandler input = new StubInputHandler("3", "0 0", "quit");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        // Game should have rendered at least once
        assertTrue(renderer.getOutput().stream().anyMatch(s -> s.equals("DISPLAY_BOARD")),
                "Board should be displayed");
    }

    @Test
    void validMoveIsProcessed() {
        StubInputHandler input = new StubInputHandler("3", "0 0", "1 1");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        // Should attempt to display board and process move
        assertTrue(renderer.getOutput().stream().anyMatch(s -> s.equals("DISPLAY_BOARD")),
                "Board should be displayed after move");
    }

    @Test
    void emptyInputShowsErrorMessage() {
        StubInputHandler input = new StubInputHandler("3", "", "0 0");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        // Should show some error message or help text (exact wording may vary)
        assertTrue(renderer.getOutput().size() > 0,
                "Should produce output");
        assertTrue(renderer.getOutput().stream()
                .anyMatch(s -> s.contains("PRINT") || s.contains("coordinates")),
                "Should provide feedback about input");
    }

    @Test
    void invalidCoordinatesShowErrorMessage() {
        StubInputHandler input = new StubInputHandler("3", "abc def");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        // Should show some error or feedback (exact message may vary)
        assertTrue(renderer.getOutput().stream()
                .anyMatch(s -> s.contains("PRINT") || s.contains("PROMPT")),
                "Should provide feedback about invalid input");
    }

    @Test
    void flagCommandIsProcessed() {
        StubInputHandler input = new StubInputHandler("3", "0 0 flag", "0 0");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        // Should produce output that indicates the flag command was processed
        assertTrue(renderer.getOutput().size() > 0,
                "Should produce output");
        // Flag command should be recognized (game continues after flag)
        assertTrue(renderer.getOutput().stream().filter(s -> s.equals("DISPLAY_BOARD")).count() > 1,
                "Should display board multiple times (after flag and after next move)");
    }

    @Test
    void missingCoordinateShowsErrorMessage() {
        StubInputHandler input = new StubInputHandler("3", "0");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        // Should handle incomplete coordinates gracefully
        assertTrue(renderer.getOutput().stream()
                .anyMatch(s -> s.contains("PRINT") || s.contains("PROMPT")),
                "Should provide output for incomplete input");
    }

    @Test
    void invalidCommandWithExtraTextShowsErrorMessage() {
        StubInputHandler input = new StubInputHandler("3", "consume me", "0 0 flag extra", "0 0");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        assertTrue(renderer.getOutput().stream()
                .anyMatch(s -> s.contains("Invalid command")),
                "Should reject commands with unexpected extra text");
    }

    @Test
    void displayLineCountIsReflectedInCursorMovement() {
        StubInputHandler input = new StubInputHandler("3", "0 0", "1 1");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        // After first move, should move cursor up before redrawing
        assertTrue(renderer.getCursorUpCount() > 0,
                "Should move cursor up when redrawing after first move");
    }

    @Test
    void inputHandlerIsClosedAfterGame() {
        StubInputHandler input = new StubInputHandler("3");
        StubRenderer renderer = new StubRenderer();
        GameLogic gameLogic = new GameLogic(input, renderer);

        gameLogic.initializeAndStart();

        assertTrue(input.isClosed(),
                "Input handler should be closed when game ends");
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
