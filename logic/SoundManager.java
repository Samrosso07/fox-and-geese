package logic;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    private static Clip backgroundClip;
    private static boolean isMuted = false;
    private static long clipPosition = 0;

    // --- MÚSICA DE FONDO (LOOP) ---
    public static void playMusic(String filepath) {
        if (backgroundClip != null && backgroundClip.isRunning()) return;

        try {
            File musicPath = new File(filepath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(audioInput);
                
                FloatControl gainControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f); // Volumen música más bajo

                if (!isMuted) {
                    backgroundClip.start();
                    backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- NUEVO: EFECTOS DE SONIDO (SFX) ---
    public static void playSFX(String filepath) {
        if (isMuted) return; // Si está silenciado, no reproducir efectos

        new Thread(() -> { // Usamos un hilo nuevo para no congelar el juego
            try {
                File soundPath = new File(filepath);
                if (soundPath.exists()) {
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundPath);
                    Clip sfxClip = AudioSystem.getClip();
                    sfxClip.open(audioInput);
                    
                    // Volumen normal (o ajusta si quieres)
                    sfxClip.start(); 
                }
            } catch (Exception e) {
                System.out.println("Error reproduciendo SFX: " + filepath);
            }
        }).start();
    }

    public static void toggleMute() {
        isMuted = !isMuted;
        if (backgroundClip == null) return;

        if (isMuted) {
            clipPosition = backgroundClip.getMicrosecondPosition();
            backgroundClip.stop();
        } else {
            backgroundClip.setMicrosecondPosition(clipPosition);
            backgroundClip.start();
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static boolean isMuted() {
        return isMuted;
    }

    public static void stopMusic() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }
}