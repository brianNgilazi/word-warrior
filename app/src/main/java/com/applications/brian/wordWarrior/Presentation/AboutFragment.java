package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.brian.wordWarrior.R;

/**
 * A simple {@link Fragment} subclass.

 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {


    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_about,container,false);
        view.findViewById(R.id.eulaButton).setOnClickListener(this);
        view.findViewById(R.id.creditButton).setOnClickListener(this);
        final Toolbar toolbar=(Toolbar)view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });
        return view;
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.eulaButton:
                // TODO: 2018/03/07 Add eula stuff
                break;
            case R.id.creditButton:
                // TODO: 2018/03/07 Add credit stuff
                break;
        }
    }
}
