package com.applications.brian.wordWarrior.Utilities;

import java.util.Locale;

/**
 * Created by brian on 2018/03/04.
 *
 */

public class Time {
    private int hours,minutes,seconds;
    private static int MAXIMUM_MINUTES_SECONDS=59;



    Time(){
        hours=0;
        minutes=0;
        seconds=0;
    }

    void tick(){
        seconds++;
        if(seconds>MAXIMUM_MINUTES_SECONDS){
            seconds=0;
            minutes++;
        }
        if(minutes>MAXIMUM_MINUTES_SECONDS){
            minutes=0;
            hours++;
        }
    }

    void reset(){
        hours=0;
        minutes=0;
        seconds=0;
    }

    String stringValue(){
        if(hours==0){
            return String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        }
        return this.toString();
    }

    public static int timeInSeconds(String time){
        String[] data=time.split(":");
        int total=Integer.parseInt(data[data.length-1]);
        total+=Integer.parseInt(data[data.length-2])*60;
        if(data.length==3)total+=Integer.parseInt(data[0])*60*60;
        return total;
    }

    public static String secondsToTimerString(int time){
        int x=time;
        int hours=x/(60*60);x=x%(60*60);
        int minutes=x/60;
        int seconds=x%60;
        return String.format(Locale.getDefault(),"%dh%02dm%02ds",hours,minutes,seconds);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,seconds);
    }
}
