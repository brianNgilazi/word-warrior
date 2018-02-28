package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Use the {@link ArcadeHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArcadeHomeFragment extends Fragment {
    

    private OnFragmentInteractionListener mListener;

    public ArcadeHomeFragment() {
        // Required empty public constructor
    }

    
    public static ArcadeHomeFragment newInstance() {
        return new ArcadeHomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.arcade_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context,3));
            recyclerView.setAdapter(new ArcadeHomeAdapter(getItems(),mListener));
        }

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


    private static List<ArcadeGameItem> getItems(){
        List<ArcadeGameItem> list=new ArrayList<>(4);
        list.add(new ArcadeGameItem(ArcadeGameItem.TARGET,"Create as many 4+ letter words as you can using the letters displayed. Each word must contain the center letter."));
        list.add(new ArcadeGameItem(ArcadeGameItem.TEST,"Check This out y'all"));
        list.add(new ArcadeGameItem(ArcadeGameItem.SCRABBLE,"Scrabble basically. "));
        return list;
    }

    static class ArcadeGameItem{

        static final String TARGET="Target";
        static final String TEST="Test Game";
        public static final String SCRABBLE ="Scrabble?" ;

        private final String title;
        private final String detail;


        ArcadeGameItem(String title, String detail){
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

    /**
     * {@link RecyclerView.Adapter} that can display a {@link ArcadeGameItem} and makes a call to the
     * specified {@link OnFragmentInteractionListener}.
     */
    static class ArcadeHomeAdapter extends RecyclerView.Adapter<ArcadeHomeAdapter.ViewHolder> {

        private final List<ArcadeGameItem> arcadeGameItems;
        private final OnFragmentInteractionListener mListener;

        ArcadeHomeAdapter(List<ArcadeGameItem> items, OnFragmentInteractionListener listener) {
            arcadeGameItems = items;
            mListener = listener;
        }

        @Override
        public ArcadeHomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.arcade_game_item, parent, false);
            return new ArcadeHomeAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ArcadeHomeAdapter.ViewHolder holder, int position) {
            holder.mItem = arcadeGameItems.get(position);
            holder.mTitleView.setText(arcadeGameItems.get(position).getTitle());
            holder.mContentView.setText(arcadeGameItems.get(position).getDetail());
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
            return arcadeGameItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mTitleView;
            final TextView mContentView;
            ArcadeGameItem mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleView = (TextView) view.findViewById(R.id.title);
                mContentView=(TextView) view.findViewById(R.id.detail);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitleView+ "'";
            }
        }
    }

}
