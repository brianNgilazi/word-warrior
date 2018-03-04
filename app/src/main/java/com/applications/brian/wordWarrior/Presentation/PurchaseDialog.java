package com.applications.brian.wordWarrior.Presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by brian on 2018/03/02.
 *
 */

public class PurchaseDialog extends DialogFragment implements View.OnClickListener {

    public static final int TARGET_GAME=0;
    public static final int SCRABBLE_GAME=1;
    //public static final int ARCADE_GAME=2;
    private int id;
    private TextView insufficientText;
    private Controller controller;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Map<String, Integer> costMap;
    private Fragment callingFragment;

    public PurchaseDialog() {

    }


    public static PurchaseDialog newInstance(int viewID) {

        Bundle args = new Bundle();
        args.putInt("ID",viewID);
        PurchaseDialog fragment = new PurchaseDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id=getArguments().getInt("ID");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller=((MainActivity)getActivity()).controller;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.purchase_dialog,container,false);
        insufficientText=(TextView)view.findViewById(R.id.insufficentPoints);
        ((TextView)view.findViewById(R.id.currentBalance)).setText(String.format(Locale.getDefault(),"Current points: %d",controller.getProfilePoints()));
        (view.findViewById(R.id.purchaseButton)).setOnClickListener(this);
        (view.findViewById(R.id.cancelButton)).setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.purchases_List);
        addListDetail();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(costMap.get(adapter.getItem(position))>controller.getProfilePoints()){
                    insufficientText.setVisibility(View.VISIBLE);
                    insufficientText.setText(R.string.insufficient_points);
                }
                else insufficientText.setVisibility(View.INVISIBLE);
            }
        });

        return view;

    }

    private void addListDetail(){
        switch (id){
            case TARGET_GAME:
                costMap = TargetSolverDialog.getPurchaseOptionsCost();
                break;
            case SCRABBLE_GAME:
                costMap =ScrabbleSolverDialog.getPurchaseOptionsCost();
                break;
        }
        List<String> list=new ArrayList<>();
        list.addAll(costMap.keySet());
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_single_choice,list);
        listView.setAdapter(adapter);

    }

    public void setCallingFragment(Fragment fragment){
        callingFragment=fragment;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.purchaseButton) {
            int itemIndex=listView.getCheckedItemPosition();
            if(itemIndex<0){
                insufficientText.setVisibility(View.VISIBLE);
                insufficientText.setText(R.string.no_option_selected);
                return;
            }
            String item = adapter.getItem(listView.getCheckedItemPosition());
            if(item==null){
                insufficientText.setText(R.string.no_option_selected);
                return;
            }
            int cost=costMap.get(item);
            if(cost>controller.getProfilePoints()){
               return;
            }
            controller.spendPoints(cost);
            getDialog().cancel();
            switch (id){
                case TARGET_GAME:
                    TargetSolverDialog.newInstance(item).show(getFragmentManager(), null);
                    break;
                case SCRABBLE_GAME:
                    if(callingFragment!=null){
                        ((ScrabbleFragment)callingFragment).searchForLongestWord(item);
                    }
                    break;
            }


        }
        else {
            controller.updatePoints(1000);
            getDialog().cancel();
        }

    }
}
