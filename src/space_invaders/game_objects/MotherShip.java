package space_invaders.game_objects;

import processing.core.PApplet;
import space_invaders.resources.Image;

/**
 * Mother ship that flies across the top after a certain amount of time.
 *
 * @author Craig Hughes
 */
public class MotherShip extends GameObject {
    /**
     * Mother ship's width.
     */
    private int width = 60;

    /**
     * Mother ship's height.
     */
    private int height = 60;

    /**
     * Speed the mother ship will move.
     */
    private int SPEED = 5;

    /**
     * If true the mother ship will move left, else it will move right.
     */
    private boolean isMovingLeft;

    /**
     * Array of scores, to be picked from a random value.
     */
    private int[] scores = new int[]{150, 100, 50};


    /**
     * MotherShip constructor.
     *
     * @param parent Parent class to access all of the needed methods.
     * @param x Mother ship's X coordinates.
     * @param y Mother ship's Y coordinates.
     *
     * @author Craig Hughes
     */
    public MotherShip(PApplet parent, int x, int y) {
        super(parent, x,  y);

        isMovingLeft = Math.random() > 0.5;

        // Start on the right if moving left
        if(isMovingLeft)
            this.x = this.parent.width + Math.abs(x);
    }

    /**
     * Displaying the Mother ship's load.
     *
     * @author Craig Hughes
     */
    public void show() {
        parent.image(Image.BOSS.load(), x, y, width, height);
    }

    /**
     * Method will be called on repeatedly to allow the Mother ship to move.
     *
     * @author Craig Hughes
     */
    public void move() {
        x = isMovingLeft ? x - SPEED : x + SPEED;
    }

    /**
     * @param bullet Bullet to compare against the mother ship.
     * @return Returns true if the mother ship has been hit by the bullet given.
     *
     * @author Craig Hughes
     */
    public boolean hasBeenHit(Bullet bullet) {
        return  bullet.getX() >= x &&
                bullet.getX() <= x + width &&
                bullet.getY() <= y + height &&
                bullet.getY() >= y;
    }

    /**
     * @return Returns true the mother ship will move left, else it will move right.
     *
     * @author Craig Hughes
     */
    public boolean isMovingLeft() {
        return isMovingLeft;
    }

    /**
     * @return Returns the Mother ship's width.
     *
     * @author Craig Hughes
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Returns the Mother ship's height.
     *
     * @author Craig Hughes
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return Returns score to give the player once the Mother ship has been hit
     *
     * @author Craig Hughes
     */
    public int getScore() {
        // Score is randomly returned
        return scores[(int)(Math.random() * scores.length)];
    }

}
