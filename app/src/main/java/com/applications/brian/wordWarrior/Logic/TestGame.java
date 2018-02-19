package com.applications.brian.wordWarrior.Logic;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by brian on 2018/02/17.
 * class to represent a Game;
 */

public class TestGame {


    private final Context context;
    private boolean gameOver;
    private boolean paused;
    private Player player;
    private ArrayList<BackgroundItem> backgroundItems =new ArrayList<>();
    private ArrayList<Obstacle> obstacles=new ArrayList<>();
    private ArrayList<Letter> letters =new ArrayList<>();

    private static final int ITEM_COUNT=200;


    static final Random RANDOM_GENERATOR=new Random();
    static final int MINIMUM_X = 0;
    static final int MINIMUM_Y = 0;
    static int Maximum_Y;
    static int Maximum_X;

    private static int level=1;

    private static String currentWord;

    public TestGame(Context context,int maxX, int maxY, List<String> allWords){


        Maximum_Y=maxY;
        Maximum_X=maxX;
        this.context=context;

        player =new Player(context,allWords);
        currentWord=player.getCurrentWord();
        for(int i=0;i<ITEM_COUNT;i++){
            backgroundItems.add(new BackgroundItem(maxX,maxY));
        }


        letters.add(new Letter(context,maxX,maxY));



        obstacles.add(new Obstacle(context,maxX,maxY));


    }

    public void reset() {
        gameOver=false;
        paused=false;
        player.reset();
        currentWord = player.getCurrentWord();
        level=1;
        letters.clear();
        obstacles.clear();

        letters.add(new Letter(context, Maximum_X, Maximum_Y));
        obstacles.add(new Obstacle(context, Maximum_X, Maximum_Y));
    }



    public void update() {
        player.update();
        for(BackgroundItem star: backgroundItems){
            star.update();
        }
        for(Letter letter:letters){
            letter.update();
            if(Rect.intersects(player.getCollisionBoundary(),letter.getCollisionBoundary())){
                player.updateWordInProgress(letter.getText());
                letter.reset();
            }
        }

        for(Obstacle obstacle:obstacles){
            obstacle.update();
            if(Rect.intersects(player.getCollisionBoundary(),obstacle.getCollisionBoundary())){
                player.updateWordInProgress(obstacle.getPenalty());
                obstacle.reset();
            }
        }
        if(player.getLevel()!=level){
            nextLevel();
        }
        if(player.getTotalScore()<0){
            gameOver=true;
        }
    }

    private void nextLevel(){
        Log.d("Level","level is:"+level);
            level++;
            currentWord=player.getCurrentWord();
            letters.add(new Letter(context,Maximum_X,Maximum_Y));

            obstacles.add(new Obstacle(context,Maximum_X,Maximum_Y));

    }

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

    public void startMoving(int touchCoordinate) {
        player.move(touchCoordinate);
    }

    public void stopMoving() {
        player.stopMoving();
    }

    static String getCurrentWord() {
        StringBuilder builder=new StringBuilder(currentWord);
        while(builder.length()<26*(level+1)){
            builder.append(currentWord);
        }
        return builder.toString();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        paused=true;
    }

    public List<String> getLexicon(){
        return player.getAllWords();

    }
}
