package com.applications.brian.wordWarrior.Logic;

/**
 * Created by brian on 2018/02/22.
 *
 */
public class ScrabbleLetter {
    private int value;
    private char letter;
    private boolean used;


    ScrabbleLetter(char letter, int value) {
        this.letter = letter;
        this.value = value;
        used = false;
    }



    public int getValue() {
        return value;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return String.valueOf(letter);
    }
}
