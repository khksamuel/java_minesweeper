import java.util.ArrayList;
import java.util.List;

/**
 * Test stub for Renderer that captures all output.
 */
public class StubRenderer implements Renderer {
    private final List<String> output = new ArrayList<>();
    private int cursorUp = 0;
    private int clearCount = 0;

    @Override
    public void print(String message) {
        output.add("PRINT: " + message);
    }

    @Override
    public void printPrompt(String prompt) {
        output.add("PROMPT: " + prompt);
    }

    @Override
    public void clearAndRedraw(GameBoard board) {
        output.add("CLEAR_AND_REDRAW");
        cursorUp++;
        clearCount++;
    }

    @Override
    public void displayBoard(GameBoard board) {
        output.add("DISPLAY_BOARD");
    }

    @Override
    public void moveCursorUp(int lines) {
        output.add("MOVE_CURSOR_UP: " + lines);
        cursorUp += lines;
    }

    @Override
    public void clearFromCursorDown() {
        output.add("CLEAR_FROM_CURSOR_DOWN");
        clearCount++;
    }

    public List<String> getOutput() {
        return output;
    }

    public int getCursorUpCount() {
        return cursorUp;
    }

    public int getClearCount() {
        return clearCount;
    }

    public String getOutputAsString() {
        return String.join("\n", output);
    }
}
