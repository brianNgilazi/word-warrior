package com.applications.brian.wordWarrior.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by brian on 2018/02/17.
 * class to represent a Game;
 */

public class ArcadeGame {


    private final Context context;
    public static final String SCORE_FILE_NAME="arcadeHighScores";
    private final Controller controller;
    private boolean gameOver;
    private boolean paused;
    private Player player;
    private int gamePoints=0;
    private final ArrayList<BackgroundItem> backgroundItems =new ArrayList<>();
    private final ArrayList<Obstacle> obstacles=new ArrayList<>();
    private final ArrayList<Letter> letters =new ArrayList<>();
    private List<Integer> highScores;

   // private static final int ITEM_COUNT=200;


    private static final Random RANDOM_GENERATOR=new Random();
    private static final int MINIMUM_X = 0;
    private static final int MINIMUM_Y = 0;
    private static int Maximum_Y;
    private static int Maximum_X;
    private int minimumSpeed=15;

    private static int level=1;

    private static String currentWord;

    public ArcadeGame(Context context, int maxX, int maxY,Controller controller){
        Maximum_Y=maxY;
        Maximum_X=maxX;
        this.context=context;
        this.controller=controller;
        player =new Player(context,controller.getAllWords());
        int columns=maxX/50;
        int rows=maxY/100;
        for(int r=0;r<rows+1;r++){
            for(int c=1;c<columns+1;c++) {
                backgroundItems.add(new BackgroundItem(c*50, r*100));
            }
        }
        highScores=controller.getHighScores(SCORE_FILE_NAME);
        init();

    }

    private void init(){
        gameOver=false;
        paused=false;
        level=1;
        currentWord=player.getCurrentWord();
        letters.add(new Letter(context,Maximum_X,Maximum_Y));
        for(int i=0;i<2;i++) {obstacles.add(new Obstacle(context, Maximum_X, Maximum_Y));}
    }

    public void reset() {
        minimumSpeed=15;
        player.reset();
        letters.clear();
        obstacles.clear();
        init();
    }

    public void update() {
        if(!paused) {
            player.update();
            for (Letter letter : letters) {
                letter.update();
                if (Rect.intersects(player.getCollisionBoundary(), letter.getCollisionBoundary())) {
                    letter.reset();
                }
            }

            for (Obstacle obstacle : obstacles) {
                obstacle.update();
                if (Rect.intersects(player.getCollisionBoundary(), obstacle.getCollisionBoundary())) {
                    obstacle.reset();
                }
            }

            if (player.getLevel() > level) {
                nextLevel();
            }
            if (player.getTotalScore() < 0) {
                gameOver = true;

            }
        }
    }

    private void nextLevel(){
        gamePoints+=(player.getTotalScore()*level);
        level++;
        if(level>2 && level%3==0){
            obstacles.add(new Obstacle(context, Maximum_X, Maximum_Y));
        }
        else minimumSpeed+=5;
        currentWord=player.getCurrentWord();

    }

    public boolean newHighScore( ) {
        int size=highScores.size();
        if(size<5){
            highScores.add(gamePoints);
            Collections.sort(highScores);
            controller.saveHighScores(SCORE_FILE_NAME,highScores);
            return true;
        }
        int lowestScore=highScores.get(0);
        Collections.reverse(highScores);
        if(gamePoints<lowestScore)return false;
        highScores.remove(4);
        for(int i=0;i<size;i++){
            if(gamePoints>highScores.get(i)){
                highScores.add(i,gamePoints);
                break;
            }
        }
        Collections.reverse(highScores);
        controller.saveHighScores(SCORE_FILE_NAME,highScores);
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

    private static String getCurrentWord() {
        StringBuilder builder=new StringBuilder(currentWord);
        while(builder.length()<26*(level+1)){
            builder.append(currentWord);
        }
        return builder.toString();
    }

    //Game Pausing/Resuming
    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        paused=true;
    }

    public void unPause() {
        paused=false;
    }



  //Game Object Classes

    /**
     * Created by brian on 2018/02/16.
     *
     */
    public class Letter {

        private String text;
        private Bitmap icon;
        private int x,y;
        private final int maxX;
        private final int maxY;
        private int speed=ArcadeGame.this.minimumSpeed;
        private final Rect collisionBoundary;
        private String currentAlphabet;




        Letter(Context context, int maxX, int maxY){

            icon= BitmapFactory.decodeResource(context.getResources(), R.drawable.letter);
            currentAlphabet=getCurrentWord();
            text=String.valueOf(currentAlphabet.charAt(RANDOM_GENERATOR.nextInt(currentAlphabet.length())));
            this.maxX=maxX-icon.getWidth();
            this.maxY=maxY;

            x= RANDOM_GENERATOR.nextInt(this.maxX);
            y=0;
            speed=minimumSpeed+ RANDOM_GENERATOR.nextInt(5);

            collisionBoundary =new Rect(x,y,x+icon.getWidth(),y+icon.getHeight());
        }

        void update(){

            y+=speed;
            if(y>maxY){
                ArcadeGame.this.player.updateWordInProgress(this.text);
                y= MINIMUM_Y;
                x= RANDOM_GENERATOR.nextInt(maxX);
                speed=minimumSpeed+ RANDOM_GENERATOR.nextInt(5);
                text=String.valueOf(currentAlphabet.charAt(RANDOM_GENERATOR.nextInt(currentAlphabet.length())));

            }
            updateCollisionBoundary();

        }

        private void updateCollisionBoundary(){
            collisionBoundary.set(x,y,x+icon.getWidth(),y+icon.getHeight());
        }


        public Bitmap getIcon() {
            return icon;
        }

        public String getText() {
            return text;
        }


        public int getX() {
            return x;
        }


        public int getY() {
            return y;
        }

        public int getTextX() {
            return collisionBoundary.centerX();
        }



        public int getTextY() {
            return collisionBoundary.centerY()+16;
        }



        void reset() {
            y=0;
            x= RANDOM_GENERATOR.nextInt(maxX);
            speed=minimumSpeed+ RANDOM_GENERATOR.nextInt(5);
            currentAlphabet=
                    getCurrentWord();
            text=String.valueOf(currentAlphabet.charAt(RANDOM_GENERATOR.nextInt(currentAlphabet.length())));
            updateCollisionBoundary();

        }

        Rect getCollisionBoundary() {
            return collisionBoundary;
        }
    }

    /**
     * Created by brian on 2018/02/15.
     *
     */
    public static class BackgroundItem {

        private int x;
        private int y;
        private  char text;
        private String values="01";


        BackgroundItem(int x,int y){
            this.x=x;
            this.y=y;
            text= values.charAt(RANDOM_GENERATOR.nextInt(values.length()));
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
     *
     */
    public class Player {
        private Rect collisionBoundary;
        private String currentWord;
        private List<String> neededLetters;

        private Bitmap icon;


        private int x, y;
        private int maxX;
        private int maxY;
        private int totalScore = 0;
        private int level =1;
        private boolean moving;
        private int direction;


        private final List<String> allWords;


        Player(Context context,List<String> allWords) {

            icon =BitmapFactory.decodeResource(context.getResources(), R.drawable.mouse);
            this.allWords = allWords;
            wordSetUp();
            coordinatesSetUp();
        }

        //Init methods
        private void wordSetUp(){
            if(neededLetters==null)neededLetters=new ArrayList<>();
            else neededLetters.clear();
            currentWord = allWords.get(RANDOM_GENERATOR.nextInt(allWords.size())).toUpperCase();
            for(char c:currentWord.toCharArray())neededLetters.add(String.valueOf(c));
        }

        private void coordinatesSetUp(){
            this.maxX = Maximum_X - icon.getWidth();
            this.maxY = Maximum_Y;
            x = maxX / 2;
            y = maxY - icon.getHeight()+20;//a little padding
            collisionBoundary = new Rect(x, y, x + icon.getWidth(), maxY);
        }

        void reset(){
            wordSetUp();
            x=maxX/2;
            collisionBoundary = new Rect(x, y, x + icon.getWidth(), maxY);
            level=1;
            totalScore=0;

        }

        void update() {
            if (moving) {
                x += ( minimumSpeed* direction);
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
            this.direction = (touchCoordinate-x < 0 ? -1 : 1);

        }

        void stopMoving() {
            moving = false;
        }

        void losePoints(int i) {
            totalScore-=i;
        }

        void updateWordInProgress(String letter) {
            boolean removed=neededLetters.remove(letter);
            if(removed)totalScore+=5;
            else{
                totalScore-=1;
            }
            if (neededLetters.size()==0) complete();

        }

        private void complete() {
            level++;
            totalScore += currentWord.length()*level;
            wordSetUp();
        }

        //Getters
        public String getProgressDetails() {
            return String.format(Locale.getDefault(), "%s", neededLetters.toString());
        }

        int getLevel() {
            return level;
        }

        public int getTotalScore() {
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
     *
     */
    public class Obstacle {
        private String penalty;

        private Bitmap icon;
        private ArrayList<Bitmap> bitmaps;
        private final int LOW_PENALTY = 0;
        private final int MEDIUM_PENALTY = 1;
        private final int HIGH_PENALTY = 2;
        private final int VERY_HIGH_PENALTY = 3;
        private final int EXTREME_PENALTY = 4;
        private int x,y;
        private final int maxX;
        private final int maxY;
        private int speed=minimumSpeed;
        private final Rect collisionBoundary;
        private final int[] penaltyArray={5,5,5,5,5,5,5,5,5,5,10,10,10,10,10,20,20,20,40,40,80};


        Obstacle(Context context, int maxX, int maxY){


            imagesSetUp(context);
            penalty =String.valueOf(penaltyArray[RANDOM_GENERATOR.nextInt(penaltyArray.length)]);
            setIcon(Integer.parseInt(penalty));
            this.maxX=maxX-icon.getWidth();
            this.maxY=maxY;

            x= RANDOM_GENERATOR.nextInt(this.maxX);
            y=0;
            speed=15+ RANDOM_GENERATOR.nextInt(5);

            collisionBoundary =new Rect(x,y,x+icon.getWidth(),y+icon.getHeight());
        }

        private void imagesSetUp(Context context) {
            bitmaps = new ArrayList<>();
            bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_yellow));
            bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_light_orange));
            bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_dark_orange));
            bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_red));
            bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_lava));
        }

        private void setIcon(int penaltyValue){
            switch (penaltyValue){
                case 5:
                    icon=bitmaps.get(LOW_PENALTY);
                    break;
                case 10:
                    icon=bitmaps.get(MEDIUM_PENALTY);
                    break;
                case 20:
                    icon=bitmaps.get(HIGH_PENALTY);
                    break;
                case 40:
                    icon=bitmaps.get(VERY_HIGH_PENALTY);
                    break;
                case 80:
                    icon=bitmaps.get(EXTREME_PENALTY);
                    break;
            }
        }

        void update(){

            y+=speed;
            if(y>maxY){
                ArcadeGame.this.player.losePoints(Integer.parseInt(penalty));
                y=0;
                x= RANDOM_GENERATOR.nextInt(maxX);
                speed=minimumSpeed+ RANDOM_GENERATOR.nextInt(5);
                penalty =String.valueOf(penaltyArray[RANDOM_GENERATOR.nextInt(penaltyArray.length)]);
                setIcon(Integer.parseInt(penalty));

            }
            collisionBoundary.set(x,y,x+icon.getWidth(),y+icon.getHeight());

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


        void reset() {
            y=0;
            x= RANDOM_GENERATOR.nextInt(maxX);
            speed=minimumSpeed+ RANDOM_GENERATOR.nextInt(5);
            collisionBoundary.set(x,y,x+icon.getWidth(),y+icon.getHeight());
            penalty =String.valueOf(penaltyArray[RANDOM_GENERATOR.nextInt(penaltyArray.length)]);
            setIcon(Integer.parseInt(penalty));

        }

        Rect getCollisionBoundary() {
            return collisionBoundary;
        }



    }
}
