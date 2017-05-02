package com.applications.brian.targetword;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnagramFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnagramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnagramFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<String> dictionary;
    private List<String> solutions;
    private ArrayAdapter<String> adapter;

    private OnFragmentInteractionListener mListener;

    public AnagramFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnagramFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnagramFragment newInstance(String param1, String param2) {
        AnagramFragment fragment = new AnagramFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        solutions= new ArrayList<>();
        adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,solutions);
        dictionary=((MainActivity)getActivity()).dictionary.allWords();
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
        ListView listView=((ListView)view.findViewById(R.id.listView));
        assert(listView!=null);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSolutions(v);
            }
        });
        RadioGroup option=(RadioGroup)findViewById(R.id.radioGroup);
        option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(((RadioButton)findViewById(checkedId)).getText()==getString(R.string.anagram)){
                    findViewById(R.id.centerLetter).setVisibility(View.VISIBLE);
                    findViewById(R.id.centerLetterLabel).setVisibility(View.VISIBLE);

                }
                else {
                    findViewById(R.id.centerLetter).setVisibility(View.INVISIBLE);
                    findViewById(R.id.centerLetterLabel).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

  /*  // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public View findViewById(int id){
        return getActivity().findViewById(id);

    }

    public void getSolutions(View view){

        EditText input=(EditText)findViewById(R.id.editText);

        assert(input!=null);
        String pattern= input.getText().toString().toLowerCase();
        adapter.clear();
        RadioButton mode=(RadioButton) findViewById(R.id.anagramButton);
        assert mode!=null;
        HelperThread help;

        if(mode.isChecked()){
            char center=' ';
            TextView centerText=(TextView)findViewById(R.id.centerLetter);
            if(centerText.getText().length()<=0){
                Toast toast=Toast.makeText(getContext(), "No center letter added. All anagrams will be displayed", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
            else center=centerText.getText().toString().charAt(0);
            help=new HelperThread(pattern,center,(ArrayList<String>)solutions,dictionary, HelperThread.HelpType.ANAGRAMS);
        }
        else{
            help=new HelperThread(pattern,(ArrayList<String>)solutions,dictionary, HelperThread.HelpType.CROSSWORD_SOLUTION);
        }

        help.start();
        try {
            help.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collections.sort(solutions);
        int results=solutions.size();
        TextView resultText=(TextView)findViewById(R.id.searchResults);
        resultText.setText(String.format("Results for \'%s\' (%d)",pattern,results));

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
