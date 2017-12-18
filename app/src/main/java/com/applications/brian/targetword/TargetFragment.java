package com.applications.brian.targetword;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TargetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TargetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TargetFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOAD_GAME = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private boolean continueGame;

    private TextView attemptTextView;
    private ArrayList<Integer> checked;
    private Word targetWord;
    private int score;
    private TextView scoreText;
    private ProgressBar progress;
    private ArrayList<String> found;
    private StringBuilder stringBuilder;
    private boolean loadedGame;
    private boolean exiting=true;
    private FileOutputStream outputStream;


    private OnFragmentInteractionListener mListener;
    private String savepath;

    public TargetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TargetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TargetFragment newInstance(boolean param1, String param2) {
        TargetFragment fragment = new TargetFragment();
        Bundle args = new Bundle();
        args.putBoolean(LOAD_GAME, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            continueGame = getArguments().getBoolean(LOAD_GAME);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }

        found=new ArrayList<>();
        if(continueGame && loadGame()) {loadedGame=true;return;}
        targetWord =new Word(((MainActivity)getActivity()).dictionary);
        score=0;



    }

    @Override
    public void onPause() {
        super.onPause();
        if(exiting) saveGameDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_target, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.onFragmentInteraction(view);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setUp(){

        initialise();
        prepareButtons();
        prepareGrid();
        savepath=getContext().getFilesDir().getPath();
    }


    private void initialise(){
        if(!loadedGame) {
            nextWord();
            //targetWord =new Word(((MainActivity)getActivity()).dictionary);score=0;found.clear();
        }
        //targets
        int[] targets= targetWord.getTargets();

        TextView target=(TextView)findViewById(R.id.targetTextView);
        assert target != null;
        target.setText(String.format(Locale.getDefault(),"Good: %d    Great: %d    Perfect: %d",targets[0],targets[1],targets[2]));

        //Progress
        progress=(ProgressBar)findViewById(R.id.progressBar);
        assert progress != null;
        progress.setProgress(score);
        progress.setMax(targets[2]);

        //attempt
        attemptTextView =(TextView)findViewById(R.id.newWord);
        if (attemptTextView == null) throw new AssertionError();
        attemptTextView.setText("");
        checked=new ArrayList<>();
        stringBuilder=new StringBuilder();

        //score
        scoreText=(TextView)findViewById(R.id.scoreTextView);
        scoreText.setText(String.format(Locale.getDefault(),"Found Words: %d",score));

        ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, found);
        adapter.setNotifyOnChange(true);
        ListView listView=(ListView)findViewById(R.id.foundWords);
        listView.setAdapter(adapter);

        (findViewById(R.id.goodStar)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.greatStar)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.perfectStar)).setVisibility(View.INVISIBLE);
        checkLevel("");


    }

    private void prepareGrid(){

        List<String> letters= new ArrayList<>();
        ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,letters);
        populateAdapter(adapter);

        GridView grid = (GridView) findViewById(R.id.gridView);
        assert grid != null;
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //   RandomAccessFile file = getFile();
                TextView t = ((TextView) view);
                if(!checked.contains(position)) {
                    checked.add(position);
                    stringBuilder.append(t.getText());
                    if (attemptTextView == null) throw new AssertionError();
                    attemptTextView.setText(stringBuilder.toString());

                }

            }

        });

    }

    private void prepareButtons(){
        @SuppressLint("WrongViewCast") final AppCompatImageButton ok=(AppCompatImageButton)findViewById(R.id.submitButton);
        assert ok != null;
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWord();
            }
        });

        @SuppressLint("WrongViewCast") final AppCompatImageButton clearButton=(AppCompatImageButton) findViewById(R.id.clearButton);
        assert clearButton != null;
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        @SuppressLint("WrongViewCast") final AppCompatImageButton backSpace=(AppCompatImageButton) findViewById(R.id.backSpaceButton);
        assert backSpace != null;
        backSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stringBuilder.length()>0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    TextView newWord = ((TextView) findViewById(R.id.newWord));
                    if (newWord == null) throw new AssertionError();
                    newWord.setText(stringBuilder.toString());
                    checked.remove(checked.size() - 1);
                }
            }
        });

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

    }

    private void submitWord(){
        TextView word=((TextView)findViewById(R.id.newWord));
        if (word == null) throw new AssertionError();
        String potential=word.getText().toString();
        if(!found.contains(potential) && targetWord.getAnswers().contains(potential.toLowerCase())){
            incrementProgress(potential);
            clear();
            Uri ringtone= RingtoneManager.getActualDefaultRingtoneUri(getContext(),RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mp=MediaPlayer.create(getContext(),ringtone);
            //Ringtone rt=RingtoneManager.getRingtone(this,ringtone);
            mp.start();
            //mp.release();
        }
        else if(found.contains(potential)){
            alreadyFound(potential.toLowerCase());
        }
        else incorrectWord(potential.toLowerCase());
    }

    private void clear(){
        checked.clear();
        stringBuilder.delete(0,stringBuilder.length());
        TextView newWord=((TextView)findViewById(R.id.newWord));
        if (newWord == null) throw new AssertionError();
        newWord.setText("");
    }

    private void incrementProgress(String word){
        found.add(word);
        scoreText.setText(String.format(Locale.getDefault(),"Found Words: %d",++score));
        progress.incrementProgressBy(1);
        checkLevel(word);

    }

    private void populateAdapter(ArrayAdapter<String> adapter){
        for(char c: targetWord.shuffle()){
            adapter.add(Character.toString(c).toUpperCase());
        }


    }

    private void checkLevel(String word){
        //check targets
        int[] t= targetWord.getTargets();
        View star;
        if(score==t[0]) {
            star=findViewById(R.id.goodStar);
            assert (star) != null;
            star.setVisibility(View.VISIBLE);
        }

        if(score==t[1]) {
            star=findViewById(R.id.greatStar);
            assert (star) != null;
            star.setVisibility(View.VISIBLE);
        }

        if(score==t[2]){
            star=findViewById(R.id.perfectStar);
            assert (star) != null;
            star.setVisibility(View.VISIBLE);
            victory("Congratulations! You found all the words.");
        }

        if(word.length()==9){
            victory("Congratulations! You got the 9 Letter word.");
        }

    }

    private  void incorrectWord(String s){
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

    private void alreadyFound(String s){
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

    private void victory(String message){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        aBuilder.setMessage(message);
        aBuilder.setTitle("Victory");
        aBuilder.setIcon(R.mipmap.ic_target);
        aBuilder.setPositiveButton("Next Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refresh();
            }
        });
        if(score!= targetWord.getTargets()[2]) {
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

        PrintWriter writer= null;
        try {
            writer = new PrintWriter(savepath+"/savedGame");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (writer == null) throw new AssertionError();
        writer.println(targetWord);
        writer.println(targetWord.getJumbledWord());
        for(String w:found){
            writer.println(w);
        }
        writer.close();


    }

    private boolean loadGame(){
        FileInputStream inputStream;
        try {

            inputStream=getActivity().openFileInput("savedGame");
        } catch (FileNotFoundException e) {
            return false;
        }
        Scanner scanner=new Scanner(inputStream);
        String actualWord="";
        String anagram="";
        if(scanner.hasNextLine())actualWord=scanner.nextLine();
        if(scanner.hasNextLine())anagram=scanner.nextLine();
        if(anagram.length()==0||actualWord.length()==0)return false;
        targetWord=new Word(actualWord,anagram,(((MainActivity)getActivity()).dictionary));
        while(scanner.hasNextLine()){
            found.add(scanner.nextLine());
            score++;
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();

       // checkLevel("");
        return true;
    }


    private void refresh(){

         System.gc();
         exiting=false;
        ((MainActivity)getActivity()).startTarget(false);

    }

    private View findViewById(int id){
        return getActivity().findViewById(id);

    }

    private void saveGameDialog() {
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
    }

    private void nextWord(){
        ParallelTask p=new ParallelTask(getActivity());
        p.execute(((MainActivity)getActivity()).dictionary);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(View view);
    }



}
