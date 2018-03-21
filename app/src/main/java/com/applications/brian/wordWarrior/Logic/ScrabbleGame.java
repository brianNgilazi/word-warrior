package com.applications.brian.wordWarrior.Logic;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by brian on 2018/02/06.
 * Class to represent a Scrabble TargetGameWord ScrabbleGame
 */

public class ScrabbleGame {
    public static final String SAVE_FILE_NAME="scrabbleSavedGames";
    public static final String SCORE_FILE_NAME="scrabbleHighScores";
    private GameDictionary gameDictionary;
    private ScrabbleLetterCollection collection;
    private int score;
    private List<String> foundWords;
    private Controller controller;
    private boolean newGame;
    private List<ScrabbleLetter> scrabbleLetters;
    private AtomicInteger solutionStatus;

    private static final int SEARCHING=0;
    private static final int FOUND=1;
    private static final int NOT_FOUND=-1;




    /**
     * enum representing status of save
     */
    public  enum SAVING_STATUS {OVERRIDE, NEW_SAVE}


    public ScrabbleGame(Controller controller){
        init(controller);
        score=0;
        foundWords=new ArrayList<>();
        scrabbleLetters=collection.getLetterCollection();

    }

    /**
     *  Constructor for starting a saved game
     * @param controller the controller
     * @param data the data required for loading;
     */
    public ScrabbleGame(Controller controller, String data){
        init(controller);
        load(data);
        newGame=false;
    }

    private void init(Controller controller){
        this.controller=controller;
        gameDictionary=controller.getLexicon();
        collection=new ScrabbleLetterCollection();
        newGame=true;
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
        if(gameDictionary.hasWord(attempt)){
            updateProgress(attempt);
            return true;
        }
        return false;
    }


    private void updateProgress(String word){
        foundWords.add(word);
        score+=collection.calculateScore(word);
    }

    public int getPoints() {
        return score*5;
    }

    public int scoreForWord(String word){
        return collection.calculateScore(word);
    }

    public boolean newHighScore( ) {
        List<Integer> highScores=controller.getHighScores(SCORE_FILE_NAME);
        int size=highScores.size();
        if(size<5){
            highScores.add(score);
            controller.saveHighScores(SCORE_FILE_NAME,highScores);
            return true;
        }
        int lowestScore=highScores.get(4);
        if(score<lowestScore)return false;

        for(int i=0;i<size;i++){
            if(score>highScores.get(i)){
                highScores.add(i,score);
                break;
            }
        }
        controller.saveHighScores(SCORE_FILE_NAME,highScores.subList(0,5));
        return true;

    }

    public boolean isNewGame() {
        return newGame;
    }

    public List<String> getFoundWords(){
        return foundWords;
    }

    public int getScore(){
        return score;
    }

    /**
     * Method to reset game with new values
     */
    public void resetGame(){
        score=0;
        foundWords.clear();
        scrabbleLetters= collection.getLetterCollection();
        newGame=true;
    }

    public void save(SAVING_STATUS saving_status){
        List<SavedGame> savedGames =controller.savedGamesList(SAVE_FILE_NAME);
        SavedGame latestSavedGame =new ScrabbleSavedGame(getSaveGameData());
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

        controller.saveGame(SAVE_FILE_NAME,savedGames);
    }

    private String getSaveGameData(){
        StringBuilder builder=new StringBuilder();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE-dd-MMM-yyyy'|'HH:mm", Locale.getDefault());
        builder.append(simpleDateFormat.format(new Date(System.currentTimeMillis()))).append(" ");//0

        for(ScrabbleLetter letter:scrabbleLetters){//1
            builder.append(letter.getLetter());
        }
        builder.append(" ");

        for(ScrabbleLetter letter:scrabbleLetters){//1
            builder.append(letter.isUsed()).append(";");
        }

        builder.deleteCharAt(builder.length()-1).append(" ");//2

        builder.append(score).append(" ");//3

        for(String w:foundWords){
            builder.append(w).append(" ");//4+
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    private void load(String line){

        ScrabbleSavedGame savedGame=new ScrabbleSavedGame(line);
        boolean[] selected=savedGame.getSelectionArray();
        char[] letters=savedGame.gameLetters.toCharArray();
        scrabbleLetters=new ArrayList<>();
        for(int i=0;i<letters.length;i++){
            ScrabbleLetter scrabbleLetter=new ScrabbleLetter(letters[i],collection.calculateScore(String.valueOf(letters[i])));
            scrabbleLetter.setUsed(selected[i]);
            scrabbleLetters.add(scrabbleLetter);
        }

        foundWords=new ArrayList<>();
        foundWords.addAll(Arrays.asList(savedGame.getFoundWords()));
        score=savedGame.getScore();

    }

    public List<ScrabbleLetter> getGameLetters(){
        return scrabbleLetters;
    }

    public  static boolean isAdjacent(int current, int previous)
    {//check for adjacency of tile to last clicked tile
        int gridLength=6;
        int h=0;

        return(
                (current==previous+5+h && (previous-1)%gridLength!=0) ||
                        (current==previous-5-h && previous%gridLength!=0) ||
                        current==previous+7+h && previous%gridLength!=0||
                        current==previous-7-h && (previous-1)%gridLength!=0 ||
                        (current==previous+1 && previous%gridLength!=0 ) ||
                        (current==previous-1 && (previous-1)%gridLength!=0) ||
                        current==previous-gridLength ||
                        current==previous+gridLength
        );
    }

    private List<Integer> getAdjacent(int position, List<Integer> visited){
        ArrayList<Integer> adjacent=new ArrayList<>();

        for(int i=0;i<scrabbleLetters.size();i++){
            if(isAdjacent(position+1,i+1) && !scrabbleLetters.get(i).isUsed())
            {
                if(!visited.contains(i)) adjacent.add(i);
            }
        }

        return adjacent;
    }

    public boolean gameOver(){
        checkForWords();
        //noinspection StatementWithEmptyBody
        while(solutionStatus.get()==ScrabbleGame.SEARCHING){

        }

        return solutionStatus.get() != FOUND;
    }

    private void checkForWords() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (solutionStatus == null) solutionStatus= new AtomicInteger();
            solutionStatus.set(SEARCHING);
            ArrayList<RecursiveAction> solutionThreads = new ArrayList<>();
            for (int i = 0; i < scrabbleLetters.size(); i++) {
                ScrabbleLetter letter = scrabbleLetters.get(i);
                if (!letter.isUsed()) {
                    List<Integer> visited=new ArrayList<>();
                    visited.add(i);
                    RecursiveAction solutionFinder=new SolutionFinder(letter.toString(), getAdjacent(i, visited),visited);
                    solutionThreads.add(solutionFinder);
                    solutionFinder.fork();
                }
            }

            for(RecursiveAction action:solutionThreads){
                action.join();
            }

        }
        if(solutionStatus.get()==SEARCHING)solutionStatus.set(NOT_FOUND);
    }

    public List<String> longestWord(){
        List<String> found=new ArrayList<>();
        found= Collections.synchronizedList(found);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ArrayList<RecursiveAction> solutionThreads = new ArrayList<>();
            for (int i = 0; i < scrabbleLetters.size(); i++) {
                ScrabbleLetter letter = scrabbleLetters.get(i);
                if (!letter.isUsed()) {
                    List<Integer> visited=new ArrayList<>();
                    visited.add(i);
                    RecursiveAction solutionFinder=new SolutionListThread(letter.toString(), getAdjacent(i, visited),visited,found);
                    solutionThreads.add(solutionFinder);
                    solutionFinder.fork();
                }
            }

            for(RecursiveAction action:solutionThreads){
                action.join();
            }

        }
        return found;
    }

    //Helper Classes
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class SolutionFinder extends RecursiveAction{

        final List<Integer> adjacent;
        final String wordInProgress;
        final List<Integer> visited;


        SolutionFinder(String currentWord,List<Integer> adjacentPositions,List<Integer> visited){
            wordInProgress=currentWord;
            adjacent=adjacentPositions;
            this.visited=visited;
        }

        @Override
        public void compute() {
            //return if a solution has been found
            if(solutionStatus.get()==FOUND)return;

            if(!gameDictionary.couldBeWord(wordInProgress))return;

            for(int i:adjacent){
                String newWord=wordInProgress+scrabbleLetters.get(i).toString();
                if(newWord.length()>2 && gameDictionary.hasWord(newWord.toLowerCase())){
                    Log.i("Found",newWord);
                    solutionStatus.set(FOUND);
                    return;
                }
                List<Integer> integers=new ArrayList<>(visited);
                integers.add(i);
                new SolutionFinder(newWord,getAdjacent(i,visited),integers).fork();
            }
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class SolutionListThread extends RecursiveAction{

        final List<Integer> adjacent;
        final String wordInProgress;
        final List<Integer> visited;
        final List<String> found;


        SolutionListThread(String currentWord,List<Integer> adjacentPositions,List<Integer> visited,List<String> found){
            wordInProgress=currentWord;
            adjacent=adjacentPositions;
            this.visited=visited;
            this.found=found;
        }

        @Override
        public void compute() {
            if(!gameDictionary.couldBeWord(wordInProgress) ||found.size()>=1)return;
            for(int i:adjacent){
                String newWord=wordInProgress+scrabbleLetters.get(i).toString();
                if(newWord.length()>2 && gameDictionary.hasWord(newWord) &&!found.contains(newWord)){
                    Log.i("Found",newWord);
                    found.add(newWord);
                }
                List<Integer> integers=new ArrayList<>(visited);
                integers.add(i);
                new SolutionListThread(newWord,getAdjacent(i,visited),integers,found).fork();
            }
        }



    }
    
    private class ScrabbleLetterCollection {

        private int totalTiles() {
            int total = 0;
            for (int aFREQUENCY : FREQUENCY) {

                total = total + aFREQUENCY;
            }
            return total;
        }

        private final char[] LETTER = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H','I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T','U', 'V', 'W', 'X', 'Y','Z'};
        private final String LETTER_STRING=String.valueOf(LETTER);
        private final int[] FREQUENCY = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        private final int[] VALUE = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

        private ScrabbleLetter[] makeTileArray() {

            ScrabbleLetter[] tiles = new ScrabbleLetter[totalTiles()];
            int instanceIndex = 0;

            for (int tileNumber=0; tileNumber<LETTER.length; tileNumber++) {
                char tileLetter = LETTER[tileNumber];
                int tileValue = VALUE[tileNumber];

                for (int instances=0; instances<FREQUENCY[tileNumber]; instances++) {
                    tiles[instanceIndex] =new ScrabbleLetter(tileLetter,tileValue);
                    instanceIndex++;
                }
            }
            return tiles;
        }


        private void shuffleTiles(ScrabbleLetter[] tileArray) {

            Random random = new Random();
            int numberOfRuns = tileArray.length;

            while (numberOfRuns>0) {

                numberOfRuns--;

                for (int index=0; index<tileArray.length-1; index++) {

                    int destination = random.nextInt(tileArray.length);

                    ScrabbleLetter tile = tileArray[destination];
                    tile.setUsed(false);
                    tileArray[destination] = tileArray[index];
                    tileArray[index]=tile;
                }
            }
        }

        /* **************** Instance variables and methods *************** */
        final ScrabbleLetter[] collection;

        /**
         * Create a new collection of scrabble tiles.
         */
        private ScrabbleLetterCollection() {

            collection= makeTileArray();
        }



        private List<ScrabbleLetter> getLetterCollection(){
            List<ScrabbleLetter> grid=new ArrayList<>(36);
            shuffleTiles(collection);
            grid.addAll(Arrays.asList(collection).subList(0, 36));
            return grid;
        }

        private int calculateScore(String word){
            int score=0;
            for(char c:word.toCharArray()){
                score+=VALUE[LETTER_STRING.indexOf(c)];
            }
            return score;
        }
    }

    /**
     * Created by brian on 2018/02/09.
     * Class to handle saved games in the form of strings
     */
    public static class ScrabbleSavedGame implements SavedGame {

        private int score;
        private int total;
        private String rawData;
        private String name;
        private String gameLetters;
        private String selection;
        private String[] foundWords;


        public ScrabbleSavedGame(String data){
            rawData=data;
            String[] dataArray=data.split(" ");
            name=dataArray[0];
            gameLetters =dataArray[1];
            selection =dataArray[2];
            total=Integer.parseInt(dataArray[3]);

            foundWords= Arrays.copyOfRange(dataArray,4,dataArray.length);
        }

        public int getScore() {
            return total;
        }


        public String getName() {
            return name;
        }

        private String[] getFoundWords() {
            return foundWords;
        }

        public String getRow(int rowNumber){
            return gameLetters.substring((rowNumber-1)*6,6*rowNumber);

        }

        @Override
        public String toString() {
            return rawData;
        }

        private boolean[] getSelectionArray(){
            String[] data=selection.split(";");
            boolean[] selectionArray=new boolean[data.length];
            for(int i=0;i<selectionArray.length;i++){
                selectionArray[i]=Boolean.parseBoolean(data[i]);
            }
            return selectionArray;
        }


        @Override
        public int compareTo(@NonNull SavedGame another) {
            if(another instanceof ScrabbleSavedGame){
                return gameLetters.compareTo(((ScrabbleSavedGame)another).gameLetters);
            }
            return 0;
        }

        public void set(SavedGame another) {
            ScrabbleSavedGame other=(ScrabbleSavedGame)another;
            rawData=other.rawData;
            name=other.name;
            gameLetters =other.gameLetters;
            selection=other.selection;
            total=other.total;
            score=other.score;
            foundWords= other.foundWords;
        }
    }





}
