public interface Renderer {
    /**
     * Print a message to the user.
     */
    void print(String message);

    /**
     * Print a prompt for user input.
     */
    void printPrompt(String prompt);

    /**
     * Clear the screen and redraw the board.
     */
    void clearAndRedraw(GameBoard board);

    /**
     * Display the board.
     */
    void displayBoard(GameBoard board);

    /**
     * Move the cursor up by the specified number of lines.
     */
    void moveCursorUp(int lines);

    /**
     * Clear from the cursor position to the end of the screen.
     */
    void clearFromCursorDown();
}
