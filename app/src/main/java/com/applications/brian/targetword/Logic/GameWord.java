package com.applications.brian.targetword.Logic;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;



public class GameWord {
    private ArrayList<String> answers;
    private String word, gameAnagram;
    private GameDictionary gameDictionary;
    private char center;
    private int[] targets;



    public GameWord(GameDictionary dict){
        gameDictionary =dict;
        word= gameDictionary.gameWord(TargetGame.GAME_LEVEL.RANDOM);
        populateList();
        setTargets();

    }

    /**
     * Constructor to get a word from a saved game
     * @param actualWord- the n letter word
     * @param anagram- an anagram  of the b letter word
     * @param dict- the gameDictionary
     *
     */
    GameWord(String actualWord, String anagram, GameDictionary dict){
        center=anagram.charAt(4);
        gameAnagram=anagram;
        word=actualWord;
        answers=new ArrayList<>();
        gameDictionary =dict;
        populateList();
        setTargets();
    }


    GameWord(GameDictionary dictionary, TargetGame.GAME_LEVEL game_level){

        String[] dataArray=dictionary.gameWord(game_level).split(";");
        word=dataArray[0];
        center=dataArray[1].charAt(0);
        gameDictionary=dictionary;
        populateList();
        setTargets();
    }


    private void populateList(String data){
        String[] answerArray = new StringBuilder(data).deleteCharAt(0).deleteCharAt(data.length()-2).toString().split(", ");
        answers=new ArrayList<>();
        answers.addAll(Arrays.asList(answerArray));
    }

    private void populateList(){
        answers=new ArrayList<>();
        //choose new center letter if not chosen already
        if((int)center==0) center = word.charAt(new Random().nextInt(word.length()));
        for (String word:gameDictionary.allWords()) {
            if (word.length() > 3
                    && (word.contains(String.valueOf(center)))
                    && GameWord.isAnagram(word,this.word))
                answers.add(word);
        }
    }


    ArrayList<String> getAnswers(){return answers;}

    public char getCenter() {return center;}

    int[] getTargets() {
        return targets;
    }

    /**
     * Checks if a word is an anagram of another word
     * @param potential  the potential anagram
     * @param word the actual word
     * @return true if words are anagrams
     */
    public static boolean isAnagram(String potential, String word){

        for(char character:potential.toCharArray()) {
            if(GameWord.instances0f(character,potential)>GameWord.instances0f(character,word))return false;
        }
        return true;
    }

    private static int instances0f(char character,String aWord){
        int counter = 0;
        for (char c : aWord.toCharArray()) {
            if (character == c) {
                counter++;
            }
        }
        return counter;
    }

    /*
     * @param pattern the pattern of the query word
     * @param potential the word that the pattern is being compared to
     * @return true if the potential word matches the pattern
     */
    /*public static boolean patternMatch(String pattern,String potential){
        if(potential.length()!=pattern.length())return  false;
        char[] patternArray=pattern.toCharArray();
        char[] potentialArray=potential.toCharArray();
        for(int i=0;i<patternArray.length;i++){
            if(patternArray[i]=='.')continue;
            if(patternArray[i]!=potentialArray[i])return false;
        }
        return true;
    }*/

    /**
     * method to return the letters to be used in a game as character array
     * @return char[] of letters in a random order
     */
    char[] getGameLetters(){
        if(gameAnagram !=null){return gameAnagram.toCharArray();}
        char[] original=word.toCharArray();
        char[] shuffled=new char[original.length];
        boolean centerPlaced=false;
        ArrayList<Integer> visited=new ArrayList<>();
        Random r=new Random();
        for (char anOriginal : original) {
            int n = r.nextInt(original.length);
            while (visited.contains(n) || (n == 4 && !centerPlaced))
                n = r.nextInt(original.length);//find unvisited
            if (anOriginal == center && !visited.contains(4)) {//if center found and unallocated allocate
                n = 4;
                centerPlaced = true;
            }

            shuffled[n] = anOriginal;
            visited.add(n);


        }
        gameAnagram =String.valueOf(shuffled);
        return shuffled;
    }

    private void setTargets(){

        targets=new int[3];
        targets[0]= (int) Math.round((0.33)*answers.size());
        targets[1]=(int) Math.round((0.67)*answers.size());
        targets[2]=answers.size();

    }

    String getGameAnagram() {
        return gameAnagram;
    }

    public String toString(){
        return word;
    }



}
