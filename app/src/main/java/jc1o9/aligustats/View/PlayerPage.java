package jc1o9.aligustats.View;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import jc1o9.aligustats.Controller.Constants;
import jc1o9.aligustats.Model.PlayerDetails;
import jc1o9.aligustats.R;

import java.util.ArrayList;

/**
 * Activity for displaying specific player data
 * through object passed by the top players sliding tab
 *
 * @author JC1o9
 */
public class PlayerPage extends AppCompatActivity {
    ArrayList<PlayerDetails> mPlayerDetails;

    /*
    set up the page layout and grab data from parcelable intent passed through
    sliding tab
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_page);

        TextView playerName = (TextView) findViewById(R.id.view_player_playerName);
        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.sc_typeface));
        playerName.setTypeface(typeface);

        //Get player data based on selected player from sliding tab listview
        Intent playerIntent = getIntent();
        int playerID = playerIntent.getIntExtra(Constants.PLAYER_ID, 1);
        mPlayerDetails = playerIntent.getParcelableArrayListExtra(Constants.ARRAY_LIST);

        //Set back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        populateViews(playerID);
    }

    /*
    Populate the view for the user with player information
     */
    private void populateViews(int i) {

        //Get references
        TextView name = (TextView) findViewById(R.id.view_player_playerName);
        TextView earnings = (TextView) findViewById(R.id.view_player_earnings);
        TextView romanizedName = (TextView) findViewById(R.id.view_player_realName);
        TextView team = (TextView) findViewById(R.id.view_player_team);
        TextView ranking = (TextView) findViewById(R.id.view_player_ranking);
        ImageView race = (ImageView) findViewById(R.id.img_player_race);

        //Populate textviews
        name.setText(mPlayerDetails.get(i).getName());
        romanizedName.setText(mPlayerDetails.get(i).getRomanizedName());
        earnings.setText("$" + mPlayerDetails.get(i).getTotalEarnings());
        team.setText(mPlayerDetails.get(i).getCurrentTeam());
        ranking.setText(String.valueOf(mPlayerDetails.get(i).getRanking()));

        //Get race image based on player's race(what faction they play ingame)
        String raceStr = mPlayerDetails.get(i).getRace();
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
