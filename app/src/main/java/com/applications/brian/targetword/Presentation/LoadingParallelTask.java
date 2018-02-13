package com.applications.brian.targetword.Presentation;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;

import com.applications.brian.targetword.Logic.Controller;
import com.applications.brian.targetword.R;

/**
 * Created by Brian on 19-Jan-17.
 *
 */
class LoadingParallelTask extends AsyncTask<Void,Void, Void> {

    private final Context context;



    LoadingParallelTask(Context context){
        this.context=context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        ((MainActivity)context).controller=new Controller(context);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        ((MainActivity)context).selectGameModeDialog();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        LoadingFragment loadingFragment =  LoadingFragment.newInstance();
        FragmentTransaction transaction=((MainActivity)context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, loadingFragment);
        transaction.commit();
    }
}
