package src;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, MouseMotionListener {
    
    // Final variables
    private final int strikerDiameter = 50;
    private final int marginOffset = 50;
    private final int spawnOffset = 150;

    private final int puckSize = 50;
    private final int playerSpeed = -1; // To inverse given up is minus

    private final double frameTimePacing = 2; // Run the engine at 60 FPS
    private final int FPS = 120;    

    private final SoundManager soundManager;
    private final Image backgroundImage;
    private final Image pauseImage;

    private final Image player1WinImage;
    private final Image player2WinImage;

    private final BufferedImage image;

    private final CounterAnimation counterAnim;
    private RespawnAnimation respawnAnim;

    private final int winValue = 7;

    // Normal Variables
    private Thread gameThread;

    private int p1Score = 0;
    private int p2Score = 0;

    private int winner = 0;

    private Striker playerStriker;
    private Striker secondStriker;
    private BotStriker botStriker;

    private int p1PuckX;
    private int p2PuckX;
    private int spawnSide = 1;

    private Puck puck;

    // Component Variables
    private boolean gameStarted;
    private boolean isPaused;
    private boolean hasWinner;
    private boolean newGame;
    private boolean isRestarting;

    @SuppressWarnings("unused")
    private volatile boolean running = true;

    public GamePanel()
    {
        playerStriker = null;
        secondStriker = null;
        botStriker = null;
        puck = null;

        running = false;
        hasWinner = false;
        newGame = true;
        isRestarting = false;

        soundManager = SoundManager.getInstance();
        spawnSide = 1;

        backgroundImage = ImageManager.loadImage("gfx/images/air_hockey_bg.png");
        pauseImage = ImageManager.loadImage("gfx/images/bg_paused.png");

        player1WinImage = ImageManager.loadImage("gfx/images/winner_1.png");
        player2WinImage = ImageManager.loadImage("gfx/images/winner_2.png");

        image = new BufferedImage(858, 525, BufferedImage.TYPE_INT_RGB);

        counterAnim = new CounterAnimation();
        initializeListener();
    }

    private void initializeListener() { addMouseMotionListener(this); }

    @Override
    public void mouseMoved(MouseEvent e) { handleMovement(e); }
    @Override
    public void mouseDragged(MouseEvent e) { handleMovement(e); }

    private void handleMovement(MouseEvent e)
    {
        int mouseY = e.getY() - (strikerDiameter / 2);
        int mouseX = e.getX() - (strikerDiameter / 2);

        int minY = 0;
        int maxY = getHeight() - strikerDiameter;

        int minX = 0;
        int maxX = (getWidth() / 2) - strikerDiameter;
        
        if (!getPauseState())
        {
            mouseY = Math.max(minY, Math.min(maxY, mouseY));
            mouseX = Math.max(minX, Math.min(maxX, mouseX));

            if (playerStriker != null && gameStarted && !hasWinner)
            {
                playerStriker.setPosY(mouseY);
                playerStriker.setPosX(mouseX);
            }
        }
    }

    public boolean getPauseState() { return isPaused; }
    public boolean getGameState() { return gameStarted; }
    public boolean getHasWinner() { return hasWinner; }

    public void pauseGame() { isPaused = !isPaused; }

    public int getPlayerScores(int player)
    {
        if (player == 1)
            return p1Score;
        else
            return p2Score;
    }

    public void increasePlayerScore(int player)
    {
        if (player == 1)
        {
            spawnSide = 2;
            p1Score++;
        }
        else
        {
            spawnSide = 1;
            p2Score++;
        }    
    }

    public void startNewGame()
    {
        // For an entirely new match
        if (newGame)
        {
            if (!hasWinner)
                counterAnim.start();

            // Let's give the player a chance here
            spawnSide = 1;

            soundManager.playClip ("background", true);
            soundManager.setVolume("background", -20.0f);

            for (int i = 1; i < 3; i++)
                soundManager.setVolume("hit" + i, 1f);

            newGame = false;
        }

        playerStriker.setPosY((getHeight() / 2) - (strikerDiameter / 2));
        playerStriker.setPosX(50);

        secondStriker.setPosY((getHeight() / 2) - (strikerDiameter / 2));
        secondStriker.setPosX(getWidth() - 50 - (strikerDiameter / 2));
    }

    public void restartGame()
    {
        if (p1Score < winValue && p2Score < winValue)
        {
            hasWinner = false;

            // For a new round, wait before having the game start
            Timer timer = new Timer();
            TimerTask task = new TimerTask()
            {
                int seconds = 1;

                @Override
                public void run() {
                    if (seconds > 0) {
                        isRestarting = true;
                        gameStarted = false;
                        seconds--;
                    } else {
                        isRestarting = false;
                        timer.cancel();
                    }
                }
            };

            timer.scheduleAtFixedRate(task, 0, 1000);
        }
        else
        {
            if (p1Score > p2Score)
                displayWinCondition(1);
            else
                displayWinCondition(2);

            newGame = true;
            hasWinner = true;
            gameStarted = false;
            
            Timer timer = new Timer();
            TimerTask task = new TimerTask()
            {
                int seconds = 3;

                @Override
                public void run() {
                    if (seconds > 0) {
                        seconds--;
                    } else {
                        hasWinner = false;
                        counterAnim.start();
                        timer.cancel();
                    }
                }
            };
        
            // Schedule the task to run every 1000ms (1 second)
            timer.scheduleAtFixedRate(task, 0, 1000);

            soundManager.stopClip("background");
        }

        // To Recreate game entities
        running = false;

        if (gameThread != null && gameThread.isAlive())
            gameThread.interrupt();

        gameThread = null;

        playerStriker = null;
        secondStriker = null;
        puck = null;

        createGameEntities();
        startGameThread();
    }

    public void displayWinCondition(int player)
    {
        hasWinner = true;

        if (player == 1)
            winner = 1;
        else
            winner = 2;

        p1Score = 0;
        p2Score = 0;

        Scoreboard gameScore = GameWindow.getScoreboard();
        gameScore.updateScoreBoard(p1Score, p2Score);
    }

    public void createGameEntities()
    {
        // Create new paddles at opposite edges of the screen
        int p1SpawnX = marginOffset;
        int botSpawnX = getWidth() - (marginOffset + strikerDiameter);
        int spawnHeight = (getHeight() / 2) - strikerDiameter;

        playerStriker = new Striker(this, strikerDiameter, p1SpawnX, spawnHeight, 1);
        secondStriker = new Striker(this, strikerDiameter, botSpawnX, spawnHeight, 2);

        // Spawn ball at screen center
        int centerY = (getHeight() / 2) - (puckSize / 2);

        p1PuckX = (getWidth() / 2) - (puckSize / 2) - spawnOffset;
        p2PuckX = (getWidth() / 2) - (puckSize / 2) + spawnOffset;

        if (spawnSide <= 1)
            puck = new Puck(this, p1PuckX, centerY, puckSize);
        else
            puck = new Puck(this, p2PuckX, centerY, puckSize);

        botStriker = new BotStriker(this, puck, secondStriker);
        repaint();

        respawnAnim = new RespawnAnimation(puck);
        respawnAnim.start();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);

        counterAnim.draw(g);

        if (!counterAnim.isStillActive() && isPaused)
            g.drawImage(pauseImage, 0, 0, getWidth(), getHeight(), this);

        if (hasWinner)
        {
            if (winner == 1)
                g.drawImage(player1WinImage, 0, 0, getWidth(), getHeight(), this);
            else
                if (winner == 2)
                    g.drawImage(player2WinImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (isRestarting)
            respawnAnim.draw(g);
    }

    public void startGameThread()
    {
        if (gameThread == null || !gameThread.isAlive())
        {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
            startNewGame();
        }
    }

    @Override
    public void run()
    {
        double drawInterval = 100000000/FPS;
		long lastTime = System.nanoTime();
		double timeDelta = 0;
		
		while(!Thread.currentThread().isInterrupted())
        {
			long currentTime = System.nanoTime();

			timeDelta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if(timeDelta >= frameTimePacing)
            {
                if (!isRestarting)
                    gameStarted = !counterAnim.isStillActive();

                doubleBufferBackground();
				timeDelta -= frameTimePacing;
		    }
		}
    }

    private void doubleBufferBackground()
    {
        Graphics2D imageContext = (Graphics2D) image.getGraphics();

        imageContext.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        updateGameEntities(imageContext);

        repaint();
    }
 
    public void updateGameEntities(Graphics2D g2)
    {
        playerStriker.move(g2);
        botStriker.moveAI(g2);

        if (!isRestarting)
            puck.move(g2);
    }

    public Striker getPaddle(int player)
    {
        if (player == 1)
            return playerStriker;
        else
            return secondStriker;
    }

    public int getMovementSpeed() { return playerSpeed; }
}
