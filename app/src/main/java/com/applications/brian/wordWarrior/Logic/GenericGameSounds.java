package com.applications.brian.wordWarrior.Logic;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.applications.brian.wordWarrior.R;

/**
 * Created by brian on 2018/03/10.
 * Class to manage sounds for non arcade games
 */

public class GenericGameSounds {

    private SoundPool soundPool;
    private int failSoundID;
    private int successSoundID;
    private int targetSoundID;
    private int victorySoundID;
    private int clickSoundID;
    private int purchaseSoundID;
    private float volume;

    //private int highScoreSoundID;

    public GenericGameSounds(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            soundPool = builder.build();
        } else {
            //noinspection deprecation
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        failSoundID = soundPool.load(context, R.raw.fail, 1);
        successSoundID = soundPool.load(context, R.raw.victory, 1);
        victorySoundID = soundPool.load(context, R.raw.level_up, 1);
        targetSoundID = soundPool.load(context, R.raw.target_achieved, 1);
        clickSoundID = soundPool.load(context, R.raw.grid_item_click, 1);
        purchaseSoundID = soundPool.load(context, R.raw.purchase_made, 1);
        volume = 1.0f;
    }


    public void success() {
        soundPool.play(successSoundID, volume, volume, 1, 0, 1.0f);
    }

    public void fail() {
        soundPool.play(failSoundID, volume, volume, 1, 0, 1.0f);
    }

    public void victory() {
        soundPool.play(victorySoundID, volume, volume, 1, 0, 1.0f);
    }

    public void targetReached() {
        soundPool.play(targetSoundID, volume, volume, 1, 0, 1.0f);
    }

    public void click() {
        soundPool.play(clickSoundID, volume, volume, 1, 0, 1.0f);
    }

    public void purchase() {
        soundPool.play(purchaseSoundID, volume, volume, 1, 0, 1.0f);
    }


}
