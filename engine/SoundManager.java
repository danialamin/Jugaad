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
    private boolean zombieMode = false;
    private boolean badEndingMode = false;

    public void setZombieMode(boolean zombie) {
        this.zombieMode = zombie;
    }

    public void setBadEndingMode(boolean active) {
        this.badEndingMode = active;
    }

    private String getMusicFilePathForZone(ZoneType zone) {
        if (zombieMode) {
            switch (zone) {
                case LIBRARY:     return "assets/sound/ZombieLibrary.wav";
                case GROUND:      return "assets/sound/ZombieGround.wav";
                case WALKWAY:     return "assets/sound/ZombieWalkway.wav";
                case CORRIDOR:    return "assets/sound/ZombieCorridor.wav";
                case SERVER_ROOM: return "assets/sound/ZombieServerRoom.wav";
                case CAFETERIA:   return "assets/sound/ZombieCafe.wav";
                case CLASSROOM:   return "assets/sound/ZombieClassroom.wav";
                default:          return "assets/sound/ZombieGround.wav";
            }
        }
        switch (zone) {
            case LIBRARY: return "assets/sound/LibraryTheme.wav";
            case PRAYER_AREA: return "assets/sound/PrayerArea.wav";
            case CAFETERIA: return "assets/sound/Cafe.wav";
            case GROUND: return "assets/sound/Ground.wav";
            case CORRIDOR: return "assets/sound/Corridor.wav";
            case CLASSROOM: return "assets/sound/Classroom.wav";
            case SERVER_ROOM: return "assets/sound/ServerRoom.wav";
            case AI_LAB: return "assets/sound/AILab.wav";
            case WALKWAY: return "assets/sound/Walkway.wav";
            default: return "assets/sound/startMenu.wav";
        }
    }

    public void playFightMusic(String enemyInternalName) {
        String path;
        switch (enemyInternalName) {
            case "zombie_librarian":           path = "assets/sound/FightLibrarian.wav"; break;
            case "zombie_cafe_uncle":          path = "assets/sound/FightCafeUncle.wav"; break;
            case "zombie_faizan":              path = "assets/sound/FightFaizan.wav"; break;
            case "zombie_javeria":             path = "assets/sound/FightJaveria.wav"; break;
            case "zombie_student_a":           path = "assets/sound/FightStudent.wav"; break;
            case "zombie_student_b":           path = "assets/sound/FightStudent.wav"; break;
            case "zombie_student_c":           path = "assets/sound/FightStudent.wav"; break;
            case "final_boss":                 path = "assets/sound/FightFinalBoss.wav"; break;
            default:                           path = "assets/sound/FightDefault.wav"; break;
        }
        playCustomMusic(path);
    }

    public void playZoneMusic(ZoneType zone) {
        if (!musicOn) return;

        // Bad ending overrides all zone music with a single looping track
        if (badEndingMode) {
            if ("assets/sound/badEnding.wav".equals(currentCustomPath)) return;
            stopMusic();
            currentCustomPath = "assets/sound/badEnding.wav";
            currentMusicZone = null;
            try {
                File file = new File("assets/sound/badEnding.wav");
                if (file.exists()) {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
                    bgmClip = AudioSystem.getClip();
                    bgmClip.open(ais);
                    FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(musicVolume);
                    bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    System.err.println("[SoundManager] badEnding.wav not found: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("[SoundManager] Could not load badEnding.wav: " + e.getMessage());
            }
            return;
        }

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
        if (textClip != null) return; 
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