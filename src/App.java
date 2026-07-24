public class App {
    public static void main(String[] args) {
        // main method to start the game
        InputHandler inputHandler = new ConsoleInputHandler();
        Renderer renderer = new ConsoleRenderer();
        GameLogic gameLogic = new GameLogic(inputHandler, renderer);
        gameLogic.initializeAndStart();
    }
}
