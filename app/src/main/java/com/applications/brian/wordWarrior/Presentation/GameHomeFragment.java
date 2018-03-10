package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.applications.brian.wordWarrior.Logic.ArcadeGame;
import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.SavedGame;
import com.applications.brian.wordWarrior.Logic.ScrabbleGame;
import com.applications.brian.wordWarrior.Logic.TargetGame;
import com.applications.brian.wordWarrior.R;
import com.applications.brian.wordWarrior.Utilities.Util;

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

    // the fragment initialization parameters
    private static final String GAME_PARAM = "GAME PARAMETER";

    //parameters
    private int game;

    //Game Type
    public static final int GAME_COUNT=3;
    public static final int  TARGET=0;
    public static final int  SCRABBLE=1;
    public static final int  ARCADE=2;

    //Game Title
    public static final String NEW_GAME="New Game";
    public static final String LOAD_GAME="Load Game";
    public static final String HELP="Help/Info";
    public static final String HIGH_SCORE="High Scores";

    //sample data
    private List<SavedGame> savedGames;
    private List<Integer> highScores;
    private Controller controller;

    private int color;

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
           switch (game){
               case TARGET:
                   highScores =controller.getHighScores(TargetGame.SCORE_FILE_NAME);
                   savedGames =controller.savedGamesList(TargetGame.SAVE_FILE_NAME);
                   color=Util.TARGET_COLOR;
                   break;
               case SCRABBLE:
                   highScores =controller.getHighScores(ScrabbleGame.SCORE_FILE_NAME);
                   savedGames =controller.savedGamesList(ScrabbleGame.SAVE_FILE_NAME);
                   color= Util.SCRABBLE_COLOR;
                   break;
               case ARCADE:
                   highScores =controller.getHighScores(ArcadeGame.SCORE_FILE_NAME);
                   color=Util.ARCADE_COLOR;
                   break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller=((MainActivity)getActivity()).controller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_game_home,container,false);

        ImageView imageView=(ImageView)view.findViewById(R.id.imageView);
        TextView info=(TextView)view.findViewById(R.id.game_info);
        ListView listView=(ListView)view.findViewById(R.id.scores_list);
        listView.setAdapter(HighScoreDialog.scoresListDetail(highScores,game,getContext()));
        SavedGamesDialog  dialog;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (game){
           case TARGET:
               imageView.setImageResource(R.drawable.target);
               info.setText(R.string.target_info);
               dialog = SavedGamesDialog.newInstance(TargetGame.SAVE_FILE_NAME,controller.savedGamesData(TargetGame.SAVE_FILE_NAME));
               transaction.add(R.id.saved_games_list, dialog).commit();
               break;
           case SCRABBLE:
               imageView.setImageResource(R.drawable.scrabble);
               info.setText(R.string.scrabble_info);
               dialog = SavedGamesDialog.newInstance(ScrabbleGame.SAVE_FILE_NAME,controller.savedGamesData(ScrabbleGame.SAVE_FILE_NAME));
               transaction.add(R.id.saved_games_list, dialog).commit();
               break;
           case ARCADE:
               imageView.setImageResource(R.drawable.arcade);
               info.setText(R.string.arcade_info);
               break;
           default:
               view=inflater.inflate(R.layout.no_items_layout,container,false);
       }


       return view;
    }





}
