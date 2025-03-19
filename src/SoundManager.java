package src;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class SoundManager {

    HashMap<String, Clip> clips = new HashMap<>();
	private static SoundManager instance = null;	// keeps track of Singleton instance

	private SoundManager ()
	{	
		Clip clip = loadClip("audio/music/background_music.wav");	// played from start of the game
		clips.put("background", clip);

        clip = loadClip("audio/sfx/sfx_score_1.wav");
		clips.put("score1", clip);

		clip = loadClip("audio/sfx/sfx_score_2.wav");
		clips.put("score2", clip);

        clip = loadClip("audio/sfx/sfx_wall_bounce.wav");
		clips.put("wall", clip);

		clip = loadClip("audio/sfx/sfx_hit_1.wav");
		clips.put("hit1", clip);

        clip = loadClip("audio/sfx/sfx_hit_2.wav");
		clips.put("hit2", clip);

		clip = loadClip("audio/sfx/sfx_hit_heavy.wav");
		clips.put("hit_heavy", clip);
	}

	// Class method to retrieve instance of Singleton
	public static SoundManager getInstance()
	{	
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}		

    public void setVolume(String clipName, float volume) {

        Clip clip = clips.get(clipName);

        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(volume);
    }

	// Ggets clip from the specified file
    public Clip loadClip (String fileName) 
	{	
 		AudioInputStream audioIn;
		Clip clip = null;

		try {
			File file = new File(fileName);
			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		}
		catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
 			System.out.println ("Error opening sound files: " + e);
		}
    		return clip;
    }

	public Clip getClip (String title) { return clips.get(title); }

    public void playClip(String title, boolean looping)
	{    
		Clip clip = getClip(title);

		if (clip != null) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
    }

    public void stopClip(String title)
	{
		Clip clip = getClip(title);

		if (clip != null) {
			clip.stop();
		}
    }
}
