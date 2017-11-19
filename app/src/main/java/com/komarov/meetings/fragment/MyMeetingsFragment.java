package com.komarov.meetings.fragment;

import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.komarov.meetings.LoginActivity;

/**
 * Created by Ilia on 19.11.2017.
 */

public class MyMeetingsFragment extends MeetingsListFragment {

    public MyMeetingsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-meetings").child(getUid());
    }

    @Override
    public String getUid() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
            return currentUser.getUid();
        else {
            startActivity(new Intent(this.getContext(), LoginActivity.class));
            return null;
        }
    }
}
