package com.applications.brian.wordWarrior.Presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.TargetGame;
import com.applications.brian.wordWarrior.R;
import com.applications.brian.wordWarrior.Utilities.ClockTask;

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
public class TargetFragment extends Fragment implements View.OnClickListener {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOAD_GAME = "LOAD_GAME_PARAMETER";
    private static final String LOAD_GAME_DATA = "LOAD_GAME_DATA_PARAMETER";
    private static final String LEVEL = "LEVEL_PARAMETER";
    private boolean loadGame;

    //Other Fields
    private TargetGame targetGame;
    private LetterAdapter adapter;
    private ArrayAdapter<String> foundWordsAdapter;
    private StringBuilder stringBuilder;
    
    //Controller
    private  Controller controller;

    //Views
    private TextView attemptTextView,foundTextView;
    private TextView goodText,greatText,perfectText;
    private ImageView goodStar;
    private ImageView greatStar;
    private ImageView perfectStar;
    private ProgressBar progressBar;
    private GridView grid;
    private TextView timer;
    private boolean firstAttempt;
    private ClockTask clock;
    private boolean viewedSolutions;

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
        controller=((MainActivity) getActivity()).controller;
        AlertDialog dialog=loadDialog();
        dialog.show();
        if (getArguments() != null) {
            String level=getArguments().getString(LEVEL);
            loadGame = getArguments().getBoolean(LOAD_GAME);
            if(loadGame){
                String loadGameData = getArguments().getString(LOAD_GAME_DATA);
                targetGame = new TargetGame(controller, loadGameData) ;
                dialog.dismiss();
                return;
            }
            
            targetGame=new TargetGame(controller,TargetGame.GAME_LEVEL.valueOf(level));


        }
        dialog.dismiss();

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

    @Override
    public void onPause() {
        super.onPause();
        clock.pause();

    }

    @Override
    public void onResume() {
        super.onResume();
        clock.start(timer);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clock.stop();
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

        //Time
        timer=(TextView)view.findViewById(R.id.timer);
        clock=new ClockTask(timer,getActivity());

        initialiseViewValues();
        prepareButtons(view);
        prepareGrid(view);

    }

    private void initialiseViewValues(){
        clock.start(timer);
        viewedSolutions=false;
        goodStar.setActivated(false);
        greatStar.setActivated(false);
        perfectStar.setActivated(false);
        goodText.setText(String.format(Locale.getDefault(),"Good: %d", targetGame.getGoodTarget()));
        greatText.setText(String.format(Locale.getDefault(),"Great: %d", targetGame.getGreatTarget()));
        perfectText.setText(String.format(Locale.getDefault(),"Perfect: %d", targetGame.getPerfectTarget()));
        attemptTextView.setText("");
        progressBar.setMax(targetGame.getPerfectTarget());
        progressBar.setProgress(targetGame.getScore());
        foundTextView.setText(String.format(Locale.getDefault(),"Found Words: %d", targetGame.getScore()));
        if(loadGame)foundWordsAdapter.addAll(targetGame.getFoundWords());
        firstAttempt=true;
    }

    private void prepareGrid(View view){

        List<String> letters= new ArrayList<>();
        adapter=new LetterAdapter(getContext(),letters);
        populateAdapter(adapter);
        grid = (GridView)view.findViewById(R.id.gridView);
        grid.setDrawSelectorOnTop(true);
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
                    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(adapter.getItem(position)));
                    attemptTextView.setText(stringBuilder.toString());
                }

            }
        });
    }

    private void prepareButtons(final View view){
        view.findViewById(R.id.submitWord).setOnClickListener(this);
        view.findViewById(R.id.clearButton).setOnClickListener(this);

        Button clearButton=(Button)view.findViewById(R.id.clearButton);
        assert clearButton != null;
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        view.findViewById(R.id.newGameIcon).setOnClickListener(this);
        view.findViewById(R.id.newGameText).setOnClickListener(this);

        view.findViewById(R.id.solutionsIcon).setOnClickListener(this);
        view.findViewById(R.id.solutionsText).setOnClickListener(this);

        view.findViewById(R.id.overFlowIcon).setOnClickListener(this);
    }

    private void submitWord(){
        if(firstAttempt){
            controller.updateGamesPlayed();
            firstAttempt=false;
        }
        String potential=attemptTextView.getText().toString();
        if(!targetGame.checkPlayed(potential) && targetGame.submitWord(potential)){
            incrementProgress(potential);
            MediaPlayer mp=MediaPlayer.create(getContext(),R.raw.success);
            mp.start();
            return;
        }
        else if(targetGame.checkPlayed(potential)){
            showAlreadyFoundMessage(potential.toLowerCase());
        }
        else showIncorrectMessage(potential.toLowerCase());
        clear();

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
        if(word.length()== targetGame.targetWordLength() &&targetGame.target_status()!= TargetGame.TARGET_STATUS.PERFECT){
            boolean highScore=targetGame.newHighScore(timer.getText().toString())&&!viewedSolutions;
            victory("Congratulations! You got the 9 Letter word.",highScore);
            controller.updateGamesWon();
        }
        clear();

    }

    private void populateAdapter(LetterAdapter adapter){
        for(char c: targetGame.getGameLetters()){
            adapter.add(Character.toString(c).toUpperCase());
        }
    }

    private  void checkLevel(){
        switch (targetGame.target_status()){
            case GOOD:
                goodStar.setActivated(true);
                break;
            case GREAT:
                goodStar.setActivated(true);
                greatStar.setActivated(true);
                break;
            case PERFECT:
                goodStar.setActivated(true);
                greatStar.setActivated(true);
                perfectStar.setActivated(true);
                victory("You found all the words.Congratulations on being adequate.",false);
                break;
        }
    }

    private void updatePoints(){
        int points=targetGame.getPoints();
        controller.updatePoints(points);
        Toast.makeText(getContext(),String.format(Locale.getDefault(),"+%d Points",points),Toast.LENGTH_SHORT).show();
    }

    void purchaseMade(){
        viewedSolutions=true;
    }

    private void reset(){
        AlertDialog dialog=loadDialog();
        clock.stopAndReset();
        updatePoints();
        clear();
        adapter.clear();
        foundWordsAdapter.clear();
        targetGame.resetGame();
        controller.updateGamesPlayed();
        initialiseViewValues();
        populateAdapter(adapter);
        checkLevel();
        dialog.dismiss();


    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.submitWord){
            submitWord();
        }
        if(id==R.id.clearButton){
            clear();
        }

        else if(id==R.id.newGameIcon||id==R.id.newGameText){
            reset();
        }


        else if(id==R.id.solutionsIcon||id==R.id.solutionsText){
            PurchaseDialog dialogFragment=PurchaseDialog.newInstance(PurchaseDialog.TARGET_GAME);
            dialogFragment.setCallingFragment(this);
            dialogFragment.show(getFragmentManager(),null);
        }

        else if(id==R.id.overFlowIcon||id==R.id.moreText){
            menuDialog();
        }
    }


    //Dialogs
    private void saveGameDialog() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(
                getContext());
        aBuilder.setMessage("Would you like to save your game?").
                setTitle("Save Game?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!targetGame.isNewGame()){
                    dialog.dismiss();
                    overwriteDialog();
                }
               else {
                    targetGame.save(TargetGame.SAVING_STATUS.NEW_SAVE);
                    dialog.dismiss();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        menuItems.add("High Scores");
        menuItems.add("Exit (without saving)");
        menuItems.add("Settings");
        menuItems.add("Help");
        String[] m=new String[menuItems.size()];
        AlertDialog.Builder builder=new  AlertDialog.Builder(getContext());
        builder.setTitle("Select An Option");
        builder.setItems(menuItems.toArray(m), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity mainActivity=((MainActivity)getContext());
                switch (which){
                    case 0:
                        saveGameDialog();
                        break;
                    case 1:
                        mainActivity.savedGamesDialog(TargetGame.SAVE_FILE_NAME);
                        break;
                    case 2:
                        mainActivity.selectLevel();
                        break;
                    case 3:
                        mainActivity.highScoresDialog(TargetGame.SCORE_FILE_NAME);
                        break;
                    case 4:
                        clock.stop();
                        mainActivity.onBackPressed();
                        break;
                    default:
                        Toast.makeText(getContext(),"Item not yet Available",Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        Window window=dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        dialog.show();

    }

    private AlertDialog loadDialog(){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(
                getContext());
        aBuilder.setMessage("Loading...").
                setTitle("Loading").setIcon(R.drawable.ic_target);
        aBuilder.setCancelable(false);
        return aBuilder.create();
    }

    private  void showIncorrectMessage(String s){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        aBuilder.setMessage("Sorry, \""+s+ "\" is an invalid word.");
        aBuilder.setTitle("Invalid Word");
        aBuilder.setIcon(R.drawable.ic_target);
        aBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog= aBuilder.create();
        dialog.show();
        MediaPlayer mp=MediaPlayer.create(getContext(),R.raw.fail);
        mp.start();

    }

    private void showAlreadyFoundMessage(String s){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        aBuilder.setMessage("\""+s+"\" has already been found");
        aBuilder.setTitle("GameWord Already Found");
        aBuilder.setIcon(R.drawable.ic_target);
        aBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }

    private void victory(String message, boolean highScore){
        clock.pause();
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        if(highScore)message=String.format(Locale.getDefault(),"%s%n%s",message,"[New High Score!]");
        aBuilder.setMessage(message);
        aBuilder.setTitle("Victory");
        aBuilder.setIcon(R.drawable.ic_target);
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
                    clock.start(timer);
                }
            });
        }

        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }


    //Helper Classes
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
                textView.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                item.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.center_selector));

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
