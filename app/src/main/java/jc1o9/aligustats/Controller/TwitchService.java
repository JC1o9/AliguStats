package com.example.jose.aligustats.Controller;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.jose.aligustats.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class which handles the twitch service
 * which queries twitch API and checks if the
 * GSL stream is online or offline
 */
public class TwitchService extends IntentService {
    HttpURLConnection mUrlConnect;
    boolean mGSLStatus;

    public TwitchService() {
        super("IntentService");
    }

    /*
    *queries the twitch API and sendBroadcast
    * with the result of checking if the GSL stream
    * is online or not
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String json;
        JSONObject streamObj = null;
        try {
            //Check url
            URL url = new URL(Constants.URL_CHECK_STREAMS);
            mUrlConnect = (HttpURLConnection) url.openConnection();
            try {
                //get json string
                BufferedReader reader = new BufferedReader(new InputStreamReader(mUrlConnect.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                reader.close();
                //final json string
                json = builder.toString();
            } finally {
                mUrlConnect.disconnect();
            }

            //Process json
            try {

                //get stream json obj
                JSONObject jsonRootObject = new JSONObject(json);
                streamObj = jsonRootObject.getJSONObject(getString(R.string.stream));

                //check if stream obj is null ===== Error Trap
                if (streamObj.isNull(getString(R.string.stream))) {
                    mGSLStatus = true;

                    //Set-up intent with result
                    Intent resultIntent = new Intent(Constants.STREAM_RESULT);
                    resultIntent.putExtra(Constants.STREAM_RESULT_VAL, mGSLStatus);
                    sendBroadcast(new Intent(resultIntent));
                }
            } catch (JSONException streamOffline) {
                //Exception for null value on stream
                mGSLStatus = false;

                //set-up intent with result
                Intent resultIntent = new Intent(Constants.STREAM_RESULT);
                resultIntent.putExtra(Constants.STREAM_RESULT_VAL, mGSLStatus);
                sendBroadcast(new Intent(resultIntent));

                streamOffline.printStackTrace();
            }
        } catch (Exception e) {
            //Exception for no connection
            Intent noConnection = new Intent(Constants.NO_CONNECTION);
            sendBroadcast(noConnection);
            Log.e(getString(R.string.error), e.getMessage(), e);
        }
    }

    @Override
    public void onDestroy() {
        Intent broadcast = new Intent("DESTROYED");
        sendBroadcast(new Intent(broadcast));
    }
}