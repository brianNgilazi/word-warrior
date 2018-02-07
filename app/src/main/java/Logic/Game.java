package Logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 2018/02/06.
 * Class to represent a Target Word Game
 */

public class Game {
    private Word currentGameWord;
    private int score;
    private List<String> foundWords;
    private Controller controller;

    /**
     * enum representing progress towards targtes
     */
    public enum TARGET_STATUS {GOOD, GREAT, PERFECT,NONE }


    public Game(Controller controller){
        this.controller=controller;
        currentGameWord=controller.getWord();
        score=0;
        foundWords=new ArrayList<>();
    }

    /**
     *  Constructor for starting a saved game
     * @param controller the controller
     * @param actualWord the actual game word
     * @param anagram the scrambled word
     */
    public Game(Controller controller, String actualWord,String anagram){
        this.controller=controller;
        currentGameWord=controller.getWord(actualWord,anagram);
        score=0;
        foundWords=new ArrayList<>();
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

    public String getGameWord(){
        return currentGameWord.toString();
    }

    public String getJumbledGameWord(){
        return currentGameWord.getJumbledWord();
    }



    /**
     * Method to reset game with new values
     */
    public void resetGame(){
        currentGameWord=controller.getWord();
        score=0;
        foundWords.clear();
    }

    public char[] getGameLetters(){
        return currentGameWord.shuffle();
    }

    public int targetWordLength(){
        return currentGameWord.toString().length();
    }

    public  boolean allSolutionsFound(){
        return score==currentGameWord.getAnswers().size();
    }


}
