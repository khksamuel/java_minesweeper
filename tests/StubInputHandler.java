import java.util.ArrayList;
import java.util.List;

/**
 * Test stub for InputHandler that provides predetermined input.
 */
public class StubInputHandler implements InputHandler {
    private final List<String> inputs;
    private int index = 0;
    private boolean closed = false;

    public StubInputHandler(String... inputs) {
        this.inputs = new ArrayList<>();
        for (String input : inputs) {
            this.inputs.add(input);
        }
    }

    @Override
    public String readLine() {
        if (index < inputs.size()) {
            return inputs.get(index++);
        }
        return "";
    }

    @Override
    public boolean hasNext() {
        return index < inputs.size() && !closed;
    }

    @Override
    public boolean hasNextInt() {
        if (index >= inputs.size()) {
            return false;
        }
        try {
            Integer.parseInt(inputs.get(index));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public int nextInt() {
        if (index < inputs.size()) {
            return Integer.parseInt(inputs.get(index++));
        }
        throw new IllegalStateException("No more input available");
    }

    @Override
    public String nextLine() {
        return readLine();
    }

    @Override
    public void close() {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
