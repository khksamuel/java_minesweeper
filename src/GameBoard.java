public interface GameBoard {
    
    // Reveal the cell at the given coordinates.
    // return BOMB if the cell is a bomb, SAFE if it's safe, ALREADY_HANDLED if already revealed or flagged
     
    int revealCell(int row, int col);

    // Toggle the flag state of the cell at the given coordinates.
    void flagCell(int row, int col);

    // Display the board to the output.
    void display();

    // Check if the player has won (all non-bomb cells revealed).
    boolean isWon();

    // Get the number of display lines the board occupies.
    int getDisplayLineCount();
}
