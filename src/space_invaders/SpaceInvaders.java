package space_invaders;

import processing.core.PApplet;

import processing_gui.Pane;
import processing_gui.controls.Button;
import processing_gui.controls.Label;
import space_invaders.game_objects.Player;
import space_invaders.managers.AlienManager;
import space_invaders.managers.MotherShipManager;
import space_invaders.managers.PowerUpManager;
import space_invaders.managers.WallManager;
import space_invaders.resources.Image;
import space_invaders.resources.Sound;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

/**
 * Main game class (with entry point) this manages the game and and its objects.
 *
 * @author Toby Essex, Craig Hughes
 */
public class SpaceInvaders extends PApplet {

    /**
     * Size of each block as this game is pixelated.
     */
    public static final int BLOCK_SIZE = 5;

    /**
     * If true the game will have 2 players.
     */
    private boolean twoPlayerMode = false;

    /**
     * The player.
     */
    private Player player;

    /**
     * The 2nd player.
     */
    private Player player2;

    /**
     * If true the player is moving/ going to move.
     */
    private boolean movePlayer = false;

    /**
     * For 2nd player
     *
     * @see SpaceInvaders#movePlayer
     */
    private boolean movePlayer2 = false;

    /**
     * If true the player will move left, else he will move right.
     */
    private boolean moveLeft = false;

    /**
     * For 2nd player
     *
     * @see SpaceInvaders#moveLeft
     */
    private boolean moveLeft2 = false;

    /**
     * The current state of the game.
     */
    private static GameState currentGameState = GameState.START_MENU;

    /**
     * Container for all start menu nodes.
     */
    private Pane startMenuPane;

    /**
     * Container for all end menu nodes.
     */
    private Pane gameOverPane;

    private Label scoreLabel;
    private Label scoreLabel2 = new Label(" ", 121, 425, 30);

    private Label highScoreLabel;

    /**
     * Has gone through twice
     */
    private boolean hasBeenThrough = false;

    /**
     * Entry point.
     *
     * @param args Command line arguments.
     *
     * @author Toby Essex
     */
    public static void main(String[] args) {
        PApplet.main("space_invaders.SpaceInvaders");
    }

    /**
     * Register any game settings here.
     *
     * @author Toby Essex
     */
    public void settings() {
        size(800, 800);
    }

    /**
     * Sets up game objects here.
     *
     * @author Toby Essex
     */
    public void setup() {
        /*
        Load all the images
        This is called so all the images are ever only loaded once
        If they were loaded every time they were needed the game would be incredibly slow
        */
        Image.loadImages(this);

        textFont(createFont(getClass().getResource("game-font.ttf").toString(), 18));

        WallManager.getInst().setParent(this);
        AlienManager.getInst().setParent(this);
        MotherShipManager.getInst().setParent(this);
        PowerUpManager.getInst().setParent(this);

        if (twoPlayerMode) {
            player = new Player(this, 225);
            player2 = new Player(this, width - 300);
        } else {
            player = new Player(this, width / 2 - SpaceInvaders.BLOCK_SIZE * 5);
        }

        // Schedules sounds effects to start after one second and then to play every second
        new Timer().schedule(new TimerTask() {
            private boolean playHigh = true;

            @Override
            public void run() {
                if (currentGameState == GameState.GAME_OVER)
                    cancel();

                (playHigh ? Sound.ALIEN_MOVE_HIGH_PITCH : Sound.ALIEN_MOVE_LOW_PITCH).play();

                playHigh = !playHigh;
            }
        }, 1000, 1000);

        setupStartMenu();
        setupGameOverMenu();
    }

    /**
     * Called continuously. (Main game loop)
     *
     * @author Craig Hughes, Toby Essex
     */
    public void draw() {
        background(0);

        switch (currentGameState) {
            case START_MENU:
                startMenuPane.show();
                startMenuExtras();
                break;
            case GAME:
                // Sets-up Player 2, again.
                if (twoPlayerMode && !hasBeenThrough) {
                    player2 = new Player(this, width - 300);
                    hasBeenThrough = true;
                }
                game();
                break;
            case GAME_OVER:
                // Refreshes scores.
                scoreLabel.setText("PLAYER 1 SCORE: " + player.getScore());
                if(twoPlayerMode) {
                    scoreLabel2.setText("PLAYER 2 SCORE: " + player2.getScore());
                }

                highScoreLabel.setText("HIGHSCORE: " + getHighScore());
                gameOverPane.show();
                break;
        }
    }

    /**
     * Called when mouse is pressed.
     *
     * @author Toby Essex, Craig Hughes
     */
    public void mousePressed() {
        if (startMenuPane != null && currentGameState == GameState.START_MENU)
            startMenuPane.checkMousePressed();

        if (gameOverPane != null && currentGameState == GameState.GAME_OVER)
            gameOverPane.checkMousePressed();

        if(currentGameState == GameState.START_MENU && mouseX >= width/2 - 35 && mouseX <= width/2 + 35 && mouseY >= height - 240 && mouseY <= height - 180) {
            if (Sound.isMute()) {
                Sound.unMute();
            } else {
                Sound.mute();
            }
        }
    }

    /**
     * Called if a key is pressed down.
     *
     * @author Toby Essex
     */
    public void keyPressed() {
        if (twoPlayerMode && player.isDead() && player2.isDead()) {
            return;
        } else if (player.isDead()) {
            return;
        }

        if (key == 'a' || key == 'A') {
            movePlayer = true;
            moveLeft = true;
        } else if (key == 'd' || key == 'D') {
            movePlayer = true;
            moveLeft = false;
        } else if (twoPlayerMode && keyCode == LEFT) {
            movePlayer2 = true;
            moveLeft2 = true;
        } else if (twoPlayerMode && keyCode == RIGHT) {
            movePlayer2 = true;
            moveLeft2 = false;
        } else if (key == ' ') {
            player.fireBullet();
        } else if (twoPlayerMode && keyCode == UP) {
            player2.fireBullet();
        }

    }

    /**
     * Called if a key is released.
     *
     * @author Toby Essex
     */
    public void keyReleased() {
        if (twoPlayerMode && player.isDead() && player2.isDead() || player.isDead())
            return;

        if (key == 'a' || key == 'A') {
            movePlayer = false;
        } else if (key == 'd' || key == 'D') {
            movePlayer = false;
        } else if (twoPlayerMode && keyCode == LEFT) {
            movePlayer2 = false;
        } else if (twoPlayerMode && keyCode == RIGHT) {
            movePlayer2 = false;
        }
    }

    /**
     * Sets game state.
     *
     * @param gameState New state for the game.
     *
     * @see GameState
     *
     * @author Toby Essex, Craig Hughes
     */
    public static void setGameState(GameState gameState) {
        currentGameState = gameState;
    }

    /**
     * Displays elements needed for Start screen.
     *
     * @author Toby Essex, Craig Hughes
     */
    private void setupStartMenu() {
        Button buttonOnePlayer = new Button("ONE PLAYER", width/2, 385, 175, 20);
        Button buttonTwoPlayer = new Button ("TWO PLAYER", width/2, 465, 200, 20);
        Button buttonExit = new Button("EXIT", width/2, height-100, 200, 20);
        Label spaceLabel = new Label("SPACE", width/2 - 225, 120, 90);
        Label invadersLabel = new Label("INVADERS", width/2 - 225,170,56);

        buttonOnePlayer.setBackgroundColour(0,0,0);
        buttonOnePlayer.setTextColour(255,255,255);
        buttonOnePlayer.setFontSize(30);
        buttonOnePlayer.setHoverAnimation(Button.HoverAnimation.ENLARGE);
        buttonOnePlayer.setOnActionListener(e -> {
            twoPlayerMode = false;
            currentGameState = GameState.GAME;
        });

        buttonTwoPlayer.setBackgroundColour(0,0,0);
        buttonTwoPlayer.setTextColour(255,255,255);
        buttonTwoPlayer.setFontSize(30);
        buttonTwoPlayer.setHoverAnimation(Button.HoverAnimation.ENLARGE);
        buttonTwoPlayer.setOnActionListener(e -> {
            twoPlayerMode = true;
            currentGameState = GameState.GAME;
        });

        buttonExit.setBackgroundColour(0,0,0);
        buttonExit.setTextColour(255,255,255);
        buttonExit.setFontSize(30);
        buttonExit.setHoverAnimation(Button.HoverAnimation.ENLARGE);
        buttonExit.setOnActionListener(e -> exit());

        spaceLabel.setTextColour(255,255,255);
        invadersLabel.setTextColour(255,255,255);

        startMenuPane = new Pane(this);
        startMenuPane.addNodes(buttonOnePlayer, buttonTwoPlayer, buttonExit, spaceLabel, invadersLabel);
    }

    /**
     * Add in extra UI features for Start Menu.
     *
     * @author Craig Hughes
     */
    private void startMenuExtras(){
        image(Sound.isMute() ? Image.NO_SOUND.load() : Image.SOUND.load(), width / 2 - 35,height - 250,70,70);
    }

    /**
     * Displays elements needed for Game Over screen.
     *
     * @author Toby Essex, Craig Hughes
     */
    private void setupGameOverMenu() {
        Button buttonPlayAgain = new Button("PLAY AGAIN", 270, 650, 175, 20);
        Button buttonExit = new Button("EXIT", 600, 650, 200, 20);
        Label gameOverLabel = new Label("GAME OVER", width/2 - 310,120, 70);

        scoreLabel = new Label("YOUR SCORE P1: " + player.getScore(), 121,350,30);
        if(twoPlayerMode)
            scoreLabel2 = new Label("YOUR SCORE P2: " + player2.getScore(), 121,400,30);


        highScoreLabel = new Label("HIGHSCORE: " + getHighScore(),121,500,30);

        buttonPlayAgain.setBackgroundColour(0,0,0);
        buttonPlayAgain.setTextColour(255,255,255);
        buttonPlayAgain.setFontSize(30);
        buttonPlayAgain.setHoverAnimation(Button.HoverAnimation.ENLARGE);
        buttonPlayAgain.setOnActionListener(e -> {
            reset();
            currentGameState = GameState.GAME;
        });

        buttonExit.setBackgroundColour(0,0,0);
        buttonExit.setTextColour(255,255,255);
        buttonExit.setFontSize(30);
        buttonExit.setHoverAnimation(Button.HoverAnimation.ENLARGE);
        buttonExit.setOnActionListener(e -> exit());

        gameOverLabel.setTextColour(255,255,255);
        scoreLabel.setTextColour(255,255,255);

        if(scoreLabel2 != null)
            scoreLabel2.setTextColour(255,255,255);

        highScoreLabel.setTextColour(255,255,255);

        gameOverPane = new Pane(this);
        gameOverPane.addNodes(buttonPlayAgain, buttonExit, gameOverLabel, scoreLabel, scoreLabel2, highScoreLabel);

        if(scoreLabel2 != null && twoPlayerMode) {
            gameOverPane.addNode(scoreLabel2);
        }
    }

    /**
     * Game elements to be drawn.
     *
     * @author Craig Hughes, Toby Essex
     */
    private void game() {
        stroke(255);
        textSize(30);

        if (!player.isDead())
            player.show();

        if (twoPlayerMode && !player2.isDead())
            player2.show();

        WallManager.getInst().showWalls();

        AlienManager.getInst().showAliens();
        AlienManager.getInst().playerHit(player);

        MotherShipManager.getInst().spawnMotherShip();

        if (twoPlayerMode) {
            AlienManager.getInst().playerHit(player2);
            PowerUpManager.getInst().showPowerUp(player, player2);
        } else {
            PowerUpManager.getInst().showPowerUp(player);
        }

        displayUI();
        checkKeyPressed();
    }

    private void reset() {
        setup();
        WallManager.getInst().reset();
        AlienManager.getInst().reset();
    }

    /**
     * Displays the UI.
     *
     * @author Craig Hughes
     */
    private void displayUI() {
        checkScore();

        // Line Separator
        rect(width / 2, height - 50, width - 60, 2);

        if(!twoPlayerMode){
            textSize(16);
            text(String.format("SCORE:%d | HIGHSCORE: %d", player.getScore(), getHighScore()), 30, 40);

            IntStream.range(0, player.getLives()).forEach(i -> image(Image.LIFE.load(), (i * 30) + 30, height - 40, 30, 30));
        } else {
            textSize(15);
            text(String.format("P1 SCORE:%d | HIGHSCORE: %d | P2 SCORE:%d", player.getScore(), getHighScore(), player2.getScore()), 30, 40);

            textSize(12);
            text("P1 HEALTH:", 30, height - 27);
            IntStream.range(0, player.getLives()).forEach(i -> image(Image.LIFE.load(), (i * 22) + 160, height - 42, 20, 20));
            text("P2 HEALTH:", 30, height - 7);
            IntStream.range(0, player2.getLives()).forEach(i -> image(Image.LIFE.load(), (i * 22) + 160, height - 22, 20, 20));
        }

        // Literally no use, needed for visual aesthetic only.
        textSize(18);
        text("CREDITS:∞", width - 200, height - 15);
    }

    /**
     * Checks current score against saved High Score.
     *
     * @author Craig Hughes
     */
    private void checkScore() {
        if (player.getScore() > getHighScore()) {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("highscore.txt"), "utf-8"))) {
                writer.write(String.valueOf(player.getScore()));
            } catch (IOException e) {
                System.out.println("Error checking or writing high score: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * @return Returns saved High Score.
     *
     * @author Craig Hughes
     */
    private int getHighScore() {
        createFileIfNotCreated();

        String hs = "0";


        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("highscore.txt"))) {
            hs = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Error reading high score: " + e.getMessage());
            e.printStackTrace();
        }

        return Integer.parseInt(hs == null ? "0" : hs);
    }

    /**
     * Creates a new high score text file if it hasn't already been created.
     *
     * @author Toby Essex, Craig Hughes
     */
    private void createFileIfNotCreated() {
        try {
            File file = new File("highscore.txt");

            // noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called in draw check to see if a key is being pressed. <br>
     * This is needed to allow multiple key to be pressed at once.
     *
     * @author Toby Essex
     */
    private void checkKeyPressed() {
        if (twoPlayerMode && player.isDead() && player2.isDead()) {
            return;
        } else if (player.isDead()) {
            return;
        }

        if (movePlayer) {
            player.move(moveLeft);
        }

        if (twoPlayerMode && movePlayer2) {
            player2.move(moveLeft2);
        }
    }

    /**
     * Different states of the game.
     */
    public enum GameState {
        /**
         * Displays the start menu.
         */
        START_MENU,

        /**
         * Displays the game.
         */
        GAME,

        /**
         * Displays the game over screen
         */
        GAME_OVER,
    }
}
