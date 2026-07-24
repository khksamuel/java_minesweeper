public class ConsoleRenderer implements Renderer {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_WHITE = "\u001B[37m";
    // 256-color orange. Falls back to terminal default if unsupported.
    private static final String ANSI_ORANGE = "\u001B[38;5;208m";

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public void printPrompt(String prompt) {
        System.out.print(prompt);
    }

    @Override
    public void clearAndRedraw(GameBoard board) {
        moveCursorUp(board.getDisplayLineCount());
        clearFromCursorDown();
        displayBoard(board);
    }

    @Override
    public void displayBoard(GameBoard board) {
        // Delegate to the board's own display method
        board.display();
    }

    @Override
    public void moveCursorUp(int lines) {
        if (lines > 0) {
            System.out.print("\033[" + lines + "A");
        }
    }

    @Override
    public void clearFromCursorDown() {
        System.out.print("\033[J");
        System.out.flush();
    }
}
