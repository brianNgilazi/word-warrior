package com.applications.brian.wordWarrior.Presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.brian.wordWarrior.Logic.Controller;
import com.applications.brian.wordWarrior.R;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    public static final String Target = "Target";
    public static final String Scrabble = "Scrabble";
    public static final String ArcadeGame = "Arcade Game";
    Controller controller;
    private Toolbar toolbar;
    private DrawerLayout drawer;


    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Dashboard.
     */

    public static Dashboard newInstance() {
        return new Dashboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = ((MainActivity) getActivity()).controller;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dashboard, container, false);


        toolbar = (Toolbar) view.findViewById(R.id.toolbar);


        //Navigation Drawer
        drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                }
                drawer.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        dashboard();
        return view;
    }

    void dashboard() {
        Home dashboard = Home.newInstance(toolbar);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, dashboard);
        transaction.commit();
    }

    void gameHome(int game) {
        GameHomeFragment home = GameHomeFragment.newInstance(game, toolbar);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, home);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void solver() {
        SolverFragment solverFragment=SolverFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, solverFragment);
        transaction.commit();
    }

    void resetDialog() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(
                getContext());
        aBuilder.setMessage("Resetting your profile will fortunately get rid of your pathetic stats. But unfortunately it will also clear all your data.\nAre you sure you want to continue?").
                setTitle("Reset")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        controller.resetProfile();
                        //Stats
                        View view=getView();
                        TextView points = (TextView) view.findViewById(R.id.pointsText);
                        points.setText(String.format(Locale.getDefault(), "Points: %d", controller.getProfilePoints()));
                        TextView played = (TextView) view.findViewById(R.id.playedText);
                        played.setText(String.format(Locale.getDefault(), "Played: %d", controller.getPlayedGames()));
                        TextView rate = (TextView) view.findViewById(R.id.winText);
                        rate.setText(String.format(Locale.getDefault(), "Win Rate: %.1f%%", controller.getWinRate()));
                    }
                })
                .setNegativeButton("Never Mind", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = aBuilder.create();
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dashboard:
                ((MainActivity) getActivity()).dashboard();
                break;
            case R.id.target:
                toolbar.setTitle("Target");
                gameHome(GameHomeFragment.TARGET);
                break;
            case R.id.scrabble:
                toolbar.setTitle("Scrabble");
                gameHome(GameHomeFragment.SCRABBLE);
                break;
            case R.id.arcade:
                toolbar.setTitle("Arcade");
                gameHome(GameHomeFragment.ARCADE);
                break;
            case R.id.nav_exit:
                ((MainActivity) getActivity()).exitDialog();
                break;
            case R.id.nav_about:
                ((MainActivity) getActivity()).aboutPage();
                break;
            case R.id.reset_profile:
                resetDialog();
                break;
            case R.id.solver_menu_item:
                drawer.closeDrawers();
                solver();
                break;
            default:
                Toast.makeText(getContext(), "Item not available", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}
