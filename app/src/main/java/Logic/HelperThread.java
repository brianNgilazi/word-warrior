package Logic;



import java.util.List;

/**
 * Created by Brian on 18-Jan-17.
 * Class to help with computation
 */


public class HelperThread extends Thread {
    private String word;
    private Adder list;
    private List<String> allWords;
    private int start;
    private int end;
    private int wordSize;
    private char center;



    public enum HelpType {FIND_TARGET_ANSWERS, CROSSWORD_SOLUTION, N_LETTER_WORDS, ANAGRAMS}

    private HelpType helpType;

    private HelperThread(List<String> aList, List<String> all, int s, int e, HelpType type) {
        list = new Adder(aList);
        allWords = all;
        start = s;
        end = e;
        helpType = type;



    }

    public HelperThread(String targetWord, char center, List<String> aList, List<String> all, HelpType help){

        this(aList,all,0,all.size(),help);
        word=targetWord;
        this.center=center;

    }

    public HelperThread(String crossword,List<String> aList,List<String> all,HelpType help){
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
                        if (w.length() > 3 && (w.contains(String.valueOf(center))||center==' ') && Word.checkCharacter(w,word))
                            list.add(w);
                    }
                    return;

                case CROSSWORD_SOLUTION:
                    for (int i = start; i < end; i++) {
                        String w = allWords.get(i);
                        if (Word.patternMatch(word,w)) list.add(w);
                    }
                    return;

                case N_LETTER_WORDS:
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
            case N_LETTER_WORDS:
                left.wordSize=right.wordSize=wordSize;
                break;
        }

        left.start();
        right.run();
        try {
            left.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    private class Adder {

        List<String> array;

        Adder(List<String> arrayList) {
            array = arrayList;
        }

        synchronized void add(String s) {
            array.add(s);
        }


    }
}
