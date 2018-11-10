package space_invaders.managers;

import processing.core.PApplet;
import space_invaders.resources.Sound;
import space_invaders.game_objects.Bullet;
import space_invaders.game_objects.MotherShip;
import space_invaders.game_objects.Player;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Manges the mother ships in the game which are spawned randomly. <br>
 * This class uses the singleton pattern. Using lazy initialisation.
 *
 * @author Toby Essex
 */
public class MotherShipManager {
    /**
     * Used as this class is a singleton.
     */
    private static MotherShipManager inst;

    /**
     * Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class. <br>
     * This class won't be used here. Instead it will be passed to created alien created.
     */
    private PApplet parent;

    /**
     * Mother ships' instance once spawned.
     */
    private MotherShip motherShip;

    /**
     * Used so the timer which generates the mother ship after a 30 seconds is only created once.
     */
    private boolean isTimerCreated = false;

    /**
     * Used to the sound effect timer is only created once per spawn.
     */
    private boolean isSoundTimerCreated = false;

    /**
     * @return Returns the class' instance.
     */
    public static synchronized MotherShipManager getInst() {
        if(inst == null)
            inst = new MotherShipManager();
        return inst;
    }

    private MotherShipManager() {}

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
     * Spawned the mother ship after 30 seconds.
     *
     * @author Toby Essex
     */
    public void spawnMotherShip() {
        // Only create spawn timer once
        if(!isTimerCreated) {
            // This spawns a new mother ship after 30 seconds continuously
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    motherShip = new MotherShip(parent, -60, 40);
                }
            }, 30000, 30000);
            isTimerCreated = true;
        }

        // If the mother ship is not null add sounds and show and move the ship
        if(motherShip != null) {
            if(!isSoundTimerCreated) {
                // Plays the sounds effect for the mother ship continuously
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(motherShip == null)
                            cancel();
                        Sound.MOTHER_SHIP_MOVE.play();
                    }
                }, 0, 150);
                isSoundTimerCreated = true;
            }

            motherShip.show();
            motherShip.move();

            // Check for out of bounds, if so remove it
            if ((motherShip.isMovingLeft() && motherShip.getX() + motherShip.getWidth() < 0) || (!motherShip.isMovingLeft() && motherShip.getX() > parent.width)) {
                motherShip = null;
                isSoundTimerCreated = false;
            }
        }
    }

    /**
     * Checks to see if the mother ship was hit by a bullet.
     *
     * @param player Player instance.
     * @param bullet Bullet instance.
     * @return Returns true if the mother ship was hit, false if not.
     *
     * @author Toby Essex
     */
    public boolean hasBeenHit(Player player, Bullet bullet){
        // Do stuff is mother ship hit
        if (motherShip != null && motherShip.hasBeenHit(bullet)){
            player.addScore(motherShip.getScore());
            motherShip = null;
            isSoundTimerCreated = false;
            return true;
        }
        return false;
    }
}
