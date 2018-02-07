package Logic;



import java.util.ArrayList;
import java.util.Random;



public class Word {
    private ArrayList<String> answers;
    private String word,jumbledWord;
    private Dictionary dictionary;
    private char center;
    private int[] targets;


    public Word(Dictionary dict){
        dictionary=dict;
        word=dictionary.gameWord();
        jumbledWord=null;
        answers=new ArrayList<>();

        populateList();
        setTargets();

    }

    /**
     * Constructor to get a word from a saved game
     * @param actualWord- the n letter word
     * @param anagram- an anagram  of the b letter word
     * @param dict- the dictionary
     *
     */
    Word(String actualWord, String anagram, Dictionary dict){
        center=anagram.charAt(4);
        word=actualWord;
        answers=new ArrayList<>();
        dictionary=dict;
        populateList();
        setTargets();
    }

    private void populateList(){
        //choose new center letter if not chosen already
        if((int)center==0) center = word.charAt(new Random().nextInt(word.length()));

        HelperThread helperThread=new HelperThread(word,center,answers,dictionary.allWords(),HelperThread.HelpType.FIND_TARGET_ANSWERS);
        helperThread.setPriority(Thread.MIN_PRIORITY);
        helperThread.start();
        try {
            helperThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    ArrayList<String> getAnswers(){return answers;}

    public char getCenter() {return center;}

    int[] getTargets() {
        return targets;
    }

    static boolean checkCharacter(String potential,String word){
        for(char c:potential.toCharArray()) {
            int potentialCounter = 0;
            for (char s : potential.toCharArray()) {
                if (s == c) {
                    potentialCounter++;
                }
            }
            int counter2 = 0;
            for (char s : word.toCharArray()) {
                if (s == c) {
                    counter2++;                }
            }

            if(potentialCounter>counter2)return false;
        }
        return true;
    }

    static boolean patternMatch(String pattern,String potential){
        if(potential.length()!=pattern.length())return  false;
        char[] patternArray=pattern.toCharArray();
        char[] potentialArray=potential.toCharArray();
        for(int i=0;i<patternArray.length;i++){
            if(patternArray[i]=='.')continue;
            if(patternArray[i]!=potentialArray[i])return false;
        }
        return true;
    }

    char[] shuffle(){
        if(jumbledWord!=null){return jumbledWord.toCharArray();}
        char[] original=word.toCharArray();
        char[] shuffled=new char[original.length];
        boolean centerPlaced=false;
        ArrayList<Integer> visited=new ArrayList<>();
        Random r=new Random();
        for (char anOriginal : original) {
            int n = r.nextInt(original.length);
            while (visited.contains(n) || (n == 4 && !centerPlaced))
                n = r.nextInt(original.length);//find unvisited
            if (anOriginal == center && !visited.contains(4)) {//if center found and unallocated allocate
                n = 4;
                centerPlaced = true;
            }

            shuffled[n] = anOriginal;
            visited.add(n);


        }
        jumbledWord=new String(shuffled);
        return shuffled;

    }

    private void setTargets(){

        targets=new int[3];
        targets[0]= (int) Math.round((0.33)*answers.size());
        targets[1]=(int) Math.round((0.67)*answers.size());
        targets[2]=answers.size();

    }

    String getJumbledWord() {
        return jumbledWord;
    }

    public String toString(){
        return word;
    }



}
