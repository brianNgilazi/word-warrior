package com.applications.brian.targetword;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import Logic.HelperThread;


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
    private List<String> solutions;
    private String unsolved;
    private ArrayAdapter<String> adapter;
    private OnFragmentInteractionListener mListener;
    private TextView resultText;
    private EditText input;


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
        solutions= new ArrayList<>();
        adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,solutions);
        lexiconList =((MainActivity)getActivity()).controller.getAllWords();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anagram, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);


    }

    private void init(View view){
        resultText=(TextView)view.findViewById(R.id.searchResults);
        input=(EditText)view.findViewById(R.id.editText);
        GridView gridView=((GridView)view.findViewById(R.id.listView));
        if (gridView == null) throw new AssertionError();
        gridView.setAdapter(adapter);
        FloatingActionButton fab=(FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSolutions(input.getText().toString());
            }
        });
        if(unsolved!=null){
            getSolutions(unsolved);
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

    

    private void getSolutions(String pattern){
        pattern=pattern.toLowerCase();
        adapter.clear();
        HelperThread help=new HelperThread(pattern,pattern.charAt(4),solutions, lexiconList, HelperThread.HelpType.ANAGRAMS);

        help.start();
        try {
            help.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collections.sort(solutions);
        resultText.setText(String.format(Locale.getDefault(),"Results for \'%s\' (%d)",pattern,solutions.size()));


    }


}
