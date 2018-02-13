package com.applications.brian.targetword.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.applications.brian.targetword.Logic.TargetGame;
import com.applications.brian.targetword.R;

import java.util.List;

/**
 * Created by brian on 2018/02/12.
 *
 */

public class LevelPickerDialog extends DialogFragment {

    private OnFragmentInteractionListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.level_picker_fragment,container,false);
        final List<String> levels= TargetGame.Levels();
        SeekBar seekBar=(SeekBar)view.findViewById(R.id.seekBar);
        final TextView textView=(TextView)view.findViewById(R.id.levelTextView);
        textView.setText(levels.get(0));
        seekBar.setMax(levels.size());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(levels.get(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Button button=(Button)view.findViewById(R.id.ok_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLevelSelect(textView.getText().toString());
                getDialog().dismiss();
            }
        });
        getDialog().setTitle("Pick A Level");



        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
