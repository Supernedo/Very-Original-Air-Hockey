package src;
import java.awt.Image;
import javax.swing.ImageIcon;

public class ImageManager {
    
    public ImageManager () { }

	public static Image loadImage (String fileName) {
		return new ImageIcon(fileName).getImage();
	}
}
