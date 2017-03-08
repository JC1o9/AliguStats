package jc1o9.aligustats.View.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import jc1o9.aligustats.Controller.Constants;
import jc1o9.aligustats.Controller.PlayerDetailsDBHelper;
import jc1o9.aligustats.Controller.UtilsCheckNet;
import jc1o9.aligustats.Model.PlayerDetails;
import jc1o9.aligustats.R;
import jc1o9.aligustats.View.PlayerPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * class which handles top players in sliding tabs
 * performs async then passes a specific selected player
 * from the listview to the PlayerPage class
 */
public class TopPlayers extends Fragment {
    private ArrayList<PlayerDetails> mPlayerDetails;
    private TopPlayersAdapter mTopPlayersAdapter;
    private PlayerDetailsDBHelper mPlayerDB;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_players, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        ListView displayName = (ListView) getActivity().findViewById(R.id.list_home_players);

        mPlayerDB = new PlayerDetailsDBHelper(getActivity());
        mPlayerDetails = new ArrayList<PlayerDetails>();
        mTopPlayersAdapter = new TopPlayersAdapter(getActivity(), mPlayerDetails);

        displayName.setAdapter(mTopPlayersAdapter);

        new AsyncTopPlayersDetails().execute();

        //ItemClickListener to handle when a user selects a top player for more
        //information
        displayName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tempView = (TextView) view.findViewById(R.id.view_home_playerName);
                String name = tempView.getText().toString();

                Intent playerIntent = new Intent(getActivity(), PlayerPage.class);

                for (int i = 0; i < mPlayerDetails.size(); i++) {
                    if (name.equals(mPlayerDetails.get(i).getName())) {
                        playerIntent.putExtra(Constants.PLAYER_ID, i);
                        playerIntent.putParcelableArrayListExtra(Constants.ARRAY_LIST, mPlayerDetails);
                    }
                }
                startActivity(playerIntent);
            }
        });
    }

    /*
    Adapter class for populating the list via information
    acquired through the API
    */
    public class TopPlayersAdapter extends BaseAdapter {
        private ArrayList<PlayerDetails> mTopPlayers;
        private Context mContext;

        public TopPlayersAdapter(Context context, ArrayList<PlayerDetails> name) {
            this.mTopPlayers = name;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mTopPlayers.size();
        }

        @Override
        public Object getItem(int position) {
            return mTopPlayers.get(position);
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
                view = LayoutInflater.from(mContext).inflate(R.layout.home_list_top_players, null);
            }

            PlayerDetails currentName = mTopPlayers.get(position);
            String raceStr = currentName.getRace();

            TextView playerName = (TextView) view.findViewById(R.id.view_home_playerName);
            TextView playerRank = (TextView) view.findViewById(R.id.view_home_pos);
            ImageView race = (ImageView) view.findViewById(R.id.img_home_race);

            //Set custom typeface for player name and rank
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.sc_typeface));
            playerName.setTypeface(typeface);
            playerRank.setTypeface(typeface);
            playerName.setTextColor(Color.BLACK);
            playerRank.setTextColor(Color.BLACK);

            switch (raceStr) {
                case "Z":
                    race.setImageResource(R.drawable.zerg_logo);
                    break;
                case "T":
                    race.setImageResource(R.drawable.terran_logo);
                    break;
                case "P":
                    race.setImageResource(R.drawable.protoss_logo);
                    break;
            }

            playerName.setText(currentName.getName());
            playerRank.setText(String.valueOf(currentName.getRanking()) + ". ");

            return view;
        }
    }

    //Async Connecting to API and requesting json data
    class AsyncTopPlayersDetails extends AsyncTask<Void, Void, String> {
        ProgressDialog mTopPlayerDialog;
        HttpURLConnection mUrlConnect;

        @Override
        protected void onPreExecute() {

            //Set up progress dialog
            mTopPlayerDialog = new ProgressDialog(getActivity());
            mTopPlayerDialog.setTitle(getString(R.string.connecting_to));
            mTopPlayerDialog.setMessage(getString(R.string.please_wait));
            mTopPlayerDialog.setProgressStyle(mTopPlayerDialog.STYLE_SPINNER);
            mTopPlayerDialog.setCancelable(false);
            mTopPlayerDialog.show();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {
            String json;
            if (UtilsCheckNet.isInternetAvailable()) {
                try {
                    URL url = new URL(Constants.URL_GET_10PLAYERS + Constants.API_KEY);
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

                        //Grab array of to player objects
                        JSONObject jsonRootObject = new JSONObject(json);
                        JSONArray jsonObjectArray = jsonRootObject.getJSONArray(getString(R.string.objects));

                        for (int i = 0; i < jsonObjectArray.length(); i++) {
                            JSONObject currentObj = jsonObjectArray.getJSONObject(i);

                            //Grab data from json objects
                            String name = currentObj.optString(getString(R.string.tag));
                            String race = currentObj.optString(getString(R.string.race));
                            String earnings = currentObj.optString(getString(R.string.earnings));
                            String romanizedName = currentObj.optString(getString(R.string.romanizedName));
                            String currentTeam;

                            //Error trap for players with no korean name
                            if (romanizedName.equals(getString(R.string.noRName))) {
                                romanizedName = getString(R.string.not_applicable);
                            }

                            //get player's current team from json arrays and object
                            JSONArray current_teams_obj = currentObj.getJSONArray(getString(R.string.currentTeams));

                            //Error trap for players with no current team
                            if (current_teams_obj.isNull(0)) {
                                currentTeam = getString(R.string.noActiveTeam);
                            } else {
                                JSONObject teamName = current_teams_obj.getJSONObject(0);
                                JSONObject obj = teamName.getJSONObject(getString(R.string.team));
                                currentTeam = obj.optString(getString(R.string.name));
                            }
                            int id = currentObj.optInt(getString(R.string.playerID));

                            //Get current_rating uri to get player's global rank
                            JSONObject current_rating = currentObj.getJSONObject(getString(R.string.currentRating));
                            String resource_uri = current_rating.optString(getString(R.string.resourceUri));
                            String resource_uriFinal = resource_uri + "?";
                            int ranking = 0;

                            try {
                                String jsonRatingStr;
                                URL urlRating = new URL(Constants.URL_GET_RANK + resource_uriFinal + Constants.API_KEY);
                                mUrlConnect = (HttpURLConnection) urlRating.openConnection();
                                try {
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(mUrlConnect.getInputStream()));
                                    StringBuilder builder = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        builder.append(line).append("\n");
                                    }
                                    reader.close();
                                    jsonRatingStr = builder.toString();
                                } finally {
                                    mUrlConnect.disconnect();
                                }
                                try {

                                    //Get Player rank from ranking API
                                    JSONObject jsonRating = new JSONObject(jsonRatingStr);
                                    ranking = jsonRating.optInt(getString(R.string.rank));

                                } catch (Exception e) {
                                    Log.e(getString(R.string.error), e.getMessage(), e);
                                }
                            } catch (Exception e) {
                                Log.e(getString(R.string.error), e.getMessage(), e);
                            }

                            //Add player to database and create new instance object for adapter use
                            mPlayerDB.createPlayer(new PlayerDetails(name, race, romanizedName, earnings, currentTeam, ranking, id));
                            PlayerDetails currentPlayer = new PlayerDetails(name, race, romanizedName, earnings, currentTeam, ranking, id);

                            mPlayerDetails.add(currentPlayer);
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
                return getString(R.string.connection_timeout);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mTopPlayerDialog.dismiss();

            //Run database if no connection otherwise update adapter
            if (!s.equals(getString(R.string.connection_timeout))) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                mTopPlayersAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                runDB();
            }
        }

        /*
        run database if the user has no connection to the aligulac API
        grabs all players from database and updates adapter
         */
        private void runDB() {
            mPlayerDetails = mPlayerDB.getAllPlayers();
            mTopPlayersAdapter.mTopPlayers = mPlayerDetails;
            mTopPlayersAdapter.notifyDataSetChanged();

            //Notify the user there is no existing database
            if (mPlayerDetails.size() == 0) {
                Toast.makeText(getActivity(), R.string.dbEmpty, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), R.string.loadedDB, Toast.LENGTH_LONG).show();
            }
        }

    }
}