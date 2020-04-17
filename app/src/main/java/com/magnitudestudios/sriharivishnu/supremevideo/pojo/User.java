package com.magnitudestudios.sriharivishnu.supremevideo.pojo;

public class User {
    public String email;
    public String username;
    public User (String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
