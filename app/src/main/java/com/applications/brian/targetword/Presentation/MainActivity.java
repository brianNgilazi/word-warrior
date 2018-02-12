package com.applications.brian.targetword.Presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.applications.brian.targetword.Logic.Controller;
import com.applications.brian.targetword.R;



public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {


    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //controller = new Controller(MainActivity.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LoadingParallelTask task=new LoadingParallelTask(this);
        task.execute();
    }



    private void arcadeHome(){
        ArcadeHomeFragment arcadeHomeFragment =  ArcadeHomeFragment.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, arcadeHomeFragment);
        if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void solver(String anagramWord){
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

    }

    public void startTarget(boolean load,String loadData,String level){
       TargetFragment targetFragment=TargetFragment.newInstance(load,loadData,level);
       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       transaction.replace(R.id.fragmentContainer,targetFragment);
        if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
       transaction.addToBackStack(null);
       transaction.commit();

   }

    private void loadGameDialog() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(this);
        aBuilder.setMessage("Load Saved TargetGame??");
        aBuilder.setTitle("Load TargetGame");
        aBuilder.setIcon(R.mipmap.ic_target);
        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadPickerDialog();

            }
        });
        aBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectLevel();
            }
        });


        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }

    private void loadPickerDialog(){
        SavedGamesDialog savedGamesDialog=SavedGamesDialog.newInstance(controller.savedGamesData());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,savedGamesDialog);
        if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void selectLevel(){
        GameLevelFragment gameLevelFragment = GameLevelFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, gameLevelFragment);
        if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.commit();
    }
    
    void selectGameModeDialog(){
        GameModeFragment gameModeFragment = GameModeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, gameModeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    

    //Fragment Interaction Methods
    @Override
    public void onArcadeGameSelected(String gameSelected) {
        switch(gameSelected){
            case "Target":
                loadGameDialog();
                break;

        }

    }

    @Override
    public void loadGameData(String string,String level) {
        startTarget(true,string,level);
    }

    @Override
    public void onLevelSelect(String mItem) {
        startTarget(false,null,mItem);
    }

    @Override
    public void onGameModeSelected(String title) {
        switch (title){
            case GameModeFragment.GameMode.TOOLS:
            case GameModeFragment.GameMode.CONTINUE_STORY_MODE:
            case GameModeFragment.GameMode.STORY_MODE:
                Toast toast=Toast.makeText(this,"Mode Currently Unavailable.",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                break;
            case GameModeFragment.GameMode.ARCADE_MODE:
                arcadeHome();
                break;
        }
    }
}
