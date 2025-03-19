package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameWindow extends JFrame implements KeyListener {

    private final int scoreboardHeight;

    private final JLabel p1Score;
    private final JLabel p2Score;
    private final JLabel countdownTimer;

    private final Container container;

    private final JPanel mainPanel;
    private final JPanel scoreboardPanel;
    private final JPanel overlayPanel;

    private final GamePanel gamePanel;
    private static Scoreboard gameScore;
    
    public GameWindow()
    {
        setTitle("Very Original Air Hockey");
        
        scoreboardHeight = 50; // Increasing height to accommodate the scoreboard
        setSize(858, 525 + scoreboardHeight);

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        // Setup game panels
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Setup overlay for game start and restart
        overlayPanel = new JPanel();
        overlayPanel.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        overlayPanel.setBackground(Color.WHITE.darker());
        overlayPanel.setLayout(null);

        countdownTimer = new JLabel("Get Ready!");

        countdownTimer.setForeground(Color.BLACK);
        countdownTimer.setFont(new Font("Arial", Font.BOLD, 50));

        countdownTimer.setVerticalAlignment(JLabel.CENTER);
        countdownTimer.setHorizontalAlignment(JLabel.CENTER);
        countdownTimer.setBounds(0, 0, getWidth(), getHeight() - scoreboardHeight);

        overlayPanel.add(countdownTimer);

        scoreboardPanel = new JPanel();
        scoreboardPanel.setPreferredSize(new Dimension(this.getWidth(), scoreboardHeight));
        scoreboardPanel.setBackground(Color.BLACK);
        
        p1Score = new JLabel("Player 1: 0");
        p1Score.setFont(new Font("Arial", Font.BOLD, 24));
        p1Score.setForeground(Color.WHITE);

        p2Score = new JLabel("Player 2: 0");
        p2Score.setFont(new Font("Arial", Font.BOLD, 24));
        p2Score.setForeground(Color.WHITE);

        scoreboardPanel.add(p1Score);
        scoreboardPanel.add(p2Score);

        gameScore = new Scoreboard(p1Score, p2Score);

        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        gamePanel.setBackground(Color.BLACK);

        mainPanel.add(overlayPanel, BorderLayout.CENTER);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(scoreboardPanel, BorderLayout.NORTH);
        
        mainPanel.setBackground(Color.WHITE);

        container = getContentPane();
        container.add(mainPanel);

        setVisible(true);

        gamePanel.createGameEntities();
        gamePanel.repaint();

        // Add listener, focus on the game and then begin the game loop
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        initializeListener();

        gamePanel.startGameThread();
    }

    private void initializeListener() { gamePanel.addKeyListener(this); }
    public static Scoreboard getScoreboard() { return gameScore; }

    @Override
    public void keyPressed(KeyEvent e)
    {
        // General Controls
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            gamePanel.pauseGame();
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }
}