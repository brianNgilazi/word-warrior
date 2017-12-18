package com.applications.brian.targetword;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.InputStream;

/**
 * Created by Brian on 19-Jan-17.
 */
public class ParallelTask extends AsyncTask<Dictionary,Void,Word> {

    ProgressDialog progress;
    Context context;

    public ParallelTask(Context context1){
        context=context1;
        progress=new ProgressDialog(context1);

    }

    @Override
    protected Word doInBackground(Dictionary... params) {
        Word targetWord =new Word(params[0]);
        return targetWord;
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
