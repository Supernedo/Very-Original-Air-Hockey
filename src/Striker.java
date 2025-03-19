package src;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;

public class Striker {

    // Final variables
    private final JPanel panel;
    private final Image strikerImage;
    
    private final int diameter;
    private final int maxSpeed = 50;

    // Normal variables
    private float dy;
    private float dx;

    private float xPos;
    private float yPos;
    
    private float newPosX;
    private float newPosY;
    
    public Striker(JPanel panel, int diameter, float xPos, float yPos, int currentPlayer)
    {
        this.panel = panel;
        this.xPos = xPos;
        this.yPos = yPos;

        this.diameter = diameter;

        if (currentPlayer == 1)
            strikerImage = ImageManager.loadImage("gfx/images/air_hockey_striker_blue.png");
        else
            strikerImage = ImageManager.loadImage("gfx/images/air_hockey_striker_red.png");
    }

    public void draw(Graphics2D g2)
    {
        if (g2 != null)
            g2.drawImage(strikerImage, (int)xPos, (int)yPos, diameter, diameter, null);
    }

    public void move(Graphics2D g2)
    {
        if (!panel.isVisible ()) return;

        // This will get the speed at which the striker moves to influence the puck
        dx = newPosX - xPos;
        dy = newPosY - yPos;

        xPos = newPosX;
        yPos = newPosY;

        // Limit ball speed
        if (dx > maxSpeed)
            dx = maxSpeed;
        if (dx < -maxSpeed)
            dx = -maxSpeed;

        if (dy > maxSpeed)
            dy = maxSpeed;
        if (dy < -maxSpeed)
            dy = -maxSpeed;

        draw(g2);
    }

    public Ellipse2D.Double getEllipseArea() { return new Ellipse2D.Double(xPos, yPos, diameter, diameter); }

    public void setPosY(float newY) { newPosY = newY; }
    public void setPosX(float newX) { newPosX = newX; }

    public float getDX() { return dx; }
    public float getDY() { return dy; }

    public float getX() { return xPos; }
    public float getY() { return yPos; }

    public int getDiameter() { return diameter; }
}
