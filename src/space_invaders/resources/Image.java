package space_invaders.resources;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.Arrays;

/**
 * Enum used to load and display images.
 *
 * @author Toby Essex
 */
public enum Image {
    BOSS ("boss.png"),

    LIFE ("life.png"),

    NO_SOUND ("sounds/no-sound.png"),
    SOUND ("sounds/sound.png"),

    ALIEN_TIER_1 ("aliens/normal/tier-1.png"),
    ALIEN_TIER_2 ("aliens/normal/tier-2.png"),
    ALIEN_TIER_3 ("aliens/normal/tier-3.png"),

    ALIEN_TIER_1_ALT ("aliens/alts/tier-1-alt.png"),
    ALIEN_TIER_2_ALT ("aliens/alts/tier-2-alt.png"),
    ALIEN_TIER_3_ALT ("aliens/alts/tier-3-alt.png");

    /**
     * Path of the image.
     */
    private String path;

    /**
     * Actual image after it has been loaded.
     */
    private PImage image;

    /**
     * Image constructor.
     *
     * @param path Path of the image.
     */
    Image(String path) {
        this.path = path;
    }

    /**
     * @return Returns the loaded image
     *
     * @author Toby Essex
     */
    public PImage load() {
        return image;
    }

    /**
     * Loads all the images.
     *
     * @param parent PApplet class so we can access the 'loadImage' method.
     *
     * @author Toby Essex
     */
    public static void loadImages(PApplet parent) {
        Arrays.stream(Image.values()).forEach(image -> image.image = parent.loadImage("images/" + image.path));
    }
}
