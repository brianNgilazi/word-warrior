package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.applications.brian.wordWarrior.R;

import java.util.Comparator;
import java.util.Locale;

/**
 * Created by brian on 2018/02/08.
 *
 */

public class SolverDialog extends DialogFragment {




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.solver_dialog_layout,container,false);
        ((MainActivity)getActivity()).controller.getCurrentWordSolutions();
        ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ((MainActivity)getActivity()).controller.getCurrentWordSolutions());
        GridView gridView=(GridView)view.findViewById(R.id.gridView);
        adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                int lengthDifference =lhs.length()-rhs.length();
                if(lengthDifference!=0)return lengthDifference;
                return lhs.compareTo(rhs);
            }
        });
        gridView.setAdapter(adapter);
        getDialog().setTitle(String.format(Locale.getDefault(),"Results (%d)",adapter.getCount()));
        return view;
    }

}
