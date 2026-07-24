public class Game {
    private final GameBoard board;
    private boolean gameOver;
    private boolean won;

    public Game(GameBoard board) {
        this.board = board;
        this.gameOver = false;
        this.won = false;
    }

    /**
     * Reveal the cell at the given coordinates.
     * @return the result code (BOMB, SAFE, or ALREADY_HANDLED)
     */
    public int reveal(int row, int col) {
        if (gameOver) {
            return Grid.ALREADY_HANDLED;
        }
        int result = board.revealCell(row, col);
        if (result == Grid.BOMB) {
            gameOver = true;
        } else if (result == Grid.SAFE) {
            checkWin();
        }
        return result;
    }

    /**
     * Toggle the flag state of the cell at the given coordinates.
     */
    public void flag(int row, int col) {
        if (!gameOver) {
            board.flagCell(row, col);
        }
    }

    /**
     * Check if the player has won.
     */
    private void checkWin() {
        won = board.isWon();
        if (won) {
            gameOver = true;
        }
    }

    /**
     * Check if the game is over (either won or lost).
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Check if the player has won.
     */
    public boolean isWon() {
        return won;
    }

    /**
     * Get the board.
     */
    public GameBoard getBoard() {
        return board;
    }
}
