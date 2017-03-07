package com.example.jose.aligustats.View;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jose.aligustats.Controller.Constants;
import com.example.jose.aligustats.Model.TeamDetails;
import com.example.jose.aligustats.R;

import java.util.ArrayList;

/**
 * Activity for displaying specific player data
 * through object passed by the homepage
 *
 * @author Jose
 */
public class TeamsPage extends AppCompatActivity {
    ArrayList<TeamDetails> mTeamDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_page);

        TextView teamName = (TextView) findViewById(R.id.view_team_teamName);
        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.sc_typeface));
        teamName.setTypeface(typeface);

        Intent playerIntent = getIntent();
        int playerID = playerIntent.getIntExtra(Constants.PLAYER_ID, 1);
        mTeamDetails = playerIntent.getParcelableArrayListExtra(Constants.ARRAY_LIST);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        populateViews(playerID);
    }

    /*
    Populate the view for the user with player information
     */
    private void populateViews(int i) {
        TextView name = (TextView) findViewById(R.id.view_team_teamName);
        TextView akScore = (TextView) findViewById(R.id.view_team_akScore);
        TextView plScore = (TextView) findViewById(R.id.view_team_plScore);
        TextView roster = (TextView) findViewById(R.id.view_team_activePlayers);
        ImageView logo = (ImageView) findViewById(R.id.img_team_logo);

        name.setText(mTeamDetails.get(i).getName());
        akScore.setText(String.format("%.2f", mTeamDetails.get(i).getAllKillScore()) + "%");
        plScore.setText(String.format("%.2f", mTeamDetails.get(i).getProleagueScore()) + "%");
        roster.setText(String.valueOf(mTeamDetails.get(i).getActiveRoster()));

        switch (mTeamDetails.get(i).getName()) {
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
