package com.applications.brian.wordWarrior.Presentation;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
interface OnFragmentInteractionListener {

    void loadGame(String fileName, String string, String level);

    void onLevelSelect(String mItem);

    void onGameOptionSelect(int game, String mItem);


    void startGame(String string);
}
