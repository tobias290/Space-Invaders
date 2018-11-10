package space_invaders.managers;

import processing.core.PApplet;
import space_invaders.game_objects.Player;
import space_invaders.game_objects.PowerUp;

/**
 * Manages the power-ups in the game. <br>
 * This class uses the singleton pattern. Using lazy initialisation.
 *
 * @author Toby Essex
 */
public class PowerUpManager {
    private static PowerUpManager inst = new PowerUpManager();

    /**
     * Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class.
     */
    private PApplet parent;

    /**
     * Power up that the user can pick up.
     */
    private PowerUp powerUp;

    /**
     * Used to determine when the player should get another power-up
     */
    private int previousPlayerScore;

    /**
     * @return Returns a instance of this class.
     *
     * @author Toby Essex
     */
    public static synchronized PowerUpManager getInst() {
        if (inst == null)
            inst = new PowerUpManager();
        return inst;
    }

    private PowerUpManager() { }

    /**
     * Sets the parent.
     *
     * @param parent Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class.
     *
     * @author Toby Essex
     */
    public void setParent(PApplet parent) {
        this.parent = parent;
    }

    /**
     * Generate a new power-up if allowed. <br>
     * If a power-up was generated it displays it and continuously check to see if the player has picked it up.
     *
     * @param player Player's instance to check to see if they have picked up a a power-up.
     *
     * @author Toby Essex
     */
    public void showPowerUp(Player player) {
        generatePowerUp(player.getScore());

        if(powerUp != null) {
            powerUp.show();

            if(hasPlayerHitPowerUp(player)) {
                player.pickUpPowerUp(powerUp.getPowerUpType());
                powerUp = null;
            }
        }
    }

    /**
     * Generate a new power-up if allowed. <br>
     * If a power-up was generated it displays it and continuously check to see if the player has picked it up.
     *
     * @param player Player's instance to check to see if they have picked up a a power-up.
     * @param player2 Second player's instance to check to see if they have picked up a a power-up.
     *
     * @author Toby Essex
     */
    public void showPowerUp(Player player, Player player2) {
        generatePowerUp(player.getScore());

        if(powerUp != null) {
            powerUp.show();

            if(hasPlayerHitPowerUp(player)) {
                player.pickUpPowerUp(powerUp.getPowerUpType());
                powerUp = null;
            } else if(hasPlayerHitPowerUp(player2)) {
                player2.pickUpPowerUp(powerUp.getPowerUpType());
                powerUp = null;
            }
        }
    }

    /**
     * Generate a new power up after a random amount of time.
     *
     * @param playerScore Player's score. Needed to see if a new power-up can be generated.
     *
     * @author Toby Essex
     */
    private void generatePowerUp(int playerScore) {
        // If the score if zero then the score needed to generate a power-up if 90
        // Otherwise it is double the last power-up score
        if(previousPlayerScore == 0) {
            if(playerScore >= 90) {
                powerUp = new PowerUp(parent, (int)(Math.random() * (parent.width - 50) + 10));
                previousPlayerScore = playerScore;
            }
        } else {
            // Check current score is twice the last score needed to generate a power-up
            if(playerScore > previousPlayerScore * 2) {
                powerUp = new PowerUp(parent, (int)(Math.random() * (parent.width - 50) + 10));
                previousPlayerScore = playerScore;
            }
        }
    }

    /**
     * Compares the players position against the power-up's to see if the player is picking it up.
     *
     * @param player Player's instance to check to see if they have picked up a a power-up.
     * @return Returns true if the player if within bounds to pick it up.
     *
     * @author Toby Essex
     */
    private boolean hasPlayerHitPowerUp(Player player) {
        return  powerUp.getX() - powerUp.getWidth() / 2 >= player.getX() &&
                powerUp.getX() + powerUp.getWidth() / 2 <= player.getX() + player.getWidth();
    }
}
