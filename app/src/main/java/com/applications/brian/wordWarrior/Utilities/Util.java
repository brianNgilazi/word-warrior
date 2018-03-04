package com.applications.brian.wordWarrior.Utilities;

import android.content.Context;

/**
 * Created by brian on 2018/03/04.
 * Utility Class
 */

public class Util {

    public static float pxAsDp(float px, Context context){
        return px/context.getResources().getDisplayMetrics().density;
    }

    public static float dpAspx(float dp, Context context){
        return dp*context.getResources().getDisplayMetrics().density;
    }
}
