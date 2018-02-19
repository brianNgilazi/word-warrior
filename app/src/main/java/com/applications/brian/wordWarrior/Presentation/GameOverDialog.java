package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applications.brian.wordWarrior.R;

/**
 * Created by brian on 2018/02/17.
 *
 */

public class GameOverDialog extends DialogFragment {

    public static final String TITLE_ARG="DIALOG TITLE";
    public static final String GAME_OVER="GAME OVER";
    public static final String GAME_OVER_MESSAGE="Wow! You couldn't beat the game? Utterly unsurprising.";
    public static final String PAUSE_GAME="PAUSED";
    public static final String PAUSE_MESSAGE="Press the back button to continue. Or don't. I'm not your boss.";
    public static final String MESSAGE_ARG="MESSAGE";
    static View.OnClickListener listener;
    static GameView.CancelListener cancelListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.pause_dialog_layout,container,false);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        ((TextView)view.findViewById(R.id.titleText)).setText(getArguments().getString(TITLE_ARG));
        ((TextView)view.findViewById(R.id.contentText)).setText(getArguments().getString(MESSAGE_ARG));
        view.findViewById(R.id.newGameButton).setOnClickListener(listener);
        view.findViewById(R.id.quitButton).setOnClickListener(listener);


        return view;
    }


    public static GameOverDialog newInstance(String title, String message,View.OnClickListener aListener,GameView.CancelListener onCancelListener) {

        Bundle args = new Bundle();
        args.putString(TITLE_ARG,title);
        args.putString(MESSAGE_ARG,message);
        GameOverDialog fragment = new GameOverDialog();
        fragment.setArguments(args);
        listener=aListener;
        cancelListener =onCancelListener;
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((GameView.CancelListener)cancelListener).onCancel();
    }
}
