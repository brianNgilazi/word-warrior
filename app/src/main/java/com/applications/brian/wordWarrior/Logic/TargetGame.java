package com.applications.brian.wordWarrior.Logic;

import android.support.annotation.NonNull;
import android.util.Log;

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
 * Class to represent a Target TargetGameWord TargetGame
 */

public class TargetGame {
    public static final String SAVE_FILE_NAME="targetSavedGames";
    public static final String SCORE_FILE_NAME="targetHighScores";
    private TargetGameWord currentTargetGameWord;
    private int score;
    private List<String> foundWords;
    private final Controller controller;
    private boolean newGame=true;
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
    public  enum GAME_LEVEL {QUICK, AVERAGE, LONG, MARATHON}
    private GAME_LEVEL game_level;



    public TargetGame(Controller controller,GAME_LEVEL level){
        this.controller=controller;
        currentTargetGameWord =new TargetGameWord(controller.getLexicon(),level);
        score=0;
        foundWords=new ArrayList<>();
        game_level=level;

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
         if(currentTargetGameWord.getAnswers().contains(attempt.toLowerCase())){
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
        }
    }

    /**
     *
     * @return  {@link TARGET_STATUS} depending on progress
     *          NONE if no target met etc.
     */
    public TARGET_STATUS target_status(){
        if(score== currentTargetGameWord.getTargets()[0]){
            return TARGET_STATUS.GOOD;
        }

        if(score== currentTargetGameWord.getTargets()[1]){
            return TARGET_STATUS.GREAT;
        }

        if(score== currentTargetGameWord.getTargets()[2]){
            return TARGET_STATUS.PERFECT;
        }

        return TARGET_STATUS.NONE;
    }

    public boolean newHighScore(String time) {
        if(!newGame)return false;
        List<Integer> highScores=controller.getHighScores(SCORE_FILE_NAME);
        int timeInt= Time.timeInSeconds(time);
        int size=highScores.size();
        if(size<5){
            Log.d("Score small",timeInt+"");
            highScores.add(timeInt);
            controller.saveHighScores(SCORE_FILE_NAME,highScores);
            return true;
        }
        int lowestScore=highScores.get(0);
        if(timeInt>lowestScore)return false;
        Collections.reverse(highScores);
        for(int i=0;i<size;i++){
            if(timeInt<highScores.get(i)){
                highScores.add(i,timeInt);
                break;
            }
        }

        controller.saveHighScores(SCORE_FILE_NAME,highScores.subList(0,5));
        return true;
    }

    public boolean isNewGame() {
        return newGame;
    }

    public int getGoodTarget(){
        return currentTargetGameWord.getTargets()[0];
    }

    public int getGreatTarget(){
        return currentTargetGameWord.getTargets()[1];
    }

    public int getPerfectTarget(){
        return currentTargetGameWord.getTargets()[2];
    }

    public List<String> getFoundWords(){
        return foundWords;
    }

    public int getScore(){
        return score;
    }

    private String getGameWord(){
        return currentTargetGameWord.toString();
    }

    private String getJumbledGameWord(){
        return currentTargetGameWord.getGameAnagram();
    }

    /**
     * Method to reset game with new values
     */
    public void resetGame(){
        currentTargetGameWord =new TargetGameWord(controller.getLexicon(),game_level);
        score=0;
        foundWords.clear();
        nineLetterWords=0;
        newGame=true;

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
        currentTargetGameWord =new TargetGameWord(data[2],data[1],controller.getLexicon());
        game_level=GAME_LEVEL.valueOf(data[4]);
        foundWords=new ArrayList<>();
        foundWords.addAll(Arrays.asList(Arrays.copyOfRange(data,5,data.length)));
        score=foundWords.size();

    }

    public char[] getGameLetters(){
        return currentTargetGameWord.getGameLetters();
    }

    public int targetWordLength(){
        return currentTargetGameWord.toString().length();
    }

    public  boolean allSolutionsFound(){
        return score== currentTargetGameWord.getAnswers().size();
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

    public List<String> solutions(){
        return currentTargetGameWord.getAnswers();
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
