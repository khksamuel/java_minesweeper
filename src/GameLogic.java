public class GameLogic {
    private final InputHandler inputHandler;
    private final Renderer renderer;

    private static final String DIMENSION_PROMPT = "Enter grid dimensions: ";
    private static final String INVALID_DIMENSION_MESSAGE = "Please enter a positive integer for dimensions.";
    private static final String GAME_STARTED_MESSAGE = "Game started.";
    private static final String NO_MORE_INPUT_MESSAGE = "No more input. Exiting game.";
    private static final String EMPTY_INPUT_MESSAGE = "Please enter coordinates like: 1 2 or 1 2 flag";
    private static final String MISSING_COORDINATES_MESSAGE = "Missing coordinates. Use: x y or x y flag";
    private static final String INVALID_COMMAND_MESSAGE = "Invalid command. Use: x y or x y flag";
    private static final String INVALID_COORDINATES_MESSAGE = "Coordinates must be numbers. Use: x y";
    private static final String COMMAND_HINT = "Enter: x y   or   x y flag";
    private static final String FLAG_STATUS_PREFIX = "Flag updated at (";
    private static final String REVEAL_STATUS_PREFIX = "Revealed (";
    private static final String CELL_HANDLED_MESSAGE = "Cell already revealed or flagged.";
    private static final String BOMB_MESSAGE = "Game Over! You hit a bomb.";
    private static final String WIN_MESSAGE = "Congratulations! You won!";
    private static final String END_MESSAGE = "Game ended.";

    public GameLogic(InputHandler inputHandler, Renderer renderer) {
        this.inputHandler = inputHandler;
        this.renderer = renderer;
    }

    /**
     * Initialize the game: read dimensions, create board, and start playing.
     */
    public void initializeAndStart() {
        int dimensions = -1;
        do {
            renderer.print(DIMENSION_PROMPT);
            if (!inputHandler.hasNextInt()) {
                renderer.print(INVALID_DIMENSION_MESSAGE);
                if (!inputHandler.hasNext()) {
                    inputHandler.close();
                    return;
                }
                inputHandler.nextLine(); // Consume the invalid input
                continue;
            }
            dimensions = inputHandler.nextInt();
            if (dimensions <= 0) {
                renderer.print(INVALID_DIMENSION_MESSAGE);
                if (!inputHandler.hasNext()) {
                    inputHandler.close();
                    return;
                }
                inputHandler.nextLine(); // Consume the invalid input
                continue;
            }
            inputHandler.nextLine(); // Consume trailing newline
        } while (dimensions <= 0);

        // Create the board and game
        GameBoard board = new Grid(dimensions);
        Game game = new Game(board);

        // Start the game loop
        start(game);
    }

    private void start(Game game) {
        GameBoard board = game.getBoard();
        String statusMessage = GAME_STARTED_MESSAGE;
        boolean hasRenderedFrame = false;
        int previousFrameLines = 0;

        while (!game.isGameOver()) {
            clearPreviousFrameIfNeeded(hasRenderedFrame, previousFrameLines);

            renderer.displayBoard(board);
            renderer.print(statusMessage);
            renderer.print(COMMAND_HINT);
            renderer.printPrompt("> ");
            hasRenderedFrame = true;
            previousFrameLines = board.getDisplayLineCount() + 4;

            if (!inputHandler.hasNext()) {
                statusMessage = NO_MORE_INPUT_MESSAGE;
                break;
            }

            String input = inputHandler.readLine().trim();
            if (input.isEmpty()) {
                statusMessage = EMPTY_INPUT_MESSAGE;
                continue;
            }

            String[] parts = input.split("\\s+");
            if (parts.length < 2) {
                statusMessage = MISSING_COORDINATES_MESSAGE;
                continue;
            }
            if (parts.length > 3) {
                statusMessage = INVALID_COMMAND_MESSAGE;
                continue;
            }

            int x;
            int y;
            try {
                x = Integer.parseInt(parts[0]);
                y = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ex) {
                statusMessage = INVALID_COORDINATES_MESSAGE;
                continue;
            }

            // User enters x y as column row, but the grid is indexed row column.
            int row = y;
            int col = x;

            boolean flag = parts.length == 3;
            if (flag && !parts[2].equalsIgnoreCase("flag")) {
                statusMessage = INVALID_COMMAND_MESSAGE;
                continue;
            }

            try {
                if (flag) {
                    game.flag(row, col);
                    statusMessage = FLAG_STATUS_PREFIX + x + ", " + y + ").";
                } else {
                    int result = game.reveal(row, col);
                    if (result == Grid.BOMB) {
                        board.revealAllCells();
                        clearPreviousFrameIfNeeded(hasRenderedFrame, previousFrameLines);
                        renderer.displayBoard(board);
                        renderer.print(BOMB_MESSAGE);
                        inputHandler.close();
                        return;
                    } else if (result == Grid.SAFE) {
                        statusMessage = REVEAL_STATUS_PREFIX + x + ", " + y + ").";
                    } else {
                        statusMessage = CELL_HANDLED_MESSAGE;
                    }
                }
            } catch (IllegalArgumentException ex) {
                statusMessage = ex.getMessage();
            }
        }

        if (hasRenderedFrame) {
            clearPreviousFrameIfNeeded(true, previousFrameLines);
        }
        renderer.displayBoard(board);
        inputHandler.close();
        if (game.isWon()) {
            renderer.print(WIN_MESSAGE);
        } else {
            renderer.print(END_MESSAGE);
        }
    }

    private void clearPreviousFrameIfNeeded(boolean hasRenderedFrame, int previousFrameLines) {
        // Keep the console output stable by redrawing over the prior frame block only.
        if (hasRenderedFrame) {
            renderer.moveCursorUp(previousFrameLines);
            renderer.clearFromCursorDown();
        }
    }
}
