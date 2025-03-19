package src;

import java.awt.Graphics;
import java.awt.Image;

public class CounterAnimation {
    
    private final Animation animation;

    public CounterAnimation() 
    {
        animation = new Animation(false);

        Image startFrame1 = ImageManager.loadImage("gfx/animations/start_anim/start_anim_1.png");
        Image startFrame2 = ImageManager.loadImage("gfx/animations/start_anim/start_anim_2.png");
        Image startFrame3 = ImageManager.loadImage("gfx/animations/start_anim/start_anim_3.png");

        animation.addFrame(startFrame3, 1000);
        animation.addFrame(startFrame2, 1000);
        animation.addFrame(startFrame1, 1000);
    }

    public void start() { animation.start(); }

    public boolean isStillActive() { return animation.isStillActive(); }

    public void draw(Graphics g)
    {
		if (!animation.isStillActive())
			return;

        animation.update();
		g.drawImage(animation.getImage(), 0, 0, 858, 525, null);
	}
}
