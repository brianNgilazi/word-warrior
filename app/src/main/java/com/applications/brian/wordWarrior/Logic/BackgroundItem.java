package com.applications.brian.wordWarrior.Logic;

import java.util.Random;

/**
 * Created by brian on 2018/02/15.
 *
 */

public class BackgroundItem {


    private int x,y;
    private int maxX;
    private int maxY;
    private int minY;
    private int item;


    private int speed=0;

    public BackgroundItem(int screenX, int screenY){
        maxX=screenX;
        maxY=screenY;
        minY=0;
        Random random=new Random();
        speed=25;
        x=random.nextInt(maxX);
        y=random.nextInt(maxY);
        item =random.nextInt(2);

    }

    public void update(){
        y+=speed;
        if(y>maxY){
            y=minY;
            Random random=new Random();
            x=random.nextInt(maxX);
            item =random.nextInt(2);
        }
    }


    public int getItem() {
        return item;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
