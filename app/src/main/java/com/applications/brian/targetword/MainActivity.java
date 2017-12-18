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
import android.view.WindowManager;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener {
    Dictionary dictionary;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        home();
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBar = getSupportActionBar();
        if (actionBar == null) throw new AssertionError();
        actionBar.setLogo(R.mipmap.ic_target);
        actionBar.setTitle("Home");
        dictionary=new Dictionary(getInputStream(),9);
    }


    @Override
    public void onFragmentInteraction(View view) {
        switch (view.getId()){
            case R.id.target:
                startTarget();
                break;
            case R.id.help:
                viewHelp();
                break;
            case R.id.crossWordHelp:
                cross();
                break;

        }
    }

    private void home(){
        HomeFragment homeFragment=new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,homeFragment).commit();



    }

    private void startTarget(){
        loadGameDialog();
    }

   public void startTarget(boolean load){

       TargetFragment targetFragment=TargetFragment.newInstance(load,null);
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


    private void cross(){

        AnagramFragment anagramFragment=new AnagramFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,anagramFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        actionBar.setTitle("Solver");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private InputStream getInputStream(){

        InputStream stream=null;
        try {
            stream= getBaseContext().getResources().openRawResource(R.raw.englishwords);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stream;

    }

}
