package com.example.jose.aligustats.View;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.jose.aligustats.Controller.Constants;
import com.example.jose.aligustats.Controller.TwitchService;
import com.example.jose.aligustats.R;
import com.example.jose.aligustats.TabsLibrary.SlidingTabLayout;
import com.example.jose.aligustats.View.Fragments.PredictMatch;
import com.example.jose.aligustats.View.Fragments.TopPlayers;
import com.example.jose.aligustats.View.Fragments.TopTeams;

/**
 * Homepage and main fragment activity
 * that handles the fragment and sliding tabs of
 * the application as well broadcast for service
 *
 * @author Jose
 */

public class Homepage extends AppCompatActivity {
    private Context mContext;

    /*
    On create sets up the sliding tabs and their properties
    registers receiver for broadcast
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //get references for pager and tabs
        ViewPager mPager = (ViewPager) findViewById(R.id.viewPager);
        SlidingTabLayout mTabs = (SlidingTabLayout) findViewById(R.id.slidingTabs);

        //set tab and pager properties
        mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mTabs.setViewPager(mPager);
        mPager.setCurrentItem(1);
        mPager.setOffscreenPageLimit(3);
        mTabs.setDistributeEvenly(true);
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.ColorBackground));
        mTabs.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));

        mContext = this;

        //set receivers
        registerReceiver(streamReceiver, new IntentFilter(Constants.STREAM_RESULT));
        registerReceiver(noConnectionReceiver, new IntentFilter(Constants.NO_CONNECTION));
    }

    /*
    unregister receivers when activity destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(streamReceiver);
        unregisterReceiver(noConnectionReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homepage, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return true;
    }


    //Starts the twitch service
    public void StartService(MenuItem item) {
        Intent twitchIntent = new Intent(this, TwitchService.class);
        startService(twitchIntent);
    }

    /*
    fragment adapter class that handles the sliding tabs
    using tabsLibrary
     */
    class PagerAdapter extends FragmentPagerAdapter {

        String[] tabs;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
        }

        //Set the class to call when user slides tab
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PredictMatch();
                case 1:
                    return new TopPlayers();
                case 2:
                    return new TopTeams();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];

        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    /*
    Broadcast receiver for twitch service handles result
     */
    private BroadcastReceiver streamReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Get result and ouput a toast based on result
            boolean gslStatus = intent.getBooleanExtra(Constants.STREAM_RESULT_VAL, false);
            if (gslStatus){
                Toast.makeText(mContext, R.string.gslOnline,Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(mContext, R.string.gslOffline,Toast.LENGTH_LONG).show();
            }
        }
    };

    /*
    Broadcast receiver for twitch service handles a no connection available result
     */
    private BroadcastReceiver noConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                Toast.makeText(mContext, R.string.FailedConnectionTwitch,Toast.LENGTH_LONG).show();
        }
    };
}
