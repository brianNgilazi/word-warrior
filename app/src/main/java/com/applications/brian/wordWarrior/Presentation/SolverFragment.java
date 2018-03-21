package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.applications.brian.wordWarrior.Logic.TargetGameWord;
import com.applications.brian.wordWarrior.R;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SolverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SolverFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WORD_TO_SOLVE_ARG = "wordToSolve";
    


    private List<String> lexiconList;
    private String unsolved;
    private ArrayAdapter<String> adapter;
    private OnFragmentInteractionListener mListener;
    private TextView resultText;
    private EditText input;

    private enum SOLUTION_TYPE {TARGET,CROSSWORD,ANAGRAM}


    public SolverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param wordToSolve a String of jumbled characters to be solved into anagrams
     * @return A new instance of fragment SolverFragment.
     */

    public static SolverFragment newInstance(String wordToSolve) {
        SolverFragment fragment = new SolverFragment();
        Bundle args = new Bundle();
        args.putString(WORD_TO_SOLVE_ARG, wordToSolve);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            unsolved = getArguments().getString(WORD_TO_SOLVE_ARG,null);
        }
        adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        //lexiconList =((MainActivity)getActivity()).controller.;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_solver, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);


    }

    private void init(View view){
        resultText=(TextView)view.findViewById(R.id.searchResults);
        input=(EditText)view.findViewById(R.id.editText);
        GridView gridView=((GridView)view.findViewById(R.id.gridView));
        if (gridView == null) throw new AssertionError();
        gridView.setAdapter(adapter);
        FloatingActionButton fab=(FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSolutions(input.getText().toString(),SOLUTION_TYPE.ANAGRAM);
            }
        });
        if(unsolved!=null){
            getSolutions(unsolved,SOLUTION_TYPE.TARGET);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    

    private void getSolutions(String pattern,SOLUTION_TYPE solution_type){
        input.setText(pattern);
        pattern=pattern.toLowerCase();
        adapter.clear();
        switch (solution_type){

            case TARGET:
                for (String word:lexiconList) {
                    if(TargetGameWord.isAnagram(word,pattern) && word.contains(String.valueOf(pattern.charAt(4)))) adapter.add(word);
                }
                break;
            case CROSSWORD:
                break;
            case ANAGRAM:
                for (String word:lexiconList) {
                    if(TargetGameWord.isAnagram(word,pattern)) adapter.add(word);
                }
                break;
        }


        adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                int lengthDifference =lhs.length()-rhs.length();
                if(lengthDifference!=0)return lengthDifference;
                return lhs.compareTo(rhs);
            }
        });
        resultText.setText(String.format(Locale.getDefault(),"Results for \'%s\' (%d)",pattern,adapter.getCount()));
    }




}
