package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.TargetGame;
import com.applications.brian.wordWarrior.R;
import com.applications.brian.wordWarrior.Utilities.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by brian on 2018/03/04.
 *
 */

public class HighScoreDialog extends DialogFragment {

    private final static String FILE_NAME_ARG="fileName";
    private String fileName;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Controller controller;

    public HighScoreDialog() {

    }

    public static HighScoreDialog newInstance(String fileName) {

        Bundle args = new Bundle();
        args.putString(FILE_NAME_ARG,fileName);
        HighScoreDialog fragment = new HighScoreDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller=((MainActivity)getActivity()).controller;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.high_scores_layout,container,false);
        listView = (ListView) view.findViewById(R.id.scores);
        fileName=getArguments().getString(FILE_NAME_ARG,"");
        addListDetail();
        getDialog().setTitle("High Scores");
        if(adapter.getCount()==0)return inflater.inflate(R.layout.no_items_layout,container,false);
        return view;
    }

    private void addListDetail(){

        List<Integer> list=controller.getHighScores(fileName);
        List<String> stringList = new ArrayList<>();
        if(!fileName.equals(TargetGame.SCORE_FILE_NAME)) {
            Collections.reverse(list);
            int position = 1;
            for (int score : list) {
                stringList.add(String.format(Locale.getDefault(), "%d. %d", position, score));
                position++;
            }
        }
        else{
            int position = 1;
            for (int score : list) {
                stringList.add(String.format(Locale.getDefault(),"%d. %s",position, Time.secondsToTimerString(score)));
                position++;
            }

        }

        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,stringList);
        listView.setAdapter(adapter);

    }
}
