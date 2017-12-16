package com.komarov.meetings.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.komarov.meetings.LoginActivity;
import com.komarov.meetings.MeetingDetailActivity;
import com.komarov.meetings.R;
import com.komarov.meetings.adapter.MeetingAdapter;
import com.komarov.meetings.model.Meeting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilia on 25.11.2017.
 */

public class SearchFragment extends Fragment {

    private RecyclerView mRecycler;
    private RecyclerView.Adapter mAdapter;
    private final List<Meeting> mFilteredMeetings = new ArrayList<>();
    private EditText mSearchText;
    private Button mSearchButton;
    private DatabaseReference db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_search_meetings, container, false);

        mRecycler = rootView.findViewById(R.id.meetings_list);
        mRecycler.setHasFixedSize(true);

        mSearchButton = rootView.findViewById(R.id.search_button);
        mSearchText = rootView.findViewById(R.id.search_edit_text);

        mSearchButton.setOnClickListener(v -> {
            String searchText = mSearchText.getText().toString();
            if (searchText.length() > 0) {
                db.addValueEventListener(new MeetingListListener(db, searchText, mFilteredMeetings));
            }
        });

        return rootView;
    }

    public class MeetingListListener implements ValueEventListener {
        private DatabaseReference databaseReference;
        private String mSearchText;
        private List<Meeting> mFilteredMeetings;

        MeetingListListener(DatabaseReference ref, String searchText, List<Meeting> filtered) {
            this.databaseReference = ref;
            this.mSearchText = searchText;
            this.mFilteredMeetings = filtered;
            this.mFilteredMeetings.clear();
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            try {
                Iterable<DataSnapshot> meetingsIterable = dataSnapshot.child(Meeting.MEETINGS_KEY).getChildren();
                for (DataSnapshot snapshot : meetingsIterable) {
                    final Meeting meeting = snapshot.getValue(Meeting.class);
                    if (meeting != null) {
                        final boolean hasInDescription = meeting.getDescription() != null && meeting.getDescription().contains(mSearchText);
                        final boolean hasInTitle = meeting.getTitle() != null && meeting.getTitle().contains(mSearchText);
                        if (hasInDescription || hasInTitle)
                            mFilteredMeetings.add(meeting);
                    }
                }
                setMeetingsRecycler(mFilteredMeetings);
            } catch (Exception e) {
                Log.e("UpdateMeeting_E", e.getMessage());
            } finally {
                databaseReference.removeEventListener(this);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("Error:", databaseError.getMessage());
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
    }

    private void setMeetingsRecycler(final List<Meeting> meetings) {
        RecyclerView.Adapter mAdapter = new MeetingAdapter(meetings, meeting -> v -> {
            Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
            intent.putExtra(MeetingDetailActivity.EXTRA_MEETING_KEY, meeting.getKey());
            intent.putExtra(MeetingDetailActivity.EXTRA_MEETING, meeting);
            startActivity(intent);
        });
        mRecycler.setAdapter(mAdapter);
    }

    public String getUid() {
        final FirebaseUser currentUser = getCurrentUser();
        if (currentUser != null)
            return currentUser.getUid();
        else {
            getActivity().startActivity(new Intent(this.getContext(), LoginActivity.class));
            getActivity().finish();
            return null;
        }
    }

    public FirebaseUser getCurrentUser() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
            return currentUser;
        else {
            getActivity().startActivity(new Intent(this.getContext(), LoginActivity.class));
            getActivity().finish();
            return null;
        }
    }

}
