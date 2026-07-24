public class GameLogic {
    private final InputHandler inputHandler;
    private final Renderer renderer;

    public GameLogic(InputHandler inputHandler, Renderer renderer) {
        this.inputHandler = inputHandler;
        this.renderer = renderer;
    }

    /**
     * Initialize the game: read dimensions, create board, and start playing.
     */
    public void initializeAndStart() {
        // Prompt for grid dimensions
        renderer.print("Enter grid dimensions: ");
        if (!inputHandler.hasNextInt()) {
            renderer.print("No valid dimension provided. Exiting game.");
            inputHandler.close();
            return;
        }
        int dimensions = inputHandler.nextInt();
        inputHandler.nextLine(); // Consume trailing newline so first command prompt reads real input.

        // Create the board and game
        GameBoard board = new Grid(dimensions);
        Game game = new Game(board);

        // Start the game loop
        start(game);
    }

    private void start(Game game) {
        GameBoard board = game.getBoard();
        String statusMessage = "Game started.";
        boolean hasRenderedFrame = false;
        int previousFrameLines = 0;

        while (!game.isGameOver()) {
            // Redraw only the previous frame block to avoid terminal scrolling.
            if (hasRenderedFrame) {
                renderer.moveCursorUp(previousFrameLines);
                renderer.clearFromCursorDown();
            }

            renderer.displayBoard(board);
            renderer.print(statusMessage);
            renderer.print("Enter: x y   or   x y flag");
            renderer.printPrompt("> ");
            hasRenderedFrame = true;
            previousFrameLines = board.getDisplayLineCount() + 4;

            if (!inputHandler.hasNext()) {
                statusMessage = "No more input. Exiting game.";
                break;
            }

            String input = inputHandler.readLine().trim();
            if (input.isEmpty()) {
                statusMessage = "Please enter coordinates like: 1 2 or 1 2 flag";
                continue;
            }

            String[] parts = input.split("\\s+");
            if (parts.length < 2) {
                statusMessage = "Missing coordinates. Use: x y or x y flag";
                continue;
            }

            int x;
            int y;
            try {
                x = Integer.parseInt(parts[0]);
                y = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ex) {
                statusMessage = "Coordinates must be numbers. Use: x y";
                continue;
            }

            // User enters x y as column row, but the grid is indexed row column.
            int row = y;
            int col = x;

            boolean flag = parts.length >= 3 && parts[2].equalsIgnoreCase("flag");

            try {
                if (flag) {
                    game.flag(row, col);
                    statusMessage = "Flag updated at (" + x + ", " + y + ").";
                } else {
                    int result = game.reveal(row, col);
                    if (result == Grid.BOMB) {
                        if (hasRenderedFrame) {
                            renderer.moveCursorUp(previousFrameLines);
                            renderer.clearFromCursorDown();
                        }
                        renderer.displayBoard(board);
                        renderer.print("Game Over! You hit a bomb.");
                        inputHandler.close();
                        return;
                    } else if (result == Grid.SAFE) {
                        statusMessage = "Revealed (" + x + ", " + y + ").";
                    } else {
                        statusMessage = "Cell already revealed or flagged.";
                    }
                }
            } catch (IllegalArgumentException ex) {
                statusMessage = ex.getMessage();
            }
        }

        if (hasRenderedFrame) {
            renderer.moveCursorUp(previousFrameLines);
            renderer.clearFromCursorDown();
        }
        renderer.displayBoard(board);
        inputHandler.close();
        if (game.isWon()) {
            renderer.print("Congratulations! You won!");
        } else {
            renderer.print("Game ended.");
        }
    }
}
