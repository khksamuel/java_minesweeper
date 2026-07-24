public interface InputHandler {
    // Read a line of input from the user.
    String readLine();

    // Check if there is more input available.
    boolean hasNext();

    // Check if the next token is an integer.
    boolean hasNextInt();

    // Read the next integer token.
    int nextInt();

    // Read a full line of input.
    String nextLine();

    // Close the input source.
    void close();
}
