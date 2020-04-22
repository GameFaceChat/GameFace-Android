package com.magnitudestudios.GameFace.Activities;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.magnitudestudios.GameFace.Bases.BasePermissionsActivity;
import com.magnitudestudios.GameFace.Network.GetNetworkRequest;
import com.magnitudestudios.GameFace.R;
import com.magnitudestudios.GameFace.Utils.CustomPeerConnectionObserver;
import com.magnitudestudios.GameFace.Utils.CustomSdpObserver;
import com.twilio.rest.proxy.v1.service.SessionCreator;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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


import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RTCStatsCollectorCallback;
import org.webrtc.RTCStatsReport;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

import static com.magnitudestudios.GameFace.Constants.*;

public class MainActivity extends BasePermissionsActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private PeerConnectionFactory peerConnectionFactory;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;

    private MediaConstraints audioConstraints;
    private MediaConstraints videoConstraints;
    private MediaConstraints sdpConstraints;
    private PeerConnection localPeer, remotePeer;

    VideoRenderer localRenderer;
    VideoRenderer remoteRenderer;

    private AudioSource audioSource;
    private AudioTrack localAudioTrack;

    private SurfaceViewRenderer localVideo, remoteVideo;

    private ProgressBar progressBar;

    private Button connect, disconnect, signout;

    private FirebaseAuth mAuth;

    //Network Handler
    @SuppressLint("HandlerLeak")
    private final Handler mUrlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_COMPLETED:
                    Log.d(TAG, "handleMessage: " + (String) msg.obj);
                    break;
                case STATE_URL_FAILED:
                    Log.d(TAG, "handleMessage: " + (String) msg.obj);
                    Toast.makeText(MainActivity.this, "Cannot Connect to Server", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, "SUCCESS!", Toast.LENGTH_LONG).show();
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
        mAuth = FirebaseAuth.getInstance();

        localVideo = findViewById(R.id.localVideo);
        remoteVideo = findViewById(R.id.remoteVideo);
        connect = findViewById(R.id.connectButton);
        disconnect = findViewById(R.id.disconnectButton);
        signout = findViewById(R.id.main_button_signout);
        progressBar = findViewById(R.id.progressBar);
        connect.setOnClickListener(this);
        disconnect.setOnClickListener(this);
        signout.setOnClickListener(this);

        EglBase rootEglBase = EglBase.create();
        localVideo.init(rootEglBase.getEglBaseContext(), null);
        remoteVideo.init(rootEglBase.getEglBaseContext(), null);
        localVideo.setZOrderMediaOverlay(true);
        remoteVideo.setZOrderMediaOverlay(true);

//        setUnclickable(disconnect);

        startCamera();

    }

    private void startCamera() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true);
        //Create a new PeerConnectionFactory instance.
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionFactory = new PeerConnectionFactory(options);

        //Now create a VideoCapturer instance. Callback methods are there if you want to do something!
        videoCapturer = createVideoCapturer();
        //Create MediaConstraints - Will be useful for specifying video and audio constraints. More on this later!
        audioConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();

        //Create a VideoSource instance
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer);
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);

        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

        videoCapturer.startCapture(500, 500, 30);

        //create surface renderer, init it and add the renderer to the track
        localVideo.setMirror(true);
        localRenderer = new VideoRenderer(localVideo);
        localVideo.setEnableHardwareScaler(true);

        localVideoTrack.addRenderer(localRenderer);
    }

    private void call() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();

        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"));

        localPeer = peerConnectionFactory.createPeerConnection(iceServers, sdpConstraints, new CustomPeerConnectionObserver(" LOCAL") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                localPeer.addIceCandidate(iceCandidate);
            }
            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
            }
        });

        remotePeer = peerConnectionFactory.createPeerConnection(iceServers, sdpConstraints, new CustomPeerConnectionObserver(" REMOTE") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                remotePeer.addIceCandidate(iceCandidate);
            }
            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                remotePeer.getStats(new RTCStatsCollectorCallback() {
                    @Override
                    public void onStatsDelivered(RTCStatsReport rtcStatsReport) {
                        Log.d(TAG, "onStatsDelivered: "+rtcStatsReport.toString());
                    }
                });
                gotRemoteStream(mediaStream);
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                super.onIceGatheringChange(iceGatheringState);
            }
        });

        MediaStream stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(localAudioTrack);
        stream.addTrack(localVideoTrack);
        localPeer.addStream(stream);
//        gotRemoteStream(stream);

        localPeer.createOffer(new CustomSdpObserver("localCreateOffer"){
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                //we have localOffer. Set it as local desc for localpeer and remote desc for remote peer.
                //try to create answer from the remote peer.
                super.onCreateSuccess(sessionDescription);
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocalDesc"), sessionDescription);
                remotePeer.setRemoteDescription(new CustomSdpObserver("remoteSetRemoteDesc"), sessionDescription);
                remotePeer.createAnswer(new CustomSdpObserver("remoteCreateOffer") {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);
                        Log.d(TAG, "onCreateSuccess2: " + sessionDescription.description);
                        remotePeer.setLocalDescription(new CustomSdpObserver("remoteSetLocalDesc"), sessionDescription);
                        localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemoteDesc"), sessionDescription);

                    }
                }, new MediaConstraints());
            }
        }, sdpConstraints);
    }

    private void gotRemoteStream(MediaStream stream) {
        //we have remote video stream. add to the renderer.
        final VideoTrack videoTrack = stream.videoTracks.getFirst();
        final AudioTrack audioTrack = stream.audioTracks.getFirst();
        Log.d(TAG, "gotRemoteStream: "+stream.videoTracks);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    remoteRenderer = new VideoRenderer(remoteVideo);
                    remoteVideo.setVisibility(View.VISIBLE);
                    videoTrack.addRenderer(remoteRenderer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        videoCapturer = createCameraCapturer(new Camera1Enumerator(false));
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // Trying to find a front facing camera!
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // We were not able to find a front cam. Look for other cameras
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }


    /* Callbacks */

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
        GetNetworkRequest a = new GetNetworkRequest(mUrlHandler, getString(R.string.backend_cloud_function));
        a.execute();
    }

    private void disconnect() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectButton:
                call();
                break;
            case R.id.disconnectButton:
                disconnect();
                break;
            case R.id.main_button_signout:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}

