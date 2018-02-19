package com.applications.brian.wordWarrior.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.applications.brian.wordWarrior.R;

/**
 * Created by brian on 2018/02/16.
 *
 */

public class Letter {

    private String text;
    private Bitmap icon;
    private int x,y;
    private  int maxX,maxY;
    private int speed=10;
    private Rect collisionBoundary;
    private static char[] alphabet={'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    private String currentAlphabet;




    Letter(Context context, int maxX, int maxY){

        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_letter);

        currentAlphabet=String.valueOf(alphabet)+TestGame.getCurrentWord();
        text=String.valueOf(currentAlphabet.charAt(TestGame.RANDOM_GENERATOR.nextInt(currentAlphabet.length())));
        this.maxX=maxX-icon.getWidth();
        this.maxY=maxY;

        x=TestGame.RANDOM_GENERATOR.nextInt(this.maxX);
        y=0;
        speed=30+TestGame.RANDOM_GENERATOR.nextInt(5);

        collisionBoundary =new Rect(x,y,x+icon.getWidth(),y+icon.getHeight());
    }

    void update(){

        y+=speed;
        if(y>maxY){
            y=TestGame.MINIMUM_Y;
           
            x=TestGame.RANDOM_GENERATOR.nextInt(maxX);
            speed=30+TestGame.RANDOM_GENERATOR.nextInt(5);
            text=String.valueOf(currentAlphabet.charAt(TestGame.RANDOM_GENERATOR.nextInt(currentAlphabet.length())));

        }
        collisionBoundary.set(x,y,x+icon.getWidth(),y+icon.getHeight());

    }

   
    public String getText() {
        return text;
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

    public int getTextX() {
        return collisionBoundary.centerX();
    }


    public int getTextY() {
        return collisionBoundary.centerY();
    }



    void reset() {
        y=0;
        x=TestGame.RANDOM_GENERATOR.nextInt(maxX);
        speed=30+TestGame.RANDOM_GENERATOR.nextInt(5);
        currentAlphabet=String.valueOf(alphabet)+TestGame.getCurrentWord();
        text=String.valueOf(currentAlphabet.charAt(TestGame.RANDOM_GENERATOR.nextInt(currentAlphabet.length())));
        collisionBoundary.set(x,y,x+icon.getWidth(),y+icon.getHeight());

    }

    Rect getCollisionBoundary() {
        return collisionBoundary;
    }
    



}
