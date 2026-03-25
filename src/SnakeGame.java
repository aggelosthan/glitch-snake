import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{


    Color snakeHeadColor = new Color(2,231,61);
    Color snakeBodyColor = new Color(110,178,101);
    
    private class Tile{
        int x;
        int y;

        Tile(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 55;

    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    // Game Logic
    Timer gameLoop;
    int speedX;
    int speedY;
    boolean gameOver = false;

    SnakeGame(int boardWidth, int boardHeight){
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        // SnakeHead Generation
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        // Food Generation
        food = new Tile(10, 10);
        random = new Random();
        placeFood();
        speedX = 0;
        speedY = 0;

        // Game Loop Generation
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics snakeDraw){
        super.paintComponent(snakeDraw);
        draw(snakeDraw);
    }

    public void draw(Graphics draw){

        // Food
        draw.setColor(Color.darkGray);
        draw.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Snake Head

        draw.setColor(snakeHeadColor);
        draw.fill3DRect(snakeHead.x * tileSize,snakeHead.y * tileSize, tileSize,tileSize, true);

        // Snake Body
        for(int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
            draw.setColor(snakeBodyColor);
            draw.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Score
        draw.setFont(new Font("Arial", Font.PLAIN, 16));
            if(gameOver){
                draw.setColor(Color.red);
                draw.drawString("Game Over: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
            }else {
                draw.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
            }
        }


    public void placeFood(){
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move(){
        // eat food
        if(collision(snakeHead, food)){
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Snake Body
        for (int i = snakeBody.size() - 1; i >= 0; i--){
            Tile snakePart = snakeBody.get(i);
            if(i == 0){
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Snake Head
        snakeHead.x += speedX;
        snakeHead.y += speedY;

        // game over
        for(int i = 0;i < snakeBody.size();i++){
            Tile snakePart = snakeBody.get(i);
            if(collision(snakeHead, snakePart)){
                gameOver = true;
            }
        }

        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardWidth || snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight) {
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            gameLoop.stop();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP && speedY != 1){
            speedX = 0;
            speedY = -1;
        }else if(e.getKeyCode() == KeyEvent.VK_DOWN && speedY != -1){
            speedX = 0;
            speedY = 1;
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT && speedX != 1){
            speedX = -1;
            speedY = 0;
        }else if(e.getKeyCode() == KeyEvent.VK_RIGHT && speedX != -1){
            speedX = 1;
            speedY = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

}
