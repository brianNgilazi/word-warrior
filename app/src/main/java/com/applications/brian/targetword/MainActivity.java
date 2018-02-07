package com.applications.brian.targetword;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import Logic.Controller;


public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private ActionBar actionBar;
    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar == null) throw new AssertionError();
        actionBar.setTitle("Home");


        controller=new Controller(getResources().openRawResource(R.raw.englishwords));
        home();
    }


    @Override
    public void onFragmentInteraction(View view) {


        try{

            String selected=(String)((TextView)view).getText();
            switch(selected){
                case "Target":
                    startTarget();
                    break;
                case "Help":
                    viewHelp();
                    break;
                case "Anagram Solver":
                    solver(null);
                    break;

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void manageWord(String string) {
        solver(string);
    }

    private void home(){
        HomeFragment homeFragment=  HomeFragment.newInstance(null,null);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,homeFragment).commit();



    }

    private void startTarget(){
        loadGameDialog();
    }

   public void startTarget(boolean load){

       TargetFragment targetFragment=TargetFragment.newInstance(load);
       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       transaction.replace(R.id.fragmentContainer,targetFragment);
       if(getSupportFragmentManager().getBackStackEntryCount()>0) getSupportFragmentManager().popBackStack();
       transaction.addToBackStack(null);
       transaction.commit();
       actionBar.setDisplayHomeAsUpEnabled(true);
       actionBar.setHomeButtonEnabled(true);
       actionBar.setTitle("Target");
   }

    private void loadGameDialog() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(this);
        aBuilder.setMessage("Load Last Saved Game??");
        aBuilder.setTitle("Load Game");
        aBuilder.setIcon(R.mipmap.ic_target);
        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startTarget(true);
            }
        });
        aBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startTarget(false);
            }
        });


        AlertDialog dialog= aBuilder.create();
        dialog.show();
    }


    private void viewHelp(){
        Intent intent=new Intent(this,HelpActivity.class);
        startActivity(intent);
    }


    private void solver(String string){

        SolverFragment solverFragment = SolverFragment.newInstance(string);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, solverFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        actionBar.setTitle("solver");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


}
