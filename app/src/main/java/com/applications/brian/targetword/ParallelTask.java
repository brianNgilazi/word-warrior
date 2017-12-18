package com.applications.brian.targetword;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Brian on 19-Jan-17.
 */
class ParallelTask extends AsyncTask<Dictionary,Void,Word> {

    private ProgressDialog progress;
    private Context context;

    public ParallelTask(Context context1){
        context=context1;
        progress=new ProgressDialog(context1);

    }

    @Override
    protected Word doInBackground(Dictionary... params) {
        return new Word(params[0]);
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
