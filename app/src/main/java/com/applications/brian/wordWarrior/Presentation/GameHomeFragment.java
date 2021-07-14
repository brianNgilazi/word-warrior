package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.applications.brian.wordWarrior.Logic.ArcadeGame;
import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.ScrabbleGame;
import com.applications.brian.wordWarrior.Logic.TargetGame;
import com.applications.brian.wordWarrior.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameHomeFragment extends Fragment {

    //Game Type
    public static final int TARGET = 0;
    public static final int SCRABBLE = 1;
    public static final int ARCADE = 2;
    // the fragment initialization parameters
    private static final String GAME_PARAM = "GAME PARAMETER";
    //parameters
    private int game;
    //sample data
    private List<String> savedGames;
    private List<Integer> highScores;
    private Controller controller;
    private Toolbar toolbar;

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

    public static GameHomeFragment newInstance(int game, Toolbar toolbar) {
        GameHomeFragment fragment = new GameHomeFragment();
        Bundle args = new Bundle();
        args.putInt(GAME_PARAM, game);
        fragment.setArguments(args);
        fragment.toolbar = toolbar;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            game = getArguments().getInt(GAME_PARAM);
            switch (game) {
                case TARGET:
                    highScores = controller.getHighScores(TargetGame.SCORE_FILE_NAME);
                    savedGames = controller.savedGamesData(TargetGame.SAVE_FILE_NAME);
                    toolbar.setTitle("Target");
                    break;
                case SCRABBLE:
                    highScores = controller.getHighScores(ScrabbleGame.SCORE_FILE_NAME);
                    savedGames = controller.savedGamesData(ScrabbleGame.SAVE_FILE_NAME);
                    toolbar.setTitle("Scrabble");
                    break;
                case ARCADE:
                    highScores = controller.getHighScores(ArcadeGame.SCORE_FILE_NAME);
                    toolbar.setTitle("Arcade");
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = ((MainActivity) getActivity()).controller;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        toolbar.setTitle("Home");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_experiment, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView info = (TextView) view.findViewById(R.id.game_info);
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_scrolling);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onGameOptionSelect(game, null);
            }
        });
        ListView listView = (ListView) view.findViewById(R.id.scores_list);
        listView.setAdapter(HighScoreDialog.scoresListDetail(highScores, game, getContext()));
        SavedGamesDialog dialog;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (game) {
            case TARGET:
                imageView.setImageResource(R.drawable.target);
                info.setText(R.string.target_info);
                dialog = SavedGamesDialog.newInstance(TargetGame.SAVE_FILE_NAME, savedGames);
                transaction.add(R.id.saved_games_list, dialog).commit();
                break;
            case SCRABBLE:
                imageView.setImageResource(R.drawable.scrabble);
                info.setText(R.string.scrabble_info);
                dialog = SavedGamesDialog.newInstance(ScrabbleGame.SAVE_FILE_NAME, savedGames);
                transaction.add(R.id.saved_games_list, dialog).commit();
                break;
            case ARCADE:
                imageView.setImageResource(R.drawable.bug_invaders);
                info.setText(R.string.arcade_info);
                view.findViewById(R.id.games_card).setVisibility(View.GONE);
                break;
            default:
                view = inflater.inflate(R.layout.no_items_layout, container, false);
        }


        return view;
    }


}
