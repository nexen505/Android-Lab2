package com.komarov.meetings.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ilia on 11.11.2017.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
