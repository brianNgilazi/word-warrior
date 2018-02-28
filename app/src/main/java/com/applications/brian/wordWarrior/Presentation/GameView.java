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
import android.widget.Toast;

import com.applications.brian.wordWarrior.Logic.ArcadeGame;
import com.applications.brian.wordWarrior.Logic.BackgroundItem;
import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.Letter;
import com.applications.brian.wordWarrior.Logic.Obstacle;
import com.applications.brian.wordWarrior.Logic.Player;
import com.applications.brian.wordWarrior.R;

import java.util.Locale;

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
    private DialogFragment gameStoppedDialog;

    private Controller controller;
    private final CancelListener cancelListener =new CancelListener();


    public GameView(Context context){
        super(context);

    }
    public GameView (Context context, int maxX, int maxY, Controller controller){
        super(context);
        this.controller=controller;
        game=new ArcadeGame(context,maxX, maxY, controller.getAllWords());
        surfaceHolder = getHolder();
        paint=new Paint();
        this.maxX=maxX;
        this.maxY=maxY;
        pauseIcon= BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pause_game);
        pauseButtonRect=new Rect(maxX-pauseIcon.getWidth()-50,0,maxX,pauseIcon.getHeight()+50);

    }

   private void draw(){
       if(surfaceHolder.getSurface().isValid()){
           Player player=game.getPlayer();
           canvas=surfaceHolder.lockCanvas();
           canvas.drawColor(Color.BLACK);
           paint.setColor(Color.WHITE);
           paint.setFakeBoldText(true);

           //progress
           paint.setTextSize(40);
           paint.setTextAlign(Paint.Align.CENTER);
           canvas.drawText(player.getTotalScore()+"",paint.getTextSize(),paint.getTextSize(),paint);
           canvas.drawText(player.getProgressDetails(),maxX/2,paint.getTextSize(),paint);
           canvas.drawBitmap(pauseIcon,maxX-pauseIcon.getWidth(),0,paint);


           //backgroundItems
           for(BackgroundItem item: game.getBackgroundItems()){

               paint.setStrokeWidth(2);
               canvas.drawLine(0,item.getLinePosition(),maxX,item.getLinePosition(),paint);
           }
            //Character
          canvas.drawBitmap(player.getIcon(), player.getX(), player.getY(),paint);

           //   Letters
           paint.setStyle(Paint.Style.STROKE);
           paint.setStrokeWidth(2);

           for(Letter letter:game.getLetters()){
               paint.setStyle(Paint.Style.STROKE);
               paint.setColor(Color.BLUE);
               canvas.drawCircle(letter.getX(),letter.getY(),64,paint);

               paint.setTextSize(64);
               paint.setStyle(Paint.Style.FILL);
               paint.setColor(Color.WHITE);
               canvas.drawText(letter.getText(),letter.getX(),letter.getY(),paint);

           }

           //  Obstacles
           for(Obstacle obstacle:game.getObstacles()){
               canvas.drawBitmap(obstacle.getIcon(),obstacle.getX(),obstacle.getY(),paint);
               paint.setTextSize(64);
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
            gameStoppedDialog.setCancelable(true);
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

    void pause(){
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

    private void updatePoints(){
        int points=game.getPoints();
        controller.updatePoints(points);
        Toast.makeText(getContext(),String.format(Locale.getDefault(),"%d points earned",points),Toast.LENGTH_SHORT).show();
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
