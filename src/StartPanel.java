import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class StartPanel extends JPanel implements KeyListener {

    private Image bgImage;
    private CardLayout cardLayout;
    private JPanel container;
    private SnakeGame snakeGame;
    private Font myFont;

    StartPanel(int width, int height, CardLayout cardLayout, JPanel container, SnakeGame snakeGame) {
        this.cardLayout = cardLayout;
        this.container = container;
        this.snakeGame = snakeGame;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        try {
            bgImage = ImageIO.read(new File("assets\\screen.png"));
        } catch (Exception e) {
            bgImage = null;
        }

        try {
            myFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets\\PressStart2P-Regular.ttf"));
        } catch (Exception e) {
            myFont = new Font("Arial", Font.PLAIN, 14);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback background
            g.setColor(Color.black);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Subtle grid
            g.setColor(new Color(25, 25, 25));
            for (int i = 0; i < getHeight(); i += 25) g.drawLine(0, i, getWidth(), i);
            for (int i = 0; i < getWidth(); i += 25)  g.drawLine(i, 0, i, getHeight());
        }

        // Title
        g.setFont(myFont.deriveFont(Font.BOLD, 36f));
        g.setColor(new Color(2, 231, 61));
        g.drawString("GLITCH", 110, 220);
        g.setColor(new Color(136, 0, 21));
        g.drawString("SNAKE", 130, 270);

        // Subtitle
        g.setFont(myFont.deriveFont(Font.PLAIN, 10f));
        g.setColor(new Color(255,255,255));
        g.drawString("A math-based survival game", 95, 320);

        // Prompt
        g.setFont(myFont.deriveFont(Font.PLAIN, 11f));
        g.setColor(new Color(198, 255, 255));
        g.drawString("Press SPACE to start", 110, 420);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            cardLayout.show(container, "GAME");
            snakeGame.requestFocus();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
