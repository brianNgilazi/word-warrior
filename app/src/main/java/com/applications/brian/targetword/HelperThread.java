package com.applications.brian.targetword;



import java.util.ArrayList;

/**
 * Created by Brian on 18-Jan-17.
 */




public class HelperThread extends Thread {
    private String word;
    private Adder list;
    private ArrayList<String> allWords;
    private int start;
    private int end;
    private int wordSize;
    private char center;



    public enum HelpType {FIND_TARGET_ANSWERS, CROSSWORD_SOLUTION, NLETTERWORDS, ANAGRAMS}

    private HelpType helpType;

    private HelperThread(ArrayList<String> aList, ArrayList<String> all, int s, int e, HelpType type) {
        list = new Adder(aList);
        allWords = all;
        start = s;
        end = e;
        helpType = type;



    }

    public HelperThread(String targetWord,char center,ArrayList<String> aList,ArrayList<String> all,HelpType help){

        this(aList,all,0,all.size(),help);
        word=targetWord;
        this.center=center;

    }

    public  HelperThread(String crossword,ArrayList<String> aList,ArrayList<String> all,HelpType help){
        this(aList,all,0,all.size(),help);
        word=crossword;
    }


    @Override
    public void run() {
        if (end - start < 10000) {
            switch (helpType) {
                case ANAGRAMS:

                case FIND_TARGET_ANSWERS:
                    for (int i = start; i < end; i++) {
                        String w = allWords.get(i);
                        if (w.length() > 3 && (w.contains(center + "")||center==' ') && Word.checkCharacter(w,word))
                            list.add(w);
                    }
                    return;

                case CROSSWORD_SOLUTION:
                    for (int i = start; i < end; i++) {
                        String w = allWords.get(i);
                        if (Word.patternMatch(word,w)) list.add(w);
                    }
                    return;

                case NLETTERWORDS:
                    for (int i = start; i < end; i++) {
                        String w = allWords.get(i);
                        if (w.length() > wordSize) list.add(w);
                    }
                    return;


            }
        }

        HelperThread left = new HelperThread(list.array, allWords, start, (end + start) / 2,helpType);
        HelperThread right = new HelperThread(list.array, allWords, (end + start) / 2, end,helpType);
        switch (helpType) {
            case ANAGRAMS:
            case FIND_TARGET_ANSWERS:
                left.center=right.center=center;
                left.word=right.word=word;
                break;
            case CROSSWORD_SOLUTION:
                left.word=right.word=word;
                break;
            case NLETTERWORDS:
                left.wordSize=right.wordSize=wordSize;
                break;
        }

        left.start();
        right.run();
       
    }



    private class Adder {

        ArrayList<String> array;

        public Adder(ArrayList<String> arrayList) {
            array = arrayList;
        }

        public synchronized void add(String s) {
            array.add(s);
        }
       // public synchronized boolean contains(String word){return  array.contains(word);}

    }
}
