package com.applications.brian.wordWarrior.Logic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;



class GameDictionary {
    private static final Random RANDOM_GENERATOR = new Random();
    private final List<String> wordList;
    private final HashMap<Character,List<String>> indexedWords;
    private final List<List<String>> gameLevelsList;
    private final InputStream[] gameLevelInputStreams;

    GameDictionary(InputStream inputStream, InputStream[] gameLevelInputStreams){
        wordList=new ArrayList<>();
        indexedWords =new HashMap<>();
        populateAllWordsList(inputStream);
        this.gameLevelInputStreams=gameLevelInputStreams;
        gameLevelsList=new ArrayList<>(gameLevelInputStreams.length);
        for (InputStream ignored : gameLevelInputStreams) {
            gameLevelsList.add(null);
        }
    }



    String gameWord(TargetGame.GAME_LEVEL game_level){
        List<String> list;
        Random random=new Random();
        int i;
        switch (game_level){
            case QUICK:
                i=0;
                break;
            case AVERAGE:
                i=1;
                break;
            case LONG:
                i=2;
                break;
            case MARATHON:
                i=3;
                break;
            default:
                i=random.nextInt(gameLevelsList.size());
        }
        list=gameLevelsList.get(i);
        if(list==null){
            populateIndividualList(i);
            list=gameLevelsList.get(i);
        }
        return list.get(random.nextInt(list.size()));
    }

    String getRandomWord(){
       return wordList.get(RANDOM_GENERATOR.nextInt(wordList.size())).toUpperCase();
    }

    /**
     *
     * @return List of all words
     */
    List<String> allWords(){
        return wordList;
    }

    boolean hasWord(String word){
        word=word.toLowerCase();
        char letter=word.charAt(0);
        List<String> list=indexedWords.get(letter);
        for(String s:list){
            if(s.equalsIgnoreCase(word))return true;
        }
        return false;
    }

    boolean couldBeWord(String wordInProgress){
        String word=wordInProgress.toLowerCase();
        if(!EnglishChecker.couldBeEnglishWord(word))return false;
        char letter=word.charAt(0);
        List<String> list=indexedWords.get(letter);
        for(String s:list){
            if(s.startsWith(word))return true;
        }

        return false;
    }


    /**
     * Method to populate the required lists
     *
     * @param input an InputStream connected to a text file containing the list of all words
     */
    private void populateAllWordsList(InputStream input){
        if (input == null) throw new AssertionError();
        Scanner scanner=new Scanner(input);

        //if an n letter word is required make a list of n letter words.
        //TODO: Consider creating text file of different word sizes to improve efficiency
        while (scanner.hasNextLine()) {
            String word = scanner.nextLine();
            word=word.toLowerCase();
            wordList.add(word);
            char firstLetter=word.charAt(0);
            if(indexedWords.keySet().contains(firstLetter)){
                indexedWords.get(firstLetter).add(word);
            }
            else{
                List<String> list=new ArrayList<>();
                list.add(word);
                indexedWords.put(firstLetter,list);
            }
        }
        scanner.close();

    }

    private void populateIndividualList(int listNumber){
        Scanner scanner=new Scanner(gameLevelInputStreams[listNumber]);
        gameLevelsList.set(listNumber,new ArrayList<String>());
        while (scanner.hasNextLine()){
            gameLevelsList.get(listNumber).add(scanner.nextLine());
        }
        scanner.close();
    }



}
