package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {


    public static final String Target = "Target";
    public static final String Scrabble = "Scrabble";
    public static final String ArcadeGame = "Arcade Game";
    Controller controller;
    private Toolbar toolbar;


    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Dashboard.
     */

    public static Home newInstance(Toolbar toolbar) {
        Home home = new Home();
        home.toolbar = toolbar;
        return home;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, container, false);
        //Stats
        TextView points = (TextView) view.findViewById(R.id.pointsText);
        points.setText(String.format(Locale.getDefault(), "Points: %d", controller.getProfilePoints()));
        TextView played = (TextView) view.findViewById(R.id.playedText);
        played.setText(String.format(Locale.getDefault(), "Played: %d", controller.getPlayedGames()));
        TextView rate = (TextView) view.findViewById(R.id.winText);
        rate.setText(String.format(Locale.getDefault(), "Win Rate: %.1f%%", controller.getWinRate()));
        //Recycler
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gamesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(new GameItemRecyclerViewAdapter(getGameItems(), (OnFragmentInteractionListener) getContext()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = ((MainActivity) getActivity()).controller;
    }

    void showHome(int game) {
        GameHomeFragment home = GameHomeFragment.newInstance(game, toolbar);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, home);
        transaction.addToBackStack(null);
        transaction.commit();
    }




    private List<GameItem> getGameItems() {
        List<GameItem> list = new ArrayList<>();
        list.add(new GameItem(Target, R.drawable.target, GameHomeFragment.TARGET));
        list.add(new GameItem(Scrabble, R.drawable.scrabble, GameHomeFragment.SCRABBLE));
        list.add(new GameItem(ArcadeGame, R.drawable.bug_invaders, GameHomeFragment.ARCADE));
        return list;
    }


    //Helper Classes
    class GameItemRecyclerViewAdapter extends RecyclerView.Adapter<GameItemRecyclerViewAdapter.ViewHolder> {

        private final List<GameItem> options;
        private final OnFragmentInteractionListener mListener;

        GameItemRecyclerViewAdapter(List<GameItem> options, OnFragmentInteractionListener listener) {
            this.options = options;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dashboard_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final GameItem gameItem = options.get(position);
            holder.title.setText(gameItem.getTitle());
            if (gameItem.getTitle().equals(Scrabble)) {
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            holder.imageView.setImageResource(gameItem.getImage());
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.startGame(gameItem.getTitle());
                }
            });
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHome(gameItem.getGame());
                }
            });
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView title;
            private final ImageView imageView;
            private final ImageView play;


            private ViewHolder(View view) {
                super(view);

                play = (ImageView) view.findViewById(R.id.play_game);
                title = (TextView) view.findViewById(R.id.title);
                imageView = (ImageView) view.findViewById(R.id.imageView);

            }

        }
    }

    private class GameItem {
        private String title;
        private int game;
        private int image;

        private GameItem(String title, int image, int game) {
            this.title = title;
            this.image = image;
            this.game = game;
        }

        public String getTitle() {
            return title;
        }


        int getImage() {
            return image;
        }

        public int getGame() {
            return game;
        }
    }
}
