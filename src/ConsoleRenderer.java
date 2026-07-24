public class ConsoleRenderer implements Renderer {
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
