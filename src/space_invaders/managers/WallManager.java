package space_invaders.managers;

import processing.core.PApplet;
import space_invaders.game_objects.Bullet;
import space_invaders.game_objects.GameObject;
import space_invaders.game_objects.Wall;

import java.util.ArrayList;

/**
 * Manages multiple wall instances. <br>
 * This class uses the singleton pattern. Using lazy initialisation.
 *
 * @author Toby Essex
 */
final public class WallManager {
    /**
     * Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class.
     */
    private PApplet parent;

    /**
     * Static instance used as this class used the singleton pattern.
     */
    private static WallManager inst = new WallManager();

    /**
     * Number of walls to display in the game.
     */
    private final int NO_OF_WALLS = 4;

    /**
     * List of all the walls.
     */
    private ArrayList<Wall> walls;

    /**
     * @return Returns a instance of this class.
     *
     * @author Toby Essex
     */
    public static synchronized WallManager getInst() {
        // Create a instance if it has not been created
        if(inst == null)
            inst = new WallManager();
        return inst;
    }

    /** Private constructor so this class can't be instantiated. */
    private WallManager() {}

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
     * Deletes the walls so they will be remade, next time they are shown
     *
     * @author Toby Essex
     */
    public void reset() {
        walls = null;
    }

    /**
     * Displays each wall.
     *
     * @author Toby Essex
     */
    public void showWalls() {
        if(walls == null)
            createWalls();

        walls.forEach(Wall::show);
    }

    /**
     * Determines whether the wall has been hit by the given bullet.
     *
     * @param bullet Bullet instance to compare against each wall.
     * @return Returns true if a wall has been hit.
     *
     * @author Toby Essex
     */
    public boolean hasHitWall(Bullet bullet) {
        // Loop over each wall
        for (Wall wall : walls) {
            // Loop over each section in each wall
            for (Wall.Section section : Wall.Section.values()) {
                // Loop over each block in each section
                for (GameObject block : section.getBlocks(wall)) {
                    // If true the bullet is within the X coordinates of the current wall
                    boolean within_x = (
                            bullet.getX() + 5 >= block.getX() - 2.5 && bullet.getX() + 5 <= block.getX() + 2.5 ||
                            bullet.getX() >= block.getX() - 2.5 && bullet.getX() <= block.getX() + 2.5
                    );

                    // If true the bullet is within the Y coordinates of the current wall
                    boolean within_y = bullet.getY() >= block.getY() - 2.5 && bullet.getY() <= block.getY() + 2.5;

                    if(within_x && within_y) {
                        // Damage the wall
                        wall.damage(bullet.getX(), bullet.getY(), bullet.isBomb());
                        return true;
                    }
                }
            }
        }

        // No section was hit, return false
        return false;
    }

    /**
     * Create all the walls
     *
     * @author Toby Essex
     */
    private void createWalls() {
        walls = new ArrayList<>();

        for (int i = 1; i <= NO_OF_WALLS; i++) {
            walls.add(new Wall(parent, (200 * i) - 100 - 47, parent.height - 200));
        }
    }
}
