package com.applications.brian.wordWarrior.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

/**
 * Created by brian on 2018/03/04.
 * Utility Class
 */

public class Util {

    public static int SCRABBLE_COLOR=Color.parseColor("#1976D2");
    public static int TARGET_COLOR=Color.parseColor("#EEEEEE");
    public static int ARCADE_COLOR= Color.parseColor("#4CAF50");
    public static int TOOLBAR_COLOR=Color.parseColor("#212121");


    public static float pxAsDp(float px, Context context){
        return px/context.getResources().getDisplayMetrics().density;
    }

    public static float convertDpToPx(float dp, Context context){
        return dp*context.getResources().getDisplayMetrics().density;
    }

    public static Bitmap resizedBitmap(Bitmap bitmap,int newWidth,int newHeight){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        float scaleWidth=((float)newWidth)/width;
        float scaleHeight=((float)newHeight)/width;

        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);

        Bitmap resized=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,false);
        bitmap.recycle();
        return resized;
        
    }
    
    
    
    
}
