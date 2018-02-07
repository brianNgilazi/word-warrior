package Logic;

import java.io.InputStream;
import java.util.List;

/**
 * Created by brian on 2018/02/05.
 * Controller class to serve as an API between UI layer and logic layer
 */

public class Controller {
    private Dictionary dictionary;


    public Controller(InputStream inputStream){
        dictionary=new Dictionary(inputStream,9);
    }

    public List<String> getAllWords(){
        return dictionary.allWords();
    }

    public Word getWord(String actualWord,String anagram){
        return new Word(actualWord,anagram,dictionary);
    }

    public Word getWord(){
        return new Word(dictionary);
    }
}
