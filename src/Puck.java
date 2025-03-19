package src;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class Puck {

    // Final variables
    private final GamePanel panel;
    private final SoundManager soundManager;
    private final Image puckImage;

    private final int maxFrame = 100;
    private final int maxAnimTime = 500;
    private final int diameter;
    
    private final float maxSpeed = 2f;

    // Normal variables
    private float dx;
    private float dy;

    private float xPos;
    private float yPos;

    private int frameCount = 0;
    private int currentAnimFrame = 0;

    private boolean canContact;
    private boolean canAnimate;

    public Puck(GamePanel panel, float xPos, float yPos, int diameter)
    {
        this.panel = panel;
        this.xPos = xPos;
        this.yPos = yPos;
        this.diameter = diameter;

        canContact = true;
        canAnimate = false;

        soundManager = SoundManager.getInstance();
        puckImage = ImageManager.loadImage("gfx/images/air_hockey_puck.png");
    }

    public void draw(Graphics2D g2)
    {
        if (g2 == null)
            return;
            
        g2.drawImage(puckImage, (int)xPos, (int)yPos, diameter, diameter, null);

        if (canAnimate)
        {
            Image flameGif = ImageManager.loadImage("gfx/animations/flame_anim/flame.gif");

            // Took some...slight..fine tuning...
            double angle = Math.atan2(-dx, dy);

            // Calculate the center point of the GIF
            int centerX = (int) xPos + diameter / 2;
            int centerY;
            centerY = (int) yPos + diameter / 2;

            // Create a rotation transform around the center point
            AffineTransform oldTransform = g2.getTransform();
            AffineTransform transform = new AffineTransform();

            transform.rotate(angle, centerX, centerY);
            g2.setTransform(transform);

            g2.drawImage(flameGif, (int)xPos, (int)yPos, diameter, diameter, null);
            g2.setTransform(oldTransform);
        }
    }

    public void move(Graphics2D g2)
    {
        CalculateCollisions();

        if (!canContact)
            if (frameCount >= maxFrame)
            {
                frameCount = 0;
                canContact = true;
            }
            else
                frameCount++;

        if (canAnimate)
            if (currentAnimFrame >= maxAnimTime)
            {
                currentAnimFrame = 0;
                canAnimate = false;
            }
            else
                currentAnimFrame++;

        if (!panel.getPauseState() && panel.getGameState() && !panel.getHasWinner())
        {
            xPos += dx;
            yPos += dy;
        }
        
        draw(g2);
    }

    private void CalculateCollisions()
    {
        // The puck reached the barrier on the Y Axis
        if ((yPos + dy > 0 - (diameter / 2) && yPos + dy < panel.getHeight() - (diameter / 2)) == false)
        {
            dy = -dy;

            if (!panel.getPauseState() && !panel.getHasWinner())
            {
                soundManager.playClip("wall", false);
                soundManager.setVolume("wall", -10.0f);
            }

            return;
        }

        // The puck reached the barrier on the X Axis
        if (xPos + dx + (diameter / 2) <= 0 || xPos + dx + (diameter / 2) >= panel.getWidth())
        {
            dx = -dx;

            // Check if it hit the score area (150px is the height I made the score area in photoshop, so 75 is used to clamp)
            if (yPos >= ((panel.getHeight() / 2) - 75) && yPos <= ((panel.getHeight() / 2) + 75))
            {
                // Check if the right wall was hit for player 1 score increase
                if (xPos + dx + diameter >= panel.getWidth() - diameter)
                {
                    panel.increasePlayerScore(1);

                    Random random = new Random();
                    int scoreRand = random.nextInt(2) + 1;
                    
                    if (!panel.getPauseState() && !panel.getHasWinner())
                    {
                        soundManager.setVolume("score" + scoreRand, 6.0f);
                        soundManager.playClip("score" + scoreRand, false);
                    }
                }
                else
                {
                    panel.increasePlayerScore(2);

                    Random random = new Random();
                    int scoreRand = random.nextInt(2) + 1;

                    if (!panel.getPauseState() && !panel.getHasWinner())
                    {
                        soundManager.setVolume("score" + scoreRand, 6.0f);
                        soundManager.playClip("score" + scoreRand, false);
                    }
                }

                // Restart round
                Scoreboard gameScore = GameWindow.getScoreboard();

                gameScore.updateScoreBoard(panel.getPlayerScores(1), panel.getPlayerScores(2));
                panel.restartGame();
            }
            
            return;
        }

        // Check if the puck collides with a striker
        if (!canContact)
            return;

        Striker p1 = panel.getPaddle(1);
        Striker p2 = panel.getPaddle(2);

        Ellipse2D.Double puck = new Ellipse2D.Double(xPos, yPos, diameter, diameter);

        Random random = new Random();
        int randomHit = random.nextInt(2) + 1;

        if (collisionCheck(p1.getEllipseArea(), puck))
        {
            double speed = handleCollision(p1);

            if (speed >= maxSpeed * 5)
            {
                canAnimate = true;
                if (!panel.getPauseState() && !panel.getHasWinner())
                    soundManager.playClip("hit_heavy", false);
            }
            else
                if (!panel.getPauseState() && !panel.getHasWinner())
                    soundManager.playClip("hit" + randomHit, false);

            canContact = false;
            return;
        }

        if (collisionCheck(p2.getEllipseArea(), puck))
        {
            double speed = handleCollision(p2);

            if (speed >= maxSpeed * 5)
            {
                canAnimate = true;

                if (!panel.getPauseState() && !panel.getHasWinner())
                    soundManager.playClip("hit_heavy", false);
            }
            else
                if (!panel.getPauseState() && !panel.getHasWinner())
                    soundManager.playClip("hit" + randomHit, false);

            canContact = false;
        }
    }

    private boolean collisionCheck (Ellipse2D.Double circle1, Ellipse2D.Double circle2)
    {
        // Using double rather than float for the sake of the formula
        double newDX = circle1.getCenterX() - circle2.getCenterX();
        double newDY = circle1.getCenterY() - circle2.getCenterY();

        double distanceSquared = newDX * newDX + newDY * newDY;
        double radiusSum = (circle1.width / 2) + (circle2.width / 2);

        return distanceSquared <= (radiusSum * radiusSum);
    }

    private double handleCollision(Striker striker)
    {
        double newDX = xPos - striker.getX();
        double newDY = yPos - striker.getY();
        
        double length = Math.sqrt(newDX * newDX + newDY * newDY);
        newDX /= length;
        newDY /= length;

        // Use striker speed to speed up the puck
        double strikerDX = striker.getDX();
        double strikerDY = striker.getDY();

        double normalizedStriker = Math.sqrt(strikerDX * strikerDX + strikerDY * strikerDY);
        float newScale = 0;

        if (normalizedStriker >= maxSpeed * 5)
            newScale = 0.1f;
        
        // Reflect puck's velocity
        double speed = Math.sqrt(dx * dx + dy * dy) + newScale;
        dx = (float) ((newDX * speed) + strikerDX);
        dy = (float) ((newDY * speed) + strikerDY);

        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        if (currentSpeed > maxSpeed)
        {
            double scale = (maxSpeed / currentSpeed) + newScale;
            dx *= scale;
            dy *= scale;
        }

        // Return the normalized speed of the striker
        // For that heavy hit
        return normalizedStriker;
    }
    
    public float getX() { return xPos; }
    public float getY() { return yPos; }

    public int getDiameter() { return diameter; }
}
