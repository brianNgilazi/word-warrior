package com.applications.brian.wordWarrior.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;

import com.applications.brian.wordWarrior.R;
import com.applications.brian.wordWarrior.Utilities.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by brian on 2018/02/17.
 * class to represent a Game;
 */

public class ArcadeGame {


    public static final String SCORE_FILE_NAME = "arcadeHighScores";
    private static final Random RANDOM_GENERATOR = new Random();
    private static final int MINIMUM_X = 0;
    private static int MINIMUM_Y = 0;
    private static int Maximum_Y;
    private static int Maximum_X;
    private static int level = 1;
    private static String currentWord;
    private final Context context;
    private final Controller controller;
    private final ArrayList<BackgroundItem> backgroundItems = new ArrayList<>();
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();
    private final ArrayList<Letter> letters = new ArrayList<>();

    // private static final int ITEM_COUNT=200;
    public Bitmap gameOverImage, pauseImage;
    private boolean gameOver;
    private boolean paused;
    private Player player;
    private int gamePoints = 0;
    private List<Bitmap> obstacleImages = new ArrayList<>();
    private Bitmap letterImage;
    private int minimumSpeed;
    private SoundPool soundPool;
    private int explodeSoundID;
    private int upgradeLevelID;
    private int penaliseSoundID;
    private int collideSoundID;


    public ArcadeGame(Context context, int maxX, int maxY, Controller controller) {
        Maximum_Y = maxY;
        Maximum_X = maxX;
        MINIMUM_Y = Math.round(Util.convertDpToPx(56, context));
        this.context = context;
        this.controller = controller;
        minimumSpeed = Math.round(Util.convertDpToPx(8, context));
        player = new Player(context, controller.getLexicon());
        int columns = maxX / 50;
        int rows = maxY / 100;
        for (int r = 1; r < rows + 1; r++) {
            if ((r * 100) <= MINIMUM_Y) continue;
            for (int c = 1; c < columns + 1; c++) {
                backgroundItems.add(new BackgroundItem(c * 50, r * 100));
            }
        }

        setUpImages();
        init();
        sounds();

    }

    private static String getCurrentWord() {
        StringBuilder builder = new StringBuilder(currentWord);
        while (builder.length() < 26 * (level + 1)) {
            builder.append(currentWord);
        }
        return builder.toString();
    }

    private void init() {
        gameOver = false;
        paused = false;
        level = 1;
        gamePoints = 0;
        currentWord = player.getCurrentWord();
        letters.add(new Letter(Maximum_X, Maximum_Y));
        for (int i = 0; i < 2; i++) {
            obstacles.add(new Obstacle(Maximum_X, Maximum_Y));
        }
    }

    private void sounds() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(4);
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        }
        explodeSoundID = soundPool.load(context, R.raw.game_over, 1);
        upgradeLevelID = soundPool.load(context, R.raw.level_up, 1);
        penaliseSoundID = soundPool.load(context, R.raw.needed_letter_collide, 1);
        collideSoundID = soundPool.load(context, R.raw.bug_collide, 1);
    }

    public void reset() {
        minimumSpeed = Math.round(Util.convertDpToPx(8, context));
        player.reset();
        letters.clear();
        obstacles.clear();
        init();
    }

    public void update() {
        if (!paused) {
            player.update();
            for (Letter letter : letters) {
                letter.update();
                if (Rect.intersects(player.getCollisionBoundary(), letter.getCollisionBoundary())) {
                    if (player.penalise(letter.getText()))
                        soundPool.play(penaliseSoundID, 0.25f, 0.25f, 2, 0, 1.0f);
                    letter.reset();
                }
            }

            for (Obstacle obstacle : obstacles) {
                obstacle.update();
                if (Rect.intersects(player.getCollisionBoundary(), obstacle.getCollisionBoundary())) {
                    soundPool.play(collideSoundID, 1.0f, 1.0f, 2, 0, 1.0f);
                    obstacle.reset();


                }
            }

            if (player.getLevel() > level) {
                nextLevel();
            }
            if (player.getHealthPoints() < 0) {
                gameOver = true;
                soundPool.play(explodeSoundID, 0.8f, 0.8f, 1, 1, 1.0f);
            }
        }

    }

    private void nextLevel() {
        soundPool.play(upgradeLevelID, 1.0f, 1.0f, 1, 0, 1.0f);
        gamePoints += (player.getHealthPoints() * level);
        level++;
        if (level > 2 && level % 3 == 0) {
            obstacles.add(new Obstacle(Maximum_X, Maximum_Y));
        } else {
            increaseSpeed();

        }
        currentWord = player.getCurrentWord();

    }

    private void increaseSpeed() {
        minimumSpeed += minimumSpeed / 5;

    }

    private int objectSpeed() {
        return minimumSpeed + RANDOM_GENERATOR.nextInt(minimumSpeed / 5);
    }

    public boolean newHighScore() {
        List<Integer> highScores = controller.getHighScores(SCORE_FILE_NAME);
        int size = highScores.size();
        if (size < 5) {
            highScores.add(gamePoints);
            controller.saveHighScores(SCORE_FILE_NAME, highScores);
            return true;
        }
        int lowestScore = highScores.get(4);
        if (gamePoints < lowestScore) return false;
        for (int i = 0; i < size; i++) {
            if (gamePoints > highScores.get(i)) {
                highScores.add(i, gamePoints);
                break;
            }
        }
        controller.saveHighScores(SCORE_FILE_NAME, highScores.subList(0, 5));
        return true;
    }

    //Getters
    public Player getPlayer() {
        return player;
    }

    public ArrayList<BackgroundItem> getBackgroundItems() {
        return backgroundItems;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public ArrayList<Letter> getLetters() {
        return letters;
    }

    public int getGamePoints() {
        return gamePoints;
    }

    //Player Movements
    public void startMoving(int touchCoordinate) {
        player.move(touchCoordinate);
    }

    public void stopMoving() {
        player.stopMoving();
    }

    //Game Pausing/Resuming
    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        paused = true;
    }

    public void unPause() {
        paused = false;
    }

    private void setUpImages() {

        //game Objects
        int newSide = Maximum_Y / 15;
        letterImage = Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.letter), newSide, newSide);
        newSide = Maximum_Y / 20;
        obstacleImages = new ArrayList<>();
        obstacleImages.add(Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_yellow), newSide, newSide));
        obstacleImages.add(Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_light_orange), newSide, newSide));
        obstacleImages.add(Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_dark_orange), newSide, newSide));
        obstacleImages.add(Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_red), newSide, newSide));
        obstacleImages.add(Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_lava), newSide, newSide));

        //game info
        gameOverImage = Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_over), Maximum_X, Maximum_Y / 2);
        pauseImage = Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.paused), Maximum_X, Maximum_Y / 2);
    }


    //Game Object Classes

    /**
     * Created by brian on 2018/02/15.
     */
    public static class BackgroundItem {

        private int x;
        private int y;
        private char text;
        private String values = "01";


        BackgroundItem(int x, int y) {
            this.x = x;
            this.y = y;
            text = values.charAt(RANDOM_GENERATOR.nextInt(values.length()));
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public String getText() {
            return String.valueOf(text);
        }
    }

    /**
     * Created by brian on 2018/02/15.
     * Class to represent the player;
     */
    public class Player {
        private Rect collisionBoundary;
        private String currentWord;
        private List<String> neededLetters;

        private Bitmap icon;


        private int x, y;
        private int maxX;
        private int maxY;
        private int totalScore = 50;
        private int level = 1;
        private boolean moving;
        private int direction;


        private GameDictionary gameDictionary;


        Player(Context context, GameDictionary gameDictionary) {

            icon = Util.resizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shield), Maximum_X / 6, Maximum_Y / 10);
            this.gameDictionary = gameDictionary;
            wordSetUp();
            coordinatesSetUp();
        }

        //Init methods
        private void wordSetUp() {
            if (neededLetters == null) neededLetters = new ArrayList<>();
            else neededLetters.clear();
            currentWord = gameDictionary.getRandomWord();
            while (currentWord.length() > 10 || currentWord.length() < 5) {
                currentWord = gameDictionary.getRandomWord();
            }
            for (char c : currentWord.toCharArray()) neededLetters.add(String.valueOf(c));
        }

        private void coordinatesSetUp() {
            this.maxX = Maximum_X - icon.getWidth();
            this.maxY = Maximum_Y;
            x = maxX / 2;
            y = maxY - (icon.getHeight() + Math.round(Util.convertDpToPx(4, context)));//a little padding
            collisionBoundary = new Rect(x, y, x + icon.getWidth(), maxY);
        }

        void reset() {
            wordSetUp();
            x = maxX / 2;
            collisionBoundary = new Rect(x, y, x + icon.getWidth(), maxY);
            level = 1;
            totalScore = 50;
        }

        void update() {
            if (moving) {
                x += (minimumSpeed * direction);
                if (x > maxX) {
                    x = MINIMUM_X;
                }
                if (x < MINIMUM_X) {
                    x = maxX;
                }
                collisionBoundary.set(x, y, x + icon.getWidth(), maxY);
            }

        }

        void move(int touchCoordinate) {
            moving = true;
            this.direction = (touchCoordinate - x < 0 ? -1 : 1);

        }

        void stopMoving() {
            moving = false;
        }

        void losePoints(int i) {
            totalScore -= i;
        }

        void updateWordInProgress(String letter) {
            boolean removed = neededLetters.remove(letter);
            if (removed) totalScore += 5;
            if (neededLetters.size() == 0) complete();

        }

        boolean penalise(String letter) {
            if (neededLetters.contains(letter)) {
                totalScore -= 2;
                return true;
            }
            return false;
        }

        private void complete() {
            level++;
            totalScore += currentWord.length() * level;
            wordSetUp();
        }

        //Getters
        public String getHealth() {
            return String.format(Locale.getDefault(), "HP: %d", totalScore);
        }

        public String getPoints() {
            return String.format(Locale.getDefault(), "Pts: %d    Lvl: %d", ArcadeGame.this.gamePoints, level);
        }

        public String getCurrentWordProgress() {
            return String.format(Locale.getDefault(), "%s", neededLetters.toString().replace(", ", " "));
        }

        int getLevel() {
            return level;
        }

        int getHealthPoints() {
            return totalScore;
        }

        public Bitmap getIcon() {
            return icon;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        String getCurrentWord() {
            return currentWord;
        }

        Rect getCollisionBoundary() {
            return collisionBoundary;
        }

    }

    /**
     * Created by brian on 2018/02/16.
     */
    public class Letter {

        private final int maxX;
        private final int maxY;
        private final Rect collisionBoundary;
        private String text;
        private Bitmap icon;
        private int x, y;
        private int speed;


        Letter(int maxX, int maxY) {

            icon = ArcadeGame.this.letterImage;
            text = String.valueOf(getCurrentWord().charAt(RANDOM_GENERATOR.nextInt(getCurrentWord().length())));
            this.maxX = maxX - icon.getWidth();
            this.maxY = maxY;

            x = RANDOM_GENERATOR.nextInt(this.maxX);
            y = MINIMUM_Y;
            speed = ArcadeGame.this.objectSpeed();

            collisionBoundary = new Rect(x, y, x + icon.getWidth(), y + icon.getHeight());
        }

        void update() {

            y += speed;
            if (y > maxY) {
                ArcadeGame.this.player.updateWordInProgress(this.text);
                y = MINIMUM_Y;
                x = RANDOM_GENERATOR.nextInt(maxX);
                speed = minimumSpeed + RANDOM_GENERATOR.nextInt(5);
                text = String.valueOf(getCurrentWord().charAt(RANDOM_GENERATOR.nextInt(getCurrentWord().length())));

            }
            updateCollisionBoundary();

        }

        private void updateCollisionBoundary() {
            collisionBoundary.set(x, y, x + icon.getWidth(), y + icon.getHeight());
        }


        public String getText() {
            return text;
        }


        public int getTextX() {
            return collisionBoundary.centerX();
        }

        public int getTextY() {
            return collisionBoundary.centerY() + Math.round(Util.convertDpToPx(32, context)) / 2;
        }


        void reset() {
            y = MINIMUM_Y;
            x = RANDOM_GENERATOR.nextInt(maxX);
            speed = ArcadeGame.this.objectSpeed();
            text = String.valueOf(getCurrentWord().charAt(RANDOM_GENERATOR.nextInt(getCurrentWord().length())));
            updateCollisionBoundary();
        }

        Rect getCollisionBoundary() {
            return collisionBoundary;
        }
    }

    /**
     * Created by brian on 2018/02/16.
     */
    public class Obstacle {
        private final int LOW_PENALTY = 0;
        private final int MEDIUM_PENALTY = 1;
        private final int HIGH_PENALTY = 2;
        private final int VERY_HIGH_PENALTY = 3;
        private final int EXTREME_PENALTY = 4;
        private final int maxX;
        private final int maxY;
        private final Rect collisionBoundary;
        private final int[] penaltyArray = {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 10, 10, 10, 10, 10, 20, 20, 20, 40, 40, 80};
        private String penalty;
        private Bitmap icon;
        private int x, y;
        private int speed;


        Obstacle(int maxX, int maxY) {


            penalty = String.valueOf(penaltyArray[RANDOM_GENERATOR.nextInt(penaltyArray.length)]);
            setIcon(Integer.parseInt(penalty));
            this.maxX = maxX - icon.getWidth();
            this.maxY = maxY;

            x = RANDOM_GENERATOR.nextInt(this.maxX);
            y = MINIMUM_Y;
            speed = ArcadeGame.this.objectSpeed();

            collisionBoundary = new Rect(x, y, x + icon.getWidth(), y + icon.getHeight());
        }

        void update() {

            y += speed;
            if (y > maxY) {
                ArcadeGame.this.player.losePoints(Integer.parseInt(penalty));
                y = MINIMUM_Y;
                x = RANDOM_GENERATOR.nextInt(maxX);
                speed = minimumSpeed + RANDOM_GENERATOR.nextInt(5);
                penalty = String.valueOf(penaltyArray[RANDOM_GENERATOR.nextInt(penaltyArray.length)]);
                setIcon(Integer.parseInt(penalty));

            }
            collisionBoundary.set(x, y, x + icon.getWidth(), y + icon.getHeight());

        }

        public Bitmap getIcon() {
            return icon;
        }

        private void setIcon(int penaltyValue) {
            switch (penaltyValue) {
                case 5:
                    icon = ArcadeGame.this.obstacleImages.get(LOW_PENALTY);
                    break;
                case 10:
                    icon = ArcadeGame.this.obstacleImages.get(MEDIUM_PENALTY);
                    break;
                case 20:
                    icon = ArcadeGame.this.obstacleImages.get(HIGH_PENALTY);
                    break;
                case 40:
                    icon = ArcadeGame.this.obstacleImages.get(VERY_HIGH_PENALTY);
                    break;
                case 80:
                    icon = ArcadeGame.this.obstacleImages.get(EXTREME_PENALTY);
                    break;
            }
        }

        public int getX() {
            return x;
        }


        public int getY() {
            return y;
        }


        void reset() {
            y = MINIMUM_Y;
            x = RANDOM_GENERATOR.nextInt(maxX);
            speed = ArcadeGame.this.objectSpeed();
            collisionBoundary.set(x, y, x + icon.getWidth(), y + icon.getHeight());
            penalty = String.valueOf(penaltyArray[RANDOM_GENERATOR.nextInt(penaltyArray.length)]);
            setIcon(Integer.parseInt(penalty));

        }

        Rect getCollisionBoundary() {
            return collisionBoundary;
        }


    }
}
