package com.magnitudestudios.GameFace.Network;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.magnitudestudios.GameFace.Interfaces.RoomCallback;
import com.magnitudestudios.GameFace.pojo.EmitMessage;
import com.magnitudestudios.GameFace.pojo.IceCandidatePOJO;
import com.magnitudestudios.GameFace.pojo.SessionInfoPOJO;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static FirebaseHelper firebaseHelper = null;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser firebaseUser;

    private ChildEventListener childEventListener;

    private String currentRoom = null;
    private String currentUser = null;

    public boolean started;

    public static FirebaseHelper getInstance() {
        if (firebaseHelper == null) {
            firebaseHelper = new FirebaseHelper();
        }
        return firebaseHelper;
    }

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        started = false;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void readMessage(RoomCallback callback) {
        if (currentRoom == null) return;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "DATA: "+dataSnapshot.getValue());
                EmitMessage emitMessage = dataSnapshot.getValue(EmitMessage.class);
                if (emitMessage == null) {
                    Log.e(TAG, "onChildAdded: NULL MESSAGE");
                    return;
                }
                Log.i(TAG, "onChildAdded: "+emitMessage.type);
                if (emitMessage.userID == null) {
                    Log.e(TAG, "onChildAdded: "+"NO USER ID");
                }
                else if (emitMessage.userID.equals(currentUser)) {
                    return;
                }
                Gson gson = new Gson();
                switch (emitMessage.type) {
                    case "JOINED":
                        callback.newParticipantJoined(emitMessage.userID);
                        break;
                    case "LEFT":
                        callback.participantLeft(emitMessage.userID);;
                        break;
                    case "OFFER":
                        callback.offerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO.class));
                        break;
                    case "ANSWER":
                        callback.answerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO.class));
                        break;
                    case "ICECANDIDATE":
                        callback.iceServerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), IceCandidatePOJO.class));
                        break;
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { Log.e(TAG, "onCancelled: "+databaseError.getMessage()); }
        };
        mDatabase.getReference("rooms").child(currentRoom).addChildEventListener(childEventListener);
    }

    private Task<Void> sendMessage(String type, Object data) {
        return mDatabase.getReference().child("rooms").child(currentRoom).push().setValue(new EmitMessage(currentUser, type, data));
    }

    public void createRoom(String roomName, String username, RoomCallback callback) {
        currentRoom = roomName;
        currentUser = username;
        sendMessage("JOINED", username).addOnCompleteListener(task -> {
            callback.onCreateRoom();
            readMessage(callback);
        });
    }

    public void closeRoom() {
        mDatabase.getReference().child("rooms").child(currentRoom).removeValue();
    }

    public void joinRoom(String roomName, String username, RoomCallback callback) {
        currentRoom = roomName;
        currentUser = username;
        sendMessage("JOINED", username).addOnCompleteListener(task -> {
            callback.onJoinedRoom(true);
            readMessage(callback);
        });
    }

    public void leaveRoom(RoomCallback callback) {
        if (currentUser != null) {
            mDatabase.getReference("rooms").child(currentRoom).removeEventListener(childEventListener);
            sendMessage("LEFT", currentUser).addOnCompleteListener(task -> {
                callback.onLeftRoom();
            });
        }
    }

    public void sendOffer(SessionDescription session) {
        sendMessage("OFFER", new SessionInfoPOJO(session.type.toString(), session.description));
    }

    public void sendAnswer(SessionDescription session) {
        sendMessage("ANSWER", new SessionInfoPOJO(session.type.toString(), session.description));
    }


    public void addIceCandidate(IceCandidate iceCandidate) {
        sendMessage("ICECANDIDATE", new IceCandidatePOJO(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp, iceCandidate.serverUrl));
    }

    public String getEmail() {
        if (mAuth != null) return mAuth.getCurrentUser().getEmail();
        return "NOEMAIL";
    }

    public void signOut() {
        mAuth.signOut();
    }

//    private void referenceExists(ReferenceExists exists, String... path) {
//        DatabaseReference databaseReference = mDatabase.getReference();
//        for (String ref : path) databaseReference = databaseReference.child(ref);
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                exists.referenceExists(dataSnapshot.exists(), dataSnapshot);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });
//    }

}
