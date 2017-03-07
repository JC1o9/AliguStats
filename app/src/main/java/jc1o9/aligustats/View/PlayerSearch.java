package jc1o9.aligustats.View;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jc1o9.aligustats.Controller.Constants;
import jc1o9.aligustats.Controller.UtilsCheckNet;
import jc1o9.aligustats.Model.PlayerDetails;
import jc1o9.aligustats.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Player search class handles all search requests for players
 * uses the aligulac API gets user input player name then searches
 * API for player matching name grabs matching ID and then requests player object
 * based on player for specific details
 *
 * @author JC1o9
 */
public class PlayerSearch extends AppCompatActivity {
    private ArrayList<PlayerDetails> mPlayerDetails;
    private String mPlayerTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_page);

        TextView playerName = (TextView) findViewById(R.id.view_player_playerName);
        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.sc_typeface));
        playerName.setTypeface(typeface);
        mPlayerDetails = new ArrayList<PlayerDetails>();

        handleIntent(getIntent());

        //set back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /*
    handles the input from searchview
     */
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mPlayerTag = intent.getStringExtra(SearchManager.QUERY);

            //remove all characters except text
            mPlayerTag = mPlayerTag.replaceAll("[^A-Za-z]", "");

            //execute Aysnc for API connection
            new AsyncSearchPlayer().execute();
        }
    }

    /*
    Populate the view for the user with player information
    from search
     */
    private void populateViews() {

        TextView name = (TextView) findViewById(R.id.view_player_playerName);
        TextView earnings = (TextView) findViewById(R.id.view_player_earnings);
        TextView romanizedName = (TextView) findViewById(R.id.view_player_realName);
        TextView team = (TextView) findViewById(R.id.view_player_team);
        TextView rank = (TextView) findViewById(R.id.view_player_ranking);
        ImageView race = (ImageView) findViewById(R.id.img_player_race);

        //set textviews
        name.setText(mPlayerDetails.get(0).getName());
        romanizedName.setText(mPlayerDetails.get(0).getRomanizedName());
        earnings.setText("$" + mPlayerDetails.get(0).getTotalEarnings());
        team.setText(mPlayerDetails.get(0).getCurrentTeam());
        rank.setText(String.valueOf(mPlayerDetails.get(0).getRanking()));

        //Get race image based on player's race(what faction they play ingame)
        String raceStr = mPlayerDetails.get(0).getRace();
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
    }

    /*
    Async class for handling the user search request with the API
     */
    class AsyncSearchPlayer extends AsyncTask<Void, Void, String> {
        ProgressDialog mPredictDialog;
        HttpURLConnection mUrlConnect;

        @Override
        protected void onPreExecute() {
            //Set up  progress dialog
            mPredictDialog = new ProgressDialog(PlayerSearch.this);
            mPredictDialog.setTitle(R.string.connecting_to);
            mPredictDialog.setMessage(getString(R.string.please_wait));
            mPredictDialog.setProgressStyle(mPredictDialog.STYLE_SPINNER);
            mPredictDialog.setCancelable(false);
            mPredictDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String json;

            //check if user can connect to the internet
            if (UtilsCheckNet.isInternetAvailable()) {

                //====GET PLAYER ID========//
                int mPlayerID;
                try {
                    //API search player request
                    URL url = new URL(Constants.URL_SEARCH_PLAYER + mPlayerTag);
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

                        //grab the matching player and return their id
                        JSONObject jsonRootObject = new JSONObject(json);
                        JSONArray jsonObjectArray = jsonRootObject.getJSONArray(getString(R.string.players));

                        //Error trap for no result (null object)
                        if (!jsonObjectArray.isNull(0)) {
                            JSONObject currentObj = jsonObjectArray.getJSONObject(0);
                            mPlayerID = currentObj.optInt(getString(R.string.player_id));
                        } else {
                            return getString(R.string.plNotFound);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return getString(R.string.errorTryAgain);
                    }
                } catch (Exception e) {
                    Log.e(getString(R.string.error), e.getMessage(), e);
                    return getString(R.string.errorTryAgain);
                }

                //========GET PROFILE========//
                //Now that matching id is found
                //we can request the complete player object
                //using the matching id
                try {

                    //create url for specific player profile
                    URL url = new URL(Constants.URL_GET_PLAYER_PROFILE + mPlayerID + "/?" + Constants.API_KEY);
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
                        JSONObject jsonRootObject = new JSONObject(json);

                        //Grab data from json objects
                        String name = jsonRootObject.optString(getString(R.string.tag));
                        String race = jsonRootObject.optString(getString(R.string.race));
                        String earnings = jsonRootObject.optString(getString(R.string.earnings));
                        String romanizedName = jsonRootObject.optString(getString(R.string.romanizedName));

                        //Error trap for player with no korean name
                        if (romanizedName.equals(getString(R.string.noRName))){
                            romanizedName = getString(R.string.not_applicable);
                        }

                        int id = jsonRootObject.optInt(getString(R.string.playerID));

                        //get rating uri to grab player's global ranking
                        JSONObject current_rating = jsonRootObject.getJSONObject(getString(R.string.currentRating));
                        String resource_uri = current_rating.optString(getString(R.string.resourceUri));

                        String resource_uriFinal = resource_uri + "?";

                        //get player's current team from json arrays and object
                        String currentTeam;
                        JSONArray current_teams_obj = jsonRootObject.getJSONArray(getString(R.string.currentTeams));
                        if (current_teams_obj.isNull(0)){
                            currentTeam = getString(R.string.noActiveTeam);
                        }else {
                            JSONObject teamName = current_teams_obj.getJSONObject(0);
                            JSONObject obj = teamName.getJSONObject(getString(R.string.team));
                            currentTeam = obj.optString(getString(R.string.name));
                        }

                        int ranking = 0;

                        //request the player's ranking which is in a different obj in the API
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

                                //handle player global ranking
                                JSONObject jsonRating = new JSONObject(jsonRatingStr);
                                ranking = jsonRating.optInt(getString(R.string.rank));

                            }catch (Exception e){
                                Log.e(getString(R.string.error), e.getMessage(), e);
                            }
                        }catch (Exception e){
                            Log.e(getString(R.string.error), e.getMessage(), e);
                        }

                        //create new player object and add it to the list
                        PlayerDetails searchedPlayer = new PlayerDetails(name, race, romanizedName, earnings, currentTeam, ranking, id);
                        mPlayerDetails.add(searchedPlayer);
                        return getString(R.string.connected);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return getString(R.string.error);
                    }

                } catch (Exception e) {
                    Log.e(getString(R.string.error), e.getMessage(), e);
                    return null;
                }
            } else {
                return getString(R.string.connection_timeout);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mPredictDialog.dismiss();

            //Error traps for player's not found or errors
            if (s.equals(getString(R.string.connected))) {
                populateViews();
            } else if (s.equals(getString(R.string.plNotFound))) {
                Toast.makeText(PlayerSearch.this, R.string.plNotFound, Toast.LENGTH_LONG).show();
                onBackPressed();
            } else if (s.equals(getString(R.string.error))) {
                Toast.makeText(PlayerSearch.this, R.string.errorTryAgain, Toast.LENGTH_LONG).show();
                onBackPressed();
            } else if (s.equals(getString(R.string.connection_timeout))) {
                Toast.makeText(PlayerSearch.this, R.string.failedConnection, Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}