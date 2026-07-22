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
            System.out.print("Add the word 'flag' after the coordinates to flag a cell (e.g., '1 2 flag'): ");
            if (!scanner.hasNextInt()) {
                System.out.println("No more input. Try again.");
                continue;
            }
            int x = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                System.out.println("Missing y coordinate. Try again.");
                continue;
            }
            int y = scanner.nextInt();
            boolean flag = false;
            if (scanner.hasNext() && scanner.next().equalsIgnoreCase("flag")) {
                flag = true;
            }

            if (flag) {
                grid.flagCell(x, y);
                System.out.println("You flagged the cell.");
            } else {
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
        }
        scanner.close();
        if (grid.hiddenCellsCount == 0) {
            System.out.println("Congratulations! You won!");
        }
    }
}
