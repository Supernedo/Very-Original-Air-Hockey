package src;

import javax.swing.JLabel;

public class Scoreboard {
    
    private final JLabel p1Score;
    private final JLabel p2Score;

    public Scoreboard(JLabel p1Score, JLabel p2Score)
    {
        this.p1Score = p1Score;
        this.p2Score = p2Score;

        this.p1Score.setAlignmentX(0);
    }

    public void updateScoreBoard(int player1Score, int player2Score)
    {
        p1Score.setText("Player 1: " + player1Score);
        p2Score.setText("Player 2: " + player2Score);
    }
}
