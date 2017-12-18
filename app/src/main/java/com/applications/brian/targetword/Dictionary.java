package com.applications.brian.targetword;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


class Dictionary {
    private InputStream input=null;
    private ArrayList<String> wordList;
    private ArrayList<String> nLetterWords;
    private int wordSize;

    public Dictionary(InputStream i,int gameWordSize){
        input=i;
        wordList=new ArrayList<>();
        nLetterWords =new ArrayList<>();
        wordSize=gameWordSize;
        populateList();


    }

    /*public Dictionary(InputStream[] streams,int size){
        wordList=new ArrayList<>();
        nLetterWords =new ArrayList<>();
        wordSize=size;

        ThreadHelp[] threads=new ThreadHelp[streams.length];
        for(int i=0;i<streams.length;i++){
            threads[i]=new ThreadHelp(streams[i],new Adder(wordList),new Adder(nLetterWords));
            threads[i].start();
        }
        for (ThreadHelp thread : threads) {
            try {thread.join();} catch (InterruptedException e) {e.printStackTrace();}
        }
    }*/

 /*   public Dictionary(ArrayList<String> list,int gameWordSize){
        wordList=list;
        nLetterWords =new ArrayList<>();
        wordSize=gameWordSize;
        HelperThread help=new HelperThread(nLetterWords,wordList,wordSize, HelperThread.HelpType.NLETTERWORDS);
        help.start();
        try {
            help.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Dictionary(ArrayList<String> list){
        this(list,0);
    }*/



    public String gameWord(){
        return nLetterWords.get(new Random().nextInt(nLetterWords.size()));
    }

    public ArrayList<String> allWords(){
        if(wordList.size()>0) return wordList;
        populateList();
        return wordList;

    }

    private void populateList(){
        Scanner s;
        if (input == null) throw new AssertionError();
        s=new Scanner(input);

        if(wordSize!=0) {
            while (s.hasNextLine()) {
                String word = s.nextLine();
                wordList.add(word);
                if (word.length() == wordSize) nLetterWords.add(word);
            }
        }
        else{
            while (s.hasNextLine()) {
                String word = s.nextLine();
                wordList.add(word);
            }
        }

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


/*
    private class Adder {

        ArrayList<String> array;

        public Adder(ArrayList<String> arrayList) {
            array = arrayList;
        }

        public synchronized void add(String s) {
            array.add(s);
        }


    }

    private class ThreadHelp extends Thread{
        InputStream input;
        Adder main,sub;
        public ThreadHelp(InputStream i,Adder m,Adder s){
            input=i;
            main=m;
            sub=s;
        }
        @Override
        public void run() {
            addWords(input,main,sub);
        }

        private void addWords(InputStream i, Adder main,Adder sub){
            Scanner s;
            assert(i!=null);
            s=new Scanner(input);
            if(wordSize!=0) {
                while (s.hasNextLine()) {
                    String word = s.nextLine();
                    main.add(word);
                    if (word.length() == wordSize) sub.add(word);
                }
            }
            else{
                while (s.hasNextLine()) {
                    String word = s.nextLine();
                    main.add(word);
                }
            }

            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

}
