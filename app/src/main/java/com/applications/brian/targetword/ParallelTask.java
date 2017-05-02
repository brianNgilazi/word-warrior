package com.applications.brian.targetword;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.InputStream;

/**
 * Created by Brian on 19-Jan-17.
 */
public class ParallelTask extends AsyncTask<Dictionary,Void,Void> {

    ProgressDialog progress;
    @Override
    protected Void doInBackground(Dictionary... params) {
        params[0].populateList();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
}
