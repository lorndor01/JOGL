package mygraphicslib;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	private File soundFile;
	private Clip audioClip;
	public SoundPlayer(String soundFileLocation) {
		soundFile = new File(soundFileLocation);
	}
	
	public void play() throws LineUnavailableException {
		try {
			AudioInputStream audioInput =  AudioSystem.getAudioInputStream(soundFile.toURI().toURL());
			audioClip = AudioSystem.getClip();
			audioClip.open(audioInput);
			audioClip.start();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loopForever() {
		audioClip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
		audioClip.stop();
	}
	
	public void switchFile(String soundFileLocation) {
		soundFile = new File(soundFileLocation);
	}
}
