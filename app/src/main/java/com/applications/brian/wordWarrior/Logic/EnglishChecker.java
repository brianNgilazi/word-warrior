package com.applications.brian.wordWarrior.Logic;

/**
 * Created by brian on 2018/02/22.
 */

class EnglishChecker {

    private static final String vowels = "aeiouy";


    static boolean couldBeEnglishWord(String word) {
        word = word.toLowerCase();
        if (word.length() < 6) return true;
        if (word.length() > 21) return false;
        int chainVowelCount = 0;
        int chainConsonantCount = 0;
        for (char c : word.toCharArray()) {
            if (vowels.contains(String.valueOf(c))) {
                chainVowelCount++;
                chainConsonantCount = 0;
                if (chainVowelCount == 6) return false;
            } else {
                chainConsonantCount++;
                chainVowelCount = 0;
                if (chainConsonantCount == 6) return false;
            }

        }
        return true;
    }
}
