package com.applications.brian.targetword.Logic;

import android.content.Context;

import com.applications.brian.targetword.Presentation.TargetFragment;
import com.applications.brian.targetword.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by brian on 2018/02/05.
 * Controller class to serve as an API between UI layer and logic layer
 */

public class Controller {
    private final GameDictionary gameDictionary;
    private GameWord word;
    private final Context context;




    public Controller(Context context){
        InputStream quick=context.getResources().openRawResource(R.raw.target_small);
        InputStream avg=context.getResources().openRawResource(R.raw.target_medium);
        InputStream large=context.getResources().openRawResource(R.raw.target_large);
        InputStream extra_large=context.getResources().openRawResource(R.raw.target_extra_large);
        gameDictionary =new GameDictionary(context.getResources().openRawResource(R.raw.englishwords), new InputStream[]{quick,avg,large,extra_large});
        this.context=context;
    }

    public List<String> getAllWords(){
        return gameDictionary.allWords();
    }


    GameWord getWord(String actualWord, String anagram){
        word=new GameWord(actualWord,anagram, gameDictionary);
        return  word;

    }



    GameWord getWord(TargetGame.GAME_LEVEL game_level){
        word= new GameWord(gameDictionary,game_level);
        return word;
    }
    public List<String> getCurrentWordSolutions(){
        return word.getAnswers();
    }

    //Utility Methods
    public List<String> savedGamesData() {
        List<String> data = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(TargetFragment.SAVE_FILE_NAME);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                data.add(scanner.nextLine());
            }
            scanner.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    List<SavedGame> savedGamesList() {

        List<SavedGame> savedGames = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(TargetFragment.SAVE_FILE_NAME);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                savedGames.add(new SavedGame(scanner.nextLine()));
            }
            scanner.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return savedGames;
    }

    void saveGame(List<SavedGame> savedGames){
        try {
            PrintStream printStream = new PrintStream(context.openFileOutput(TargetFragment.SAVE_FILE_NAME, Context.MODE_PRIVATE));
            for (SavedGame savedGame : savedGames) {
                printStream.println(savedGame);
            }
            printStream.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void delete(String name) {
        List<SavedGame> savedGames = savedGamesList();
        for(int i=0;i<savedGames.size();i++){
            if(savedGames.get(i).getName().equals(name)){
                savedGames.remove(i);
            }
        }
        saveGame(savedGames);
    }
}
