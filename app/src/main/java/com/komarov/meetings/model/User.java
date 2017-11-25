package com.komarov.meetings.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Ilia on 11.11.2017.
 */

@IgnoreExtraProperties
public class User implements Serializable {

    public static final String USERS_KEY = "users";

    private String key;
    private String username;
    private String email;
    private String position;

    public User() {
    }

    public User(String username, String email, String position) {
        this.username = username;
        this.email = email;
        this.position = position;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
