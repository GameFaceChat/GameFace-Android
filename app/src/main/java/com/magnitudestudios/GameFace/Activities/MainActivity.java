package com.magnitudestudios.GameFace.Activities;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.magnitudestudios.GameFace.Bases.BasePermissionsActivity;
import com.magnitudestudios.GameFace.GameFace;
import com.magnitudestudios.GameFace.Interfaces.RoomCallback;
import com.magnitudestudios.GameFace.Network.GetNetworkRequest;
import com.magnitudestudios.GameFace.R;
import com.magnitudestudios.GameFace.Utils.CustomPeerConnectionObserver;
import com.magnitudestudios.GameFace.Utils.CustomSdpObserver;
import com.magnitudestudios.GameFace.pojo.IceCandidatePOJO;
import com.magnitudestudios.GameFace.pojo.IceServer;
import com.magnitudestudios.GameFace.pojo.ServerInformation;
import com.magnitudestudios.GameFace.pojo.SessionInfoPOJO;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

import static com.magnitudestudios.GameFace.Constants.*;

public class MainActivity extends BasePermissionsActivity implements View.OnClickListener, RoomCallback {
    private static final String TAG = "MainActivity";

    private PeerConnectionFactory peerConnectionFactory;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;

    private MediaConstraints audioConstraints;
    private MediaConstraints videoConstraints;
    private MediaConstraints sdpConstraints;
    private PeerConnection localPeer;

    private List<PeerConnection.IceServer> iceServers;

    EglBase rootEglBase;

    private AudioSource audioSource;
    private AudioTrack localAudioTrack;

    private SurfaceViewRenderer localVideo, remoteVideo;

    private ProgressBar progressBar;

    private Button connect, disconnect, signout, joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iceServers = new ArrayList<>();

        firebaseHelper = ((GameFace) getApplicationContext()).firebaseHelper;

        localVideo = findViewById(R.id.localVideo);
        remoteVideo = findViewById(R.id.remoteVideo);
        connect = findViewById(R.id.connectButton);
        disconnect = findViewById(R.id.disconnectButton);
        signout = findViewById(R.id.main_button_signout);
        progressBar = findViewById(R.id.progressBar);
        joinButton = findViewById(R.id.joinButton);
        connect.setOnClickListener(this);
        disconnect.setOnClickListener(this);
        signout.setOnClickListener(this);
        joinButton.setOnClickListener(this);

        rootEglBase = EglBase.create();
        localVideo.init(rootEglBase.getEglBaseContext(), null);
        remoteVideo.init(rootEglBase.getEglBaseContext(), null);
        localVideo.setZOrderMediaOverlay(true);
        remoteVideo.setZOrderMediaOverlay(true);

    }

    //Network Handler
    @SuppressLint("HandlerLeak")
    private final Handler mUrlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_COMPLETED:
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "handleMessage: " + (String) msg.obj);
                    Gson gson = new Gson();
                    try {
                        ServerInformation serverInformation = gson.fromJson((String) msg.obj, ServerInformation.class);
                        serverInformation.printAll();
                        addToIceServers(serverInformation);
                        if (firebaseHelper.initiator) firebaseHelper.createRoom("ROOM2", "SRIHARI", MainActivity.this);
                        else firebaseHelper.joinRoom("ROOM2", "SRIHARI2", MainActivity.this);

                        onTryToStart();
                    } catch (JsonParseException e) {
                        Log.e(TAG, "handleMessage: " + "COULD NOT PARSE JSON", e);
                    }
                    break;
                case STATE_URL_FAILED:
                    Log.d(TAG, "handleMessage: " + (String) msg.obj);
                    Toast.makeText(MainActivity.this, "Cannot Connect to Server", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void addToIceServers(ServerInformation serverInformation) {
        for (IceServer iceServer : serverInformation.iceServers) {
            Log.e(TAG, "FOUND ICE SERVER: " + iceServer.url);
            PeerConnection.IceServer peerIceServer;
            if (iceServer.credential == null) {
                peerIceServer = PeerConnection.IceServer.builder(iceServer.url).createIceServer();
                iceServers.add(peerIceServer);
            } else {
                peerIceServer = PeerConnection.IceServer.builder(iceServer.url)
                        .setUsername(iceServer.username)
                        .setPassword(iceServer.credential)
                        .createIceServer();
                iceServers.add(peerIceServer);
            }
        }
    }

    private void startCamera() {
        Log.e(TAG, "startCamera: "+"STARTING CAMERA");
        //Initialize PeerConnectionFactory globals.
        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();

        //Create MediaConstraints - Will be useful for specifying video and audio constraints. More on this later!
        audioConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();

        //Now create a VideoCapturer instance. Callback methods are there if you want to do something!
        videoCapturer = createCameraCapturer(new Camera1Enumerator(false));
        if (videoCapturer != null) {
            SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
            videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
            videoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
        }

        //Create a VideoSource instance
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);

        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

        videoCapturer.startCapture(1024, 720, 30);

        //create surface renderer, init it and add the renderer to the track
        localVideo.setMirror(true);
        localVideo.setEnableHardwareScaler(true);
        localVideoTrack.addSink(localVideo);
    }

    private void create() {
        firebaseHelper.initiator = true;
        Log.e(TAG, "call: "+"CALLING");
        iceServers = new ArrayList<>();
        getIceServers();
        startCamera();
    }
    // Try moving getting ice servers before creating room
    private void join() {
        firebaseHelper.initiator = false;
        Log.e(TAG, "join: "+"JOINING");
        iceServers = new ArrayList<>();
        getIceServers();
        startCamera();
    }

    private void createPeerConnection() {
        Log.e(TAG, "createPeerConnection: " + iceServers.toString());
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);

                Log.e(TAG, "onIceCandidate: "+iceCandidate.sdp);
                firebaseHelper.addIceCandidate(iceCandidate);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                gotRemoteStream(mediaStream);
            }
        });

        MediaStream stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(localAudioTrack);
        stream.addTrack(localVideoTrack);
        localPeer.addStream(stream);

    }

    public void onTryToStart() {
        runOnUiThread(() -> {
            Log.e(TAG, "onTryToStart: "+"TRYING TO START");
            if (localVideoTrack != null && !firebaseHelper.started) {
                createPeerConnection();
                firebaseHelper.started = true;
            }
        });
    }

    private void gotRemoteStream(MediaStream stream) {
        Log.e(TAG, "gotRemoteStream: " + "GOT REMOTE STREAM");
        //we have remote video stream. add to the renderer.
        final VideoTrack videoTrack = stream.videoTracks.get(0);

        runOnUiThread(() -> {
            try {
                remoteVideo.setVisibility(View.VISIBLE);
                videoTrack.addSink(remoteVideo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void getIceServers() {
        progressBar.setVisibility(View.VISIBLE);
        GetNetworkRequest a = new GetNetworkRequest(mUrlHandler, getString(R.string.backend_cloud_function));
        a.execute();
    }

    private void disconnect() {
        hangUp();
        firebaseHelper.leaveRoom(this);
        firebaseHelper.started = false;
        localVideo.clearImage();
        localVideo.release();
        try {
            if (videoCapturer != null) videoCapturer.stopCapture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectButton:
                create();
                break;
            case R.id.joinButton:
                join();
                break;
            case R.id.disconnectButton:
                disconnect();
                break;
            case R.id.main_button_signout:
                firebaseHelper.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void hangUp() {
        try {
            if (localPeer != null) {
                localPeer.close();
            }
            localPeer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnect();

    }

    @Override
    public void onCreateRoom() {
        //Send offer
        Log.e(TAG, "CREATED ROOM");
        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        localPeer.createOffer(new CustomSdpObserver("localCreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                Log.d(TAG, "onCreateSuccess234: "+sessionDescription.description);
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocalDesc"), sessionDescription);
                //Send to peer
                firebaseHelper.sendOffer(sessionDescription);
            }
        }, sdpConstraints);
    }

    @Override
    public void offerReceived(SessionInfoPOJO session) {
        //Received offer
        Log.e(TAG, "offerReceived: "+session.description);
        localPeer.setRemoteDescription(new CustomSdpObserver("gotOffer"), new SessionDescription(SessionDescription.Type.fromCanonicalForm(session.type.toLowerCase()), session.description));
        localPeer.createAnswer(new CustomSdpObserver("localCreateAns") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                onTryToStart();
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocal"), sessionDescription);
                firebaseHelper.sendAnswer(sessionDescription);
            }
        }, new MediaConstraints());
    }

    @Override
    public void answerReceived(SessionInfoPOJO session) {
        Log.e(TAG, "answerReceived: " + session);
        localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemote"), new SessionDescription(SessionDescription.Type.fromCanonicalForm(session.type.toLowerCase()), session.description));
    }

    @Override
    public void newParticipantJoined(String user) {
        Log.e(TAG, "newParticipantJoined: "+user);
    }

    @Override
    public void iceServerReceived(IceCandidatePOJO iceCandidate) {
        Log.e(TAG, "iceServerReceived: ");

        localPeer.addIceCandidate(new IceCandidate(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp));
    }


    @Override
    public void participantLeft(String s) {
        Log.e(TAG, "participantLeft: "+s);
        Toast.makeText(MainActivity.this, "PARTICIPANT LEFT", Toast.LENGTH_SHORT).show();
        disconnect();
        firebaseHelper.closeRoom();
    }

    @Override
    public void onJoinedRoom(boolean b) {
        Log.e(TAG, "onJoinedRoom: ");
    }

    @Override
    public void onLeftRoom() {
        Toast.makeText(MainActivity.this, "Left Room", Toast.LENGTH_SHORT).show();
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

}

