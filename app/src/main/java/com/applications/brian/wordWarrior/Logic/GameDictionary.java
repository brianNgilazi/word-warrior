package com.applications.brian.wordWarrior.Logic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;



class GameDictionary {
    private final List<String> wordList;
    private final List<List<String>> gameLevelsList;
    private final InputStream[] gameLevelInputStreams;

    GameDictionary(InputStream inputStream,InputStream[] gameLevelInputStreams){
        wordList=new ArrayList<>();
        populateAllWordsList(inputStream);
        this.gameLevelInputStreams=gameLevelInputStreams;
        gameLevelsList=new ArrayList<>(gameLevelInputStreams.length);
        for (InputStream gameLevelInputStream : gameLevelInputStreams) {
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

    /**
     *
     * @return List of all words
     */
    List<String> allWords(){
        return wordList;
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
            wordList.add(word);
        }
        scanner.close();

    }


   /* private  void populateGameLevelsLists(InputStream[] inputStreams){
        for(int i=0;i<inputStreams.length;i++){
            gameLevelsList.add(new ArrayList<String>());
            Scanner scanner=new Scanner(inputStreams[i]);
            while (scanner.hasNextLine()){
                gameLevelsList.get(i).add(scanner.nextLine());
            }
            scanner.close();
        }
    }*/

    private void populateIndividualList(int listNumber){
        Scanner scanner=new Scanner(gameLevelInputStreams[listNumber]);
        gameLevelsList.set(listNumber,new ArrayList<String>());
        while (scanner.hasNextLine()){
            gameLevelsList.get(listNumber).add(scanner.nextLine());
        }
        scanner.close();
    }


}
