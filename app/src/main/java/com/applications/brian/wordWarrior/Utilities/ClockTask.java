package com.applications.brian.wordWarrior.Utilities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by brian on 2018/03/08.
 */
public class ClockTask {
    private TextView timeTextView;
    private Activity activity;
    private Time time;
    private HelperTask helperTask;
    private HelperThread helper;
    private volatile boolean active;
    private volatile boolean running;


    public ClockTask(TextView textView, Activity activity) {
        timeTextView = textView;
        time = new Time();
        textView.setText(time.stringValue());
        running = false;
        active = false;
        this.activity = activity;
        helper = new HelperThread();
        helperTask = new HelperTask();
    }

    public void start(TextView textView) {

        if (textView != timeTextView) timeTextView = textView;
        if (!active) {
            Log.d("Timer", "brand new start called");
            active = true;
            running = true;
            helper.start();
            /*helperTask.cancel(true);
            helperTask=new HelperTask();
            helperTask.execute();*/

            return;
        }
        Log.d("Timer", "Timer resumed");
        running = true;
    }

    public void pause() {
        running = false;
        Log.d("Timer", "pause called");
    }

    public void stopAndReset() {
        Log.d("Timer", "Reset called");
        time.reset();
        timeTextView.setText(time.stringValue());
        running = false;
    }

    public void stop() {
        Log.d("Timer", "Stop Called");
        //helperTask.cancel(true);
        running = false;
        active = false;
        try {
            helper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private class HelperTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (active) {
                while (running) {
                    time.tick();
                    SystemClock.sleep(1000);
                    publishProgress();
                }
            }
            Log.d("Timer", "do in background done");
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            timeTextView.setText(time.stringValue());
        }
    }

    private class HelperThread extends Thread {

        @Override
        public void run() {
            while (active) {
                while (running) {
                    time.tick();
                    SystemClock.sleep(1000);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeTextView.setText(time.stringValue());
                        }
                    });
                }
            }
            Log.d("Timer", "do in background done");

        }
    }

}
