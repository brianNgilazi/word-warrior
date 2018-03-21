package com.applications.brian.wordWarrior.Utilities;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by brian on 2018/03/10.
 */
public class RandomColour {

    private final int colors[] = {Color.parseColor("#0D47A1"), Color.parseColor("#00BCD4"),
            Color.parseColor("#4CAF50"), Color.parseColor("#7B1FA2"),
            Color.parseColor("#B71C1C"), Color.parseColor("#AD1457"),
            Color.parseColor("#009688"),Color.parseColor("#673AB7"),
            Color.parseColor("#21963F"),Color.parseColor("#FF5722"),
            Color.parseColor("#CDDC39"),Color.parseColor("#FFEB3B")};

    private List<Integer> used;
    private int currentColorIndex = -1;

    public RandomColour() {
        used = new ArrayList<>();
        nextColor();
    }

    public int getCurrentColor() {
        return colors[currentColorIndex];
    }

    public void nextColor() {
        if (currentColorIndex >= 0) used.add(currentColorIndex);
        if (used.size() >= colors.length) used.clear();
        Random random = new Random();
        int nextIndex = random.nextInt(colors.length);
        while (used.contains(nextIndex)) nextIndex = random.nextInt(colors.length);
        Log.i("Color", nextIndex + "");
        currentColorIndex = nextIndex;
    }

}
