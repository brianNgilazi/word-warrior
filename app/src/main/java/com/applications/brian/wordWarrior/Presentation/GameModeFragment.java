package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Game Modes.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GameModeFragment extends Fragment {
    
    
    private OnFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameModeFragment() {
    }

    
    @SuppressWarnings("unused")
    public static GameModeFragment newInstance() {
        return new GameModeFragment();
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_mode_fragment, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyGameModeRecyclerViewAdapter(getItems(), mListener));
        }
        return view;
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

    
    
    static class GameMode{

        static final String STORY_MODE="STORY MODE";
        //static final String CONTINUE_STORY_MODE="CONTINUE STORY MODE";
        static final String ARCADE_MODE="ARCADE MODE";
        static final String TOOLS ="UTILITIES";
        
        private final String detail;
        private final String title;
        
        GameMode(String title, String detail){
            this.title=title;
            this.detail=detail;
        }

        String getDetail() {
            return detail;
        }

        String getTitle() {
            return title;
        }
        
      
    }

    private static List<GameMode> getItems(){
        List<GameMode> list=new ArrayList<>(4);
        list.add(new GameMode(GameMode.ARCADE_MODE,"Test your skills in a game of your choice"));
        //list.add(new GameMode(GameMode.CONTINUE_STORY_MODE,"Continue your journey from the last save point."));
        list.add(new GameMode(GameMode.STORY_MODE,"Embark on a fun (but ultimately meaningless in the greater scheme of things) adventure"));
        list.add(new GameMode(GameMode.TOOLS,"General word related tools"));
        return list;
    }

    /**
     * {@link RecyclerView.Adapter} that can display a and makes a call to the
     * specified {@link OnFragmentInteractionListener}.
     * TODO: Replace the implementation with code for your data type.
     */
    static class MyGameModeRecyclerViewAdapter extends RecyclerView.Adapter<MyGameModeRecyclerViewAdapter.ViewHolder> {

        private final List<GameMode> modes;
        private final OnFragmentInteractionListener mListener;

        MyGameModeRecyclerViewAdapter(List<GameMode> modes, OnFragmentInteractionListener listener) {
            this.modes = modes;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.game_mode_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mode = modes.get(position);
            holder.title.setText(holder.mode.getTitle());
            holder.details.setText(modes.get(position).getDetail());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return modes.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView title;
            private final TextView details;
            private GameMode mode;

            private ViewHolder(View view) {
                super(view);
                mView = view;
                title = (TextView) view.findViewById(R.id.game_mode_title);
                details = (TextView) view.findViewById(R.id.game_mode_detail);
            }

            @Override
            public String toString() {
                return super.toString() + mode.getTitle();
            }
        }
    }
}
