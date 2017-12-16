package com.komarov.meetings.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class AllMeetingsFragment extends MeetingsListFragment {

    public AllMeetingsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("meetings");
    }

}
