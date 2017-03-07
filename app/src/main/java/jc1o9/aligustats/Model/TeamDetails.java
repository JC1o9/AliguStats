package jc1o9.aligustats.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model object that holds team data used throughout the app
 * Contains getters, setters and is parcelable to pass through intents
 * @author JC1o9
 */
public class TeamDetails implements Parcelable{
    private String mName;
    private double mProleagueScore;
    private double mAllKillScore;
    private int mActiveRoster;
    private int mTeamID;

    public TeamDetails() {

    }

    public TeamDetails(String name, double proleagueScore, double allKillScore, int activeRoster, int id) {
        this.mName = name;
        this.mProleagueScore = proleagueScore;
        this.mAllKillScore = allKillScore;
        this.mActiveRoster = activeRoster;
        this.mTeamID = id;
    }

    protected TeamDetails(Parcel in) {
        mName = in.readString();
        mProleagueScore = in.readDouble();
        mAllKillScore = in.readDouble();
        mActiveRoster = in.readInt();
        mTeamID = in.readInt();
    }

    public static final Creator<TeamDetails> CREATOR = new Creator<TeamDetails>() {
        @Override
        public TeamDetails createFromParcel(Parcel in) {
            return new TeamDetails(in);
        }

        @Override
        public TeamDetails[] newArray(int size) {
            return new TeamDetails[size];
        }
    };

    //=============SETTERS==============//
    public void setName(String name) {
        this.mName = name;
    }

    public void setProleagueScore(double proleagueScore) {
        this.mProleagueScore = proleagueScore;
    }

    public void setAllKillScore(double allKillScore) {
        this.mAllKillScore = allKillScore;
    }

    public void setActiveRoster(int activeRoster) {
        this.mActiveRoster = activeRoster;
    }

    public void setID(int id) {
        this.mTeamID = id;
    }

    //=============GETTERS=============//
    public String getName() {

        return mName;
    }

    public double getProleagueScore() {
        return mProleagueScore;
    }

    public double getAllKillScore() {
        return mAllKillScore;
    }

    public int getActiveRoster() {
        return mActiveRoster;
    }

    public int getID() {
        return mTeamID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeDouble(mProleagueScore);
        dest.writeDouble(mAllKillScore);
        dest.writeInt(mActiveRoster);
        dest.writeInt(mTeamID);
    }
}
