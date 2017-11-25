package com.komarov.meetings.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.komarov.meetings.MainActivity;
import com.komarov.meetings.model.Meeting;

import java.util.List;

public class AllMeetingsFragment extends MeetingsListFragment {

    public AllMeetingsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("meetings");
    }

    public List<Meeting> getMeetings() {
        return ((MainActivity) getActivity()).getRecentMeetings();
    }

}
