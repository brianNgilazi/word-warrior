package com.applications.brian.targetword.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applications.brian.targetword.Logic.Controller;
import com.applications.brian.targetword.Logic.SavedGame;
import com.applications.brian.targetword.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by brian on 2018/02/08.
 *
 */

public class SavedGamesDialog extends Fragment {
    SavedGameAdapter adapter;
    OnFragmentInteractionListener mListener;


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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout=inflater.inflate(R.layout.saved_games_layout,container,false);
        adapter=new SavedGameAdapter(getArguments().getStringArrayList("LIST"),(MainActivity)getActivity(),(((MainActivity) getActivity()).controller));
        RecyclerView recyclerView =(RecyclerView)layout.findViewById(R.id.savedGameRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        return layout;
    }

    public static SavedGamesDialog newInstance(List<String> list) {

        Bundle args = new Bundle();
        args.putStringArrayList("LIST",(ArrayList<String>)list);
        SavedGamesDialog fragment = new SavedGamesDialog();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Created by brian on 2018/02/05.
     *
     */
    static class SavedGameAdapter extends RecyclerView.Adapter<SavedGameAdapter.SavedGameHolder> {

        private List<SavedGame> savedGames;
        private OnFragmentInteractionListener listener;
        private Controller controller;

        SavedGameAdapter(List<String> data, OnFragmentInteractionListener listener, Controller controller){
            savedGames=new ArrayList<>();
            for(String s:data){
                savedGames.add(new SavedGame(s));
            }
            this.controller=controller;
            this.listener=listener;
        }
        @Override
        public SavedGameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context=parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View card = inflater.inflate(R.layout.saved_game_item_layout, parent, false);
            return new SavedGameHolder(card);

        }

        @Override
        public void onBindViewHolder(final SavedGameHolder holder, final int position) {
            holder.savedGame=savedGames.get(position);

            //TODO: set Views
            holder.row1.setText(holder.savedGame.getRow(1));
            holder.row2.setText(holder.savedGame.getRow(2));
            holder.row3.setText(holder.savedGame.getRow(3));
            holder.date.setText(holder.savedGame.getName());
            int score=holder.savedGame.getScore();
            int target=holder.savedGame.getTotal();
            holder.gameProgress.setProgress(score);
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.loadGameData(holder.savedGame.toString(),holder.savedGame.getLevel());
                }
            });
            holder.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                    MenuInflater menuInflater=popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.saved_game_context_menu,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            savedGames.remove(holder.savedGame);
                            controller.delete(holder.savedGame.getName());
                            SavedGameAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                            return true;
                        }
                    });
                    popupMenu.show();
                }

            });
            holder.fractionFound.setText(String.format(Locale.getDefault(),"%s/%s",score,target));
        }

        @Override
        public int getItemCount() {
            return savedGames.size();
        }


        static class SavedGameHolder extends RecyclerView.ViewHolder {
            TextView row1,row2,row3;
            TextView date, fractionFound;
            View card;
            ProgressBar gameProgress;
            ImageView menuButton;
            SavedGame savedGame;



            SavedGameHolder(View card) {
                super(card);
                this.card=card;
                row1= (TextView) card.findViewById(R.id.row1);
                row2= (TextView) card.findViewById(R.id.row2);
                row3= (TextView) card.findViewById(R.id.row3);
                date = (TextView) card.findViewById(R.id.date);
                fractionFound =(TextView)card.findViewById(R.id.fractionFound);
                gameProgress=(ProgressBar)card.findViewById(R.id.gameProgress);
                menuButton=(ImageView)card.findViewById(R.id.menuImageView);
            }




        }
    }
}
