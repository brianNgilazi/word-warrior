package com.applications.brian.wordWarrior.Logic;

import java.util.Locale;

/**
 * Created by brian on 2018/02/26.
 *
 */

public class Profile {
    private int points;
    private int gamesPlayed;
    private int gamesWon;
    public static final String PREFERENCE_KEY="PROFILE";

    Profile(){
        points=0;
        gamesPlayed=0;
        gamesWon=0;
    }

    Profile(String data){
        String[] dataArray = data.split(" ");
        points=Integer.parseInt(dataArray[0]);
        gamesPlayed=Integer.parseInt(dataArray[1]);
        gamesWon=Integer.parseInt(dataArray[2]);
    }

    public int getPoints() {
        return points;
    }

    void incrementPoints(int points){
        this.points+=points;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    void incrementGamesPlayed(){
        gamesPlayed++;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    void incrementGamesWon(){
        gamesWon++;
    }

    private double getWinPercentage() {
        if(gamesPlayed==0)return 0;
        return (gamesWon/(gamesPlayed*1.0))*100;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),"%d %d %d",points,gamesPlayed,gamesWon);
    }

    String profileInfo(){
        return String.format(Locale.getDefault(),"Points: %d%nGames Played: %d%nWin Percentage: %.1f%%",
                points,gamesPlayed,getWinPercentage());
    }
}