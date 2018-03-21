package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by brian on 2018/02/08.
 *
 */

public class TargetSolverDialog extends DialogFragment {

    private static final String ALL_ANSWERS="All Answers - 1000";
    private static final String GREAT_ANSWERS="Great - 750";
    private static final String GOOD_ANSWERS="Good - 500";
    private static final String NINE_LETTER_ANSWERS="9 Letter Word(s) - 500";


    private static final String PURCHASED_ARG="OPTION";
    private TargetFragment targetFragment;


    public static TargetSolverDialog newInstance(String purchasedOption,TargetFragment targetFragment) {

        Bundle args = new Bundle();
        args.putString(PURCHASED_ARG,purchasedOption);
        TargetSolverDialog fragment = new TargetSolverDialog();
        fragment.setArguments(args);
        fragment.targetFragment=targetFragment;
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.solver_dialog_layout,container,false);



        ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,getList()) ;
        GridView gridView=(GridView)view.findViewById(R.id.gridView);
        adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                int lengthDifference =lhs.length()-rhs.length();
                if(lengthDifference!=0)return lengthDifference;
                return lhs.compareTo(rhs);
            }
        });
        gridView.setAdapter(adapter);
        getDialog().setTitle(String.format(Locale.getDefault(),"Results (%d)",adapter.getCount()));
        return view;
    }


    private List<String> getList(){
        List<String> wordSolutions = targetFragment.currentGameSolutions();
        List<String> displaySolutions = new ArrayList<>();
        int[] targets=getTargets(wordSolutions);
        switch (getArguments().getString(PURCHASED_ARG,ALL_ANSWERS)){
            case ALL_ANSWERS:
                displaySolutions.addAll(wordSolutions);
                break;
            case GOOD_ANSWERS:
                for(int i=0;i<targets[0];i++){
                    displaySolutions.add(wordSolutions.get(i));
                }
                break;
            case GREAT_ANSWERS:
                for(int i=0;i<targets[1];i++){
                    displaySolutions.add(wordSolutions.get(i));
                }
                break;
            case NINE_LETTER_ANSWERS:
                for(String word:wordSolutions){
                    if(word.length()==9) displaySolutions.add(word);
                }
                break;
        }
        return displaySolutions;
    }

    public static Map<String,Integer> getPurchaseOptionsCost(){
        Map<String,Integer> map=new HashMap<>();
        map.put(ALL_ANSWERS,1000);
        map.put(GREAT_ANSWERS,750);
        map.put(GOOD_ANSWERS,500);
        map.put(NINE_LETTER_ANSWERS,500);
        return map;
    }

    private  int[] getTargets(List<String> answers){
        int[] targets=new int[3];
        targets[0]= (int) Math.round((0.33)*answers.size());
        targets[1]=(int) Math.round((0.67)*answers.size());
        targets[2]=answers.size();
        return targets;
    }






}
