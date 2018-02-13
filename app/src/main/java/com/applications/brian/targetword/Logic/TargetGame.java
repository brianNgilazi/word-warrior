package com.applications.brian.targetword.Logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by brian on 2018/02/06.
 * Class to represent a Target GameWord TargetGame
 */

public class TargetGame {
    private GameWord currentGameWord;
    private int score;
    private List<String> foundWords;
    private final Controller controller;
    private boolean newGame=true;


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
    public  enum GAME_LEVEL {QUICK, AVERAGE, LONG, EXTRA_LONG, RANDOM}
    private GAME_LEVEL game_level;

    public TargetGame(Controller controller,GAME_LEVEL level){
        this.controller=controller;
        currentGameWord=controller.getWord(level);
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
         if(currentGameWord.getAnswers().contains(attempt.toLowerCase())){
                updateProgress(attempt);
             return true;
         }
         return false;
    }


    private void updateProgress(String word){
        foundWords.add(word);
        score++;
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
    }



    public void save(SAVING_STATUS saving_status){
        List<SavedGame> savedGames=controller.savedGamesList();
        SavedGame latestSavedGame=new SavedGame(getSaveGameData());
        if(saving_status==SAVING_STATUS.NEW_SAVE){
            savedGames.add(latestSavedGame);

        }
        else if(saving_status==SAVING_STATUS.OVERRIDE){
            for (SavedGame savedGame : savedGames) {
                if (savedGame.compareTo(latestSavedGame) == 0) {
                        savedGame.set(latestSavedGame);
                }
            }
        }

        controller.saveGame(savedGames);
    }

    private String getSaveGameData(){
        StringBuilder builder=new StringBuilder();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE-dd/MMM/yy'|'HH:mm", Locale.getDefault());
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

    public static List<String> Levels(){
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add(GAME_LEVEL.QUICK.name());
        arrayList.add(GAME_LEVEL.AVERAGE.name());
        arrayList.add(GAME_LEVEL.LONG.name());
        arrayList.add(GAME_LEVEL.EXTRA_LONG.name());

        return arrayList;
    }




}
