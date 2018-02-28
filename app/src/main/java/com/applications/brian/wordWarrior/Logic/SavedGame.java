package com.applications.brian.wordWarrior.Logic;

/**
 * Created by brian on 2018/02/26.
 */

public interface SavedGame extends Comparable<SavedGame> {

    String toString();

    String getName();

    int getScore();

    void set(SavedGame savedGame);
}

