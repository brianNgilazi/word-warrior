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

import com.applications.brian.wordWarrior.Logic.Controller;
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
public class SolverFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match



    private List<String> lexiconList;
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
     * @return A new instance of fragment SolverFragment.
     */

    public static SolverFragment newInstance() {
        return new SolverFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        Controller controller = ((MainActivity) getActivity()).controller;
        lexiconList= controller.wordsList();
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

    private void init(View view) {
        resultText = (TextView) view.findViewById(R.id.searchResults);
        input = (EditText) view.findViewById(R.id.editText);
        GridView gridView = ((GridView) view.findViewById(R.id.gridView));
        if (gridView == null) throw new AssertionError();
        gridView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSolutions(input.getText().toString(), SOLUTION_TYPE.ANAGRAM);
            }
        });

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

    private void getSolutions(String pattern, SOLUTION_TYPE solution_type) {
        input.setText(pattern);
        pattern = pattern.toLowerCase();
        adapter.clear();

        switch (solution_type) {
            case TARGET:
                for (String word : lexiconList) {
                    if (TargetGameWord.isAnagram(word, pattern) && word.contains(String.valueOf(pattern.charAt(4))))
                        adapter.add(word);
                }
                break;
            case CROSSWORD:
                break;
            case ANAGRAM:
                for (String word : lexiconList) {
                    if (TargetGameWord.isAnagram(word, pattern)) adapter.add(word);
                }
                break;
        }


        adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                int lengthDifference = lhs.length() - rhs.length();
                if (lengthDifference != 0) return lengthDifference;
                return lhs.compareTo(rhs);
            }
        });
        resultText.setText(String.format(Locale.getDefault(), "Results for \'%s\' (%d)", pattern, adapter.getCount()));
    }



    private enum SOLUTION_TYPE {TARGET, CROSSWORD, ANAGRAM}


}
