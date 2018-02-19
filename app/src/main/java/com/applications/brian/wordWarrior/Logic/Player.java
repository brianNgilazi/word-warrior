package com.applications.brian.wordWarrior.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by brian on 2018/02/15.
 * Class to represent the player;
 *
 */

public class Player {
    private Rect collisionBoundary;
    private StringBuilder wordInProgress;
    private String currentWord;

    private Bitmap icon;
    private ArrayList<Bitmap> bitmaps;

    private final static int NORMAL_ICON = 0;
    private final static int LEFT_ICON = 2;
    private final static int RIGHT_ICON = 3;

    private int x, y;
    private int maxX;
    private int maxY;
    private int totalScore = 0;
    private int level =1;
    private int speed;
    private boolean moving;
    private int direction;


    private List<String> allWords;


    Player(Context context,List<String> allWords) {

        imagesSetUp(context);
        this.allWords = allWords;
        currentWord = allWords.get(TestGame.RANDOM_GENERATOR.nextInt(allWords.size())).toUpperCase();
        wordInProgress = new StringBuilder();
        for(int i = 0; i< currentWord.length(); i++){
            wordInProgress.append("-");
        }
        coordinatesSetUp();
    }

    private void coordinatesSetUp(){
        this.maxX = TestGame.Maximum_X- icon.getWidth();
        this.maxY = TestGame.Maximum_Y;
        x = maxX / 2;
        y = maxY - icon.getHeight()+20;//a little padding
        collisionBoundary = new Rect(x, y, x + icon.getWidth(), maxY);
        speed = 10;
    }

    private void imagesSetUp(Context context) {
        bitmaps = new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.big_shaq_sayan));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.big_shaq_super));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.big_shaq_left));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.big_shaq_right));

        icon = bitmaps.get(NORMAL_ICON);

    }

    public void reset(){
        currentWord = allWords.get(TestGame.RANDOM_GENERATOR.nextInt(allWords.size())).toUpperCase();
        clear();
        x=maxX/2;
        collisionBoundary = new Rect(x, y, x + icon.getWidth(), maxY);
        level=1;
        totalScore=0;

    }

    void update() {
        if (moving) {
            x += (speed * direction);
            if (x > maxX) {
                x = maxX;
            }
            if (x < TestGame.MINIMUM_X) {
                x = TestGame.MINIMUM_X;
            }
            collisionBoundary.set(x, y, x + icon.getWidth(), maxY);
        }


    }


    void move(int touchCoordinate) {
        moving = true;

        this.direction = (touchCoordinate-x < 0 ? -1 : 1);
        if (touchCoordinate-x > 0) icon = bitmaps.get(RIGHT_ICON);
        else if (touchCoordinate-x<0)icon = bitmaps.get(LEFT_ICON);
    }

    void stopMoving() {
        moving = false;
        icon = bitmaps.get(NORMAL_ICON);
    }


    void updateWordInProgress(String letter) {

        if(Character.isAlphabetic(letter.charAt(0))&& currentWord.contains(letter)){
            int index= currentWord.indexOf(letter);
            while(wordInProgress.charAt(index)==letter.charAt(0)){
                index= currentWord.indexOf(letter,index+1);
                if(index<0){
                    totalScore--;
                    return;
                }
            }
            wordInProgress.setCharAt(index,letter.charAt(0));
            totalScore+=5;
            if (wordInProgress.toString().equals(currentWord)) complete();
        }
        else {
            if(Character.isDigit(letter.charAt(0)))
            totalScore-=Integer.parseInt(letter);
        }


    }



    private void complete() {
        level++;
        totalScore += wordInProgress.length()* level;
        currentWord = allWords.get(new Random().nextInt(allWords.size())).toUpperCase();
        clear();
    }

    private void clear() {
        wordInProgress.delete(0, wordInProgress.length());
        for(int i = 0; i< currentWord.length(); i++){
            wordInProgress.append("-");
        }
    }

    public String getProgressDetails() {
        return String.format(Locale.getDefault(), "%s (%s)", wordInProgress.toString(), currentWord);
    }

    int getLevel() {
        return level;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getText() {
        return wordInProgress.toString();
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

    public String getCurrentWord() {
        return currentWord;
    }

    Rect getCollisionBoundary() {
        return collisionBoundary;
    }

    public List<String> getAllWords() {
        return allWords;
    }
}
