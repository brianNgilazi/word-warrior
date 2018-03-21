package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.annotation.ColorInt;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.applications.brian.wordWarrior.Logic.ArcadeGame;
import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.R;
import com.applications.brian.wordWarrior.Utilities.Util;

/**
 * Created by brian on 2018/02/13.
 *
 */

public class GameView extends SurfaceView implements Runnable{
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private Thread gameThread=null;
    private Paint paint;
    private Canvas canvas;
    private ArcadeGame game;
    private int maxX;
    private int maxY;
    private Bitmap pauseIcon;
    private Rect pauseButtonRect;
    private Rect resumeTextRect;
    private Rect progressRect;
    int currentTrack=0;
    int[] tracks={R.raw.music,R.raw.music3,R.raw.music2};
    @ColorInt private static final int THEME_COLOR=Color.argb(255,0,100,0);

    MediaPlayer mediaPlayer;
    private Controller controller;


    public GameView(Context context){super(context);}

    public GameView (Context context, Point size, Controller controller){
        super(context);
        this.maxX=size.x;
        this.maxY=size.y;
        this.controller=controller;
        game=new ArcadeGame(context,maxX, maxY, controller);
        surfaceHolder = getHolder();
        paint=new Paint();
        paint.setTypeface(Typeface.MONOSPACE);
        pauseIcon= BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pause_game);
        pauseButtonRect=new Rect(maxX-pauseIcon.getWidth()-50,0,maxX,pauseIcon.getHeight()+50);
        resumeTextRect=new Rect(0,maxY-((2*maxY)/3),maxX,maxY-(maxY/3));
        progressRect=new Rect(0,0,maxX,Math.round(Util.convertDpToPx(56,context)));
        mediaPlayer=MediaPlayer.create(context,R.raw.music);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                currentTrack++;
                if(currentTrack==tracks.length)currentTrack=0;
                mp=MediaPlayer.create(getContext(),tracks[currentTrack]);
                mediaPlayer=mp;
                mp.setOnCompletionListener(this);
                mp.start();
            }
        });
    }


   private void draw(){
       if(surfaceHolder.getSurface().isValid()){
           ArcadeGame.Player player=game.getPlayer();
           canvas=surfaceHolder.lockCanvas();
           canvas.drawColor(Color.BLACK);
           paint.setStyle(Paint.Style.FILL);
           paint.setColor(THEME_COLOR);
           canvas.drawRect(progressRect,paint);
           canvas.drawBitmap(pauseIcon,maxX-pauseIcon.getWidth(),0,paint);

           paint.setStyle(Paint.Style.FILL);
           paint.setColor(Color.WHITE);
           paint.setFakeBoldText(true);

           //progress
           paint.setColor(Color.WHITE);
           paint.setTextSize(Util.convertDpToPx(14,getContext()));
           paint.setTextAlign(Paint.Align.LEFT);
           canvas.drawText(player.getHealth(),paint.getTextSize(),paint.getTextSize(),paint);
           paint.setTextAlign(Paint.Align.CENTER);
           canvas.drawText(player.getPoints(),maxX/2,paint.getTextSize(),paint);
           paint.setTextSize(Util.convertDpToPx(16,getContext()));
           canvas.drawText(player.getCurrentWordProgress(),maxX/2,progressRect.bottom-paint.getTextSize()/2,paint);

           //backgroundItems
           paint.setColor(THEME_COLOR);
           paint.setTextSize(Util.convertDpToPx(12,getContext()));
           for(ArcadeGame.BackgroundItem item: game.getBackgroundItems()){
               canvas.drawText(item.getText(),item.getX(),item.getY(),paint);
           }

           //Character
          canvas.drawBitmap(player.getIcon(), player.getX(), player.getY(),paint);

           //Letters
           paint.setColor(Color.WHITE);
           paint.setStyle(Paint.Style.FILL);
           paint.setFakeBoldText(true);
           //paint.setStrokeWidth(2);
           paint.setTextSize(Util.convertDpToPx(36,getContext()));
           for(ArcadeGame.Letter letter:game.getLetters()){
               //canvas.drawBitmap(letter.getIcon(),letter.getX(),letter.getY(),paint);
               canvas.drawText(letter.getText(),letter.getTextX(),letter.getTextY(),paint);
           }

           //  Obstacles
           for(ArcadeGame.Obstacle obstacle:game.getObstacles()){
               canvas.drawBitmap(obstacle.getIcon(),obstacle.getX(),obstacle.getY(),paint);
           }


           if(game.isGameOver()){
               playing=false;
               controller.updatePoints(game.getGamePoints());
               canvas.drawColor(Color.argb(125,0,0,0));
               paint.setColor(Color.WHITE);
               paint.setTextSize(Util.convertDpToPx(12,getContext()));
               paint.setTextAlign(Paint.Align.LEFT);
               canvas.drawText("Press Here To Restart",maxX/2,(maxY/2),paint);
               canvas.drawBitmap(game.gameOverImage,0,maxY/2,paint);
               paint.setStyle(Paint.Style.FILL);
               paint.setTextAlign(Paint.Align.CENTER);
               paint.setTextSize(Util.convertDpToPx(20,getContext()));
               canvas.drawText(game.getGamePoints()+" Points",maxX/2,(maxY/2)+game.gameOverImage.getHeight()+Util.convertDpToPx(40,getContext()),paint);
               if(game.newHighScore()){
                   canvas.drawText("(New High Score)",maxX/2,(maxY/2)+game.gameOverImage.getHeight()+paint.getTextSize()+Util.convertDpToPx(60,getContext()),paint);
               }
           }

           else if(game.isPaused()){
               playing=false;
               canvas.drawColor(Color.argb(125,0,0,0));
               paint.setColor(Color.WHITE);
               paint.setTextSize(Util.convertDpToPx(12,getContext()));
               paint.setTextAlign(Paint.Align.LEFT);
               canvas.drawText("Press Here To Restart",maxX/2,(maxY/2),paint);
               canvas.drawBitmap(game.pauseImage,0,maxY/2,paint);
           }

           surfaceHolder.unlockCanvasAndPost(canvas);
       }

   }

    @Override
    public void run() {
        while (playing){
            game.update();
            draw();
            control();
        }

    }

    private void control() {
        try{
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void pause(){
        game.pause();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mediaPlayer.pause();
    }

    public  void resume(){
        playing=true;
        gameThread=new Thread(this);
        gameThread.start();
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }
        mediaPlayer.start();


    }

    public void stopGame(){
        playing=false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(mediaPlayer.isPlaying())mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX=Math.round(event.getX());
        int touchY=Math.round(event.getY());

        if(!playing &&game.isGameOver()){
            if(resumeTextRect.contains(touchX,touchY)) {
                game.reset();
                resume();
            }
            return true;
        }

        else if(!playing &&game.isPaused()){
            if(resumeTextRect.contains(touchX,touchY)) {
                game.unPause();
                resume();
            }
            return true;
        }

        if(pauseButtonRect.contains(touchX,touchY) && !game.isGameOver()){
            game.pause();
            pause();
            return true;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                game.stopMoving();
                break;
            case MotionEvent.ACTION_DOWN:
                game.startMoving(touchX);
        }
        return true;
    }



}
