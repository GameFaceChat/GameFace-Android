package com.magnitudestudios.sriharivishnu.supremevideo.Network;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.magnitudestudios.sriharivishnu.supremevideo.Constants.*;

public class GetNetworkRequest extends AsyncTask<String, Void, Void> {
    Handler mHandler;
    String stringUrl;

    public GetNetworkRequest(Handler handler, String stringUrl) {
        this.mHandler = handler;
        this.stringUrl = stringUrl;
    }
    @Override
    protected Void doInBackground(String... strings) {
        try {
            StringBuilder response  = new StringBuilder();

            URL url = new URL(stringUrl);
            HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();
            if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()),16384);
                String strLine;
                while ((strLine = input.readLine()) != null)
                {
                    response.append(strLine);
                }
                input.close();
            }
            Message message = new Message();
            message.what = STATE_COMPLETED;
            message.obj = response.toString();
            mHandler.sendMessage(message);
        } catch (Exception e) {
            Log.d("Error Occurred", "Link not found");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
