package space_invaders.game_objects;

import processing.core.PApplet;

/**
 * Represents a pick up-able object that gives the user a special ability or a reward
 *
 * @author Toby Essex
 */
public class PowerUp extends GameObject {
    /**
     * Represent which power-up this instance is.
     */
    private PowerUpType powerUp;

    /**
     * Starting size of the power-up pick up
     */
    private float currentSize = 20;

    /**
     * If true the size of the power-up pick up will increase. Else decrease.
     */
    private boolean isIncreasing = true;

    /**
     * PowerUp constructor.
     *
     * @param parent Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class.
     * @param x GameObject's X coordinates.
     */
    public PowerUp(PApplet parent, int x) {
        super(parent, x, parent.height - 80);

        powerUp = PowerUpType.getRandom();
        //powerUp = PowerUpType.SPLIT_FIRE;
    }

    /**
     * Display the power-up pick up
     *
     * @author Toby Essex
     */
    @Override
    public void show() {
        parent.fill(255);
        parent.rectMode(parent.CENTER);
        parent.rect(x, y, currentSize, currentSize);

        // Increase or decrease the physical object for a nice animation
        currentSize = isIncreasing ? currentSize + 0.1f : currentSize - 0.1f;

        // Check for max or min size of the block to stop it getting too large or too small
        if(currentSize <= 15) {
            isIncreasing = true;
        } else if(currentSize >= 25) {
            isIncreasing = false;
        }
    }

    /**
     * @return Returns the power up type
     *
     * @author Toby Essex
     */
    public PowerUpType getPowerUpType() {
        return powerUp;
    }

    /**
     * @return Returns the width of the power-up block
     *
     * @author Toby Essex
     */
    public int getWidth() {
        return 20;
    }

    /**
     * Represents the different available power ups
     *
     * @author Toby Essex
     */
    public enum PowerUpType {
        /**
         * Destroys alien and all aliens within a certain radius.
         */
        BOMB,

        /**
         * Gives the player an extra life.
         */
        EXTRA_LIFE,

        /**
         * Allows the user to rapid fire until a certain amount of bullet have been fired.
         */
        FAST_FIRE,

        /**
         * The bullet will split into 3 bullets, one deviating left, one deviating right and one going up.
         */
        SPLIT_FIRE;

        /**
         * @return Returns a random power up.
         *
         * @author Toby Essexs
         */
        public static PowerUpType getRandom() {
            double random = Math.random();

            if (random < 0.25) {
                return BOMB;
            } else if (random >= 0.25 && random < 0.5) {
                return EXTRA_LIFE;
            } else if (random >= 0.5 && random < 0.75) {
                return FAST_FIRE;
            } else {
                return SPLIT_FIRE;
            }
        }
    }
}
