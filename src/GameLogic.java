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
        scanner.nextLine(); // Consume trailing newline so first command prompt reads real input.
        Grid grid = new Grid(dimensions);
        start(grid, scanner);
    }

    public void start(Grid grid, Scanner scanner) {
        // method to start the game
        String statusMessage = "Game started.";
        while (grid.hiddenCellsCount > 0) {
            // Redraw board in place to avoid endless terminal scrolling.
            clearScreen();
            grid.display();
            System.out.println(statusMessage);
            System.out.println("Enter: x y   or   x y flag");
            System.out.print("> ");

            if (!scanner.hasNextLine()) {
                statusMessage = "No more input. Exiting game.";
                break;
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                statusMessage = "Please enter coordinates like: 1 2 or 1 2 flag";
                continue;
            }

            String[] parts = input.split("\\s+");
            if (parts.length < 2) {
                statusMessage = "Missing coordinates. Use: x y or x y flag";
                continue;
            }

            int x;
            int y;
            try {
                x = Integer.parseInt(parts[0]);
                y = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ex) {
                statusMessage = "Coordinates must be numbers. Use: x y";
                continue;
            }

            // User enters x y as column row, but the grid is indexed row column.
            int row = y;
            int col = x;

            boolean flag = parts.length >= 3 && parts[2].equalsIgnoreCase("flag");

            try {
                if (flag) {
                    grid.flagCell(row, col);
                    statusMessage = "Flag updated at (" + x + ", " + y + ").";
                } else {
                    int result = grid.revealCell(row, col);
                    if (result == -1) {
                        clearScreen();
                        grid.display();
                        System.out.println("Game Over! You hit a bomb.");
                        scanner.close();
                        return;
                    } else if (result == 1) {
                        statusMessage = "Revealed (" + x + ", " + y + ").";
                    } else {
                        statusMessage = "Cell already revealed or flagged.";
                    }
                }
            } catch (IllegalArgumentException ex) {
                statusMessage = ex.getMessage();
            }
        }

        clearScreen();
        grid.display();
        scanner.close();
        if (grid.hiddenCellsCount == 0) {
            System.out.println("Congratulations! You won!");
        } else {
            System.out.println("Game ended.");
        }
    }

    private void clearScreen() {
        // ANSI clear and cursor home keeps rendering inside a fixed terminal area.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
