import java.awt.*;
import javax.swing.*;

public class Main {
    static void main(String[] args) throws Exception {
        int boardWidth = 500;
        int boardHeight = boardWidth + 60;

        JFrame frame = new JFrame("Glitch Snake");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ICO placement on window
        try {
            frame.setIconImage(javax.imageio.ImageIO.read(new java.io.File("assets\\favicon.png")));
        } catch (Exception e) {}

        // connection between 2 panels.
        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);

        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        StartPanel startPanel = new StartPanel(boardWidth, boardHeight, cardLayout, container, snakeGame);

        container.add(startPanel, "START");
        container.add(snakeGame, "GAME");

        frame.add(container);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cardLayout.show(container, "START");
        startPanel.requestFocus();
    }
}