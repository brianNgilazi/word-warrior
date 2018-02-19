package com.applications.brian.wordWarrior.Logic;

import android.support.annotation.NonNull;

import java.util.Arrays;


/**
 * Created by brian on 2018/02/09.
 * Class to handle saved games in the form of strings
 */

public class SavedGame implements Comparable<SavedGame> {

    private int score;
    private int total;
    private String rawData;
    private String name;
    private String level;
    private String gameAnagram;
    private String[] foundWords;


    public SavedGame(String data){
        rawData=data;
        String[] dataArray=data.split(" ");
        name=dataArray[0];
        gameAnagram=dataArray[1];
        total=Integer.parseInt(dataArray[3]);
        level=dataArray[4];
        score=dataArray.length-5;
        foundWords= Arrays.copyOfRange(dataArray,5,dataArray.length);
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getRow(int rowNumber){
        switch (rowNumber){
            case 1:
                return gameAnagram.substring(0,3);
            case 2:
                return gameAnagram.substring(3,6);
            case 3:
                return gameAnagram.substring(6,9);

        }
        return " ";
    }




    @Override
    public String toString() {
        return rawData;
    }


    @Override
    public int compareTo(@NonNull SavedGame another) {
        return gameAnagram.compareTo(another.gameAnagram);
    }

    void set(SavedGame another) {
        rawData=another.rawData;
        name=another.name;
        gameAnagram=another.gameAnagram;
        total=another.total;
        score=another.score;
        level=another.level;
        foundWords= another.foundWords;
    }
}
