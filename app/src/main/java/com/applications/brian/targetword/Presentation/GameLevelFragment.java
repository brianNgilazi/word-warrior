package com.applications.brian.targetword.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applications.brian.targetword.Logic.TargetGame;
import com.applications.brian.targetword.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GameLevelFragment extends Fragment {

    
    private OnFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameLevelFragment() {
    }

    
    public static GameLevelFragment newInstance() {
        return new GameLevelFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gamelevel_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new GameLevelRecyclerViewAdapter(getItems(),mListener));
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
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private static class GameLevel{


        private final String title;
        private final int back;
        private final int front;
        

        GameLevel(String title, int b,int f){
            this.title=title;
            back=b;
            front=f;
        }

        int getBackColor() {
            return back;
        }

        int getFrontColor() {
            return front;
        }

        String getTitle() {
            return title;
        }


    }

    private static List<GameLevel> getItems(){
        List<GameLevel> list=new ArrayList<>(4);
        List<String> levels=TargetGame.Levels();

        list.add(new GameLevel(levels.get(0),R.color.white,R.color.black));
        list.add(new GameLevel(levels.get(1),R.color.light_gray,R.color.black));
        list.add(new GameLevel(levels.get(2),R.color.dark_gray,R.color.white));
        list.add(new GameLevel(levels.get(3),R.color.black,R.color.white));
        return list;
    }


    /**
     * {@link RecyclerView.Adapter} that can display a GameLevel and makes a call to the
     * specified {@link OnFragmentInteractionListener}.
     */
    static class GameLevelRecyclerViewAdapter extends RecyclerView.Adapter<GameLevelRecyclerViewAdapter.ViewHolder> {

        private final List<GameLevel> mValues;
        private final OnFragmentInteractionListener mListener;

        GameLevelRecyclerViewAdapter(List<GameLevel> items, OnFragmentInteractionListener listener) {
            mValues = items;
            mListener = listener;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_gamelevel_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            Context context=holder.mIdView.getContext();
            holder.mIdView.setText(mValues.get(position).getTitle());
            holder.mIdView.setTextColor(ContextCompat.getColor(context,holder.mItem.getFrontColor()));
            holder.mIdView.setBackgroundColor(ContextCompat.getColor(context,holder.mItem.getBackColor()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onLevelSelect(holder.mItem.getTitle());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mIdView;
            GameLevel mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mItem.getTitle()+ "'";
            }
        }
    }
}
