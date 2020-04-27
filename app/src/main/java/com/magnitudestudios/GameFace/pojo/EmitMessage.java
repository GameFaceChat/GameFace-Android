package com.magnitudestudios.GameFace.pojo;

public class EmitMessage {
    public String userID;
    public String type;
    public Object data;

    public EmitMessage() {

    }

    public EmitMessage(String user, String type, Object data) {
        this.userID = user;
        this.type = type;
        this.data = data;
    }
}
