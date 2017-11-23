package com.komarov.meetings.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ilia on 11.11.2017.
 */

@IgnoreExtraProperties
public class User {

    private String username;
    private String email;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
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
