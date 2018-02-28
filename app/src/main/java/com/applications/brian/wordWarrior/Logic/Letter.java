package com.applications.brian.wordWarrior.Logic;

import android.content.Context;
import android.graphics.Rect;

/**
 * Created by brian on 2018/02/16.
 *
 */

public class Letter {

    private String text;
    private final int textSize;
    private int x,y;
    private final int maxX;
    private final int maxY;
    private int speed=10;
    private final Rect collisionBoundary;
    private static final char[] alphabet={'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    private String currentAlphabet;




    Letter(Context context, int maxX, int maxY,int textSize){

        currentAlphabet=String.valueOf(alphabet)+ ArcadeGame.getCurrentWord();
        text=String.valueOf(currentAlphabet.charAt(ArcadeGame.RANDOM_GENERATOR.nextInt(currentAlphabet.length())));
        this.maxX=maxX-textSize;
        this.maxY=maxY;
        this.textSize=textSize;

        x= ArcadeGame.RANDOM_GENERATOR.nextInt(this.maxX);
        y=0;
        speed=15+ ArcadeGame.RANDOM_GENERATOR.nextInt(5);

        collisionBoundary =new Rect(x,y,x+textSize,y+textSize);
    }

    void update(){

        y+=speed;
        if(y>maxY){
            y= ArcadeGame.MINIMUM_Y;

            x= ArcadeGame.RANDOM_GENERATOR.nextInt(maxX);
            speed=15+ ArcadeGame.RANDOM_GENERATOR.nextInt(5);
            text=String.valueOf(currentAlphabet.charAt(ArcadeGame.RANDOM_GENERATOR.nextInt(currentAlphabet.length())));

        }
        updateCollisionBoundary();

    }

    private void updateCollisionBoundary(){
        collisionBoundary.set(x,y,x+textSize,y+textSize);
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
        return collisionBoundary.centerY();
    }



    void reset() {
        y=0;
        x= ArcadeGame.RANDOM_GENERATOR.nextInt(maxX);
        speed=15+ ArcadeGame.RANDOM_GENERATOR.nextInt(5);
        currentAlphabet=String.valueOf(alphabet)+ ArcadeGame.getCurrentWord();
        text=String.valueOf(currentAlphabet.charAt(ArcadeGame.RANDOM_GENERATOR.nextInt(currentAlphabet.length())));
        updateCollisionBoundary();

    }

    Rect getCollisionBoundary() {
        return collisionBoundary;
    }
    



}
