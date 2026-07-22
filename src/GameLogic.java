import java.util.Scanner;

public class GameLogic {
    GameLogic() {
        // read for user input grid dimensions
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter grid dimensions: ");
        int dimensions = scanner.nextInt();
        scanner.close();
        Grid grid = new Grid(dimensions);
        start(grid);
    }

    public void start(Grid grid) {
        // method to start the game
        Scanner scanner = new Scanner(System.in);
        while (grid.hiddenCellsCount > 0) {
            // get user input for cell coordinates
            System.out.print("Enter cell coordinates (x y): ");
            int x = scanner.nextInt();
            int y = scanner.nextInt();

            // reveal the cell and check the result
            int result = grid.revealCell(x, y);
            if (result == -1) {
                System.out.println("Game Over! You hit a bomb.");
                break;
            } else if (result == 1) {
                System.out.println("You revealed an alive cell.");
            } else {
                System.out.println("You revealed a dead cell.");
            }
        }
        scanner.close();
        if (grid.hiddenCellsCount == 0) {
            System.out.println("Congratulations! You won!");
        }
    }
}
