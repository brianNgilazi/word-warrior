package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.brian.wordWarrior.R;

/**
 * Created by brian on 2018/03/22.
 */

public class EulaDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.eula_layout, container, false);
        getDialog().setTitle("EULA");
        return view;
    }
}
