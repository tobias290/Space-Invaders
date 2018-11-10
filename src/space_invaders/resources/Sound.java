package space_invaders.resources;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Enum used to play sound effects.
 *
 * @author Toby Essex
 */
public enum Sound {
    /**
     * Alien shooting sound effect.
     */
    SHOOT ("shoot.wav"),

    /**
     * Alien being killed sound effect.
     */
    ALIEN_KILLED ("alien_killed.wav"),

    /**
     * User dying or bomb explosion sound effect.
     */
    EXPLOSION ("explosion.wav"),

    /**
     * Background sound low pitch sound effect. <br>
     * Played when the aliens are moving.
     */
    ALIEN_MOVE_LOW_PITCH("alien_move_low.wav"),

    /**
     * Background sound high pitch sound effect. <br>
     * Played when the aliens are moving.
     */
    ALIEN_MOVE_HIGH_PITCH("alien_move_high.wav"),

    /**
     * Mother ship sound effect.
     */
    MOTHER_SHIP_MOVE("mother_ship_move.wav");

    /**
     * Sound file's path.
     */
    private String path;

    private static boolean isMute = false;

    /**
     * Sound constructor.
     *
     * @param path Sound file's path.
     */
    Sound(String path) {
        this.path = path;
    }

    /**
     * Plays the sound effect.
     *
     * @author Toby Essex
     */
    public void play() {
        if(isMute) return;

        try (AudioInputStream input = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/" + path))) {
            Clip clip = AudioSystem.getClip();

            clip.open(input);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound file: " + path + " - with message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isMute() {
        return isMute;
    }

    public static void mute() {
        isMute = true;
    }

    public static void unMute() {
        isMute = false;
    }
}
