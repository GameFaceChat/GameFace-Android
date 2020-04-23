package com.magnitudestudios.GameFace.Network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.magnitudestudios.GameFace.Interfaces.RoomCallback;

import org.webrtc.SessionDescription;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static FirebaseHelper firebaseHelper = null;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser firebaseUser;

    private String currentRoom;
    private String currentUser;

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
    }

    public void update() {
        firebaseUser = mAuth.getCurrentUser();
    }

    public void createRoom(String roomName, String username, SessionDescription session) {
        mDatabase.getReference("rooms").child(roomName).child(username).child("SDP").setValue(session);
    }

    public void getRoomChanges(String username, String roomName, RoomCallback callback) {
        mDatabase.getReference("rooms").child(roomName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (!dataSnapshot.child("username").getValue().equals(username)) {
//                    Log.d(TAG, "NEW PARTICIPANT: " + dataSnapshot.child("username").getValue());
//                }
                Log.d(TAG, "onChildAdded: ");
                callback.newParticipantJoined();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged: ");
                callback.iceServerAdded();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
    }

    public void signOut() {
        mAuth.signOut();
    }
}
