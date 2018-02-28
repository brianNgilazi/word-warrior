package com.applications.brian.wordWarrior.Presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.ScrabbleGame;
import com.applications.brian.wordWarrior.Logic.TargetGame;
import com.applications.brian.wordWarrior.R;



public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener,View.OnClickListener {


    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        if(stackCount>1) showHome();
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



    /*private void loadPickerDialog(){
        SavedGamesDialog savedGamesDialog=SavedGamesDialog.newInstance(controller.savedGamesData(TargetGame.SAVE_FILE_NAME));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,savedGamesDialog);
        if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.addToBackStack(null);
        transaction.commit();
    }*/

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


    void showHome(){
        HomeFragment  homeFragment = HomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, homeFragment);
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
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.targetNewGame:
                selectLevel();
                break;
            case R.id.targetLoadGame:
                savedGamesDialog(TargetGame.SAVE_FILE_NAME);
                break;
            case R.id.scrabbleNewGame:
                startScrabble(false,null);
                break;
            case R.id.scrabbleLoadGame:
                savedGamesDialog(ScrabbleGame.SAVE_FILE_NAME);
                break;
            case R.id.arcadeNewGame:
                startArcadeGame();
                break;


        }

    }
}
