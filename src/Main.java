import javax.swing.*;

public class Main {
    static void main(String[] args) throws Exception {
        int boardWidth = 500;
        int boardHeight = boardWidth + 60;

        JFrame frame = new JFrame("Glitch Snake");
        frame.setVisible(true);                     // makes the  Frame visible
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);         // open up window at center of screen
        frame.setResizable(false);                  // not resizable
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // when user press X , it terminates the program

        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame);
        frame.pack();           // uses the full dimension without the white bar on top (that with the name "Glitch Snake")
        snakeGame.requestFocus();
    }
}