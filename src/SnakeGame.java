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
        int points = r.nextInt(10) + 1;

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
    Random r = new Random();

    // Obstacles Creation
    ArrayList<Tile> obstacles = new ArrayList<>();

    // Portal Tile 
    Tile portalTile = null;

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

    SnakeGame(int boardWidth, int boardHeight){
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);
        
        // Custom Font
        try {
            myFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets\\PressStart2P-Regular.ttf"));
            myFont = myFont.deriveFont(Font.PLAIN, 14f);
        } catch (Exception e) {
            myFont = new Font("Arial", Font.PLAIN, 14);
        }

        // SnakeHead Generation
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        // Food Generation
        foodTiles = new ArrayList<>();
        generateMathFood(level);
        speedX = 0;
        speedY = 0;

        // Game Loop Generation
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    // Screen Shake Effect Logic
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

    // Draw Logic (Every draw is here)
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

            // Map Drawing
            for(int i = hudHeight; i < boardHeight; i += tileSize){
                draw.setColor(new Color(25, 25, 25));
                draw.drawLine(0,i,boardWidth,i);
            }

            for(int i = 0; i < boardWidth; i += tileSize){
                draw.setColor(new Color(25, 25, 25));
                draw.drawLine(i, hudHeight, i, boardHeight);
            }

            //HUD Drawing
            draw.setColor(hudBackColor);
            draw.fillRect(0, 0, boardWidth, hudHeight);

            if(!gameOver && !gameWon){

                Graphics2D g = (Graphics2D) draw;

                // Shake Screen Effect Drawing
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
                // Scan lines Effect
                if(glitchLevel >= 1){
                    g.setColor(new Color(0, 0, 0));
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    for(int i = hudHeight; i < boardHeight; i += 4){
                        g.fillRect(0, i, boardWidth, 2);
                    }
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
    
                // Math Equation String
                draw.setFont(myFont.deriveFont(Font.PLAIN, 10f));
                draw.setColor(hudTextColor);
                draw.drawString("Math Equation", 10, boardHeight / tileSize + -1);
                // Math Equation
                draw.setFont(myFont.deriveFont(Font.PLAIN, 12f));
                draw.setColor(mathEquationColor);
                draw.drawString(currentQuestion.getDisplayString(), 15, boardHeight / tileSize + 25);

                // Score HUD String
                draw.setFont(myFont.deriveFont(Font.PLAIN, 10f));
                draw.setColor(hudTextColor);
                draw.drawString("Score", 190, boardHeight / tileSize + -2);
                // Score HUD
                draw.setFont(myFont.deriveFont(Font.PLAIN, 15f));
                draw.setColor(scoreColor);
                draw.drawString(String.valueOf(score), 210, boardHeight / tileSize + 25);

                // Glitch Level String
                draw.setFont(myFont.deriveFont(Font.PLAIN, 10f));
                draw.setColor(hudTextColor);
                draw.drawString("Glitch Lvl", 260, boardHeight / tileSize + -1);
                // Glitch Level
                draw.setFont(myFont.deriveFont(Font.PLAIN, 15f));
                draw.setColor(glitchLevelScore);
                draw.drawString(String.valueOf(glitchLevel), 300, boardHeight / tileSize + 25);

                // Map Level String
                draw.setFont(myFont.deriveFont(Font.PLAIN, 10f));
                draw.setColor(hudTextColor);
                draw.drawString("Map Level", 380, boardHeight / tileSize + -2);
                // Map Level
                draw.setFont(myFont.deriveFont(Font.PLAIN, 15f));
                draw.setColor(mapLevelScore);
                draw.drawString(String.valueOf(level), 420, boardHeight / tileSize + 25);

                // HUD Dividers
                draw.setColor(new Color(50, 50, 50));
                draw.drawLine(183, 5, 183, hudHeight - 5);
                draw.drawLine(253, 5, 253, hudHeight - 5);
                draw.drawLine(373, 5, 373, hudHeight - 5);

            // Food Tiles Drawing (Numbers)
            draw.setColor(Color.darkGray);
            for(int i = 0; i < foodTiles.size(); i++){
                FoodTile ft = foodTiles.get(i);
                draw.setColor(new Color(25, 25, 25));
                draw.fill3DRect(ft.x * tileSize, ft.y * tileSize + hudHeight, tileSize, tileSize, true);
                draw.setColor(new Color(70,70,70));
                draw.setFont(myFont.deriveFont(Font.BOLD, 12f));
                draw.drawString(String.valueOf(ft.value), ft.x * tileSize + 2, ft.y * tileSize + hudHeight + tileSize - 7); 
            }

            // Obstacles Tiles Drawing
            for(int i = 0; i < obstacles.size(); i++){
                Tile ob = obstacles.get(i);
                draw.setColor(new Color(180, 30, 30));
                draw.fill3DRect(ob.x * tileSize, ob.y * tileSize + hudHeight, tileSize, tileSize, true);
            }
            // SPACE flush hint
            draw.setFont(myFont.deriveFont(Font.PLAIN, 8f));
            draw.setColor(glitchLevelScore);
            draw.drawString("SPACE:flush", 260, boardHeight / tileSize + 40);

            // Portal Tile Drawing
            if(portalTile != null){
                Graphics2D gp = (Graphics2D) draw;
                gp.setColor(new Color(126,39,124));
                gp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                gp.fill3DRect(portalTile.x * tileSize, portalTile.y * tileSize + hudHeight, tileSize, tileSize, true);
                gp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                draw.setColor(new Color(0,0,0));
                draw.setFont(myFont.deriveFont(Font.BOLD, 9f));
                draw.drawString("P", portalTile.x * tileSize + 2, portalTile.y * tileSize + hudHeight + tileSize - 7);
            }

            // Snake Head Drawing
            draw.setColor(snakeHeadColor);
            draw.fill3DRect(snakeHead.x * tileSize,snakeHead.y * tileSize + hudHeight, tileSize,tileSize, true);

            // Snake Body Drawing
            for(int i = 0 ; i < snakeBody.size(); i++){
                Tile snakePart = snakeBody.get(i);
                draw.setColor(snakeBodyColor);
                draw.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize + hudHeight, tileSize, tileSize, true);
            }
            }else if(gameWon){
                Graphics2D g2 = (Graphics2D) draw;
                g2.setColor(new Color(0, 0, 0));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                g2.fillRect(0, hudHeight, boardWidth, boardHeight);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                FontMetrics fm = draw.getFontMetrics(myFont.deriveFont(Font.BOLD, 28f));
                draw.setFont(myFont.deriveFont(Font.BOLD, 28f));
                draw.setColor(new Color(2, 231, 61));

                String winText = "YOU WIN!";
                draw.drawString(winText, (boardWidth - fm.stringWidth(winText)) / 2, boardHeight / 2 - 20);
                draw.setFont(myFont.deriveFont(Font.PLAIN, 10f));
                draw.setColor(Color.white);

                String scoreText = "Final Score: " + score;
                FontMetrics fm2 = draw.getFontMetrics(myFont.deriveFont(Font.PLAIN, 10f));
                draw.drawString(scoreText, (boardWidth - fm2.stringWidth(scoreText)) / 2, boardHeight / 2 + 20);

                String restartText = "Press R to restart";
                draw.drawString(restartText, (boardWidth - fm2.stringWidth(restartText)) / 2, boardHeight / 2 + 45);
            }else {
                Graphics2D g2 = (Graphics2D) draw;
                g2.setColor(new Color(0, 0, 0));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                g2.fillRect(0, hudHeight, boardWidth, boardHeight);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                FontMetrics fm = draw.getFontMetrics(myFont.deriveFont(Font.BOLD, 24f));
                draw.setFont(myFont.deriveFont(Font.BOLD, 24f));
                draw.setColor(new Color(136, 0, 21));

                String overText = "GAME OVER";
                draw.drawString(overText, (boardWidth - fm.stringWidth(overText)) / 2, boardHeight / 2 - 20);
                draw.setFont(myFont.deriveFont(Font.PLAIN, 10f));
                draw.setColor(Color.white);
                FontMetrics fm2 = draw.getFontMetrics(myFont.deriveFont(Font.PLAIN, 10f));

                String scoreText = "Final Score: " + score;
                draw.drawString(scoreText, (boardWidth - fm2.stringWidth(scoreText)) / 2, boardHeight / 2 + 20);

                String restartText = "Press R to restart";
                draw.drawString(restartText, (boardWidth - fm2.stringWidth(restartText)) / 2, boardHeight / 2 + 45);
            }
        }


    // Generate Obstacles Tiles
    public void generateObstacles(){
        obstacles.clear();
        int playableTilesY = (boardHeight - hudHeight) / tileSize;
        int count = r.nextInt(2) + 2;
        int attempts = 0;
        while(obstacles.size() < count && attempts < 100){
            attempts++;
            int ox = r.nextInt(boardWidth / tileSize);
            int oy = r.nextInt(playableTilesY);
            boolean overlaps = false;
            for(int i = 0; i < foodTiles.size(); i++){
                if(foodTiles.get(i).x == ox && foodTiles.get(i).y == oy){
                    overlaps = true;
                    break;
                }
            }
            if(!overlaps){
                obstacles.add(new Tile(ox, oy));
            }
        }
    }

    // Generate Math Food Tiles
    public void generateMathFood(int level){
        currentQuestion = new MathEquation(level);
        foodTiles.clear();
        int playableTilesY = (boardHeight - hudHeight) / tileSize;
        int x = r.nextInt(boardWidth / tileSize);
        int y = r.nextInt(playableTilesY);
        int value = currentQuestion.answer;
        foodTiles.add(new FoodTile(x, y, value));
        for(int i = 0; i < 3; i++){
            int wrongValue;
            do {
                wrongValue = r.nextInt(100) + 1;
            } while(wrongValue == currentQuestion.answer);
            foodTiles.add(new FoodTile(r.nextInt(boardWidth / tileSize), r.nextInt(playableTilesY), wrongValue));
        }

        if(level >= 3){
            generateObstacles();
        }

        // Portal Spawn
        if(level == 4 && score >= 100 && portalTile == null){
            int playableTilesY2 = (boardHeight - hudHeight) / tileSize;
            portalTile = new Tile(r.nextInt(boardWidth / tileSize), r.nextInt(playableTilesY2));
        }

    }

    // Collision Detection
    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    // Level up Logic
    public void levelUp(){
        level++;
        pointsThisLevel = 0;
        gameLoop.setDelay(gameLoop.getDelay() - 15);
        if(level > 5){
            gameWon = true;
        }
    }

    // Snake Move Logic
    int score = 0;
    public void move(){
        // eat food
        for(int i = 0; i < foodTiles.size(); i++ ){
            FoodTile ft = foodTiles.get(i);
            if(snakeHead.x == ft.x && snakeHead.y == ft.y){
                if(ft.value == currentQuestion.answer){
                    snakeBody.add(new Tile(snakeHead.x, snakeHead.y));
                    score += ft.points;
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
                    generateMathFood(level);
                }
                break;
            }
        }

        // Portal tile collision
        if(portalTile != null && collision(snakeHead, portalTile)){
            portalTile = null;
            level = 5;
            pointsThisLevel = 0;
            gameLoop.setDelay(gameLoop.getDelay() - 15);
            generateMathFood(level);
        }

        // Obstacle collision
        for(int i = 0; i < obstacles.size(); i++){
            if(collision(snakeHead, obstacles.get(i))){
                gameOver = true;
            }
        }

        // // Snake Move Logic (Body)
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

        // // Snake Move Logic (Head)
        snakeHead.x += speedX;
        snakeHead.y += speedY;

        // Game Over Trigger
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

    // Game Machine
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver || gameWon){
            gameLoop.stop();
        }
    }


    // Key presses
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
                gameLoop.setDelay(gameLoop.getDelay() + 20);
            }
        }else if(e.getKeyCode() == KeyEvent.VK_R){
            snakeHead = new Tile(5, 5);
            snakeBody = new ArrayList<Tile>();
            speedX = 0;
            speedY = 0;
            glitchLevel = 0;
            level = 1;
            pointsThisLevel = 0;
            gameOver = false;
            gameWon = false;
            gameLoop.stop();
            gameLoop.setDelay(100);
            generateMathFood(level);
            gameLoop.start();
            score = 0;
            portalTile = null;
            obstacles.clear();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    // Math Question Generation
        private class MathEquation{
        int operandA;
        int operandB;
        String operator;
        int answer;
        Random r = new Random();


        MathEquation(int level){
            if(level >= 1 && level <= 2){
                if(r.nextInt(2) == 0){
                    this.operator = "+";
                }else{
                    this.operator = "-"; 
                }
                    switch(operator){
                        case "+":
                            operandA = r.nextInt(1,50) + 1;
                            operandB = r.nextInt(1,50) + 1;
                            this.answer = operandA + operandB;
                        break;
                        case "-":
                            operandA = r.nextInt(1,50) + 1;
                            operandB = r.nextInt(1,50) + 1;

                            if(operandA < operandB){
                            int temp = operandA;
                            operandA = operandB;
                            operandB = temp;
                        }
                        this.answer = operandA - operandB;
                }
            }else if(level >= 3 && level <= 4){
                if(r.nextInt(2) == 0){
                    this.operator = "*";
                }else{
                    this.operator = "-"; 
                }
                    switch(operator){
                        case "*":
                            operandA = r.nextInt(1,15) + 1;
                            operandB = r.nextInt(1,15) + 1;
                            this.answer = operandA * operandB;
                        break;
                        case "-":
                            operandA = r.nextInt(1,50) + 1;
                            operandB = r.nextInt(1,50) + 1;

                            if(operandA < operandB){
                            int temp = operandA;
                            operandA = operandB;
                            operandB = temp;
                        }
                        this.answer = operandA - operandB;
                }
            }else if(level == 5){
                if(r.nextInt(2) == 0){
                    this.operator = "*";
                }else{
                    this.operator = "/"; 
                }
                    switch(operator){
                        case "*":
                            operandA = r.nextInt(1,30) + 1;
                            operandB = r.nextInt(1,30) + 1;
                            this.answer = operandA * operandB;
                        break;
                    case "/":
                            operandB = r.nextInt(1,10) + 1;
                            operandA = operandB * r.nextInt(2,10);
                        this.answer = operandA / operandB;
                }
            }
        }
        // display string
        public String getDisplayString(){
            return operandA + " " + operator + " " + operandB + " = ?";
        }

    }

}
