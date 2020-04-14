package com.magnitudestudios.sriharivishnu.supremevideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.magnitudestudios.sriharivishnu.supremevideo.Network.GetNetworkRequest;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import static com.magnitudestudios.sriharivishnu.supremevideo.Constants.*;

public class MainActivity extends AppCompatActivity implements Connector.IConnect, View.OnClickListener {
    private static final String TAG = "MainActivity";

    private String USERNAME = "";

    private Connector mVidyoConnector;
    int remoteParticipants = 10;

    private FrameLayout videoFrame;
    private Button connect, disconnect;
    private ProgressBar progressBar;


    //Network Handler
    @SuppressLint("HandlerLeak")
    private final Handler mUrlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_COMPLETED:
                    Log.d(TAG, "handleMessage: " + (String) msg.obj);
                    ConnectToResource("prod.vidyo.io", (String) msg.obj, USERNAME, "VideoChat");
                    break;
            }
        }
    };


    //Video Service Handler
    @SuppressLint("HandlerLeak")
    private final Handler mVideoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            switch (msg.what) {
                case STATE_CONNECTED:
                    Log.d(TAG, "handleMessage: " + "SUCCESS");
                    setDisconnectButtonEnabled();
                    setUnclickable(connect);

                    Toast.makeText(getApplicationContext(), "SUCCESS!", Toast.LENGTH_LONG).show();
                    break;
                case STATE_FAILED:
                    Log.e(TAG, "handleMessage: " + "FAILED");
                    Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                    break;
                case STATE_DISCONNECTED:
                    Log.e(TAG, "handleMessage: " + "DISCONNECTED");
                    setConnectButtonEnabled();
                    setUnclickable(disconnect);
                    Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, NETWORK_STATE_CODE);
        checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_CODE);
        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();

        videoFrame = findViewById(R.id.videoFrame);
        connect = findViewById(R.id.connectButton);
        disconnect = findViewById(R.id.disconnectButton);
        progressBar = findViewById(R.id.progressBar);
        connect.setOnClickListener(this);
        disconnect.setOnClickListener(this);

        setUnclickable(disconnect);

    }

    private void ConnectToResource(String host, String token, String displayName, String resourceId) {
        mVidyoConnector.connect(host, token, displayName, resourceId, this);
    }

    /* Callbacks */

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
    }

    private void getUserName(final OnReceiveUsername listener) {
        final View v = getLayoutInflater().inflate(R.layout.dialog_view, null);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Username Input")
                .setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText usernameInput = v.findViewById(R.id.usernameInput);
                        listener.getUserName(usernameInput.getText().toString());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        alertDialog.show();

    }

    private void setUnclickable(Button button) {
        button.setBackground(getDrawable(R.drawable.disabled_background));
        button.setTextColor(getColor(R.color.darkGray));
        button.setEnabled(false);
    }

    private void setConnectButtonEnabled() {
        connect.setEnabled(true);
        connect.setBackground(getDrawable(R.drawable.connect_background));
        connect.setTextColor(getColor(android.R.color.white));
    }

    private void setDisconnectButtonEnabled() {
        disconnect.setEnabled(true);
        disconnect.setBackground(getDrawable(R.drawable.disconnect_background));
        disconnect.setTextColor(getColor(android.R.color.white));
    }

    private void connect() {
        progressBar.setVisibility(View.VISIBLE);
        mVidyoConnector = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, remoteParticipants, "warning all@VidyoConnector info@VidyoClient", "", 0);
        mVidyoConnector.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
        GetNetworkRequest a = new GetNetworkRequest(mUrlHandler, "https://us-central1-supremevideochat.cloudfunctions.net/helloWorld?userName="+USERNAME);
        a.execute();
    }

    private void disconnect() {
        if (mVidyoConnector == null) {
            Toast.makeText(MainActivity.this, "There is no Connection", Toast.LENGTH_LONG).show();
        }else {
            mVidyoConnector.disconnect();
            mVidyoConnector = null;
        }
    }


    @Override
    public void onSuccess() {
        Message msg = new Message();
        msg.what = STATE_CONNECTED;
        mVideoHandler.sendMessage(msg);
    }

    @Override
    public void onFailure(Connector.ConnectorFailReason connectorFailReason) {
        Message msg = new Message();
        msg.what = STATE_FAILED;
        mVideoHandler.sendMessage(msg);
    }

    @Override
    public void onDisconnected(Connector.ConnectorDisconnectReason connectorDisconnectReason) {
        Message msg = new Message();
        msg.what = STATE_DISCONNECTED;
        mVideoHandler.sendMessage(msg);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: "+ permissions.toString() + grantResults.toString());

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == NETWORK_STATE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Network Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Network Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == RECORD_AUDIO_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Record Audio Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Record Audio Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectButton:
                if (USERNAME.isEmpty()) {
                    getUserName(new OnReceiveUsername() {
                        @Override
                        public void getUserName(String text) {
                            USERNAME = text;
                            if (!text.isEmpty()) connect();
                        }
                    });
                    break;
                }
                connect();
                break;
            case R.id.disconnectButton:
                disconnect();
                break;
        }
    }

    interface OnReceiveUsername {
        void getUserName(String text);
    }
}
