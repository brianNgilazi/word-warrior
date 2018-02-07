package com.applications.brian.targetword;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import Logic.Dictionary;
import Logic.Word;

/**
 * Created by Brian on 19-Jan-17.
 *
 */
class ParallelTask extends AsyncTask<Dictionary,Void, Word> {

    private ProgressDialog progress;
    private Word target;



    ParallelTask(Context context1,Word aWord){

        progress=new ProgressDialog(context1);
        target=aWord;

    }

    @Override
    protected Word doInBackground(Dictionary... params) {

        target= new Word(params[0]);
        return target;
    }

    @Override
    protected void onPostExecute(Word word) {
        super.onPostExecute(word);
        if(progress.isShowing())progress.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress.setMessage("Loading...");
        progress.show();

    }
}
