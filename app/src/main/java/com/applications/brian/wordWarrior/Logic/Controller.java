package com.applications.brian.wordWarrior.Logic;

import android.content.Context;
import android.content.SharedPreferences;

import com.applications.brian.wordWarrior.Presentation.MainActivity;
import com.applications.brian.wordWarrior.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by brian on 2018/02/05.
 * Controller class to serve as an API between UI layer and logic layer
 */

public class Controller {
    private final GameDictionary gameDictionary;
    private final Context context;
    private SharedPreferences preferences;
    private Profile profile;


    public Controller(Context context) {
        InputStream quick = context.getResources().openRawResource(R.raw.target_small);
        InputStream avg = context.getResources().openRawResource(R.raw.target_medium);
        InputStream large = context.getResources().openRawResource(R.raw.target_large);
        InputStream extra_large = context.getResources().openRawResource(R.raw.target_extra_large);
        gameDictionary = new GameDictionary(context.getResources().openRawResource(R.raw.englishwords), new InputStream[]{quick, avg, large, extra_large});
        this.context = context;
        openProfile();
    }

    //Profile
    private void openProfile() {
        preferences = ((MainActivity) context).getPreferences(Context.MODE_PRIVATE);
        profile = new Profile(preferences.getString(Profile.PREFERENCE_KEY, "0 0 0"));

    }

    public void updatePoints(int points) {
        profile.incrementPoints(points);
    }

    public void spendPoints(int points) {
        updatePoints(points * -1);
    }

    public void updateGamesPlayed() {
        profile.incrementGamesPlayed();
    }

    public void updateGamesWon() {
        profile.incrementGamesWon();
    }

    public void saveProfile() {
        preferences.edit().putString(Profile.PREFERENCE_KEY, profile.toString()).apply();
    }

    public int getProfilePoints() {
        return profile.getPoints();
    }

    public int getPlayedGames() {
        return profile.getGamesPlayed();
    }

    public double getWinRate() {
        return profile.getWinPercentage();
    }

    public void resetProfile() {
        profile.reset();
        saveProfile();
        context.deleteFile(TargetGame.SAVE_FILE_NAME);
        for (String fileName : context.fileList()) {
            context.deleteFile(fileName);
        }
    }

    //Accessing Dictionary and Data

    /**
     * @return a List containing all the words in the 'dictionary'
     */
    public GameDictionary getLexicon() {
        return gameDictionary;
    }


    //Save and Delete Methods
    public List<String> savedGamesData(String fileName) {
        List<String> data = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(fileName);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                data.add(scanner.nextLine());
            }
            scanner.close();
            inputStream.close();
        } catch (IOException e) {
            //e.printStackTrace();

        }

        return data;
    }

    List<SavedGame> savedGamesList(String fileName) {

        List<SavedGame> savedGames = new ArrayList<>();
        try {

            InputStream inputStream = context.openFileInput(fileName);
            Scanner scanner = new Scanner(inputStream);
            switch (fileName) {
                case TargetGame.SAVE_FILE_NAME:
                    while (scanner.hasNextLine()) {
                        savedGames.add(new TargetGame.TargetSavedGame(scanner.nextLine()));
                    }
                    break;
                case ScrabbleGame.SAVE_FILE_NAME:
                    while (scanner.hasNextLine()) {
                        savedGames.add(new ScrabbleGame.ScrabbleSavedGame(scanner.nextLine()));
                    }
                    break;
            }

            scanner.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return savedGames;
    }

    void saveGame(String fileName, List<SavedGame> savedGames) {
        try {
            PrintStream printStream = new PrintStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            for (SavedGame savedGame : savedGames) {
                printStream.println(savedGame);
            }
            printStream.close();
        } catch (IOException e) {
            //e.printStackTrace();


        }
    }

    public void deleteSavedGame(String name, String fileName) {
        List<SavedGame> savedGames = savedGamesList(fileName);
        for (int i = 0; i < savedGames.size(); i++) {
            if (savedGames.get(i).getName().equals(name)) {
                savedGames.remove(i);
            }
        }
        saveGame(fileName, savedGames);
    }

    //High Scores
    public List<Integer> getHighScores(String fileName) {
        List<Integer> scores = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(fileName);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                scores.add(Integer.parseInt(scanner.nextLine()));
            }
            scanner.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        Collections.sort(scores);
        Collections.reverse(scores);
        return scores;
    }

    void saveHighScores(String fileName, List<Integer> highScores) {
        Collections.sort(highScores);
        Collections.reverse(highScores);
        try {
            PrintStream printStream = new PrintStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            for (Integer score : highScores) {
                printStream.println(score);
            }
            printStream.close();
        } catch (IOException e) {
            //e.printStackTrace();

        }
    }

    public List<String> wordsList(){
        return getLexicon().getWordList();
    }


}
