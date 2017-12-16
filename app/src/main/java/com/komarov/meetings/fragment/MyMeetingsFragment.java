package com.komarov.meetings.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.komarov.meetings.model.Meeting.USER_MEETINGS_KEY;

/**
 * Created by Ilia on 19.11.2017.
 */

public class MyMeetingsFragment extends MeetingsListFragment {

    public MyMeetingsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(USER_MEETINGS_KEY).child(getUid());
    }

}
