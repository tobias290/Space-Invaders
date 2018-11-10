package space_invaders.game_objects;

import processing.core.PApplet;
import processing.core.PImage;
import space_invaders.resources.Image;

/**
 * Alien class.
 *
 * @author Craig Hughes
 */
public class Alien extends GameObject {
    /**
     * Alien's width.
     */
    private int width = 40;

    /**
     * Alien's height.
     */
    private int height = 40;

    /**
     * Sets speed in which the Alien will increment by each frame.
     */
    private int xSpeed = 40;

    /**
     * space_invaders.resources.Image to be used as the Alien's background.
     */
    private PImage pImg;

    /**
     * Integer to be used to delegate points based on its value.
     */
    private int alienTier;

    /**
     * Array of scores, to be accessed based on alienTier value.
     */
    private int[] scores = new int[]{30, 20, 10};

    /**
     * If true the alien image will be its alternative (with hands out).
     */
    private boolean isAlt = false;

    /**
     * Alien constructor.
     *
     * @param parent Parent class to access all required methods.
     * @param tier Integer used to appoint a score value.
     * @param img PImage becomes Alien's background load.
     * @param x space_invaders.Alien's X coordinate.
     * @param y space_invaders.Alien's Y coordinate.
     *
     * @author Craig Hughes
     */
    public Alien(PApplet parent, int tier, PImage img, int x, int y) {
        super(parent, x,  y);

        alienTier = tier;
        pImg = img;
    }

    /**
     * Displays the Alien.
     *
     * @author Craig Hughes
     */
    public void show(){
        parent.image(pImg,x - width / 2, y, width, height);
    }

    /**
     * Will constantly repeat giving the Alien movement.
     *
     * @author Craig Hughes
     */
    public void move(){
        x += xSpeed;
    }

    /**
     * @return Checks if any alive Aliens have gone out of bounds, if so, a true value is returned.
     *
     * @author Craig Hughes
     */
    public boolean checkX(){
        return x >= parent.width - 40 || x <= 40;
    }

    /**
     * @return Checks if any alive Aliens have gone out of bounds, if so, a true value is returned.
     *
     * @author Craig Hughes
     */
    public boolean checkY(){
        return y >= parent.height - 250;
    }

    /**
     * @return get space_invaders.Alien's width value.
     *
     * @author Craig Hughes
     */
    public int getWidth(){
        return width;
    }

    /**
     * @return get space_invaders.Alien's X height value.
     *
     * @author Craig Hughes
     */
    public int getHeight(){
        return height;
    }

    /**
     * Reflects movement speed to change Alien's direction
     *
     * @author Craig Hughes
     */
    public void switchX(){
        xSpeed *= -1;
    }

    /**
     * @return get space_invaders.Alien's score value.
     *
     * @author Craig Hughes
     */
    public int getKillScore() {
        return scores[alienTier];
    }

    /**
     * Sets animation load for alien.
     *
     * @author Craig Hughes
     */
    public void setImage(){
        if (alienTier == 0) {
            pImg = isAlt ? Image.ALIEN_TIER_1_ALT.load() : Image.ALIEN_TIER_1.load();
        } else if (alienTier == 1) {
            pImg = isAlt ? Image.ALIEN_TIER_2_ALT.load() : Image.ALIEN_TIER_2.load();
        } else if (alienTier == 2) {
            pImg = isAlt ? Image.ALIEN_TIER_3_ALT.load() : Image.ALIEN_TIER_3.load();
        }

        isAlt = !isAlt;
    }

}