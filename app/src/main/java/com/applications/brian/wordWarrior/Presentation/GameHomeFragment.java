package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.brian.wordWarrior.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameHomeFragment extends Fragment {

    // the fragment initialization parameters
    private static final String GAME_PARAM = "GAME PARAMETER";

    //parameters
    private int game;

    //Game Type
    public static final int GAME_COUNT=3;
    public static final int  TARGET=0;
    public static final int  SCRABBLE=1;
    public static final int  ARCADE=3;



    public GameHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param game Parameter 1.
     * @return A new instance of fragment GameHomeFragment.
     */

    public static GameHomeFragment newInstance(int game) {
        GameHomeFragment fragment = new GameHomeFragment();
        Bundle args = new Bundle();
        args.putInt(GAME_PARAM, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            game = getArguments().getInt(GAME_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       switch (game){
           case TARGET:
               return inflater.inflate(R.layout.target_home,container,false);
           case SCRABBLE:
               return inflater.inflate(R.layout.scrabble_home,container,false);
           case ARCADE:
               return inflater.inflate(R.layout.arcade_home,container,false);
       }
       return null;
    }


}
