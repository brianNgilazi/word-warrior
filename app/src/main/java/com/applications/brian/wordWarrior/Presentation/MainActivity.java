package com.applications.brian.wordWarrior.Presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.R;



public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {


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
    public void onBackPressed() {
        FragmentManager manager=getSupportFragmentManager();
        int stackCount=manager.getBackStackEntryCount();
        if(stackCount>1)super.onBackPressed();
        else{
            exitDialog();
        }
    }

    private void exitDialog(){
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


    private void arcadeHome(){
        ArcadeHomeFragment arcadeHomeFragment =  ArcadeHomeFragment.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, arcadeHomeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    private void startTestGame(){
        TestGameFragment testGameFragment=TestGameFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,testGameFragment);
        //if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void loadGameDialog() {
        int savedGames=getPreferences(MODE_PRIVATE).getInt(TargetFragment.SAVE_FILE_NAME,0);
        Log.i("Save Count: On Check",String.format("count= %d",savedGames));
        if(savedGames<1){
            selectLevel();
        }
        else {
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
            aBuilder.setMessage("Load Saved Game??");
            aBuilder.setTitle("Load Game").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadPickerDialog();

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectLevel();
                }
            });


            AlertDialog dialog = aBuilder.create();
            dialog.show();
        }
    }

    private void loadPickerDialog(){
        SavedGamesDialog savedGamesDialog=SavedGamesDialog.newInstance(controller.savedGamesData());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,savedGamesDialog);
        if(getSupportFragmentManager().getBackStackEntryCount()>0)getSupportFragmentManager().popBackStack();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void selectLevel(){
        DialogFragment dialogFragment=new LevelPickerDialog();
        dialogFragment.show(getSupportFragmentManager(),null);
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
            case ArcadeHomeFragment.ArcadeGameItem.TARGET:
                loadGameDialog();
                break;
            case ArcadeHomeFragment.ArcadeGameItem.TEST:
                startTestGame();
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
            case GameModeFragment.GameMode.ARCADE_MODE:
                arcadeHome();
                break;
            default:
                Toast.makeText(this,"Mode Currently Unavailable.",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
