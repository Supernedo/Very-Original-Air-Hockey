package src;

import java.awt.Graphics;
import java.awt.Image;

public class RespawnAnimation {
    
    private final Animation animation;
    private final Puck puck;
    
    private final int animationLength = 5; // This will be multiplied by 4 (frames)

    public RespawnAnimation(Puck puck)
    {
        animation = new Animation(false);
        this.puck = puck;

        for (int j = 1; j <= animationLength; j++)
        {
            for (int i = 1; i <= 4; i++)
            {
                Image startFrame = ImageManager.loadImage("gfx/animations/respawn_anim/puck_fade_" + i + ".png");
                animation.addFrame(startFrame, 100);
            }

            for (int i = 4; i >= 1; i--)
            {
                Image startFrame = ImageManager.loadImage("gfx/animations/respawn_anim/puck_fade_" + i + ".png");
                animation.addFrame(startFrame, 100);
            }
        }
    }

    public void start() { animation.start(); }

    public boolean isStillActive() { return animation.isStillActive(); }

    public void draw(Graphics g)
    {
		if (!animation.isStillActive())
			return;

        animation.update();
		g.drawImage(animation.getImage(), (int)puck.getX(), (int)puck.getY(), 50, 50, null);
	}
}
