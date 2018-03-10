package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.R;
import com.applications.brian.wordWarrior.Utilities.Util;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private static final String GAME_ARG ="NAME ARG";
    private int game;
    private FragmentStatePagerAdapter adapter;
    private Toolbar toolbar;
    Controller controller;





    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HomeFragment.
     */

    public static HomeFragment newInstance(int game,Toolbar toolbar) {
        Bundle bundle=new Bundle();
        bundle.putInt(GAME_ARG,game);
        HomeFragment fragment= new HomeFragment();
        fragment.setArguments(bundle);
        fragment.toolbar=toolbar;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller=((MainActivity)getActivity()).controller;
        game=getArguments().getInt(GAME_ARG,0);
        adapter=new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return GameHomeFragment.newInstance(GameHomeFragment.TARGET);
                    case 1:
                        return GameHomeFragment.newInstance(GameHomeFragment.SCRABBLE);
                    case 2:
                        return GameHomeFragment.newInstance(GameHomeFragment.ARCADE);
                }
               return null;
            }

            @Override
            public int getCount() {
                return GameHomeFragment.GAME_COUNT;
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_home, container,false);

        final TabLayout scrollIndicator=(TabLayout) view.findViewById(R.id.scrollIndicator);

        //Pager
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        scrollIndicator.setupWithViewPager(pager,true);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case GameHomeFragment.TARGET:
                        toolbar.setTitle("Target Home");
                        toolbar.setBackgroundColor(Util.TOOLBAR_COLOR);
                        break;
                    case GameHomeFragment.SCRABBLE:
                        toolbar.setTitle("Scrabble Home");
                        toolbar.setBackgroundColor(Util.SCRABBLE_COLOR);
                        break;
                    case GameHomeFragment.ARCADE:
                        toolbar.setTitle("Arcade Home");
                        toolbar.setBackgroundColor(Util.ARCADE_COLOR);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        toolbar.setTitle("Target Home");
        toolbar.setBackgroundColor(Util.TOOLBAR_COLOR);
        pager.setCurrentItem(game);
        return view;
    }


}
