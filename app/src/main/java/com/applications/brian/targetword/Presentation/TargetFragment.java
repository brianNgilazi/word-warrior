package com.applications.brian.targetword.Presentation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.brian.targetword.Logic.TargetGame;
import com.applications.brian.targetword.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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
    private static final String LOAD_GAME_DATA = "LOAD_GAME_DATA_PARAMETER";
    private static final String LEVEL = "LEVEL_PARAMETER";
    private boolean loadGame;

    public static final String SAVE_FILE_NAME="savedGame";

    //Other Fields
    private TargetGame targetGame;
    private LetterAdapter adapter;
    private ArrayAdapter<String> foundWordsAdapter;
    private StringBuilder stringBuilder;

    //Views
    private TextView attemptTextView,foundTextView;
    private TextView goodText,greatText,perfectText;
    private ImageView goodStar;
    private ImageView greatStar;
    private ImageView perfectStar;
    private ProgressBar progressBar;
    private GridView grid;

    public TargetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param load boolean indicating if a saved targetGame should be loaded
     * @return A new instance of fragment TargetFragment.
     */

    public static TargetFragment newInstance(boolean load,String data,String level) {
        TargetFragment fragment = new TargetFragment();
        Bundle args = new Bundle();
        args.putBoolean(LOAD_GAME, load);
        args.putString(LOAD_GAME_DATA,data);
        args.putString(LEVEL,level);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String level=" ";
        if (getArguments() != null) {
            level=getArguments().getString(LEVEL);
            loadGame = getArguments().getBoolean(LOAD_GAME);
            if(loadGame){
                String loadGameData = getArguments().getString(LOAD_GAME_DATA);
                targetGame = new TargetGame(((MainActivity) getActivity()).controller, loadGameData) ;
                return;
            }
        }
        load(TargetGame.GAME_LEVEL.valueOf(level));

    }

    private void load(final TargetGame.GAME_LEVEL game_level){

        ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("LoadingFragment");
        progressDialog.setTitle("LoadingFragment");
        progressDialog.show();
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                targetGame=new TargetGame(((MainActivity) getActivity()).controller,game_level);

            }
        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
        try {
            t.join();
            progressDialog.dismiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.target_layout, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise(view);
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

    private void initialiseViewValues(){
        goodStar.setVisibility(View.INVISIBLE);
        greatStar.setVisibility(View.INVISIBLE);
        perfectStar.setVisibility(View.INVISIBLE);
        goodText.setText(String.format(Locale.getDefault(),"GOOD: %d", targetGame.getGoodTarget()));
        greatText.setText(String.format(Locale.getDefault(),"GREAT: %d", targetGame.getGreatTarget()));
        perfectText.setText(String.format(Locale.getDefault(),"PERFECT: %d", targetGame.getPerfectTarget()));
        attemptTextView.setText("");
        progressBar.setMax(targetGame.getPerfectTarget());
        progressBar.setProgress(targetGame.getScore());
        foundTextView.setText(String.format(Locale.getDefault(),"Found Words: %d", targetGame.getScore()));
        if(loadGame)foundWordsAdapter.addAll(targetGame.getFoundWords());
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
                boolean b= adapter.isSelected(position);
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
        Button submitButton=(Button)view.findViewById(R.id.submitButton);
        assert submitButton != null;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWord();
            }
        });

        Button clearButton=(Button)view.findViewById(R.id.clearButton);
        assert clearButton != null;
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });


        ImageView newGame=(ImageView) view.findViewById(R.id.newGameIcon);
        assert newGame != null;
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        ImageView solve=(ImageView) view.findViewById(R.id.solutionsIcon);
        assert solve != null;
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mListener.manageWord(targetGame.getJumbledGameWord());
                DialogFragment dialogFragment=new SolverDialog();
                dialogFragment.show(getFragmentManager(),null);
            }
        });



        ImageView menu=(ImageView) view.findViewById(R.id.overFlowIcon);
        assert menu != null;
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog();
            }
        });


    }

    private void submitWord(){

        String potential=attemptTextView.getText().toString();
        if(!targetGame.checkPlayed(potential) && targetGame.submitWord(potential)){
            incrementProgress(potential);
            clear();
            Uri ringtone= RingtoneManager.getActualDefaultRingtoneUri(getContext(),RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mp=MediaPlayer.create(getContext(),ringtone);
            mp.start();
        }
        else if(targetGame.checkPlayed(potential)){
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
        foundTextView.setText(String.format(Locale.getDefault(),"Found Words: %d", targetGame.getScore()));
        progressBar.incrementProgressBy(1);
        checkLevel();
        if(word.length()== targetGame.targetWordLength() &&targetGame.target_status()!= TargetGame.TARGET_STATUS.PERFECT)showVictoryMessage("Congratulations! You got the 9 Letter word.");

    }

    private void populateAdapter(LetterAdapter adapter){
        for(char c: targetGame.getGameLetters()){
            adapter.add(Character.toString(c).toUpperCase());
        }
    }

    private  void checkLevel(){
        switch (targetGame.target_status()){
            case GOOD:
                goodStar.setVisibility(View.VISIBLE);
                break;
            case GREAT:
                goodStar.setVisibility(View.VISIBLE);
                greatStar.setVisibility(View.VISIBLE);
                break;
            case PERFECT:
                goodStar.setVisibility(View.VISIBLE);
                greatStar.setVisibility(View.VISIBLE);
                perfectStar.setVisibility(View.VISIBLE);

                showVictoryMessage("Congratulations! You found all the words.");
                break;
        }
    }

    private  void showIncorrectMessage(String s){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        aBuilder.setMessage("Sorry, \""+s+ "\" is an invalid word.");
        aBuilder.setTitle("Invalid GameWord");
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
        aBuilder.setTitle("GameWord Already Found");
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
        if(!targetGame.allSolutionsFound()) {
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


    private void reset(){
        clear();
        adapter.clear();
        foundWordsAdapter.clear();
        targetGame.resetGame();
        initialiseViewValues();
        populateAdapter(adapter);
        checkLevel();

    }

    private void saveGameDialog() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(
                getContext());
        aBuilder.setMessage("Would you like to save your game before exiting").
                setTitle("Save Game Before Exit?").setIcon(R.mipmap.ic_target);

        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(targetGame.isNewGame()){
                    dialog.dismiss();
                    overwriteDialog();
                }
               else {
                    targetGame.save(TargetGame.SAVING_STATUS.NEW_SAVE);
                    dialog.dismiss();
                }
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

    private void  overwriteDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(
                getContext());
        builder.setMessage("A save of this already exists. Overwrite existing save?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                targetGame.save(TargetGame.SAVING_STATUS.OVERRIDE);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog= builder.create();
        dialog.show();
    }

    private void menuDialog(){
        ArrayList<String> menuItems=new ArrayList<>();

        menuItems.add("Save Game");
        menuItems.add("Load Game");
        menuItems.add("Change Level");
        menuItems.add("Exit (without saving)");
        menuItems.add("Setting");
        menuItems.add("Help");
        String[] m=new String[menuItems.size()];
        AlertDialog.Builder builder=new  AlertDialog.Builder(getContext());
        builder.setTitle("Select An Option");
        builder.setItems(menuItems.toArray(m), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        saveGameDialog();
                        break;
                    case 1:
                        ((MainActivity)getContext()).loadGameDialog();
                        break;
                    case 2:
                        ((MainActivity)getContext()).selectLevel();
                        break;
                    case 3:
                        ((MainActivity)getContext()).selectGameModeDialog();
                        break;
                    default:
                        Toast.makeText(getContext(),"Item not yet Available",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();
    }



    private class LetterAdapter extends ArrayAdapter<String>{

        private final ArrayList<Integer> selectedItems;

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
                textView.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
                textView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));

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
            return !selectedItems.contains(position);
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
