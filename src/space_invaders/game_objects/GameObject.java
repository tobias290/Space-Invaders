package space_invaders.game_objects;

import processing.core.PApplet;

/**
 * Abstract base class for any game object.
 *
 * @author Toby Essex
 */
abstract public class GameObject {
    /**
     * Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class.
     */
    PApplet parent;

    /**
     * Objects's X coordinates.
     */
    int x;

    /**
     * Objects's Y coordinates
     */
    int y;

    /**
     * GameObject's constructor.
     *
     * @param parent Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class.
     * @param x GameObject's X coordinates.
     * @param y GameObject's Y coordinates.
     */
    GameObject(PApplet parent, int x, int y) {
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    /**
     * Displays the game object
     *
     * @author Toby Essex
     */
    abstract public void show();

    /**
     * @return Returns the object's X coordinates
     *
     * @author Toby Essex
     */
    public int getX() {
        return x;
    }

    /**
     * @return Returns the object's Y coordinates
     *
     * @author Toby Essex
     */
    public int getY() {
        return y;
    }

    /**
     * set space_invaders.Alien's X coordinate.
     *
     * @param x sets space_invaders.Alien's X coordinate to this value.
     *
     * @author Craig Hughes
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * set space_invaders.Alien's Y coordinate.
     *
     * @param y sets space_invaders.Alien's Y coordinate to this value.
     *
     * @author Craig Hughes
     */
    public void setY(int y) {
        this.y = y;
    }
}
