package Logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Dictionary {
    private List<String> wordList;
    private List<String> nLetterWords;
    private int wordSize;

    Dictionary(InputStream inputStream, int gameWordSize){
        wordList=new ArrayList<>();
        nLetterWords =new ArrayList<>();
        wordSize=gameWordSize;
        populateList(inputStream);
    }


    /**
     *
     * @return random n letter word to use in game
     */
    String gameWord(){
        return nLetterWords.get(new Random().nextInt(nLetterWords.size()));
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
    private void populateList(InputStream input){

        if (input == null) throw new AssertionError();
        Scanner scanner=new Scanner(input);

        //if an n letter word is required make a list of n letter words.
        //TODO: Consider creating text file of different word sizes to improve efficiency
        if(wordSize!=0) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                wordList.add(word);
                if (word.length() == wordSize) nLetterWords.add(word);
            }
        }
        else{
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                wordList.add(word);
            }
        }

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
