package engine;

import map.ZoneType;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Handles playing the Background Music (BGM) and Sound Effects (SFX).
 * Developed considering GRASP principles (Information Expert & Low Coupling).
 */
public class SoundManager {
    
    private Clip bgmClip;
    private Clip sfxClip;
    
    // Volume controls
    private float musicVolume = -15.0f; // Lowered slightly so it's not overpowering
    private float sfxVolume = -5.0f;
    
    public boolean musicOn = true;
    public boolean sfxOn = true;

    private ZoneType currentMusicZone = null;
    private String currentCustomPath = null;

    public SoundManager() {
        // Initialization can remain empty
    }

    /**
     * Get local file path for ZoneType music
     */
    private String getMusicFilePathForZone(ZoneType zone) {
        String path = "";
        switch (zone) {
            case LIBRARY: path = "assets/sound/LibraryTheme.wav"; break;
            case PRAYER_AREA: path = "assets/sound/PrayerArea.wav"; break;
            case CAFETERIA: path = "assets/sound/Cafe.wav"; break;
            case GROUND: path = "assets/sound/Ground.wav"; break;
            case CORRIDOR: path = "assets/sound/Corridor.wav"; break;
            case CLASSROOM: path = "assets/sound/Classroom.wav"; break;
            case SERVER_ROOM: path = "assets/sound/ServerRoom.wav"; break;
            case AI_LAB: path = "assets/sound/AILab.wav"; break;
            case WALKWAY: path = "assets/sound/Walkway.wav"; break;
            default: path = "assets/sound/startMenu.wav"; break; // Fallback
        }
        return path;
    }

    public void playZoneMusic(ZoneType zone) {
        if (!musicOn) return;

        // ANTI-SPAM LOCK: Even if the file fails to load, we record that we *tried* to play this zone. 
        // This stops it from retrying 60 times a second and lagging the game!
        if (currentMusicZone == zone) return;

        stopMusic();
        
        currentMusicZone = zone; // Lock it immediately
        currentCustomPath = null; 

        try {
            String path = getMusicFilePathForZone(zone);
            File file = new File(path);
            
            if (file.exists()) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
                bgmClip = AudioSystem.getClip();
                bgmClip.open(ais);
                
                FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(musicVolume);
                
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // LOOP INFINITELY
                System.out.println("[SoundManager] Playing music for zone: " + zone);
            } else {
                // This absolute path will tell you exactly where Java is looking!
                System.err.println("[SoundManager] AUDIO FILE NOT FOUND! Looking at exact path: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("[SoundManager] Could not load music for zone: " + zone + " -> " + e.getMessage());
        }
    }

    /**
     * Plays a specific file continuously, overriding Zone music. Useful for Menus.
     */
    public void playCustomMusic(String relativeFilePath) {
        if (!musicOn) return;

        // ANTI-SPAM LOCK
        if (relativeFilePath.equals(currentCustomPath)) return;

        stopMusic();
        
        currentCustomPath = relativeFilePath; // Lock it immediately
        currentMusicZone = null;

        try {
            File file = new File(relativeFilePath);
            if (file.exists()) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
                bgmClip = AudioSystem.getClip();
                bgmClip.open(ais);
                
                FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(musicVolume);
                
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // LOOP INFINITELY
                System.out.println("[SoundManager] Playing custom music: " + relativeFilePath);
            } else {
                System.err.println("[SoundManager] CUSTOM AUDIO NOT FOUND! Looking at exact path: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("[SoundManager] Error playing custom music: " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
        }
        currentMusicZone = null;
        currentCustomPath = null;
    }

    public void playSFX(String relativeFilePath) {
        if (!sfxOn) return;
        try {
            File file = new File(relativeFilePath);
            if (file.exists()) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(sfxVolume);
                
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("[SoundManager] Could not load SFX: " + e.getMessage());
        }
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (bgmClip != null) {
            try {
                FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);
            } catch (Exception e) {}
        }
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = volume;
    }

    private Clip textClip;

    public void playTextSound() {
        if (!sfxOn) return;
        if (textClip != null && textClip.isRunning()) return; 
        try {
            File file = new File("assets/sound/textOut.wav");
            if (file.exists()) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
                textClip = AudioSystem.getClip();
                textClip.open(ais);
                FloatControl gainControl = (FloatControl) textClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(sfxVolume);
                textClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {}
    }

    public void stopTextSound() {
        if (textClip != null) {
            textClip.stop();
            textClip.close();
            textClip = null;
        }
    }
}