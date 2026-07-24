public class Cell {
    public boolean isBomb;
    public boolean isRevealed;
    public boolean isFlagged;
    public int neighbourBombs;

    Cell() {
        this.isBomb = false;
        this.neighbourBombs = 0;
        this.isRevealed = false;
        this.isFlagged = false;
    }

    int reveal() {
        // method to facilitate revealing the cell's state
        // Returns Grid.BOMB for bomb, Grid.SAFE for safe cell
        // invalid cell coordinates should be handled in the grid class
        if (isBomb) {
            return Grid.BOMB; // Indicates a bomb
        }

        return Grid.SAFE; // Indicates a safe cell
    }

    void flag() {
        // method to toggle the flagged state of the cell
        this.isFlagged = !this.isFlagged;
    }

    // this method is used to initialize the cell's state when the grid is created
    void setBomb(boolean bomb) {
        this.isBomb = bomb;
    }

    // this method is used to set the number of neighbouring bombs for the cell
    void setNeighbourBombs(int count) {
        this.neighbourBombs = count;
    }

    boolean isBomb() {
        return this.isBomb;
    }
}
