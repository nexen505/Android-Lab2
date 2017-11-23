package com.komarov.meetings.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentMeetingsFragment extends MeetingsListFragment {

    public RecentMeetingsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("meetings").limitToFirst(100);
    }

}
