package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applications.brian.wordWarrior.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brian on 2018/03/03.
 */

public class ScrabbleSolverDialog extends DialogFragment {

    private static final String LONGEST_ANSWER = "One Word - 250";

    private static final String PURCHASED_ARG = "OPTION";

    public static ScrabbleSolverDialog newInstance(String purchasedOption) {

        Bundle args = new Bundle();
        args.putString(PURCHASED_ARG, purchasedOption);
        ScrabbleSolverDialog fragment = new ScrabbleSolverDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static Map<String, Integer> getPurchaseOptionsCost() {
        Map<String, Integer> map = new HashMap<>();
        map.put(LONGEST_ANSWER, 250);
        return map;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scrabble_solver, container, false);
        ((TextView) view.findViewById(R.id.randomInfoTetView)).setText(getRandomFact());
        return view;
    }

    public void setText(String word) {
        View view = getView();
        ((TextView) view.findViewById(R.id.longestWord)).setText(word);
        ((TextView) view.findViewById(R.id.infoMessage)).setText(R.string.done);
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
    }

    private String getRandomFact() {
        //TODO consider creating util class
        //TODO: add random facts
        return "The longest word in the english language is nearly 190 000 letters and takes up to 3 hours to say.";
    }


}
