package space_invaders.game_objects;

import processing.core.PApplet;
import space_invaders.managers.WallManager;

import java.util.*;

/**
 * Represents a physical wall in the game which stops invading bullet, while also taking damage per hit.
 *
 * @author Toby Essex
 */
public class Wall extends GameObject {

    /**
     * Wall constructor.
     *
     * @param parent Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class.
     * @param x Wall's X coordinates.
     * @param y Wall's Y coordinates.
     */
    public Wall(PApplet parent, int x, int y) {
        super(parent, x, y);

        createWall();
    }

    /**
     * Displays the wall.
     *
     * @author Toby Essex
     */
    @Override
    public void show() {
        // Display each section of the wall
        Arrays.stream(Section.values()).forEach(section -> section.show(this));
    }

    /**
     * If the wall has been hit, this works each which section was hit and damages it.
     *
     * @param bulletX Bullet's X coordinates.
     * @param bulletY Bullet's Y coordinates.
     * @param isBomb If true the current bullet is a bomb.
     *
     * @see WallManager#hasHitWall(Bullet)
     *
     * @author Toby Essex
     */
    public void damage(int bulletX, int bulletY, boolean isBomb) {
        boolean isTopSection = bulletY >= y && bulletY <= y + (getWidth() / 4);

        if (bulletX >= x && bulletX <= x + getWidth() / 3) {
            isTopSection = isTopSection || Section.BOTTOM_LEFT.damageTier(this) == 0;
            (isTopSection ? Section.TOP_LEFT : Section.BOTTOM_LEFT).damage(this, isBomb);
        } else if (bulletX >= x + getWidth() / 3 && bulletX <= x + (getWidth() / 3) * 2) {
            isTopSection = isTopSection || Section.BOTTOM_MIDDLE.damageTier(this) == 0;
            (isTopSection ? Section.TOP_MIDDLE : Section.BOTTOM_MIDDLE).damage(this, isBomb);
        } else if (bulletX >= x + (getWidth() / 3) * 2 && bulletX <= x + getWidth()) {
            isTopSection = isTopSection || Section.BOTTOM_RIGHT.damageTier(this) == 0;
            (isTopSection ? Section.TOP_RIGHT : Section.BOTTOM_RIGHT).damage(this, isBomb);
        } else {
            // FIXME: sometimes the bullet is not registering as any section
            System.out.println("Not registering section");
        }
    }

    /**
     * Creates the wall.
     *
     * @author Toby Essex
     */
    private void createWall() {
        int _x = x;
        int _y = y;

        // Used to tell whether we have finished the bottom section of the wall
        boolean haveSwitched = false;

        for (Section section : Section.values()) {
            // Bottom section is finished, switch to creating the wall's top sections
            if(!section.isTop() && !haveSwitched) {
                _x = x;
                _y += 35;
                haveSwitched = true;
            }

            section.createSection(this, parent, _x, _y);
            _x += 35;
        }
    }

    /**
     * @return Wall's height.
     *
     * @author Toby Essex
     */
    public int getHeight() {
        return 105;
    }

    /**
     * @return Wall's width.
     *
     * @author Toby Essex
     */
    public int getWidth() {
        return 105;
    }

    /**
     * Enum to represents the different sections of the wall.
     *
     * @author Toby Essex
     */
    public enum Section {
        /**
         * Top left section of the wall.
         */
        TOP_LEFT,

        /**
         * Top middle section of the wall.
         */
        TOP_MIDDLE,

        /**
         * Top right section of the wall.
         */
        TOP_RIGHT,

        /**
         * Bottom left section of the wall.
         */
        BOTTOM_LEFT,

        /**
         * Bottom middle section of the wall.
         */
        BOTTOM_MIDDLE,

        /**
         * Bottom right section of the wall.
         */
        BOTTOM_RIGHT;

        /**
         * Used so we know how damaged the wall is. <br>
         * When it reached zero this particular section of the wall will be gone.
         */
        private HashMap<Wall, Integer> damageTier = new HashMap<>();

        /**
         * Hash map of all the block in each section. <br>
         * Split into key, values with a key being a wall instance and an enum is static.
         */
        private HashMap<Wall, ArrayList<GameObject>> blocks = new HashMap<>();

        /**
         * Returns all the blocks in a wall section.
         *
         * @param wall Specific wall so it can be founds in the hash map.
         * @return List of block in this section.
         *
         * @author Toby Essex
         */
        public ArrayList<GameObject> getBlocks(Wall wall) {
            return blocks.get(wall);
        }

        /**
         * Display's the wall.
         *
         * @param wall Specific wall so it can be founds in the key/value array.
         *
         * @author Toby Essex
         */
        public void show(Wall wall) {
            blocks.get(wall).forEach(GameObject::show);
        }

        /**
         * @return If true this particular section is a top section. (e.g. TOP_LEFT, TOP_MIDDLE, TOP_RIGHT)
         *
         * @author Toby Essex
         */
        public boolean isTop() {
            return this == TOP_LEFT || this == TOP_MIDDLE || this == TOP_RIGHT;
        }

        /**
         * Gets the wall's damage tier.
         *
         * @param wall Specific wall so it can be founds in the key/value array.
         * @return Returns the current damage tier (from 4 to 0).
         *
         * @author Toby Essex
         */
        public int damageTier(Wall wall) {
            return damageTier.get(wall);
        }

        /**
         * Damages the wall.
         *
         * @param wall Specific wall so it can be founds in the key/value array.
         * @param isBomb If true the current bullet is a bomb.
         *
         * @see Wall#damage(int, int, boolean)
         *
         * @author Toby Essex
         */
        public void damage(Wall wall, boolean isBomb) {
            // If the bullet is a bomb the whole section will be destroyed
            if(isBomb) {
                blocks.put(wall, new ArrayList<>());
                damageTier.put(wall, 0);
                return;
            }

            // Randomly removes blocks from the wall
            blocks.get(wall).removeIf(block ->
                    damageTier.get(wall) != 0 &&
                    blocks.get(wall).indexOf(block) % ((int)(Math.random() * blocks.size()) + 1) == 0 ||
                    damageTier.get(wall) == 1
            );

            damageTier.put(wall, damageTier.get(wall) - 1);
        }

        /**
         * Crates this particular wall section
         *
         * @param wall Specific wall so it can be created in the hash map.
         * @param parent Represents the parent class (space_invaders.SpaceInvaders) so we can access all the methods and variables declared is the PApplet class.
         * @param _x Section's X coordinates
         * @param _y Section's Y coordinates
         *
         * @see Wall#createWall()
         * @author Toby Essex
         */
        private void createSection(Wall wall, PApplet parent, int _x, int _y) {
            // Create copy's so we can return reset the original values during the for loop
            int x = _x;
            int y = _y;

            // Array for the section
            ArrayList<GameObject> section = new ArrayList<>();

            // Loop over the wall (outer array is no. of columns, inner array is no. of rows)
            for (int i = 0; i < (this == BOTTOM_MIDDLE ? 2 : 7); i++) {
                int max;

                // If statements allow for the wall to look curves
                // This is done by creating a pyramid effects on the according side
                if (this == TOP_RIGHT && i < 4) {
                    max = 7 - (4 - i);
                } else if(this == TOP_LEFT && i < 4) {
                    max = 7 - (4 - i);
                    x += 5 * (4 - i);
                } else if(this == BOTTOM_LEFT && i > 1) {
                    max = i < 5 ? 9 - i : 5;
                } else if(this == BOTTOM_RIGHT && i > 1) {
                    max = (i < 5 ? 9 - i : 5) + 1;
                    x += 5 * (7 - max);
                } else {
                    max = 7;
                }

                for (int j = 0; j < max; j++) {
                    // Add a new section (via anonymous class)
                    section.add(new GameObject(parent, x, y) {
                        @Override
                        public void show() {
                            parent.fill(255);
                            parent.rectMode(parent.CENTER);
                            parent.rect(x, y, 5, 5);
                        }
                    });
                    x += 5;
                }
                // Reset X, move down to the next row
                x = _x;
                y += 5;
            }

            // Add the section to the hash map, set the walls damage tier
            blocks.put(wall, section);
            damageTier.put(wall, 4);
        }
    }
}
