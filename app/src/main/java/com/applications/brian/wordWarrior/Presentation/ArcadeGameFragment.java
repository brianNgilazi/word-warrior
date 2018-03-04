package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.R;




/**
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArcadeGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArcadeGameFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private GameView gameView;
    private Controller controller;


    public ArcadeGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArcadeGameFragment.
     */

    public static ArcadeGameFragment newInstance() {
        return new ArcadeGameFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller=((MainActivity) getActivity()).controller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Display display=getActivity().getWindowManager().getDefaultDisplay();
        Point size= new Point();
        display.getSize(size);
        gameView=new GameView(getContext(),size,controller);
        ((ViewGroup)view.findViewById(R.id.mainArea)).addView(gameView);
    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        gameView.resume();

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

}
