package com.example.jose.aligustats.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jose.aligustats.Model.TeamDetails;

import java.util.ArrayList;

/**
 * Database helper class for showing the top teams
 * contains create, upgrade, and fetch all objects in the DB
 * @author Jose
 */
public class TeamDetailsDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "teamDB";
    private static final String TABLE_NAME = "teams";
    private static final String TEAM_ID = "mTeamID";
    private static final String TEAM_NAME = "mName";
    private static final String TEAM_PLSCORE = "mProleagueScore";
    private static final String TEAM_AKSCORE = "mAllKillScore";
    private static final String TEAM_ROSTER = "mActiveRoster";

    /*Constructor*/
    public TeamDetailsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create team table
        String CREATE_PLAYER_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + TEAM_ID + " INTEGER PRIMARY KEY, " + TEAM_NAME+ " TEXT, " + TEAM_PLSCORE + " DOUBLE, " + TEAM_AKSCORE + " DOUBLE, " +  TEAM_ROSTER +" INTEGER)";
        db.execSQL(CREATE_PLAYER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    /*add method which adds teams
    * to the database
    */
    public void createTeam(TeamDetails teamDetails) {
        // get reference of the teamDB database
        SQLiteDatabase db = this.getWritableDatabase();
        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(TEAM_ID, teamDetails.getID());
        values.put(TEAM_NAME, teamDetails.getName());
        values.put(TEAM_PLSCORE, teamDetails.getProleagueScore());
        values.put(TEAM_AKSCORE, teamDetails.getAllKillScore());
        values.put(TEAM_ROSTER, teamDetails.getActiveRoster());
        // insert team
        db.insert(TABLE_NAME, null, values);
        // close database transaction
        db.close();
    }

    /*
    * getAll method:
    * fetches all teams in the database
    * ordered by all-kill score
    * and returns in a arrayList for use
     */
    public ArrayList<TeamDetails> getAllTeams() {
        ArrayList<TeamDetails> teamDetails = new ArrayList<TeamDetails>();
        // select team query
        String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + TEAM_AKSCORE + " DESC";
        // get reference of the teamDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // parse all results
        TeamDetails team = null;
        if (cursor.moveToFirst()) {
            do {
                team = new TeamDetails();
                team.setID(cursor.getInt(0));
                team.setName(cursor.getString(1));
                team.setProleagueScore(cursor.getDouble(2));
                team.setAllKillScore(cursor.getDouble(3));
                team.setActiveRoster(cursor.getInt(4));
                // Add player to list
                teamDetails.add(team);
            } while (cursor.moveToNext());
        }
        return teamDetails;
    }
}
