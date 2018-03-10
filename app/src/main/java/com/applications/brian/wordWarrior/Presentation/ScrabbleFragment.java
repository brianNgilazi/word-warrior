package com.applications.brian.wordWarrior.Presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.ScrabbleGame;
import com.applications.brian.wordWarrior.Logic.ScrabbleLetter;
import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScrabbleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScrabbleFragment extends Fragment implements View.OnClickListener {


    // the fragment initialization parameters
    private static final String LOAD_GAME = "LOAD_GAME_PARAMETER";
    private static final String LOAD_GAME_DATA = "LOAD_GAME_DATA_PARAMETER";
    private boolean loadGame;
    
    private Controller controller;

    //Other Fields
    private ScrabbleGame scrabbleGame;
    private LetterAdapter adapter;
    private ArrayAdapter<String> foundWordsAdapter;
    private StringBuilder stringBuilder;
    private List<View> viewList;
    private List<View> allModifiedViews;

    //Views
    private TextView attemptTextView,foundTextView,currentScore,totalScore;
    private GridView grid;

    boolean firstAttempt=true;
    private final RandomColour randomColour=new RandomColour();

    public ScrabbleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param load boolean indicating if a saved scrabbleGame should be loaded
     * @return A new instance of fragment ScrabbleFragment.
     */
    public static ScrabbleFragment newInstance(boolean load, String data) {
        ScrabbleFragment fragment = new ScrabbleFragment();
        Bundle args = new Bundle();
        args.putBoolean(LOAD_GAME, load);
        args.putString(LOAD_GAME_DATA,data);
       
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
            loadGame = getArguments().getBoolean(LOAD_GAME);
            if(loadGame){
                String loadGameData = getArguments().getString(LOAD_GAME_DATA);
                scrabbleGame = new ScrabbleGame(controller, loadGameData) ;
                dialog.dismiss();
                return;
            }
            scrabbleGame =new ScrabbleGame(controller);

        }
        dialog.dismiss();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scrabble_layout, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise(view);
    }

    //  Initialisation Methods
    private void initialise(View view){


        //attempt
        attemptTextView =(TextView)view.findViewById(R.id.newWord);
        stringBuilder=new StringBuilder();
        currentScore=(TextView)view.findViewById(R.id.currentScore);


        //Progress
        GridView foundGridView=(GridView)view.findViewById(R.id.drawerList);
        totalScore=(TextView)view.findViewById(R.id.score);
        foundTextView=(TextView)view.findViewById(R.id.foundCountView);
        foundWordsAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1);
        foundGridView.setAdapter(foundWordsAdapter);
        

        initialiseViewValues();
        prepareButtons(view);
        prepareGrid(view);

    }

    private void initialiseViewValues(){
        firstAttempt=true;
        attemptTextView.setText("");
        currentScore.setText(String.valueOf(0));
       totalScore.setText(String.format(Locale.getDefault(),"Score: %d", scrabbleGame.getScore()));
        if(loadGame)foundWordsAdapter.addAll(scrabbleGame.getFoundWords());
        foundTextView.setText(String.format(Locale.getDefault(),"Found Words: %d", foundWordsAdapter.getCount()));
    }

    private void prepareGrid(View view){
        viewList=new ArrayList<>();
        allModifiedViews=new ArrayList<>();
        adapter=new LetterAdapter(new ArrayList<ScrabbleLetter>());
        populateAdapter(adapter);
        grid = (GridView)view.findViewById(R.id.gridView);
        grid.setAdapter(adapter);
        grid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        grid.setMultiChoiceModeListener(new MultiListener());
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Check if tile used
                if(!adapter.isUsed(position)) {
                  //check if item clicked is either the first item in a new attempt word or is adjacent to the last clicked item.
                    boolean adjacent= adapter.selectedItems.size()==0 || ScrabbleGame.isAdjacent(position+1,adapter.selectedItems.get(adapter.selectedItems.size()-1 )+1);
                    if (!adapter.isSelected(position) && adjacent) {
                        grid.setItemChecked(position, true);
                        adapter.setSelected(position);
                        stringBuilder.append(adapter.getItem(position));
                        if (attemptTextView == null) throw new AssertionError();
                        attemptTextView.setText(stringBuilder.toString());
                        currentScore.setText(String.valueOf(scrabbleGame.scoreForWord(stringBuilder.toString())));
                        viewList.add(view);
                    }
                    //if clicked position is selected and the last selected item (deselect)
                    else if(adapter.isSelected(position) && position==adapter.selectedItems.get(adapter.selectedItems.size()-1)){
                        grid.setItemChecked(position, false);
                        adapter.removeSelected(position);
                        stringBuilder.deleteCharAt(stringBuilder.indexOf(adapter.getItem(position).toString()));
                        attemptTextView.setText(stringBuilder.toString());
                        currentScore.setText(String.valueOf(scrabbleGame.scoreForWord(stringBuilder.toString())));
                        viewList.remove(view);
                    }
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

    private void populateAdapter(LetterAdapter adapter){
        for(ScrabbleLetter letter: scrabbleGame.getGameLetters()){
            adapter.add(letter);
        }
    }

    //Game play methods
    private void submitWord(){
        if(firstAttempt){
            controller.updateGamesPlayed();
            firstAttempt=false;
        }
        String potential=attemptTextView.getText().toString();
        if(scrabbleGame.submitWord(potential)){
            adapter.setSelectionUsed();
            incrementProgress(potential);
            MediaPlayer mp=MediaPlayer.create(getContext(),R.raw.success);
            mp.start();
            return;
        }
        else if(scrabbleGame.checkPlayed(potential)){
            showAlreadyFoundMessage(potential.toLowerCase());

        }
        else showIncorrectMessage(potential.toLowerCase());
        clear();
        MediaPlayer mp=MediaPlayer.create(getContext(),R.raw.fail);
        mp.start();
    }

    private void clear(){
        adapter.clearSelection();
        viewList.clear();
        stringBuilder.delete(0,stringBuilder.length());
        attemptTextView.setText("");
        currentScore.setText("");
    }

    private void updatePoints(){
        int points=scrabbleGame.getPoints();
        controller.updatePoints(points);
        Toast.makeText(getContext(),String.format(Locale.getDefault(),"%d points earned",points),Toast.LENGTH_SHORT).show();
    }

    private void incrementProgress(String word){
        foundWordsAdapter.add(word);
        foundTextView.setText(String.format(Locale.getDefault(),"Found Words: %d", foundWordsAdapter.getCount()));
       totalScore.setText(String.format(Locale.getDefault(),"Score: %d", scrabbleGame.getScore()));
        for (View view:viewList){
            view.setBackgroundResource(0);
            view.setBackgroundColor(randomColour.getCurrentColor());
        }
        allModifiedViews.addAll(viewList);
        randomColour.nextColor();
        if(scrabbleGame.gameOver()){
            victory("No more words left in the grid",scrabbleGame.newHighScore());
        }
        clear();

    }

    private void reset(){
        AlertDialog dialog=loadDialog();
        dialog.setCancelable(false);
        dialog.show();
        clear();
        foundWordsAdapter.clear();
        for(View view:allModifiedViews){
            view.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.grid_selector));
        }
        allModifiedViews.clear();
        updatePoints();
        scrabbleGame.resetGame();
        adapter.reset();
        initialiseViewValues();
        dialog.dismiss();

    }


    //Dialogs
    private  void showIncorrectMessage(String s){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        aBuilder.setMessage("Sorry, \""+s+ "\" is an invalid word.");
        aBuilder.setTitle("Invalid Word");
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
        aBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }

    private void victory(String message,boolean highScore){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(getContext());
        controller.updateGamesWon();
        if(highScore)message=String.format(Locale.getDefault(),"%s%n%s",message,"New High Score!");
        aBuilder.setMessage(message);
        aBuilder.setTitle("Victory");
        aBuilder.setPositiveButton("Next Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reset();
            }
        });
        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }

    private void saveGameDialog() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(
                getContext());
        aBuilder.setMessage("Would you like to save your game?").
                setTitle("Save Game?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!scrabbleGame.isNewGame()){
                    dialog.dismiss();
                    overwriteDialog();
                }
                else {
                    scrabbleGame.save(ScrabbleGame.SAVING_STATUS.NEW_SAVE);
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
                scrabbleGame.save(ScrabbleGame.SAVING_STATUS.OVERRIDE);
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
                switch (which){
                    case 0:
                        saveGameDialog();
                        break;
                    case 1:
                        ((MainActivity)getContext()).savedGamesDialog(ScrabbleGame.SAVE_FILE_NAME);
                        break;
                    case 2:
                        ((MainActivity)getContext()).highScoresDialog(ScrabbleGame.SCORE_FILE_NAME);
                        break;
                    case 3:
                       // ((MainActivity)getContext()).showHome();
                        break;
                    default:
                        Toast.makeText(getContext(),"Item not yet Available",Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        Window window=dialog.getWindow();
        if (window!=null){
            window.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();

    }

    private AlertDialog loadDialog(){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(
                getContext());
        aBuilder.setMessage("Loading...").
                setTitle("Loading");
        aBuilder.setCancelable(false);
        return aBuilder.create();
    }

    public void searchForLongestWord(String option){
        MyTask task=new MyTask(option);
        //noinspection unchecked
        task.execute();
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
            PurchaseDialog dialogFragment=PurchaseDialog.newInstance(PurchaseDialog.SCRABBLE_GAME);
            dialogFragment.setCallingFragment(this);
            dialogFragment.show(getFragmentManager(),null);
        }

        else if(id==R.id.overFlowIcon||id==R.id.moreText){
            menuDialog();
        }
    }


    //Helper Classes

    private class LetterAdapter extends BaseAdapter {

        private final List<Integer> selectedItems;
        private final List<Integer> usedItems;
        private final List<ScrabbleLetter> tiles;

        LetterAdapter(List<ScrabbleLetter> list){
            selectedItems=new ArrayList<>(9);
            usedItems=new ArrayList<>();
            tiles=list;

        }

        public void add(ScrabbleLetter letter){
            tiles.add(letter);
        }

        @Override
        public int getCount() {
            return tiles.size();
        }

        @Override
        public ScrabbleLetter getItem(int position) {
            return tiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View item=convertView;
            TileHolder holder;
            if(item==null){
                item=inflater.inflate(R.layout.scrabble_tile,parent,false);
                holder=new TileHolder(item);
                item.setTag(holder);
            }
            else{
                holder=(TileHolder)item.getTag();

            }
            holder.populateFrom(getItem(position));
            if(getItem(position).isUsed())grid.setItemChecked(position,true);
            return item;

        }

        void setSelected(int position){
            selectedItems.add(position);
        }

        void removeSelected(Integer position){
            selectedItems.remove(position);
        }

        boolean isSelected(int position){
            return selectedItems.contains(position);
        }

        void clearSelection(){
            for(int i:adapter.selectedItems){
                if(!usedItems.contains(i)) grid.setItemChecked(i,false);
            }
            selectedItems.clear();
        }

        private void clearUsed(){
            for(int i:usedItems){
                grid.setItemChecked(i,false);
            }
            usedItems.clear();
        }


        void reset(){
            tiles.clear();
            clearUsed();
            ScrabbleFragment.this.populateAdapter(this);
            notifyDataSetChanged();
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean isUsed(int position){
            return this.getItem(position).isUsed();
        }

        void setSelectionUsed(){
            for(int i:adapter.selectedItems){
                this.getItem(i).setUsed(true);

                usedItems.add(i);
            }
        }




        private class TileHolder{
            private TextView letter=null;
            private TextView value=null;


            TileHolder(View row){
                letter=(TextView)row.findViewById(R.id.letter);
                value=(TextView)row.findViewById(R.id.tileValue);
            }

            void populateFrom(ScrabbleLetter tile){
                letter.setText(tile.toString());
                value.setText(String.valueOf(tile.getValue()));
            }
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


    private class MyTask extends AsyncTask{
        final ScrabbleSolverDialog progressDialog;


        MyTask(String purchasedOption){
            progressDialog=ScrabbleSolverDialog.newInstance(purchasedOption);
        }

        @Override
        protected void onPreExecute() {;
            progressDialog.show(getFragmentManager(),null);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            return scrabbleGame.longestWord();
        }

        @Override
        protected void onPostExecute(Object o) {
            List<String> list=(List<String>)o;
           /* Collections.sort(list,new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return scrabbleGame.scoreForWord(o1)-scrabbleGame.scoreForWord(o2);
                }
            });*/
            int listSize=list.size();
            if(listSize!=0) progressDialog.setText(list.get(list.size()-1));
            else progressDialog.setText("No Words Found.");

        }
    }


    private class RandomColour{

        private final int colors[]={Color.BLUE,Color.CYAN,Color.GREEN,Color.MAGENTA,Color.RED,Color.YELLOW};
        private List<Integer> used;
        private int currentColorIndex =-1;

        RandomColour(){
            used=new ArrayList<>();
            nextColor();
        }

        int getCurrentColor(){
            return colors[currentColorIndex];
        }

        void nextColor(){
            if(currentColorIndex >=0)used.add(currentColorIndex);
            if(used.size()>=colors.length)used.clear();
            Random random=new Random();
            int nextIndex=random.nextInt(colors.length);
            while(used.contains(nextIndex))nextIndex=random.nextInt(colors.length);
            Log.i("Color",nextIndex+"");
            currentColorIndex= nextIndex;
        }

    }




}
