package src;

import java.awt.Graphics2D;

public class BotStriker {

    private final Puck puck;
    private final Striker botStriker;
    
    private final GamePanel panel;

    public BotStriker(GamePanel panel, Puck puck, Striker botStriker)
    {
        this.panel = panel;
        this.puck = puck;
        this.botStriker = botStriker;
    }
    
    public void moveAI(Graphics2D g2) {

        // Handle drawing first, so it can at least display
        botStriker.move(g2);

        if (puck == null || botStriker == null || panel == null || panel.getPauseState() || !panel.getGameState() || panel.getHasWinner())
            return;

        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        int strikerDiameter = botStriker.getDiameter();
    
        // Manage bot limits
        int minX = panelWidth / 2;
        int maxX = panelWidth - strikerDiameter;
    
        int minY = 0;
        int maxY = panelHeight - strikerDiameter;
    
        // Check if the puck is in his court
        if (puck.getX() >= panelWidth / 2)
        {
            // Target puck whilst remaining clamped
            int targetX = (int) (puck.getX() + (puck.getDiameter() / 2) - (strikerDiameter / 2));
            int targetY = (int) (puck.getY() + (puck.getDiameter() / 2) + (strikerDiameter / 2));

            // Limit boundaries
            targetX = Math.max(minX, Math.min(maxX, targetX));
            targetY = Math.max(minY, Math.min(maxY, targetY));

            // Interpolate the bot movement so it's smooth enough
            float speed = 0.02f;
            int smoothX = (int) (botStriker.getX() + (targetX - botStriker.getX()) * speed);
            int smoothY = (int) (botStriker.getY() + (targetY - botStriker.getY()) * speed);

            botStriker.setPosX(smoothX);
            botStriker.setPosY(smoothY);
        }
    }
}
