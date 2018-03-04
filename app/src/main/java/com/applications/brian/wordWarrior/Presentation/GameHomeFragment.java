package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
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

    //Game Type
    public static final String NEW_GAME="New Game";
    public static final String LOAD_GAME="Load Game";
    public static final String HELP="Help/Info";
    public static final String HIGH_SCORE="High Scores";



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
        View view;
       switch (game){
           case TARGET:
               view= inflater.inflate(R.layout.target_home,container,false);
               break;

           case SCRABBLE:
               view= inflater.inflate(R.layout.scrabble_home,container,false);
               break;

           case ARCADE:
               view= inflater.inflate(R.layout.arcade_home,container,false);
               break;
           default:
               view=inflater.inflate(R.layout.no_items_layout,container,false);
       }

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(new GameOptionRecyclerViewAdapter(getItems(),(OnFragmentInteractionListener)getContext()));

       return view;
    }

    private List<String> getItems(){
        List<String> list=new ArrayList<>();
        list.add(NEW_GAME);
        if(game!=ARCADE) list.add(LOAD_GAME);
        list.add(HIGH_SCORE);
        list.add(HELP);
        return list;
    }

    class GameOptionRecyclerViewAdapter extends RecyclerView.Adapter<GameOptionRecyclerViewAdapter.ViewHolder> {

        private final List<String> options;
        private final OnFragmentInteractionListener mListener;

        GameOptionRecyclerViewAdapter(List<String> options, OnFragmentInteractionListener listener) {
            this.options = options;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.options_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String optionName=options.get(position);
            holder.title.setText(optionName);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null) {
                        mListener.onGameOptionSelect(game,holder.title.getText().toString());

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView title;

            private ViewHolder(View view) {
                super(view);
                mView = view;
                title = (TextView) view.findViewById(R.id.title);

            }

        }
    }



}
