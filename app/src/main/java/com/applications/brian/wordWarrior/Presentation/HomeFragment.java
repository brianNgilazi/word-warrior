package com.applications.brian.wordWarrior.Presentation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {



    private ViewPager pager;
    private FragmentStatePagerAdapter adapter;
    private ListView optionsList;
    private TextView profileDetails;
    Controller controller;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HomeFragment.
     */

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller=((MainActivity)getActivity()).controller;

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
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        final TabLayout scrollIndicator=(TabLayout) view.findViewById(R.id.scrollIndicator);
        final Toolbar toolbar=(Toolbar)view.findViewById(R.id.toolbar);

        //Navigation Drawer
        final DrawerLayout drawerLayout=(DrawerLayout)view.findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(Gravity.START)){
                    drawerLayout.closeDrawers();
                }
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        profileDetails=(TextView)view.findViewById(R.id.profileDetails);
        profileDetails.setText(controller.profileInfo());
        optionsList=(ListView)view.findViewById(R.id.drawer_list);
        optionsList.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,optionsList()));
        optionsList.setOnItemClickListener(new DrawerListClickListener());


        //Pager
        pager=(ViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        scrollIndicator.setupWithViewPager(pager,true);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0:
                        toolbar.setTitle("Target");
                        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.dark_gray));
                        break;
                    case 1:
                        toolbar.setTitle("Scrabble");
                        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.darkBlue));
                        break;
                    case 2:
                        toolbar.setTitle("Arcade");
                        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.holo_green_dark));
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        pager.setCurrentItem(0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        pager.setCurrentItem(0);
    }

    private List<String> optionsList(){
        List<String> list=new ArrayList<>();
        list.add("Exit");
        return list;
    }

    private class DrawerListClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String option=optionsList.getAdapter().getItem(position).toString();
            switch (option){
                case "Exit":
                    ((MainActivity)getActivity()).exitDialog();
            }
        }
    }
}
