package space_invaders.managers;

import processing.core.PApplet;
import space_invaders.resources.Image;
import space_invaders.resources.Sound;
import space_invaders.SpaceInvaders;
import space_invaders.game_objects.Alien;
import space_invaders.game_objects.Bullet;
import space_invaders.game_objects.Player;

import java.util.*;

/**
 * Manges all the aliens in the game. <br>
 * This class uses the singleton pattern. Using lazy initialisation.
 *
 * @author Toby Essex
 */
final public class AlienManager {
    /**
     * Used as this class is a singleton.
     */
    private static AlienManager inst;

    /**
     * Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class. <br>
     * This class won't be used here. Instead it will be passed to created alien created.
     */
    private PApplet parent;

    /**
     * How many columns of aliens will be displayed.
     */
    private final int NO_OF_COLUMNS = 5;

    /**
     * Number of aliens per row.
     */
    private final int NO_OF_ALIENS_PER_ROW = 10;

    /**
     * 2D list of all the aliens
     */
    private ArrayList<ArrayList<Alien>> aliens;

    /**
     * Will retrieve the current system clock's seconds value.
     */
    private int initTime = PApplet.second();

    /**
     * Will retrieve the current system clock's seconds value. Used for timing Alien Movement.
     */
    private int secondaryTime = PApplet.second();

    /**
     * Will retrieve the current system clock's seconds value. This value will be changed as program runs.
     */
    private int secondsPassed = PApplet.second();

    /**
     * Array of all bullets fired by the aliens.
     */
    private ArrayList<Bullet> alienBullets = new ArrayList<>();

    /**
     * @return Returns a instance of this class.
     *
     * @author Toby Essex
     */
    public static synchronized AlienManager getInst() {
        // Create a instance if it has not been created
        if(inst == null)
            inst = new AlienManager();
        return inst;
    }

    /** Private constructor so this class can't be constructed. */
    private AlienManager() {}

    /**
     * Sets the parent class.
     * @param parent PApplet class instance.
     *
     * @author Toby Essex
     */
    public void setParent(PApplet parent) {
        this.parent = parent;
    }

    /**
     * Deletes the aliens so they will be remade, next time they are shown
     *
     * @author Toby Essex
     */
    public void reset() {
        aliens = null;
    }

    /**
     * This displayed all the aliens in the game. <br>
     * It will also create them if they haven't already been created.
     *
     * @author Craig Hughes, Toby Essex
     */
    public void showAliens() {
        if (aliens == null || aliens.size() == 0)
            createAliens();

        aliens.stream().flatMap(Collection::stream).forEach(alien -> {
            alien.show();

            if (secondaryTime == 59 && secondsPassed == 1)
                secondaryTime = 0;

            if (alien.checkX()) {
                switchAliens();
                aliens.stream().flatMap(Collection::stream).forEach(Alien::move);
            } else if (secondsPassed >= secondaryTime + 1 || (secondsPassed == 0 && secondaryTime == 1)) {
                secondaryTime = secondsPassed;

                aliens.stream().flatMap(Collection::stream).forEach(a -> {
                    a.move();
                    a.setImage();
                });
            }
            secondsPassed = PApplet.second();
        });

        // This removes any columns that no longer have aliens
        aliens.removeIf(r -> r.size() == 0);

        alienShoot();
    }

    /**
     * Checks players coordinates against bullet to see if collision has occurred.
     *
     * @param player Player instance to compare against the each bullet to see if they have collided.
     *
     * @author Toby Essex, Craig Hughes
     */
    public void playerHit(Player player){
        if(alienBullets == null)
            return;

        // Loop over each alien bullet and display and move it
        for (Iterator<Bullet> bulletIterator = alienBullets.iterator(); bulletIterator.hasNext();) {
            Bullet bullet = bulletIterator.next();

            if (WallManager.getInst().hasHitWall(bullet)) {
                bulletIterator.remove();
            } else if (player.hasBeenHit(bullet)) {
                bulletIterator.remove();

                player.loseLife();

                Sound.EXPLOSION.play();

                // Reset the player, delay for 2 seconds then reset the timings for the aliens movement
                player.reset();

                // Update timing so bullets are only fired every 2 seconds
                initTime = PApplet.second();
                secondaryTime = PApplet.second();
                secondsPassed = PApplet.second();

                if (player.getLives() == 0) {
                    // Game Over
                    SpaceInvaders.setGameState(SpaceInvaders.GameState.GAME_OVER);
                }
            }
        }
    }

    /**
     * This checks to see if a bullet as collided with a alien. If so it will remove the alien.
     *
     * @param player Player instance needed so their score can be incremented if an alien is killed.
     * @param bullet The fired bullet instance to it can be compared with each alien's position.
     * @return Returns true if the bullet has collided or false if it has not.
     *
     * @author Toby Essex
     */
    public boolean checkBulletHitsAlien(Player player, Bullet bullet) {
        int rowIndex = 0;

        for (ArrayList<Alien> row : aliens) {
            int alienIndex = 0;
            for (Alien alien : row) {
                if (
                    // Check the bullet if within the alien's X coordinates
                    bullet.getX() >= alien.getX() - (alien.getWidth() / 2) &&
                    bullet.getX() <= alien.getX() + (alien.getWidth() / 2) &&
                    // Check the bullet if within the alien's Y coordinates
                    bullet.getY() + (bullet.getHeight() / 2) <= alien.getY() - (alien.getHeight() / 2)
                ) {
                    // Alien has been hit, therefore kill/remove it
                    if(bullet.isBomb()) {
                        destroyAliensWithBomb(player, aliens.get(rowIndex).remove(alienIndex));
                        Sound.EXPLOSION.play();
                    } else {
                        // Remove the alien that was hit as well as get its score
                        player.addScore(aliens.get(rowIndex).remove(alienIndex).getKillScore());
                        Sound.ALIEN_KILLED.play();
                    }
                    return true;
                }
                alienIndex++;
            }
            rowIndex++;
        }
        return false;
    }

    /**
     * Sets up game objects.
     *
     * @author Toby Essex, Craig Hughes
     */
    private void createAliens() {
        aliens = new ArrayList<>();

        for (int col = 0; col < NO_OF_COLUMNS; col++) {
            ArrayList<Alien> alienRow = new ArrayList<>();

            for (int row = 0; row < NO_OF_ALIENS_PER_ROW; row++) {
                if(col == 0) {
                    alienRow.add(new Alien(parent, 0, Image.ALIEN_TIER_1.load(), 50 + (row * 40), 100));
                } else if (col == 1) {
                    alienRow.add(new Alien(parent, 1, Image.ALIEN_TIER_2.load(), 50 + (row * 40), 140));
                } else if (col == 2) {
                    alienRow.add(new Alien(parent, 1, Image.ALIEN_TIER_2.load(), 50 + (row * 40), 180));
                } else if (col == 3) {
                    alienRow.add(new Alien(parent, 2, Image.ALIEN_TIER_3.load(), 50 + (row * 40), 220));
                } else {
                    alienRow.add(new Alien(parent, 2, Image.ALIEN_TIER_3.load(), 50 + (row * 40), 260));
                }
            }

            aliens.add(alienRow);
        }
    }

    /**
     * Called if the alien was hit with a bomb instead of a bullet.
     *
     * @param player Player's instance.
     * @param alienHit Alien that was hit directly with the bomb.
     *
     * @author Toby Essex
     */
    private void destroyAliensWithBomb(Player player, Alien alienHit) {
        // Radius in which aliens can be destroyed
        int BOMB_RADIUS = 50;

        // Get score from killing alien
        player.addScore(alienHit.getKillScore());

        // Checks the current alien's position against's the alien hit directly position to see if it's within the radius, if so destroy it.
        aliens.forEach(row -> row.removeIf(alien -> {
            boolean can_remove = Math.abs(alienHit.getX() - alien.getX()) < BOMB_RADIUS && Math.abs(alienHit.getY() - alien.getY()) < BOMB_RADIUS;

            if (can_remove) player.addScore(alien.getKillScore());

            return can_remove;
        }));
    }

    /**
     * Iterates through list of Aliens and changes their direction.
     *
     * @author Craig Hughes
     */
    private void switchAliens() {
        // Push down
        aliens.stream().flatMap(Collection::stream).forEach(alien -> {
            alien.switchX();
            alien.setY(alien.getY() + 20);
        });
    }

    /**
     * Randomises which alien will shoot at an interval of two seconds.
     *
     * @author Toby Essex, Craig Hughes
     */
    private void alienShoot() {
        // Reset time if needed
        if(initTime + 2 > 59)
            initTime = 0;

        // Every 2 Seconds, Have an alien shoot.
        if(secondsPassed >= initTime + 2 && initTime != 0) {
            initTime = secondsPassed;

            // Get random alien
            int randomCol = (int) (Math.random() * aliens.size());
            Alien randomAlien = aliens.get(randomCol).get((int) (Math.random() * aliens.get(randomCol).size()));

            alienBullets.add(new Bullet(parent, randomAlien.getX(), randomAlien.getY(), false));
        } else if (initTime == 0) {
            initTime = secondsPassed;

        }

        // Loop over each alien bullet and display and move it
        for (Iterator<Bullet> bulletIterator = alienBullets.iterator(); bulletIterator.hasNext();) {
            Bullet bullet = bulletIterator.next();

            if (bullet.hasHitBottom()) {
                bulletIterator.remove();
                continue;
            }

            bullet.show();
            bullet.move();
        }

        secondsPassed = PApplet.second();
    }

    /**
     * @return returns if closest alien has gone into player's bounds.
     *
     * @author Craig Hughes
     */
    public boolean checkY(){
        Alien last;

        try {
            last = aliens.get(aliens.size() - 1).get((aliens.get(aliens.size() - 1).size() - 1));
        } catch (IndexOutOfBoundsException error){
            return false;
        }


        return last.checkY();
    }

    /**
     * @return gets list of aliens.
     *
     * @author Craig Hughes
     */
    public ArrayList getAliens(){
        return aliens;
    }
}
