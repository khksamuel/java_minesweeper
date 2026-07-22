public class Cell {
    public boolean isAlive;
    public boolean isBomb;
    public int neighbourBombs;

    Cell() {
        this.isAlive = true;
        this.isBomb = false;
        this.neighbourBombs = 0;
    }

    int interact() {
        // method to facilitate revealing the cell's state
        // Returns -1 for bomb, 1 for alive, 0 for dead
        // invalid cell coordinates should be handled in the grid class
        if (isBomb) {
            return -1; // Indicates a bomb
        } else if (isAlive) {
            return 1; // Indicates the cell is alive
        } else {
            return 0; // Indicates the cell is dead
        }
    }

    // this method is used to initialize the cell's state when the grid is created
    void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    // this method is used to initialize the cell's state when the grid is created
    void setBomb(boolean bomb) {
        this.isBomb = bomb;
    }

    // this method is used to set the number of neighbouring bombs for the cell
    void setNeighbourBombs(int count) {
        this.neighbourBombs = count;
    }
}
