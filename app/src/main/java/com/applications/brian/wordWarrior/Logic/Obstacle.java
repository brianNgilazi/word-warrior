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

public class Obstacle {
    private String penalty;
    private Bitmap icon;
    private int x,y;
    private  int maxX,maxY;
    private int speed=10;
    private Rect collisionBoundary;
    private int[] penaltyArray={5,5,5,5,5,5,5,5,5,5,10,10,10,10,10,20,20,20,40,40,80};



    Obstacle(Context context, int maxX, int maxY){


        icon= BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle_image);
        penalty =String.valueOf(penaltyArray[TestGame.RANDOM_GENERATOR.nextInt(penaltyArray.length)]);
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
            y=0;

            x=TestGame.RANDOM_GENERATOR.nextInt(maxX);
            speed=30+TestGame.RANDOM_GENERATOR.nextInt(5);
            penalty =String.valueOf(penaltyArray[TestGame.RANDOM_GENERATOR.nextInt(penaltyArray.length)]);

        }
        collisionBoundary.set(x,y,x+icon.getWidth(),y+icon.getHeight());

    }

  
    public String getPenalty() {
        return penalty;
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
        collisionBoundary.set(x,y,x+icon.getWidth(),y+icon.getHeight());

    }

    Rect getCollisionBoundary() {
        return collisionBoundary;
    }



}
