package com.applications.brian.wordWarrior.Logic;

import android.support.annotation.NonNull;

import com.applications.brian.wordWarrior.Utilities.Time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by brian on 2018/02/06.
 * Class to represent a Target GameWord TargetGame
 */

public class TargetGame {
    public static final String SAVE_FILE_NAME="targetSavedGames";
    public static final String SCORE_FILE_NAME="targetHighScores";
    private GameWord currentGameWord;
    private int score;
    private List<String> foundWords;
    private final Controller controller;
    private boolean newGame=true;
    private boolean gameWon=false;
    private List<Integer> highScores;
    private int nineLetterWords=0;

    /**
     * enum representing progress towards targets
     */
    public enum TARGET_STATUS {GOOD, GREAT, PERFECT,NONE }

    /**
     * enum representing status of save
     */
    public  enum SAVING_STATUS {OVERRIDE, NEW_SAVE}

    /**
     * enum representing status of save
     */
    public  enum GAME_LEVEL {QUICK, AVERAGE, LONG, MARATHON, RANDOM}
    private GAME_LEVEL game_level;



    public TargetGame(Controller controller,GAME_LEVEL level){
        this.controller=controller;
        currentGameWord=controller.getWord(level);
        score=0;
        foundWords=new ArrayList<>();
        game_level=level;
        highScores=controller.getHighScores(SCORE_FILE_NAME);
    }

    /**
     *  Constructor for starting a saved game
     * @param controller the controller
     * @param data the data required for loading;
     */
    public TargetGame(Controller controller, String data){
        this.controller=controller;
        load(data);
        newGame=false;
        highScores=controller.getHighScores(SCORE_FILE_NAME);
    }



    /**
     * Method to check if a word has been played previously;
     * @param attempt the word a player is trying to submit as an answer
     * @return true if the word has been played already; false if otherwise
     */
    public boolean checkPlayed(String attempt){
        return foundWords.contains(attempt);
    }

    /**
     * Method to check if a word is a correct answer;
     * @param attempt the word a player is trying to submit as an answer
     * @return true if the word is part of the solutions; false if otherwise
     */
    public boolean submitWord(String attempt){
         if(currentGameWord.getAnswers().contains(attempt.toLowerCase())){
                updateProgress(attempt);
             return true;
         }
         return false;
    }


    private void updateProgress(String word){
        foundWords.add(word);
        score++;
        if(word.length()==9){
            nineLetterWords+=1;
            gameWon=true;
        }
    }

    /**
     *
     * @return  {@link TARGET_STATUS} depending on progress
     *          NONE if no target met etc.
     */
    public TARGET_STATUS target_status(){
        if(score==currentGameWord.getTargets()[0]){
            return TARGET_STATUS.GOOD;
        }

        if(score==currentGameWord.getTargets()[1]){
            return TARGET_STATUS.GREAT;
        }

        if(score==currentGameWord.getTargets()[2]){
            return TARGET_STATUS.PERFECT;
        }

        return TARGET_STATUS.NONE;
    }

    public boolean newHighScore(String time) {
        int timeInt= Time.timeinSeconds(time);
        int size=highScores.size();
        if(size<5){
            highScores.add(timeInt);
            Collections.sort(highScores);
            controller.saveHighScores(SCORE_FILE_NAME,highScores);
            return true;
        }
        int lowestScore=highScores.get(highScores.size()-1);
        if(score>lowestScore)return false;
        highScores.remove(4);
        for(int i=0;i<size;i++){
            if(timeInt<highScores.get(i)){
                highScores.add(i,timeInt);
                break;
            }
        }
        controller.saveHighScores(SCORE_FILE_NAME,highScores);
        return true;
    }

    public boolean isNewGame() {
        return !newGame;
    }

    public int getGoodTarget(){
        return currentGameWord.getTargets()[0];
    }

    public int getGreatTarget(){
        return currentGameWord.getTargets()[1];
    }

    public int getPerfectTarget(){
        return currentGameWord.getTargets()[2];
    }

    public List<String> getFoundWords(){
        return foundWords;
    }

    public int getScore(){
        return score;
    }

    private String getGameWord(){
        return currentGameWord.toString();
    }

    private String getJumbledGameWord(){
        return currentGameWord.getGameAnagram();
    }

    /**
     * Method to reset game with new values
     */
    public void resetGame(){
        currentGameWord=controller.getWord(game_level);
        score=0;
        foundWords.clear();
        nineLetterWords=0;
        gameWon=false;
    }

    public void save(SAVING_STATUS saving_status){
        List<SavedGame> targetSavedGames =controller.savedGamesList(SAVE_FILE_NAME);
        TargetSavedGame latestTargetSavedGame =new TargetSavedGame(getSaveGameData());
        if(saving_status==SAVING_STATUS.NEW_SAVE){
            targetSavedGames.add(latestTargetSavedGame);

        }
        else if(saving_status==SAVING_STATUS.OVERRIDE){
            for (SavedGame targetSavedGame : targetSavedGames) {
                if (targetSavedGame.compareTo(latestTargetSavedGame) == 0) {
                        targetSavedGame.set(latestTargetSavedGame);
                }
            }
        }

        controller.saveGame(SAVE_FILE_NAME,targetSavedGames);
    }

    private String getSaveGameData(){
        StringBuilder builder=new StringBuilder();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE-dd-MMM-yyyy'|'HH:mm", Locale.getDefault());
        builder.append(simpleDateFormat.format(new Date(System.currentTimeMillis()))).append(" ");//0
        builder.append(getJumbledGameWord()).append(" ");//1
        builder.append(getGameWord()).append(" ");//2
        builder.append(getPerfectTarget()).append(" ");//3
        builder.append(game_level.name()).append(" ");//4
        for(String w:foundWords){
            builder.append(w).append(" ");//5+
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    private void load(String line){

        String[] data=line.split(" ");
        currentGameWord=controller.getWord(data[2],data[1]);
        game_level=GAME_LEVEL.valueOf(data[4]);
        foundWords=new ArrayList<>();
        foundWords.addAll(Arrays.asList(Arrays.copyOfRange(data,5,data.length)));
        score=foundWords.size();

    }

    public char[] getGameLetters(){
        return currentGameWord.getGameLetters();
    }

    public int targetWordLength(){
        return currentGameWord.toString().length();
    }

    public  boolean allSolutionsFound(){
        return score==currentGameWord.getAnswers().size();
    }

    public int getPoints(){
        int points=score*5;
        points+=nineLetterWords*250;
        switch (target_status()){
            case GOOD:
                points+=50;
                break;
            case GREAT:
                points+=100;
                break;
            case PERFECT:
                points+=500;
                break;
        }

        return points;

    }

    public static List<String> Levels(){
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add(GAME_LEVEL.QUICK.name());
        arrayList.add(GAME_LEVEL.AVERAGE.name());
        arrayList.add(GAME_LEVEL.LONG.name());
        arrayList.add(GAME_LEVEL.MARATHON.name());

        return arrayList;
    }


    /**
     * Created by brian on 2018/02/09.
     * Class to handle saved games in the form of strings
     */

    public static class TargetSavedGame implements SavedGame {

        private int score;
        private int total;
        private String rawData;
        private String name;
        private String level;
        private String gameAnagram;
        private String[] foundWords;


        public TargetSavedGame(String data){
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
            if(another instanceof TargetSavedGame){
                return gameAnagram.compareTo(((TargetSavedGame)another).gameAnagram);
            }
            return 0;
        }

        public void set(SavedGame another) {
            TargetSavedGame other=(TargetSavedGame)another; 
            rawData=other.rawData;
            name=other.name;
            gameAnagram=other.gameAnagram;
            total=other.total;
            score=other.score;
            level=other.level;
            foundWords= other.foundWords;
        }
    }
}
