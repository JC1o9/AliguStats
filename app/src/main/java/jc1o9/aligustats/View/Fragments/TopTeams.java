package com.example.jose.aligustats.View.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jose.aligustats.Controller.Constants;
import com.example.jose.aligustats.Controller.TeamDetailsDBHelper;
import com.example.jose.aligustats.Controller.UtilsCheckNet;
import com.example.jose.aligustats.Model.TeamDetails;
import com.example.jose.aligustats.R;
import com.example.jose.aligustats.View.TeamsPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * class handles top teams in the sliding tabs
 * Grabs the top teams from API and passes information
 * to the TeamsPage if user selects a specific team
 * runs from database if user has no connection to the API website
 */
public class TopTeams extends Fragment {
    private ArrayList<TeamDetails> mTeamDetails;
    private TopTeamsAdapter mTopTeamsAdapter;
    private TeamDetailsDBHelper mTeamDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_teams, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        ListView listTeams = (ListView) getActivity().findViewById(R.id.list_teams);

        mTeamDB = new TeamDetailsDBHelper(getActivity());
        mTeamDetails = new ArrayList<TeamDetails>();
        mTopTeamsAdapter = new TopTeamsAdapter(getActivity(), mTeamDetails);

        listTeams.setAdapter(mTopTeamsAdapter);

        new AsyncTeamDetails().execute();

        //Get selected team and pass info to the TeamsPage class
        listTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tempView = (TextView) view.findViewById(R.id.teams_name);
                String name = tempView.getText().toString();

                Intent teamIntent = new Intent(getActivity(), TeamsPage.class);
                for (int i = 0; i < mTeamDetails.size(); i++) {
                    if (name.equals(mTeamDetails.get(i).getName())) {
                        teamIntent.putExtra(Constants.PLAYER_ID, i);
                        teamIntent.putParcelableArrayListExtra(Constants.ARRAY_LIST, mTeamDetails);
                    }
                }

                startActivity(teamIntent);
            }
        });
    }

    public class TopTeamsAdapter extends BaseAdapter {
        private ArrayList<TeamDetails> mTopTeams;
        private Context mContext;

        public TopTeamsAdapter(Context context, ArrayList<TeamDetails> teamDetails) {
            this.mTopTeams = teamDetails;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mTopTeams.size();
        }

        @Override
        public Object getItem(int position) {
            return mTopTeams.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null)
                view = convertView;
            else {
                view = LayoutInflater.from(mContext).inflate(R.layout.fragment_teams_list, null);
            }

            TeamDetails currentTeam = mTopTeams.get(position);

            TextView teamName = (TextView) view.findViewById(R.id.teams_name);
            ImageView logo = (ImageView) view.findViewById(R.id.teams_icon);

            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.sc_typeface));
            teamName.setTypeface(typeface);
            teamName.setText(currentTeam.getName());

            switch (currentTeam.getName()) {
                case "SKT T1":
                    logo.setImageResource(R.drawable.skt1_logo);
                    break;
                case "KT":
                    logo.setImageResource(R.drawable.ktrolster_logo);
                    break;
                case "Liquid":
                    logo.setImageResource(R.drawable.liquid_logo);
                    break;
                case "CJ Entus":
                    logo.setImageResource(R.drawable.cjentus_logo);
                    break;
                case "Samsung":
                    logo.setImageResource(R.drawable.samsung_galaxy_logo);
                    break;
                case "Jin Air":
                    logo.setImageResource(R.drawable.green_wings_logo);
                    break;
                case "SBENU":
                    logo.setImageResource(R.drawable.sbenu_logo);
                    break;
                case "MVP":
                    logo.setImageResource(R.drawable.mvp_logo);
                    break;
            }

            return view;
        }
    }

    /*
    Async Connecting to API and requesting json data
     */
    class AsyncTeamDetails extends AsyncTask<Void, Void, String> {
        ProgressDialog mTeamDialog;
        HttpURLConnection mUrlConnect;

        @Override
        protected void onPreExecute() {
            //set progress dialog
            mTeamDialog = new ProgressDialog(getActivity());
            mTeamDialog.setTitle(getString(R.string.connecting_to));
            mTeamDialog.setMessage(getString(R.string.please_wait));
            mTeamDialog.setProgressStyle(mTeamDialog.STYLE_SPINNER);
            mTeamDialog.setCancelable(false);
            mTeamDialog.show();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {
            String json;
            if (UtilsCheckNet.isInternetAvailable()) {
                try {
                    URL url = new URL(Constants.URL_GET_10TEAMS + Constants.API_KEY);
                    mUrlConnect = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(mUrlConnect.getInputStream()));
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line).append("\n");
                        }
                        reader.close();
                        json = builder.toString();
                    } finally {
                        mUrlConnect.disconnect();
                    }

                    try {

                        //Turns extracted json into data objects for use in other parts of the app
                        JSONObject jsonRootObject = new JSONObject(json);
                        JSONArray jsonObjectArray = jsonRootObject.getJSONArray(getString(R.string.objects));

                        for (int i = 0; i < jsonObjectArray.length(); i++) {
                            JSONObject currentObj = jsonObjectArray.getJSONObject(i);

                            //Grab data from json objects
                            String teamName = currentObj.optString(getString(R.string.teamName));
                            double plScore = currentObj.optDouble(getString(R.string.proleagueScore));
                            double akScore = currentObj.optDouble(getString(R.string.allKillScore));
                            int id = currentObj.optInt(getString(R.string.player_id));

                            //change to percentage
                            plScore = plScore * 100;
                            akScore = akScore * 100;
                            int activeRoster = 0;

                            JSONArray jsonCurrentPlayers = currentObj.getJSONArray("current_players");
                            activeRoster = jsonCurrentPlayers.length();

                            //add database entry and new object
                            mTeamDB.createTeam(new TeamDetails(teamName, plScore, akScore, activeRoster, id));
                            TeamDetails currentTeam = new TeamDetails(teamName, plScore, akScore, activeRoster, id);

                            //add object to list
                            mTeamDetails.add(currentTeam);
                        }
                        return getString(R.string.connected);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return getString(R.string.errorTryAgain);
                    }

                } catch (Exception e) {
                    Log.e(getString(R.string.error), e.getMessage(), e);
                    return getString(R.string.errorTryAgain);
                }
            } else {
                return getString(R.string.failedConnection);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mTeamDialog.dismiss();
            //checks for running the database or updating the adapter
            if (!s.equals(getString(R.string.failedConnection))) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                mTopTeamsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                runDB();
            }
        }
    }

    /*
    Run from database if user has no connection to the API
     */
    private void runDB() {
        mTeamDetails = mTeamDB.getAllTeams();
        mTopTeamsAdapter.mTopTeams = mTeamDetails;
        mTopTeamsAdapter.notifyDataSetChanged();

        //Notify the user there is no existing database
        if (mTeamDetails.size() == 0) {
            Toast.makeText(getActivity(), R.string.dbEmpty, Toast.LENGTH_LONG).show();
        }else{
        Toast.makeText(getActivity(), R.string.loadedDB, Toast.LENGTH_LONG).show();
        }
    }
}