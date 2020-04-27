package com.magnitudestudios.GameFace.pojo;

import android.util.Log;

import java.util.ArrayList;

public class ServerInformation {
    public String accountSid;
    public String dateCreated;
    public String dateUpdated;

    public ArrayList<IceServer> iceServers;

    public String password;
    public int ttl;
    public String username;

    public void printAll() {
        Log.d("TAG", "printAll: "+ accountSid + " " + dateCreated + " " + dateUpdated);
        for (IceServer i : iceServers) {
            Log.d("TAG", "Ice Servers: " + i.url);
        }
        Log.d("TAG", "printAll: "+ password  + " " + ttl + " " + username);
    }

}
