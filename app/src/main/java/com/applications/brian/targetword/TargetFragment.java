package com.applications.brian.targetword;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import Logic.Game;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TargetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TargetFragment extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOAD_GAME = "LOAD_GAME_PARAMETER";


    private static final String SAVE_FILE_NAME="savedGame";

    //parameters
    private boolean continueGame;




    private Game game;
    LetterAdapter adapter;
    ArrayAdapter<String> foundWordsAdapter;
    private StringBuilder stringBuilder;



    //Views
    private TextView attemptTextView,foundTextView;
    private OnFragmentInteractionListener mListener;
    private TextView goodText,greatText,perfectText;
    ImageView goodStar,greatStar,perfectStar;
    ProgressBar progressBar;
    GridView grid;

    public TargetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param load boolean indicating if a saved game should be loaded
     * @return A new instance of fragment TargetFragment.
     */

    public static TargetFragment newInstance(boolean load) {
        TargetFragment fragment = new TargetFragment();
        Bundle args = new Bundle();
        args.putBoolean(LOAD_GAME, load);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            continueGame = getArguments().getBoolean(LOAD_GAME);
        }

        game=null;
        game=new Game(((MainActivity)getActivity()).controller);
        if(!(continueGame && loadGame())) {
            game=new Game(((MainActivity)getActivity()).controller);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.target_layout, container, false);

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise(view);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    //  Initialisation Methods
    private void initialise(View view){

        //targets
        goodText=(TextView)view.findViewById(R.id.goodTextView);
        greatText=(TextView)view.findViewById(R.id.greatTextView);
        perfectText=(TextView)view.findViewById(R.id.perfectTextView);

        //attempt
        attemptTextView =(TextView)view.findViewById(R.id.newWord);
        stringBuilder=new StringBuilder();


        //Progress

        GridView foundGridView=(GridView)view.findViewById(R.id.drawerList);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        foundTextView=(TextView)view.findViewById(R.id.foundCountView);
        foundWordsAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1);
        foundGridView.setAdapter(foundWordsAdapter);

        goodStar=(ImageView) (view.findViewById(R.id.goodStar));
        greatStar=(ImageView) (view.findViewById(R.id.greatStar));
        perfectStar=(ImageView) (view.findViewById(R.id.perfectStar));

        initialiseViewValues();
        prepareButtons(view);
        prepareGrid(view);

    }

    private void prepareGrid(View view){

        List<String> letters= new ArrayList<>();
        adapter=new LetterAdapter(getContext(),letters);
        populateAdapter(adapter);
        grid = (GridView)view.findViewById(R.id.gridView);
        grid.setAdapter(adapter);
        grid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        grid.setMultiChoiceModeListener(new MultiListener());
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean b=!adapter.isSelected(position);
                if(b)
                {
                    grid.setItemChecked(position, true);
                    adapter.setSelected(position);
                    stringBuilder.append(adapter.getItem(position));
                    if (attemptTextView == null) throw new AssertionError();
                    attemptTextView.setText(stringBuilder.toString());

                }
                else {
                    grid.setItemChecked(position, false);
                    adapter.removeSelected(position);
                    stringBuilder.deleteCharAt(stringBuilder.indexOf(adapter.getItem(position)));
                    attemptTextView.setText(stringBuilder.toString());
                }

            }
        });
    }

    private void prepareButtons(final View view){

        FloatingActionButton fab=(FloatingActionButton)view.findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWord();
            }
        });

        FloatingActionButton refresh=(FloatingActionButton)view.findViewById(R.id.reset);
        assert refresh != null;
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        FloatingActionButton solve=(FloatingActionButton)view.findViewById(R.id.solve);
        assert solve != null;
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.manageWord(game.getJumbledGameWord());
            }
        });
    }

    private void submitWord(){

        String potential=attemptTextView.getText().toString();
        if(!game.checkPlayed(potential) && game.submitWord(potential)){
            incrementProgress(potential);
            clear();
            Uri ringtone= RingtoneManager.getActualDefaultRingtoneUri(getContext(),RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mp=MediaPlayer.create(getContext(),ringtone);
            mp.start();
        }
        else if(game.checkPlayed(potential)){
            showAlreadyFoundMessage(potential.toLowerCase());
        }
        else showIncorrectMessage(potential.toLowerCase());
    }

    private void clear(){
        adapter.clearSelection();
        stringBuilder.delete(0,stringBuilder.length());
        attemptTextView.setText("");
    }

    private void incrementProgress(String word){
        foundWordsAdapter.add(word);
        foundTextView.setText(String.format(Locale.getDefault(),"Found Words: %d",game.getScore()));
        progressBar.incrementProgressBy(1);
        //check targets
        switch (game.target_status()){
            case GOOD:
                goodStar.setVisibility(View.VISIBLE);
                break;
            case GREAT:
                greatStar.setVisibility(View.VISIBLE);
                break;
            case PERFECT:
                greatStar.setVisibility(View.VISIBLE);
                showVictoryMessage("Congratulations! You found all the words.");
                break;
        }

        if(word.length()==game.targetWordLength())showVictoryMessage("Congratulations! You got the 9 Letter word.");

    }


    private void populateAdapter(LetterAdapter adapter){
        for(char c: game.getGameLetters()){
            adapter.add(Character.toString(c).toUpperCase());
        }
    }


    private  void checkLevel(){
        switch (game.target_status()){
            case GOOD:
                goodStar.setVisibility(View.VISIBLE);
            case GREAT:
                greatStar.setVisibility(View.VISIBLE);
            case PERFECT:
                greatStar.setVisibility(View.VISIBLE);
                showVictoryMessage("Congratulations! You found all the words.");
                break;
        }
    }

    private  void showIncorrectMessage(String s){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        aBuilder.setMessage("Sorry, \""+s+ "\" is an invalid word.");
        aBuilder.setTitle("Invalid Word");
        aBuilder.setIcon(R.mipmap.ic_target);
        aBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog= aBuilder.create();
        dialog.show();

    }

    private void showAlreadyFoundMessage(String s){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        aBuilder.setMessage("\""+s+"\" has already been found");
        aBuilder.setTitle("Word Already Found");
        aBuilder.setIcon(R.mipmap.ic_target);
        aBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }

    private void showVictoryMessage(String message){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        aBuilder.setMessage(message);
        aBuilder.setTitle("Victory");
        aBuilder.setIcon(R.mipmap.ic_target);
        aBuilder.setPositiveButton("Next Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reset();
            }
        });
        if(game.allSolutionsFound()) {
            aBuilder.setNegativeButton("Continue Playing", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }

    private void saveGame(){

        PrintStream writer= null;
        try {
            writer = new PrintStream(getContext().openFileOutput(SAVE_FILE_NAME,Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (writer == null) throw new AssertionError();
        writer.println(game.getGameWord());
        writer.println(game.getJumbledGameWord());
        for(String w:game.getFoundWords()){
            writer.println(w);
        }
        writer.close();


    }

    private boolean loadGame(){
        FileInputStream inputStream;
        try {

            inputStream=getActivity().openFileInput(SAVE_FILE_NAME);
        } catch (FileNotFoundException e) {
            return false;
        }
        Scanner scanner=new Scanner(inputStream);
        String actualWord="";
        String anagram="";
        if(scanner.hasNextLine())actualWord=scanner.nextLine();
        if(scanner.hasNextLine())anagram=scanner.nextLine();
        if(anagram.length()==0||actualWord.length()==0)return false;
        game=new Game(((MainActivity)getActivity()).controller,actualWord,anagram);
        while(scanner.hasNextLine()){
            game.submitWord(actualWord);
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
        return true;
    }

    private void reset(){
        clear();
        adapter.clear();
        foundWordsAdapter.clear();
        game.resetGame();
        initialiseViewValues();
        populateAdapter(adapter);
        checkLevel();

    }

    private void initialiseViewValues(){
        goodStar.setVisibility(View.INVISIBLE);
        greatStar.setVisibility(View.INVISIBLE);
        perfectStar.setVisibility(View.INVISIBLE);
        goodText.setText(String.format(Locale.getDefault(),"GOOD: %d",game.getGoodTarget()));
        greatText.setText(String.format(Locale.getDefault(),"GREAT: %d",game.getGreatTarget()));
        perfectText.setText(String.format(Locale.getDefault(),"PERFECT: %d",game.getPerfectTarget()));
        attemptTextView.setText("");
        progressBar.setMax(game.getPerfectTarget());
        progressBar.setProgress(0);
        foundTextView.setText(String.format(Locale.getDefault(),"Found Words: %d",game.getScore()));
    }


  /*  private void saveGameDialog() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(
                getContext());
        aBuilder.setMessage("Would you like to save your game before exiting");
        aBuilder.setTitle("Save Game Before Exit?");
        aBuilder.setIcon(R.mipmap.ic_target);
        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveGame();
            }
        });
        aBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }*/


    private class LetterAdapter extends ArrayAdapter<String>{

        private ArrayList<Integer> selectedItems;

        LetterAdapter(Context context,List<String> list){
            super(context, R.layout.simple_grid_item,list);
            selectedItems=new ArrayList<>(9);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View item=convertView;
            if(item==null)item=inflater.inflate(R.layout.simple_grid_item,parent,false);
            TextView textView=(TextView)item.findViewById(R.id.letter);
            (textView).setText(this.getItem(position));
            if(position==4){
                textView.setTextColor(Color.YELLOW);

            }

            return item;

        }

        void setSelected(int position){
            selectedItems.add(position);
        }

        void removeSelected(Integer position){
            selectedItems.remove(position);
        }

        boolean isSelected(int position){
            return  selectedItems.contains(position);
        }

        void clearSelection(){
            for(int i:adapter.selectedItems){grid.setItemChecked(i,false);}
            selectedItems.clear();
        }


    }


    private class MultiListener implements GridView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }





}
