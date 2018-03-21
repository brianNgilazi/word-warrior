package com.applications.brian.wordWarrior.Presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

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
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        Log.d("Backstack",""+stackCount);
        if(stackCount>1) super.onBackPressed();
        else{
            exitDialog();
        }
    }

    private void fragmentTransaction(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        Log.d("Backstack",""+getSupportFragmentManager().getBackStackEntryCount());

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



    private void startTarget(boolean load, String loadData, String level){
       fragmentTransaction(TargetFragment.newInstance(load,loadData,level));
   }

    private void startScrabble(boolean load, String loadData){
        fragmentTransaction(ScrabbleFragment.newInstance(load,loadData));
    }

    private void startArcadeGame(){
        fragmentTransaction(ArcadeGameFragment.newInstance());
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
        fragmentTransaction(AboutFragment.newInstance());

    }

    void highScoresDialog(String fileName){
        HighScoreDialog  dialog = HighScoreDialog.newInstance(fileName);
        dialog.show(getSupportFragmentManager(),null);
    }



    void dashboard(){
        Dashboard  dashboard = Dashboard.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, dashboard);
        if(getSupportFragmentManager().getBackStackEntryCount()>1)getSupportFragmentManager().popBackStack();
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
                selectLevel();
                break;

            case GameHomeFragment.SCRABBLE:
                startScrabble(false,null);
                break;

            case GameHomeFragment.ARCADE:
                startArcadeGame();
                break;
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
