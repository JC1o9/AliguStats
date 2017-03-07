package com.example.jose.aligustats.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jose.aligustats.Model.PlayerDetails;

import java.util.ArrayList;

/**
 * Database helper class for showing the top players
 * contains create, upgrade, and fetch all objects in the DB
 * @author Jose
 */
public class PlayerDetailsDBHelper extends SQLiteOpenHelper{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "playerDB";
    private static final String TABLE_NAME = "players";
    private static final String PLAYER_ID = "mPlayerID";
    private static final String PLAYER_NAME = "mName";
    private static final String PLAYER_RACE = "mRace";
    private static final String PLAYER_RNAME = "mRomanizedName";
    private static final String PLAYER_EARNINGS = "mTotalEarnings";
    private static final String PLAYER_TEAM = "mCurrentTeam";
    private static final String PLAYER_RANK = "mRanking";

    /*Constructor*/
    public PlayerDetailsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /*onCreate Method created player Table*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create player table
        String CREATE_PLAYER_TABLE = "CREATE TABLE players ( " + "mPlayerID INTEGER PRIMARY KEY, " + "mName TEXT, " + "mRace TEXT, " + "mRomanizedName TEXT, " + "mTotalEarnings TEXT, " + "mCurrentTeam TEXT, " + "mRanking INTEGER)";
        db.execSQL(CREATE_PLAYER_TABLE);
    }

    /*onUpgrade method*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop players table if already exists
        db.execSQL("DROP TABLE IF EXISTS players");
        this.onCreate(db);
    }

    /*Add method: adds a player to the database
    * takes a playerDetails obj parameter
     * and inserts them into db
    */
    public void createPlayer(PlayerDetails playerDetails) {
        // get reference of the playersDB database
        SQLiteDatabase db = this.getWritableDatabase();
        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(PLAYER_ID, playerDetails.getID());
        values.put(PLAYER_NAME, playerDetails.getName());
        values.put(PLAYER_RACE, playerDetails.getRace());
        values.put(PLAYER_RNAME, playerDetails.getRomanizedName());
        values.put(PLAYER_EARNINGS, playerDetails.getTotalEarnings());
        values.put(PLAYER_TEAM, playerDetails.getCurrentTeam());
        values.put(PLAYER_RANK, playerDetails.getRanking());
        // insert player
        db.insert(TABLE_NAME, null, values);
        // close database transaction
        db.close();
    }

    /*
    * getAll method:
    * fetches all players in the database
    * ordered by rank
    * and returns in a arrayList for use
     */
    public ArrayList<PlayerDetails> getAllPlayers() {
        ArrayList<PlayerDetails> playerDetails = new ArrayList<PlayerDetails>();
        // select player query
        String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + PLAYER_RANK + " ASC";
        // get reference of the playerDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // parse all results
        PlayerDetails player = null;
        if (cursor.moveToFirst()) {
            do {
                player = new PlayerDetails();
                player.setmID(cursor.getInt(0));
                player.setName(cursor.getString(1));
                player.setRace(cursor.getString(2));
                player.setRomanizedName(cursor.getString(3));
                player.setTotalEarnings(cursor.getString(4));
                player.setCurrentTeam(cursor.getString(5));
                player.setRanking(cursor.getInt(6));
                // Add player to list
                playerDetails.add(player);
            } while (cursor.moveToNext());
        }
        return playerDetails;
    }
}