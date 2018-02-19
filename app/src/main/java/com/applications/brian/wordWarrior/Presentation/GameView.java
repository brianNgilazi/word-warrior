package com.applications.brian.wordWarrior.Presentation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.applications.brian.wordWarrior.Logic.BackgroundItem;
import com.applications.brian.wordWarrior.Logic.Letter;
import com.applications.brian.wordWarrior.Logic.Obstacle;
import com.applications.brian.wordWarrior.Logic.Player;
import com.applications.brian.wordWarrior.Logic.TestGame;
import com.applications.brian.wordWarrior.R;

import java.util.List;

/**
 * Created by brian on 2018/02/13.
 *
 */

public class GameView extends SurfaceView implements Runnable,View.OnClickListener{
    private SurfaceHolder surfaceHolder;
    volatile boolean playing;
    Thread gameThread=null;
    Paint paint;
    Canvas canvas;
    TestGame game;
    private int maxX;
    private int maxY;
    Bitmap pauseIcon;
    Rect pauseButtonRect;
    DialogFragment gameStoppedDialog;
    private CancelListener cancelListener =new CancelListener();


    public GameView(Context context){
        super(context);

    }
    public GameView (Context context,int maxX, int maxY, List<String> allWords){
        super(context);
        game=new TestGame(context,maxX, maxY, allWords);
        surfaceHolder = getHolder();
        paint=new Paint();
        this.maxX=maxX;
        this.maxY=maxY;
        pauseIcon= BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pause_game);
        pauseButtonRect=new Rect(maxX-pauseIcon.getWidth()+50,0,maxX,pauseIcon.getHeight()+50);

    }

   private void draw(){
       if(surfaceHolder.getSurface().isValid()){
           Player player=game.getPlayer();
           canvas=surfaceHolder.lockCanvas();
           canvas.drawColor(Color.BLACK);
           paint.setColor(Color.WHITE);
           //progress
           paint.setTextSize(40);
           paint.setTextAlign(Paint.Align.CENTER);
           canvas.drawText(player.getTotalScore()+"",paint.getTextSize(),paint.getTextSize(),paint);
           canvas.drawText(player.getProgressDetails(),maxX/2,paint.getTextSize(),paint);
           canvas.drawBitmap(pauseIcon,maxX-pauseIcon.getWidth(),0,paint);


           //backgroundItems
           for(BackgroundItem item: game.getBackgroundItems()){
               paint.setTextSize(20);
               paint.setColor(Color.GREEN);

               canvas.drawText(item.getItem()+"",item.getX(),item.getY(),paint);
           }
            //Character
          canvas.drawBitmap(player.getIcon(), player.getX(), player.getY(),paint);

           //   Letters
           for(Letter letter:game.getLetters()){
               canvas.drawBitmap(letter.getIcon(),letter.getX(),letter.getY(),paint);
               paint.setStyle(Paint.Style.FILL);
               paint.setColor(Color.WHITE);
               paint.setTextSize(56);
               canvas.drawText(letter.getText(),letter.getTextX(),letter.getTextY(),paint);
           }

           //  Obstacles
           for(Obstacle obstacle:game.getObstacles()){
               canvas.drawBitmap(obstacle.getIcon(),obstacle.getX(),obstacle.getY(),paint);
               paint.setStyle(Paint.Style.FILL);
               paint.setColor(Color.WHITE);
               paint.setTextSize(56);
               canvas.drawText(obstacle.getPenalty(),obstacle.getTextX(),obstacle.getTextY(),paint);
           }

           if(game.isGameOver()){
               playing=false;

           }

           surfaceHolder.unlockCanvasAndPost(canvas);
       }

   }

    @Override
    public void run() {
        while (playing && !game.isGameOver() && !game.isPaused()){
            game.update();
            draw();
            control();
        }
        if(game.isGameOver()) {
            gameStoppedDialog = GameOverDialog.newInstance(GameOverDialog.GAME_OVER, GameOverDialog.GAME_OVER_MESSAGE, this, cancelListener);
            gameStoppedDialog.setCancelable(false);
            gameStoppedDialog.show(((MainActivity) getContext()).getSupportFragmentManager(), null);

        }

        else if(game.isPaused()) {
            gameStoppedDialog=GameOverDialog.newInstance(GameOverDialog.PAUSE_GAME,GameOverDialog.PAUSE_MESSAGE,this, cancelListener);
            gameStoppedDialog.show(((MainActivity)getContext()).getSupportFragmentManager(),null);

        }
    }

    private void control() {
        try{
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void pause(){
        playing=false;
        game.pause();
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
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX=Math.round(event.getX());
        int touchY=Math.round(event.getY());
        if(pauseButtonRect.contains(touchX,touchY)){
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
                    gameStoppedDialog.dismiss();
                    ((Activity)getContext()).onBackPressed();
                    gameStoppedDialog.dismiss();
                    break;
                case R.id.newGameButton:
                    game.reset();
                    gameStoppedDialog.dismiss();
                    resume();
                    break;
            }

    }

    class CancelListener{

        void onCancel() {
            if(!playing)GameView.this.resume();
        }

    }

}
