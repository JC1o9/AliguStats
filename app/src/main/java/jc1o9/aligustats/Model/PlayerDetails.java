package jc1o9.aligustats.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model object that holds the player data used throughout the app
 * Contains setters, getters and is parcelable to be passed through intents
 * @author JC1o9
 */
public class PlayerDetails implements Parcelable {
    private String mName;
    private String mRace;
    private String mRomanizedName;
    private String mTotalEarnings;
    private String mCurrentTeam;
    private int mRanking;
    private int mID;
    private double mScore;

    public PlayerDetails() {
    }

    public PlayerDetails(String Name, String Race, String RomanizedName, String TotalEarnings, String CurrentTeam, int ranking, int id, double score) {
        this.mName = Name;
        this.mRace = Race;
        this.mRomanizedName = RomanizedName;
        this.mTotalEarnings = TotalEarnings;
        this.mCurrentTeam = CurrentTeam;
        this.mRanking = ranking;
        this.mID = id;
        this.mScore = score;
    }

    protected PlayerDetails(Parcel in) {
        mName = in.readString();
        mRace = in.readString();
        mRomanizedName = in.readString();
        mTotalEarnings = in.readString();
        mCurrentTeam = in.readString();
        mRanking = in.readInt();
        mID = in.readInt();
        mScore = in.readDouble();
    }

    public static final Creator<PlayerDetails> CREATOR = new Creator<PlayerDetails>() {
        @Override
        public PlayerDetails createFromParcel(Parcel in) {
            return new PlayerDetails(in);
        }

        @Override
        public PlayerDetails[] newArray(int size) {
            return new PlayerDetails[size];
        }
    };

    //==========GETTERS=============//
    public String getName() {
        return mName;
    }

    public String getRace() {
        return mRace;
    }

    public String getRomanizedName() {
        return mRomanizedName;
    }

    public String getTotalEarnings() {
        return mTotalEarnings;
    }

    public String getCurrentTeam() {
        return mCurrentTeam;
    }

    public int getRanking() {
        return mRanking;
    }

    public int getID() {
        return mID;
    }

    public double getScore() {
        return mScore;
    }

    //=============SETTERS=================//


    public void setName(String name) {
        this.mName = name;
    }

    public void setRace(String race) {
        this.mRace = race;
    }

    public void setRomanizedName(String romanizedName) {
        this.mRomanizedName = romanizedName;
    }

    public void setTotalEarnings(String totalEarnings) {
        this.mTotalEarnings = totalEarnings;
    }

    public void setCurrentTeam(String currentTeam) {
        this.mCurrentTeam = currentTeam;
    }

    public void setRanking(int ranking) {
        this.mRanking = ranking;
    }

    public void setmID(int id) {
        this.mID = id;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mRace);
        dest.writeString(mRomanizedName);
        dest.writeString(mTotalEarnings);
        dest.writeString(mCurrentTeam);
        dest.writeInt(mRanking);
        dest.writeInt(mID);
        dest.writeDouble(mScore);
    }
}
