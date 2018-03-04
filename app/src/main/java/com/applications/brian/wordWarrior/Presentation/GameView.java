package com.applications.brian.wordWarrior.Presentation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.applications.brian.wordWarrior.Logic.ArcadeGame;
import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.R;

/**
 * Created by brian on 2018/02/13.
 *
 */

public class GameView extends SurfaceView implements Runnable,View.OnClickListener{
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
    private MediaPlayer mediaPlayer;

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
        pauseIcon= BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pause_game);
        pauseButtonRect=new Rect(maxX-pauseIcon.getWidth()-50,0,maxX,pauseIcon.getHeight()+50);
        resumeTextRect=new Rect(0,(maxY/2),maxX,(maxY/2)+64);
        mediaPlayer=MediaPlayer.create(context,R.raw.music);
        mediaPlayer.setLooping(true);

    }

   private void draw(){
       if(surfaceHolder.getSurface().isValid()){
           ArcadeGame.Player player=game.getPlayer();
           canvas=surfaceHolder.lockCanvas();
           canvas.drawColor(Color.BLACK);
           canvas.drawBitmap(pauseIcon,maxX-pauseIcon.getWidth(),0,paint);

           paint.setStyle(Paint.Style.FILL);
           paint.setColor(Color.WHITE);
           paint.setFakeBoldText(true);

           //progress
           paint.setTextSize(32);//Util.dpAspx(32.0f,getContext()));
           paint.setTextAlign(Paint.Align.CENTER);
           canvas.drawText(player.getTotalScore()+"",paint.getTextSize(),paint.getTextSize(),paint);
           canvas.drawText(player.getProgressDetails(),maxX/2,paint.getTextSize(),paint);

           //backgroundItems
           paint.setStyle(Paint.Style.STROKE);
           paint.setColor(Color.GREEN);
           paint.setFakeBoldText(false);
           paint.setTextSize(20);
           for(ArcadeGame.BackgroundItem item: game.getBackgroundItems()){
               paint.setColor(Color.argb(255,0,139,0));
               canvas.drawText(item.getText(),item.getX(),item.getY(),paint);
           }

           //Character
          canvas.drawBitmap(player.getIcon(), player.getX(), player.getY(),paint);

           //Letters
           paint.setColor(Color.WHITE);
           paint.setStyle(Paint.Style.STROKE);
           paint.setFakeBoldText(true);
           paint.setStrokeWidth(2);
           paint.setTextSize(64);
           for(ArcadeGame.Letter letter:game.getLetters()){
               canvas.drawBitmap(letter.getIcon(),letter.getX(),letter.getY(),paint);
               canvas.drawText(letter.getText(),letter.getTextX(),letter.getTextY(),paint);
           }

           //  Obstacles
           for(ArcadeGame.Obstacle obstacle:game.getObstacles()){
               canvas.drawBitmap(obstacle.getIcon(),obstacle.getX(),obstacle.getY(),paint);
           }


           if(game.isGameOver()){
               playing=false;
               controller.updatePoints(game.getGamePoints());
               paint.setStyle(Paint.Style.STROKE);
               paint.setTextSize(88);
               paint.setColor(Color.RED);
               canvas.drawText("GAME OVER",(maxX/2),maxY/2,paint);
               if(game.newHighScore()){
                   paint.setStyle(Paint.Style.STROKE);
                   paint.setTextSize(72);
                   paint.setColor(Color.YELLOW);
                   canvas.drawText("New High Score",(maxX/2),maxY/2+100,paint);
               }
               paint.setColor(Color.WHITE);
               paint.setTextSize(64);
               canvas.drawText("Touch to restart. Press back to exit.",(maxX/2),(maxY/2)+paint.getTextSize()+100,paint);
               paint.setTextSize(48);
               canvas.drawText(game.getGamePoints()+" point earned",(maxX/2),maxY-150,paint);
           }

           if(game.isPaused()){
               playing=false;
               paint.setStyle(Paint.Style.STROKE);
               paint.setTextSize(88);
               paint.setColor(Color.WHITE);
               canvas.drawText("Paused",(maxX/2),maxY/2,paint);
               paint.setTextSize(64);
               canvas.drawText("Touch to resume. Press back to exit.",(maxX/2),(maxY/2)+paint.getTextSize()+10,paint);
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
        mediaPlayer.pause();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  void resume(){
        playing=true;
        gameThread=new Thread(this);
        gameThread.start();
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getContext(),R.raw.music);
            mediaPlayer.setLooping(true);

        }
        mediaPlayer.start();
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

    @Override
    public void onClick(View v) {
            switch(v.getId()){
                case R.id.quitButton:
                    ((Activity)getContext()).onBackPressed();
                    break;
                case R.id.newGameButton:
                    game.reset();
                    resume();
                    break;
            }

    }


}
