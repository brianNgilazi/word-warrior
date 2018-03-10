package com.applications.brian.wordWarrior.Presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.applications.brian.wordWarrior.Logic.ArcadeGame;
import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.ScrabbleGame;
import com.applications.brian.wordWarrior.Logic.TargetGame;
import com.applications.brian.wordWarrior.R;



public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {


    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LoadingParallelTask task=new LoadingParallelTask(this);
        task.execute();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(controller!=null)controller.saveProfile();
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager=getSupportFragmentManager();
        int stackCount=manager.getBackStackEntryCount();
        if(stackCount>1) dashboard();
        else{
            exitDialog();
        }
    }

    void exitDialog(){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(
                this);
        aBuilder.setMessage("Are you sure you want to give up on this like you give up on everything else?").
                setTitle("Exit")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }

   /* private void solver(String anagramWord){
        SolverFragment solverFragment = SolverFragment.newInstance(anagramWord);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, solverFragment);
        if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void viewHelp(){
        Intent intent=new Intent(this,HelpActivity.class);
        startActivity(intent);

    }*/

    private void startTarget(boolean load, String loadData, String level){
       TargetFragment targetFragment=TargetFragment.newInstance(load,loadData,level);
       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       transaction.replace(R.id.fragmentContainer,targetFragment);
        //if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
       transaction.addToBackStack(null);
       transaction.commit();

   }

    private void startScrabble(boolean load, String loadData){
        ScrabbleFragment scrabbleFragment=ScrabbleFragment.newInstance(load,loadData);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,scrabbleFragment);
        //if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void startArcadeGame(){
        ArcadeGameFragment arcadeGameFragment= ArcadeGameFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,arcadeGameFragment);
        //if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.addToBackStack(null);
        transaction.commit();

    }


    void selectLevel(){
        DialogFragment dialogFragment=new LevelPickerDialog();
        dialogFragment.show(getSupportFragmentManager(),null);
    }

    void savedGamesDialog(String fileName){
        SavedGamesDialog  dialog = SavedGamesDialog.newInstance(fileName,controller.savedGamesData(fileName));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, dialog);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void aboutPage(){
        Fragment about=AboutFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, about);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    void highScoresDialog(String fileName){
        HighScoreDialog  dialog = HighScoreDialog.newInstance(fileName);
        dialog.show(getSupportFragmentManager(),null);
    }



    void dashboard(){
        Dashboard  dashboard = Dashboard.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, dashboard);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    


    @Override
    public void loadGame(String fileName,String string, String level) {
        switch (fileName){
            case TargetGame.SAVE_FILE_NAME:
                startTarget(true,string,level);
                break;
            case ScrabbleGame.SAVE_FILE_NAME:
                startScrabble(true,string);
                break;

        }

    }

    @Override
    public void onLevelSelect(String mItem) {
        startTarget(false,null,mItem);
    }

    @Override
    public void onGameOptionSelect(int game, String mItem) {
        switch (game){

            case GameHomeFragment.TARGET:
                if(mItem.equals(GameHomeFragment.NEW_GAME)){
                    selectLevel();
                    return;
                }
                if(mItem.equals(GameHomeFragment.LOAD_GAME)){
                    savedGamesDialog(TargetGame.SAVE_FILE_NAME);
                    return;
                }
                if(mItem.equals(GameHomeFragment.HIGH_SCORE)){
                    highScoresDialog(TargetGame.SCORE_FILE_NAME);
                    return;
                }
                break;

            case GameHomeFragment.SCRABBLE:
                if(mItem.equals(GameHomeFragment.NEW_GAME)){
                    startScrabble(false,null);
                    return;
                }
                if(mItem.equals(GameHomeFragment.LOAD_GAME)){
                    savedGamesDialog(ScrabbleGame.SAVE_FILE_NAME);
                    return;
                }
                if(mItem.equals(GameHomeFragment.HIGH_SCORE)){
                    highScoresDialog(ScrabbleGame.SCORE_FILE_NAME);
                    return;
                }
                break;

            case GameHomeFragment.ARCADE:
                if(mItem.equals(GameHomeFragment.NEW_GAME)){
                    startArcadeGame();
                    return;
                }
                if(mItem.equals(GameHomeFragment.HIGH_SCORE)){
                    highScoresDialog(ArcadeGame.SCORE_FILE_NAME);
                    //return;
                }

        }
    }

    @Override
    public void startGame(String string) {
        switch (string){
            case Dashboard.Target:
                selectLevel();
                break;
            case Dashboard.Scrabble:
                startScrabble(false,null);
                break;
            case Dashboard.ArcadeGame:
                startArcadeGame();
                break;
        }
    }


}
