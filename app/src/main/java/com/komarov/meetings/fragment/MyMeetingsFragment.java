package com.komarov.meetings.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.komarov.meetings.MainActivity;
import com.komarov.meetings.model.Meeting;

import java.util.List;

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

    public List<Meeting> getMeetings() {
        return ((MainActivity) getActivity()).getMyMeetings();
    }
}
