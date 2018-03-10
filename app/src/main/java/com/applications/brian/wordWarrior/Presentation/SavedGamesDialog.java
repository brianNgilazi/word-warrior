package com.applications.brian.wordWarrior.Presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.Logic.ScrabbleGame;
import com.applications.brian.wordWarrior.Logic.TargetGame;
import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by brian on 2018/02/08.
 *
 */

public class SavedGamesDialog extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        List<String> savedGames=getArguments().getStringArrayList("LIST");
        if(savedGames==null || savedGames.size()==0)return inflater.inflate(R.layout.no_items_layout,container,false);
        View layout=inflater.inflate(R.layout.saved_games_layout,container,false);

        RecyclerView recyclerView =(RecyclerView)layout.findViewById(R.id.savedGameRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        String fileName=getArguments().getString("FILENAME","");
        if(fileName.equals(TargetGame.SAVE_FILE_NAME)){
            TargetSavedGameAdapter adapter = new TargetSavedGameAdapter(savedGames, (MainActivity) getActivity(), (((MainActivity) getActivity()).controller));
            recyclerView.setAdapter(adapter);
        }
        else if(fileName.equals(ScrabbleGame.SAVE_FILE_NAME)){
            ScrabbleSavedGameAdapter adapter = new ScrabbleSavedGameAdapter(savedGames, (MainActivity) getActivity(), (((MainActivity) getActivity()).controller));
            recyclerView.setAdapter(adapter);
        }

        return layout;
    }

    public static SavedGamesDialog newInstance(String fileName,List<String> list) {

        Bundle args = new Bundle();
        args.putStringArrayList("LIST",(ArrayList<String>)list);
        args.putString("FILENAME",fileName);
        SavedGamesDialog fragment = new SavedGamesDialog();
        fragment.setArguments(args);
        return fragment;
    }




    /**
     * Created by brian on 2018/02/05.
     *
     */
    static class TargetSavedGameAdapter extends RecyclerView.Adapter<TargetSavedGameAdapter.TargetSavedGameHolder> {

        private final List<TargetGame.TargetSavedGame> targetSavedGames;
        private final OnFragmentInteractionListener listener;
        private final Controller controller;

        TargetSavedGameAdapter(List<String> data, OnFragmentInteractionListener listener, Controller controller){
            targetSavedGames =new ArrayList<>();
            for(String s:data){
                targetSavedGames.add(new TargetGame.TargetSavedGame(s));
            }
            this.controller=controller;
            this.listener=listener;
        }
        @Override
        public TargetSavedGameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context=parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View card = inflater.inflate(R.layout.saved_game_item_layout, parent, false);
            return new TargetSavedGameHolder(card);

        }

        @Override
        public void onBindViewHolder(final TargetSavedGameHolder holder, final int position) {
            holder.targetSavedGame = targetSavedGames.get(position);

            //TODO: set Views
            holder.row1.setText(holder.targetSavedGame.getRow(1));
            holder.row2.setText(holder.targetSavedGame.getRow(2));
            holder.row3.setText(holder.targetSavedGame.getRow(3));
            holder.date.setText(holder.targetSavedGame.getName());
            int score=holder.targetSavedGame.getScore();
            int target=holder.targetSavedGame.getTotal();
            holder.gameProgress.setProgress(score);

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.loadGame(TargetGame.SAVE_FILE_NAME,holder.targetSavedGame.toString(),holder.targetSavedGame.getLevel());
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(
                            v.getContext());
                    builder.setMessage("Are you sure you're ready to let go?")
                            .setPositiveButton("I suppose", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            targetSavedGames.remove(holder.targetSavedGame);
                            controller.deleteSavedGame(holder.targetSavedGame.getName(),TargetGame.SAVE_FILE_NAME);
                            TargetSavedGameAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                            if(targetSavedGames.size()==0){
                                ((MainActivity)listener).onBackPressed();
                            }
                            dialog.dismiss();
                        }
                    })
                            .setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

            });
            holder.fractionFound.setText(String.format(Locale.getDefault(),"%s/%s",score,target));
        }

        @Override
        public int getItemCount() {
            return targetSavedGames.size();
        }


        static class TargetSavedGameHolder extends RecyclerView.ViewHolder {
            final TextView row1;
            final TextView row2;
            final TextView row3;
            final TextView date;
            final TextView fractionFound;
            final View card;
            final ProgressBar gameProgress;
            final ImageView deleteButton;
            TargetGame.TargetSavedGame targetSavedGame;



            TargetSavedGameHolder(View card) {
                super(card);
                this.card=card;
                row1= (TextView) card.findViewById(R.id.row1);
                row2= (TextView) card.findViewById(R.id.row2);
                row3= (TextView) card.findViewById(R.id.row3);
                date = (TextView) card.findViewById(R.id.date);
                fractionFound =(TextView)card.findViewById(R.id.fractionFound);
                gameProgress=(ProgressBar)card.findViewById(R.id.gameProgress);
                deleteButton =(ImageView)card.findViewById(R.id.deleteImageView);
            }




        }
    }

    /**
     * Created by brian on 2018/02/05.
     *
     */
    static class ScrabbleSavedGameAdapter extends RecyclerView.Adapter<ScrabbleSavedGameAdapter.ScrabbleSavedGameHolder> {

        private final List<ScrabbleGame.ScrabbleSavedGame> scrabbleSavedGames;
        private final OnFragmentInteractionListener listener;
        private final Controller controller;

        ScrabbleSavedGameAdapter(List<String> data, OnFragmentInteractionListener listener, Controller controller){
            scrabbleSavedGames =new ArrayList<>();
            for(String s:data){
                scrabbleSavedGames.add(new ScrabbleGame.ScrabbleSavedGame(s));
            }
            this.controller=controller;
            this.listener=listener;
        }
        @Override
        public ScrabbleSavedGameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context=parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View card = inflater.inflate(R.layout.scraable_saved_item, parent, false);
            return new ScrabbleSavedGameHolder(card);

        }

        @Override
        public void onBindViewHolder(final ScrabbleSavedGameHolder holder, final int position) {
            holder.scrabbleSavedGame = scrabbleSavedGames.get(position);


            holder.row1.setText(holder.scrabbleSavedGame.getRow(1));
            holder.row2.setText(holder.scrabbleSavedGame.getRow(2));
            holder.row3.setText(holder.scrabbleSavedGame.getRow(3));
            holder.row4.setText(holder.scrabbleSavedGame.getRow(4));
            holder.row5.setText(holder.scrabbleSavedGame.getRow(5));
            holder.row6.setText(holder.scrabbleSavedGame.getRow(6));
            holder.date.setText(holder.scrabbleSavedGame.getName());
            holder.score.setText(String.valueOf(holder.scrabbleSavedGame.getScore()));




            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.loadGame(ScrabbleGame.SAVE_FILE_NAME,holder.scrabbleSavedGame.toString(),null);
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(
                            v.getContext());
                    builder.setMessage("Are you sure you're ready to let go?")
                            .setPositiveButton("I suppose", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    scrabbleSavedGames.remove(holder.scrabbleSavedGame);
                                    controller.deleteSavedGame(holder.scrabbleSavedGame.getName(),ScrabbleGame.SAVE_FILE_NAME);
                                    ScrabbleSavedGameAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                                    if(scrabbleSavedGames.size()==0){
                                        ((MainActivity)listener).onBackPressed();
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }

            });

        }

        @Override
        public int getItemCount() {
            return scrabbleSavedGames.size();
        }


        static class ScrabbleSavedGameHolder extends RecyclerView.ViewHolder {
            final TextView row1,row2,row3,row4,row5,row6;
            final TextView date;
            final TextView score;
            final View card;
            final ImageView deleteButton;
            ScrabbleGame.ScrabbleSavedGame scrabbleSavedGame;



            ScrabbleSavedGameHolder(View card) {
                super(card);
                this.card=card;
                row1= (TextView) card.findViewById(R.id.row1);
                row2= (TextView) card.findViewById(R.id.row2);
                row3= (TextView) card.findViewById(R.id.row3);
                row4= (TextView) card.findViewById(R.id.row4);
                row5= (TextView) card.findViewById(R.id.row5);
                row6= (TextView) card.findViewById(R.id.row6);
                date = (TextView) card.findViewById(R.id.date);
                score =(TextView)card.findViewById(R.id.score);
                deleteButton =(ImageView)card.findViewById(R.id.deleteImageView);
            }




        }
    }
}
