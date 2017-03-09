package jc1o9.aligustats.View;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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
    private ArrayList<PlayerDetails> mPlayerDetails;
    private PieChart mPieChart;
    private float[] yData = {25.1f,33.4f,25.1f};

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

        //PieChart
        mPieChart = (PieChart)findViewById(R.id.playerChart);

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

        //populate chart
        Description desc = new Description();
        desc.setText("");
        //desc.setTextSize(15);
        mPieChart.setDescription(desc);
        mPieChart.setRotationEnabled(true);
        mPieChart.setHoleRadius(25f);
        mPieChart.setCenterText("Winrates by Race");
        mPieChart.setDrawEntryLabels(true);

        addChartData();


    }

    private void addChartData() {
        ArrayList<PieEntry> yEntry = new ArrayList<>();
        ArrayList<String> xEntry = new ArrayList<>();

        for (int i =0; i<yData.length;i++){
            yEntry.add(new PieEntry(yData[i],i));
        }

        PieDataSet mPieDataSet = new PieDataSet(yEntry, "test");
        mPieDataSet.setSliceSpace(2);
        mPieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        mPieDataSet.setColors(colors);

        Legend mLegend = mPieChart.getLegend();
        mLegend.setForm(Legend.LegendForm.DEFAULT);

        PieData mPieData = new PieData(mPieDataSet);
        mPieChart.setData(mPieData);
        mPieChart.invalidate();

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
