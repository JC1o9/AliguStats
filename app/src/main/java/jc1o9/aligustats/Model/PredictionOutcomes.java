package jc1o9.aligustats.Model;

/**
 * Model object that holds prediction data used in Prediction Result activity
 * Contains getters to retrieve data
 *
 * @author Jose
 */
public class PredictionOutcomes {
    private double mProb;
    private int mScoreA;
    private int mScoreB;

    public PredictionOutcomes(double prob, int scoreA, int scoreB) {
        this.mProb = prob;
        this.mScoreA = scoreA;
        this.mScoreB = scoreB;
    }

    //=======GETTERS=========//
    public double getProb() {
        return mProb;
    }

    public int getScoreA() {
        return mScoreA;
    }

    public int getScoreB() {
        return mScoreB;
    }

}
