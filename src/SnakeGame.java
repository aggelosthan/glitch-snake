import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    
    // Custom Font
    Font myFont;

    // Map Tiles
    private class Tile{
        int x;
        int y;

        Tile(int x, int y){
            this.x = x;
            this.y = y;
        }

    }

    // Math Equation Tiles
    private class FoodTile{
        int x;
        int y;
        int value;

        FoodTile(int x, int y, int value){
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }

    // Map dimensions
    int boardWidth;
    int boardHeight;
    int hudHeight = 60;
    int tileSize = 25;

    // Snake Creation
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food Creation
    ArrayList<FoodTile> foodTiles;;
    Random random;
    Random r = new Random();
    int randomScore = r.nextInt(10);

    // Game Logic
    Timer gameLoop;
    int speedX;
    int speedY;
    boolean gameOver = false;
    boolean gameWon = false;
    int glitchLevel = 0;
    int level = 1;
    int pointsThisLevel = 0;
    


    // Math Equation Generation
    MathEquation currentQuestion = new MathEquation(level);

    @SuppressWarnings({"OverridableMethodCallInConstructor", "UseSpecificCatch", "Convert2Diamond"})
    SnakeGame(int boardWidth, int boardHeight){
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);
        

        // Custom Font
        try {
            myFont = Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P.ttf"));
            myFont = myFont.deriveFont(Font.PLAIN, 14f);
        } catch (Exception e) {
            myFont = new Font("Arial", Font.PLAIN, 14);
        }

        // SnakeHead Generation
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        // Food Generation
        foodTiles = new ArrayList<>();
        random = new Random();
        generateMathFood(level);
        speedX = 0;
        speedY = 0;


        // Game Loop Generation
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics snakeDraw){
        super.paintComponent(snakeDraw);
        if(glitchLevel >= 1 && !gameOver){
            Graphics2D g2d = (Graphics2D) snakeDraw;
            int shakeAmount = (glitchLevel >= 2) ? 4 : 2;
            int dx = r.nextInt(shakeAmount * 2 + 1) - shakeAmount;
            int dy = r.nextInt(shakeAmount * 2 + 1) - shakeAmount;
            g2d.translate(dx, dy);
        }
        draw(snakeDraw);
    }

    public void draw(Graphics draw){
            //COLORS
            Color hudBackColor = new Color(15,17,19);
            Color hudTextColor = new Color(119,119,122);
            Color snakeHeadColor = new Color(2,231,61);
            Color snakeBodyColor = new Color(110,178,101);
            Color mathEquationColor = new Color(140,231,133);
            Color scoreColor = new Color(250, 83 ,249);
            Color mapLevelScore = new Color(198, 255 ,255);
            Color glitchLevelScore = new Color(136, 0 ,21);

            for(int i = hudHeight; i < boardHeight; i += tileSize){
                draw.setColor(new Color(25, 25, 25));
                draw.drawLine(0,i,boardWidth,i);
            }

            for(int i = 0; i < boardWidth; i += tileSize){
                draw.setColor(new Color(25, 25, 25));
                draw.drawLine(i, hudHeight, i, boardHeight);
            }

            //HUD
            draw.setColor(hudBackColor);
            draw.fillRect(0, 0, boardWidth, hudHeight);

            if(!gameOver && !gameWon){

                Graphics2D g = (Graphics2D) draw;

                if(glitchLevel >= 1){
                    g.setColor(new Color(136, 0, 21));
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                    g.fillRect(0, hudHeight, boardWidth, boardHeight);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                if(glitchLevel >= 2){
                    g.setColor(new Color(136, 0, 21));
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    g.fillRect(0, hudHeight, boardWidth, boardHeight);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                // Scan lines
                if(glitchLevel >= 1){
                    g.setColor(new Color(0, 0, 0));
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    for(int i = hudHeight; i < boardHeight; i += 4){
                        g.fillRect(0, i, boardWidth, 2);
                    }
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
    
                // Math Equation String
                draw.setFont(myFont.deriveFont(Font.PLAIN, 20f));
                draw.setColor(hudTextColor);
                draw.drawString("Math Equation", 10, boardHeight / tileSize + -1);
                // Math Equation
                draw.setFont(myFont.deriveFont(Font.PLAIN, 22f));
                draw.setColor(mathEquationColor);
                draw.drawString(currentQuestion.getDisplayString(), 15, boardHeight / tileSize + 25);

                // Score HUD String
                draw.setFont(myFont.deriveFont(Font.PLAIN, 20f));
                draw.setColor(hudTextColor);
                draw.drawString("Score", 190, boardHeight / tileSize + -2);
                // Score HUD
                draw.setFont(myFont.deriveFont(Font.PLAIN, 25f));
                draw.setColor(scoreColor);
                draw.drawString(String.valueOf(snakeBody.size()), 210, boardHeight / tileSize + 25);

                // Glitch Level String
                draw.setFont(myFont.deriveFont(Font.PLAIN, 20f));
                draw.setColor(hudTextColor);
                draw.drawString("Glitch Level", 260, boardHeight / tileSize + -1);
                // Glitch Level
                draw.setFont(myFont.deriveFont(Font.PLAIN, 25f));
                draw.setColor(glitchLevelScore);
                draw.drawString(String.valueOf(glitchLevel), 300, boardHeight / tileSize + 25);

                // Map Level String
                draw.setFont(myFont.deriveFont(Font.PLAIN, 20f));
                draw.setColor(hudTextColor);
                draw.drawString("Map Level", 380, boardHeight / tileSize + -2);
                // Map Level
                draw.setFont(myFont.deriveFont(Font.PLAIN, 25f));
                draw.setColor(mapLevelScore);
                draw.drawString(String.valueOf(level), 420, boardHeight / tileSize + 25);
            }else if(gameWon){
                draw.setColor(Color.white);
                draw.drawString("You won all the levels, Score: " + String.valueOf(snakeBody.size()), boardWidth / 4, boardHeight / 2);
            }


            // Food
            draw.setColor(Color.darkGray);
            for(int i = 0; i < foodTiles.size(); i++){
                FoodTile ft = foodTiles.get(i);
                draw.setColor(new Color(25, 25, 25));
                draw.fill3DRect(ft.x * tileSize, ft.y * tileSize + hudHeight, tileSize, tileSize, true);
                draw.setColor(new Color(70,70,70));
                draw.setFont(myFont.deriveFont(Font.BOLD, 17f));
                draw.drawString(String.valueOf(ft.value), ft.x * tileSize + 2, ft.y * tileSize + hudHeight + tileSize - 7); 
            }

            // draw.setColor(Color.pink);
            // for(int i = 0; i < foodTiles.size(); i++){
            //     FoodTile ft = foodTiles.get(i);
            //     draw.setColor(new Color(25, 25, 25));
            //     draw.fill3DRect(ft.x * tileSize, ft.y * tileSize + hudHeight, tileSize, tileSize, true);
            //     draw.setColor(new Color(70,70,70));
            //     draw.setFont(myFont.deriveFont(Font.BOLD, 17f));
            //     draw.drawString("X", ft.x * tileSize + 2, ft.y * tileSize + hudHeight + tileSize - 7); 
            // }

            // Snake Head

            draw.setColor(snakeHeadColor);
            draw.fill3DRect(snakeHead.x * tileSize,snakeHead.y * tileSize + hudHeight, tileSize,tileSize, true);

            // Snake Body
            for(int i = 0 ; i < snakeBody.size(); i++){
                Tile snakePart = snakeBody.get(i);
                draw.setColor(snakeBodyColor);
                draw.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize + hudHeight, tileSize, tileSize, true);
            }
        }

    public void generateMathFood(int level){
        currentQuestion = new MathEquation(level);
        foodTiles.clear();
        int playableTilesY = (boardHeight - hudHeight) / tileSize;
        int x = r.nextInt(boardWidth / tileSize);
        int y = r.nextInt(playableTilesY);
        int value = currentQuestion.answer;
        foodTiles.add(new FoodTile(x, y, value));
        for(int i = 0; i < 3; i++){
            foodTiles.add(new FoodTile(r.nextInt(boardWidth / tileSize), r.nextInt(playableTilesY), r.nextInt(100) + 1));
        }

    }

    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void levelUp(){
        level++;
        pointsThisLevel = 0;
        gameLoop.setDelay(gameLoop.getDelay() - 15);
        if(level > 5){
            gameWon = true;
        }
    }


    public void move(){
        // eat food
        for(int i = 0; i < foodTiles.size(); i++ ){
            FoodTile ft = foodTiles.get(i);
            if(snakeHead.x == ft.x && snakeHead.y == ft.y){
                if(ft.value == currentQuestion.answer){
                    snakeBody.add(new Tile(snakeHead.x, snakeHead.y));
                    pointsThisLevel++;
                    generateMathFood(level);
                    if(pointsThisLevel >= 5){
                        levelUp();
                    }
                }else if(ft.value != currentQuestion.answer){
                    glitchLevel++;
                    gameLoop.setDelay(gameLoop.getDelay() - 20);
                    if(glitchLevel >= 3){
                        gameOver = true;
                    }
                    System.out.println("Glitch meter increased");
                    generateMathFood(level);
                }
                break;
            }
        }

        // Snake Body
        for (int i = snakeBody.size() -1; i >= 0; i--){
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
        if(gameOver || gameWon){
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
        }else if(e.getKeyCode() == KeyEvent.VK_SPACE){
            if(glitchLevel > 0 && snakeBody.size() >= 3){
                snakeBody.remove(snakeBody.size() - 1);
                snakeBody.remove(snakeBody.size() - 1);
                snakeBody.remove(snakeBody.size() - 1);
                glitchLevel--;
                gameLoop.setDelay(gameLoop.getDelay() + 15);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    // Math Question
        private class MathEquation{
        int operandA;
        int operandB;
        String operator;
        int answer;
        Random r = new Random();


        MathEquation(int level){
            if(level == 1 || level == 2){
                this.operandA = r.nextInt(1,40) + 1;
                this.operandB = r.nextInt(1,40) + 1;
                if(r.nextInt(2) == 0){
                    this.operator = "+";
                }else{
                    this.operator = "-";
                }
                    switch(operator){
                        case "+":
                            this.answer = operandA + operandB;
                        break;
                    case "-":
                        this.answer = operandA - operandB;
                }
            }else if(level == 3 || level == 4){
                this.operandA = r.nextInt(1,10) + 1;
                this.operandB = r.nextInt(1,10) + 1;
                this.operator = "*";
                this.answer = operandA * operandB;
            }else if(level == 5){
                this.operandA = r.nextInt(1,10) + 1;
                this.operandB = r.nextInt(1,10) + 1;
                this.operator = "/";
                this.answer = operandA / operandB;

            }
                 // display string

        }

        public String getDisplayString(){
            return operandA + " " + operator + " " + operandB + " = ?";
        }

    }

}
