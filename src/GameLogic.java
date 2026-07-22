import java.util.Scanner;

public class GameLogic {
    GameLogic() {
        // read for user input grid dimensions
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter grid dimensions: ");
        if (!scanner.hasNextInt()) {
            System.out.println("No valid dimension provided. Exiting game.");
            return;
        }
        int dimensions = scanner.nextInt();
        Grid grid = new Grid(dimensions);
        start(grid, scanner);
    }

    public void start(Grid grid, Scanner scanner) {
        // method to start the game
        while (grid.hiddenCellsCount > 0) {
            // get user input for cell coordinates
            grid.display(); // Display the current state of the grid
            System.out.print("Enter cell coordinates to reveal (x y): ");
            if (!scanner.hasNextInt()) {
                System.out.println("No more input. Exiting game.");
                break;
            }
            int x = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                System.out.println("Missing y coordinate. Exiting game.");
                break;
            }
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
