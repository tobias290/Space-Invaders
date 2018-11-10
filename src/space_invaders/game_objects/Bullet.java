package space_invaders.game_objects;

import processing.core.PApplet;

/**
 * Represents a bullet which can be fired by the user.
 * @see Player
 *
 * @author Toby Essex
 */
public class Bullet extends GameObject {
    /**
     * Alien's width.
     */
    private int width = 5;

    /**
     * Alien's height.
     */
    private int height = 15;

    /**
     * Represent the speed that the bullet moves.
     */
    private int SPEED = 11;

    /**
     * If true the bullet has collided with the top of the game.
     */
    private boolean hasHitTop = false;

    /**
     * If true the bullet has collided with the bottom of the game.
     */
    private boolean hasHitBottom = false;

    /**
     * If true the bullet is firing towards the aliens, otherwise it is aiming towards the players.
     */
    private boolean isShootingAliens;

    /**
     * If true this bullet is a bomb.
     * Which moves slower and destroys aliens in a radius.
     */
    private boolean isBomb;

    /**
     * If true then the bullet is part of the split fire power up.
     */
    private boolean isSplit = false;

    /**
     * If true the bullet is moving left, while in split fire mode, otherwise it is moving right.
     */
    private boolean isLeft = false;

    /**
     * space_invaders.game_objects.Bullet constructor.
     *
     * @param parent Parent class so this class can use all the needed methods in PApplet.
     * @param x Player's X coordinates so we know where along the X axis the bullet starts.
     * @param y Player's Y coordinates so we know where along the Y axis the bullet starts.
     * @param isShootingAliens - If true the bullet is fired towards the aliens, If false the bullet is being fired towards the player
     * @param isBomb - If true the bullet will be a bomb which will destroy aliens around it as well.
     */
    public Bullet(PApplet parent, int x, int y, boolean isShootingAliens, boolean isBomb) {
        super(parent, x + 35, y);

        this.isShootingAliens = isShootingAliens;
        this.isBomb = isBomb;
    }

    /**
     * space_invaders.game_objects.Bullet constructor.
     *
     * @param parent Parent class so this class can use all the needed methods in PApplet.
     * @param x Player's X coordinates so we know where along the X axis the bullet starts.
     * @param y Player's Y coordinates so we know where along the Y axis the bullet starts.
     * @param isShootingAliens - If true the bullet is fired towards the aliens, If false the bullet is being fired towards the player
     */
    public Bullet(PApplet parent, int x, int y, boolean isShootingAliens) {
        this(parent, x, y, isShootingAliens, false);
    }

    /**
     * Displays the bullet.
     *
     * @author Craig Hughes, Toby Essex
     */
    public void show() {
        parent.fill(255);
        parent.rectMode(parent.CENTER);
        parent.rect(x, y, isBomb ? 10 : width, height);
    }

    /**
     * Moves the bullet until it collides with another GameObject.
     *
     * @author Toby Essex
     */
    public void move() {
        if(y - getWidth() <= 0) {
            hasHitTop = true;
            return;
        } else if (y + getWidth() >= parent.height - 50) {
            hasHitBottom = true;
        }

        // If its a bomb half the speed
        int _SPEED = isBomb ?  SPEED / 2 : SPEED;

        y = isShootingAliens ? y - _SPEED : y + _SPEED;

        /* If is a split bullet, it will be given an X value to animate by. which way it will animate is based on the
           isLeft Boolean. */
        x = isShootingAliens && isSplit && y <= parent.height - 200 ? isLeft ? x - (_SPEED / 4) : x + (_SPEED / 4) : x;
    }

    /**
     * @return If true the bullet has collided with the top of the game.
     *
     * @author Toby Essex
     */
    public boolean hasHitTop() {
        return hasHitTop;
    }

    /**
     * @return If true the bullet has collided with the bottom of the game.
     *
     * @author Toby Essex
     */
    public boolean hasHitBottom() {
        return hasHitBottom;
    }

    /**
     * @return Returns true if direction of the bullet is towards the aliens.
     */
    public boolean isShootingAliens() {
        return isShootingAliens;
    }

    /**
     *
     * @return Returns true if this bullet is a bomb.
     */
    public boolean isBomb() {
        return isBomb;
    }

    /**
     * @return Returns the bullets height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return Returns the bullets width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets split bullet.
     *
     * @param left Sets the bullet to move left once split (used with the 'SPLIT_FIRE' power-up)
     *
     * @see PowerUp.PowerUpType
     *
     * @author Craig Hughes
     */
    public void setSplit(boolean left){
        isSplit = true;
        isLeft = left;
    }
}
