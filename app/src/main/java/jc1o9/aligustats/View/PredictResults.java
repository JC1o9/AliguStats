package jc1o9.aligustats.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import jc1o9.aligustats.Controller.Constants;
import jc1o9.aligustats.Controller.UtilsCheckNet;
import jc1o9.aligustats.Model.PredictionOutcomes;
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
 * Class that handles the requested match results from a user
 *
 * @author JC1o9
 */
public class PredictResults extends AppCompatActivity {
    private int mBest_of;
    private String mPlayer1Tag;
    private String mPlayer2Tag;
    private String mPlayer1Race;
    private String mPlayer2Race;
    private double mProbA;
    private double mProbB;
    private ArrayList<PredictionOutcomes> mPredictionOutcomes;
    private PredictAdapter mPredictAdapter;

    /*
    set up listview and handle player names
    passed through intent
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_results);
        ListView list_plScoresA = (ListView) findViewById(R.id.list_predict_pl1);

        mPredictionOutcomes = new ArrayList<PredictionOutcomes>();
        mPredictAdapter = new PredictAdapter(this, mPredictionOutcomes);
        list_plScoresA.setAdapter(mPredictAdapter);

        //handle player names passed through intents by the predict sliding tab
        Intent predictIntent = getIntent();
        mPlayer1Tag = predictIntent.getStringExtra(Constants.PLAYER1_NAME);
        mPlayer2Tag = predictIntent.getStringExtra(Constants.PLAYER2_NAME);
        mBest_of = predictIntent.getIntExtra(Constants.BEST_OF, 1);

        //set back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //start async for
        new AsyncPredictionDetails().execute();

    }

    /*
    adapter for list view handles probability
     */
    public class PredictAdapter extends BaseAdapter {
        private ArrayList<PredictionOutcomes> mPredictionOutcomes;
        private Context mContext;

        public PredictAdapter(Context context, ArrayList<PredictionOutcomes> predictions) {
            this.mPredictionOutcomes = predictions;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mPredictionOutcomes.size();
        }

        @Override
        public Object getItem(int position) {
            return mPredictionOutcomes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null)
                view = convertView;
            else {
                view = LayoutInflater.from(mContext).inflate(R.layout.predict_list_view, parent, false);
            }

            view.setSelected(isEnabled(position));

            PredictionOutcomes currentOutcome = mPredictionOutcomes.get(position);
            int scoreA = currentOutcome.getScoreA();
            int scoreB = currentOutcome.getScoreB();
            double prob = currentOutcome.getProb();

            //Populate textviews and image view with predict results
            TextView view_prob = (TextView) view.findViewById(R.id.view_predict_prob);
            TextView view_player1 = (TextView) findViewById(R.id.view_predict_player1);
            TextView view_player2 = (TextView) findViewById(R.id.view_predict_player2);
            TextView view_title = (TextView) findViewById(R.id.view_predict_players);
            TextView view_adv = (TextView) findViewById(R.id.view_predict_advantage);
            ImageView img_race1 = (ImageView) findViewById(R.id.img_predict_racepl1);
            ImageView img_race2 = (ImageView) findViewById(R.id.img_predict_racepl2);

            //handles players race(wich faction they play ingame)
            switch (mPlayer1Race) {
                case "Z":
                    img_race1.setImageResource(R.drawable.zerg_logo);
                    break;
                case "T":
                    img_race1.setImageResource(R.drawable.terran_logo);
                    break;
                case "P":
                    img_race1.setImageResource(R.drawable.protoss_logo);
                    break;
            }

            switch (mPlayer2Race) {
                case "Z":
                    img_race2.setImageResource(R.drawable.zerg_logo);
                    break;
                case "T":
                    img_race2.setImageResource(R.drawable.terran_logo);
                    break;
                case "P":
                    img_race2.setImageResource(R.drawable.protoss_logo);
                    break;
            }

            //set match advantage to player with higher probability to win
            if (mProbA > mProbB) {
                view_adv.setText(getString(R.string.matchAdv) + mPlayer1Tag);
            } else {
                view_adv.setText(getString(R.string.matchAdv) + mPlayer2Tag);
            }

            view_title.setText(getString(R.string.resultsOf) + mPlayer1Tag + getString(R.string.vs) + mPlayer2Tag);
            view_player1.setText(mPlayer1Tag + " " + String.format("%.2f", mProbA) + "%");
            view_player2.setText(mPlayer2Tag + " " + String.format("%.2f", mProbB) + "%");

            view_prob.setText(String.format("%.2f", prob) + "%" + "  " + String.valueOf(scoreA) + "-" + String.valueOf(scoreB));
            view_prob.setGravity(Gravity.CENTER_HORIZONTAL);
            return view;
        }
    }

    /*
    async to handle prediction matches
    searches for player's id then request
    a predictmatch with player's id
     */
    class AsyncPredictionDetails extends AsyncTask<Void, Void, String> {
        ProgressDialog mPredictDialog;
        HttpURLConnection mUrlConnect;

        @Override
        protected void onPreExecute() {
            //set dialog
            mPredictDialog = new ProgressDialog(PredictResults.this);
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
            if (UtilsCheckNet.isInternetAvailable()) {

                //====GET FIRST PLAYER ID========//
                int mPlayer1ID;
                try {
                    URL url = new URL(Constants.URL_SEARCH_PLAYER + mPlayer1Tag);
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

                        //get player object from search request
                        JSONObject jsonRootObject = new JSONObject(json);
                        JSONArray jsonObjectArray = jsonRootObject.getJSONArray(getString(R.string.players));

                        //Error trap if player not found
                        if (!jsonObjectArray.isNull(0)) {
                            JSONObject currentObj = jsonObjectArray.getJSONObject(0);
                            mPlayer1ID = currentObj.optInt(getString(R.string.player_id));
                        } else {
                            return getString(R.string.pl1NotFound);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return getString(R.string.errorTryAgain);
                    }
                } catch (Exception e) {
                    Log.e(getString(R.string.error), e.getMessage(), e);
                    return getString(R.string.errorTryAgain);
                }

                //====GET SECOND PLAYER ID========//
                int mPlayer2ID;
                try {
                    URL url = new URL(Constants.URL_SEARCH_PLAYER + mPlayer2Tag);
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

                        //get player object based on search result
                        JSONObject jsonRootObject = new JSONObject(json);
                        JSONArray jsonObjectArray = jsonRootObject.getJSONArray(getString(R.string.players));

                        //error trap for second player not found
                        if (!jsonObjectArray.isNull(0)) {
                            JSONObject currentObj = jsonObjectArray.getJSONObject(0);
                            mPlayer2ID = currentObj.optInt(getString(R.string.player_id));
                        } else {
                            return getString(R.string.pl2NotFound);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return getString(R.string.errorTryAgain);
                    }
                } catch (Exception e) {
                    Log.e(getString(R.string.error), e.getMessage(), e);
                    return getString(R.string.errorTryAgain);
                }

                //========GET OUTCOMES========//

                try {
                    //request predictmatch using player ids and best-of from user input
                    URL url = new URL(Constants.URL_PREDICT_MATCH + mPlayer1ID + "," + mPlayer2ID + "/?bo=" + mBest_of + Constants.API_KEY);
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

                        //grab player_a data
                        JSONObject jsonRootObject = new JSONObject(json);
                        JSONObject playerObj = jsonRootObject.getJSONObject(getString(R.string.player_a));
                        mPlayer1Tag = playerObj.optString(getString(R.string.tag));
                        mPlayer1Race = playerObj.optString(getString(R.string.race));

                        //grab player_b data
                        playerObj = jsonRootObject.getJSONObject(getString(R.string.player_b));
                        mPlayer2Tag = playerObj.optString(getString(R.string.tag));
                        mPlayer2Race = playerObj.optString(getString(R.string.race));
                        mProbA = jsonRootObject.optDouble(getString(R.string.prob_a));
                        mProbB = jsonRootObject.optDouble(getString(R.string.prob_b));

                        //probabilities to percentage
                        mProbA = mProbA * 100;
                        mProbB = mProbB * 100;


                        //grab prediction outcomes array which contains the match scores
                        //and probabilities of the matches
                        JSONArray jsonObjectArray = jsonRootObject.getJSONArray(getString(R.string.predict_outcomes));
                        for (int i = 0; i < jsonObjectArray.length(); i++) {
                            JSONObject currentObj = jsonObjectArray.getJSONObject(i);
                            double prob = currentObj.optDouble(getString(R.string.probability));
                            int scoreA = currentObj.optInt(getString(R.string.scoreA));
                            int scoreB = currentObj.optInt(getString(R.string.scoreB));

                            //probabilities to percentage
                            prob = prob * 100;

                            //create new predictionOutcomes object
                            PredictionOutcomes currentOutcome = new PredictionOutcomes(prob, scoreA, scoreB);
                            //add to list
                            mPredictionOutcomes.add(currentOutcome);
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
            mPredictDialog.dismiss();

            //check if errors occured or players not found
            if (s.equals(getString(R.string.connected))) {
                mPredictAdapter.notifyDataSetChanged();
            } else if (s.equals(getString(R.string.pl1NotFound))) {
                Toast.makeText(PredictResults.this, R.string.player1NotFoundStr, Toast.LENGTH_LONG).show();
                onBackPressed();
            } else if (s.equals(getString(R.string.pl2NotFound))) {
                Toast.makeText(PredictResults.this, R.string.player2NotFoundStr, Toast.LENGTH_LONG).show();
                onBackPressed();
            } else if (s.equals(getString(R.string.error))) {
                Toast.makeText(PredictResults.this, R.string.errorTryAgain, Toast.LENGTH_LONG).show();
                onBackPressed();
            } else if (s.equals(getString(R.string.connection_timeout))) {
                Toast.makeText(PredictResults.this, R.string.failedConnection, Toast.LENGTH_LONG).show();
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
