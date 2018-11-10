package space_invaders.game_objects;

import processing.core.PApplet;
import space_invaders.resources.Sound;
import space_invaders.SpaceInvaders;
import space_invaders.managers.AlienManager;
import space_invaders.managers.MotherShipManager;
import space_invaders.managers.WallManager;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Player GameObject that represents the player.
 *
 * @author Toby Essex, Craig Hughes
 */
public class Player extends GameObject {
    /**
     * The type of power up the user has.
     */
    private PowerUp.PowerUpType powerUp;

    /**
     * Represents a bullet that the player can fire.
     */
    private Bullet bullet;

    /**
     * If true the player has fired a bullet.
     */
    private boolean hasFiredBullet;

    /**
     * If true the player is dead and will not  be displayed
     */
    private boolean isDead = false;

    /**
     *  Player's lives.
     */
    private int lives = 3;

    /**
     * Current player's score.
     */
    private int score = 0;

    /**
     * Position the player started at.
     */
    private int startX;

    /**
     * Will retrieve the current system clock's seconds value. Used for timing Alien Movement.
     */
    private int secondaryTime = PApplet.second();

    /**
     * Will retrieve the current system clock's seconds value. This value will be changed as program runs.
     */
    private int secondsPassed = PApplet.second();

    /**
     * Array of all bullets fired by the player.
     */
    private ArrayList<Bullet> playerBullets = new ArrayList<>();

    /**
     * Array of all bullets fired by the player.
     */
    private int shotsFired = 0;


    /**
     * Player constructor.
     *
     * @param parent Parent class so this class can use all the needed methods in PApplet.
     * @param x Player'x X position
     *
     * @author Toby Essex
     */
    public Player(PApplet parent, int x) {
        super(parent, x, parent.height - 70);

        startX = x;
    }

    /**
     * Resets the player to the middle then delays for 2 seconds.
     *
     * @author Toby Essex
     */
    public void reset() {
        // Reset player to starting position
        x = startX;

        // Sleep for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Make sure the player is alive
        isDead = false;
    }

    /**
     * Displays the player. <br>
     * If the player has fired a bullet this will move the bullet as well.
     *
     * @author Toby Essex, Craig Hughes
     */
    public void show() {
        parent.fill(255);

        parent.rectMode(parent.CENTER);

        if (isDead) {
            // NOTE: paused before dead player is shown
            makeDeadPlayer();
        } else {
            makePlayer();
        }

        if (secondaryTime == 59 && secondsPassed == 1)
            secondaryTime = 0;

        if(bullet != null && hasFiredBullet) {
            if (bullet.hasHitTop() ||
                AlienManager.getInst().checkBulletHitsAlien(this, bullet) ||
                WallManager.getInst().hasHitWall(bullet) ||
                MotherShipManager.getInst().hasBeenHit(this, bullet)
            ) {
                // Reset bullet
                hasFiredBullet = false;
            } else {
                bullet.show();
                bullet.move();
            }
        }

        // Loop over each player bullet and display and move it
        for (int i = 0; i < playerBullets.size(); i++) {
            Bullet bullet = playerBullets.get(i);

            if(bullet != null) {
                if (bullet.hasHitTop() ||
                    AlienManager.getInst().checkBulletHitsAlien(this, bullet) ||
                    WallManager.getInst().hasHitWall(bullet) ||
                    MotherShipManager.getInst().hasBeenHit(this, bullet)
                ) {
                    // Reset bullet
                    playerBullets.remove(i);
                } else {
                    bullet.show();
                    bullet.move();
                }
            }
        }

        // Game over, aliens have reached the bottom
        if(AlienManager.getInst().getAliens() != null && AlienManager.getInst().checkY()){
            lives = 0;
            isDead = true;
            SpaceInvaders.setGameState(SpaceInvaders.GameState.GAME_OVER);
        }
    }

    /**
     * Moves the player either left or right.
     *
     * @param isLeft If true the player is to move left. If false the player is to move right.
     *
     * @author Toby Essex
     */
    public void move(boolean isLeft) {
        // Stops the player moving out of bounds
        if (isLeft) {
            if (x <= SpaceInvaders.BLOCK_SIZE * 2) return;
        } else {
            if (x >= parent.width - SpaceInvaders.BLOCK_SIZE * 16) return;
        }

        x = isLeft ? x - SpaceInvaders.BLOCK_SIZE : x + SpaceInvaders.BLOCK_SIZE;
    }

    /**
     * Created a new bullet then tells the GameObject that a bullet is to be fired.
     *
     * @author Toby Essex, Craig Hughes
     */
    public void fireBullet() {
        if(hasFiredBullet) return;

        boolean isBomb = powerUp != null && powerUp == PowerUp.PowerUpType.BOMB;
        boolean isFastFire = powerUp != null && powerUp == PowerUp.PowerUpType.FAST_FIRE;
        boolean isSplit = powerUp != null && powerUp == PowerUp.PowerUpType.SPLIT_FIRE;

        if (!isFastFire && !isSplit) {
            // Normal Bullet / Bomb
            bullet = new Bullet(parent, x, parent.height - 90, true, isBomb);
            hasFiredBullet = true;
            powerUp = null;
            Sound.SHOOT.play();

        } else if (isFastFire) {
            // If player has shot less than 10 fast bullets, then run.
            if (shotsFired <= 10) {
                // Initializes fast bullet array . Can not check coordinates otherwise.
                if (playerBullets.isEmpty()) {
                    playerBullets.add(new Bullet(parent, x, parent.height - 90, true, false));
                    ++shotsFired;
                    Sound.SHOOT.play();
                }
                // Will shoot as long as closest bullet is far enough away from the player.
                else if (playerBullets.get(playerBullets.size() - 1).getY() <= (this.getY() - 100)) {
                    playerBullets.add(new Bullet(parent, x, parent.height - 90, true, false));
                    ++shotsFired;
                    hasFiredBullet = false;
                    Sound.SHOOT.play();
                }
                // Don't shoot.
                else if (playerBullets.get(playerBullets.size() - 1).getY() >= (this.getY() - 100)) {
                    hasFiredBullet = true;
                }
            }
            // Reset power up.
            else {
                powerUp = null;
                shotsFired = 0;
            }

        // Split fire
        } else {
            // Only shoot if list is empty.
            if (playerBullets.isEmpty()) {
                IntStream.range(0, 3).forEach(i -> playerBullets.add(new Bullet(parent, x, parent.height - 90, true, false)));
                playerBullets.get(0).setSplit(true);
                playerBullets.get(2).setSplit(false);
                shotsFired += 3;
                hasFiredBullet = true;
                powerUp = null;
            }
        }
    }

    /**
     * Checks players coordinates against bullet to see if collision has occurred.
     *
     * @param bullet Bullet instance to compare its locations against the players to see if they collide.
     * @return Returns true if the player has been hit by the bullet, false is not.
     *
     * @author Craig Hughes
     */
    public boolean hasBeenHit(Bullet bullet) {
        return  bullet.getX() >= getX()               &&
                bullet.getX() <= getX() + getWidth()  &&
                bullet.getY() >= getY()               &&
                bullet.getY() <= getY() + getHeight() &&
                getLives() > 0;
    }

    /**
     * Called when the user picks up a power up.
     *
     * @param powerUp The type of the power up the user gained.
     *
     * @author Toby Essex
     */
    public void pickUpPowerUp(PowerUp.PowerUpType powerUp) {
        if (powerUp == PowerUp.PowerUpType.EXTRA_LIFE) {
            // Gain life from extra life power-up
            lives ++;
        } else {
            // Any other power-up is to be used later on
            this.powerUp = powerUp;
        }
    }

    /**
     * @return returns Player's lives.
     *
     * @author Craig Hughes - 23641835
     */
    public int getLives(){
        return lives;
    }

    /**
     * @return Returns the player's current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Adds to the player's current score.
     *
     * @param score Extra score to add.
     */
    public void addScore(int score) {
        this.score += score;
    }

    /**
     * Makes the player lose a life.
     */
    public void loseLife() {
        lives--;
    }

    /**
     * @return Returns true if the player is dead.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Kills the alien.
     */
    public void kill() {
        isDead = true;
        lives = 0;
    }

    /**
     * @return Returns the player's height.
     */
    public int getHeight() {
        return 35;
    }

    /**
     * @return Returns the player's width.
     */
    public int getWidth() {
        return 80;
    }

    /**
     * Creates the players GameObject.
     *
     * @author Toby Essex
     */
    private void makePlayer() {
        // Column for loop, inner for loop is each row but depending on the column decides how many blocks it is
        for (int i = 0; i < 7; i++) {
            if (i == 0) {
                parent.rect(
                    x + (8 * SpaceInvaders.BLOCK_SIZE) - SpaceInvaders.BLOCK_SIZE,
                    parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 60,
                    SpaceInvaders.BLOCK_SIZE,
                    SpaceInvaders.BLOCK_SIZE
                );
            } else if(i == 1 || i == 2) {
                for (int j = 0; j < 3; j++) {
                    parent.rect(
                        x + (8 * SpaceInvaders.BLOCK_SIZE) - (j * SpaceInvaders.BLOCK_SIZE),
                        parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 60,
                        SpaceInvaders.BLOCK_SIZE,
                        SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else if(i == 3){
                for (int j = 0; j < 13; j++) {
                    parent.rect(
                        x + (j * SpaceInvaders.BLOCK_SIZE) + SpaceInvaders.BLOCK_SIZE,
                        parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 60,
                        SpaceInvaders.BLOCK_SIZE,
                        SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else {
                for (int j = 0; j < 15; j++) {
                    parent.rect(
                        x + (j * SpaceInvaders.BLOCK_SIZE),
                        parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 60,
                        SpaceInvaders.BLOCK_SIZE,
                        SpaceInvaders.BLOCK_SIZE
                    );
                }
            }
        }
    }

    /**
     * Displays the player after it has been exploded
     *
     * @author Toby Essex
     */
    private void makeDeadPlayer() {
        for (int i = 0; i < 11; i++) {
            if (i == 1) continue;

            if (i == 0) {
                parent.rect(
                    x + (4 * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                    SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                );
            } else if (i == 2) {
                for (int j = 0; j < 15; j++) {
                    if (j != 2 && j != 7  && j != 11) continue;

                    parent.rect(
                        x +  (j * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                        SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else if(i == 3){
                for (int j = 0; j < 15; j++) {
                    if (j != 4 && j != 6 && j != 10) continue;

                    parent.rect(
                        x +  (j * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                        SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else if(i == 4){
                for (int j = 0; j < 15; j++) {
                    if (j != 2 && j != 6 && j != 12 && j != 13) continue;

                    parent.rect(
                        x +  (j * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                        SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else if(i == 5){
                for (int j = 0; j < 15; j++) {
                    if (j != 2 && j != 14) continue;

                    parent.rect(
                        x + (j * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                        SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else if(i == 6) {
                for (int j = 0; j < 15; j++) {
                    if (j != 5 && j != 7 && j != 8) continue;

                    parent.rect(
                        x + (j * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                        SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else if(i == 7){
                for (int j = 0; j < 15; j++) {
                    if (!(j == 0 || j > 3 && j < 13)) continue;

                    parent.rect(
                        x + (j * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                        SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else if(i == 8){
                for (int j = 0; j < 15; j++) {
                    if (!(j > 2 && j < 14)) continue;

                    parent.rect(
                        x + (j * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                        SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                    );
                }
            } else {
                for (int j = 0; j < 15; j++) {
                    parent.rect(
                        x + (j * SpaceInvaders.BLOCK_SIZE), parent.height - (SpaceInvaders.BLOCK_SIZE * (7 - i)) - 75,
                        SpaceInvaders.BLOCK_SIZE, SpaceInvaders.BLOCK_SIZE
                    );
                }
            }
        }
    }
}
