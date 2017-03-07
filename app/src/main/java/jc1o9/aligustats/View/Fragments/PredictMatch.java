package jc1o9.aligustats.View.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import jc1o9.aligustats.Controller.Constants;
import jc1o9.aligustats.R;
import jc1o9.aligustats.View.PredictResults;

/**
 * class predict match using in sliding tab handles
 * the user input and then passes control to predictResults class
 */
public class PredictMatch extends Fragment {
    private String mPlayer1Name;
    private String mPlayer2Name;
    private int mBestOf;
    private Spinner mSpinner;
    private EditText mEditPlayer1Name;
    private EditText mEditPlayer2Name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_predict_match, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        mEditPlayer1Name = (EditText) getActivity().findViewById(R.id.predict_editPlayer1);
        mEditPlayer2Name = (EditText) getActivity().findViewById(R.id.predict_editPlayer2);
        mSpinner = (Spinner) getActivity().findViewById(R.id.predict_bestOf);
        Button btnPredict = (Button) getActivity().findViewById(R.id.btnPredict);

        //Listener for submit prediction button gathers information from editTexts and spinner
        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer1Name = mEditPlayer1Name.getText().toString();
                mPlayer2Name = mEditPlayer2Name.getText().toString();

                //Get selected value from best of series -- 1/3/5/7
                switch (mSpinner.getSelectedItemPosition()) {
                    case 0:
                        mBestOf = 1;
                        break;
                    case 1:
                        mBestOf = 3;
                        break;
                    case 2:
                        mBestOf = 5;
                        break;
                    case 3:
                        mBestOf = 7;
                        break;
                }

                //create intent and start new activity for prediction results add data as extra
                Intent intent = new Intent(getActivity(), PredictResults.class);
                intent.putExtra(Constants.PLAYER1_NAME, mPlayer1Name);
                intent.putExtra(Constants.PLAYER2_NAME, mPlayer2Name);
                intent.putExtra(Constants.BEST_OF, mBestOf);
                startActivity(intent);
            }

        });
    }
}