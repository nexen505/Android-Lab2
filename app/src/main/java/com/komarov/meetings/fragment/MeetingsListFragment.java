package com.komarov.meetings.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.komarov.meetings.LoginActivity;
import com.komarov.meetings.MeetingDetailActivity;
import com.komarov.meetings.R;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.viewholder.MeetingViewHolder;

import java.util.List;


public abstract class MeetingsListFragment extends Fragment {

    private static final String TAG = "MeetingsListFragment";

    private RecyclerView mRecycler;
    //    private RecyclerView.Adapter mAdapter;
    private FirebaseRecyclerAdapter<Meeting, MeetingViewHolder> mAdapter;

    public MeetingsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_meetings, container, false);

        mRecycler = rootView.findViewById(R.id.meetings_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        Query meetingsQuery = getQuery(FirebaseDatabase.getInstance().getReference());
        initializeRecyclerView(meetingsQuery);
    }

    private void initializeRecyclerView(Query meetingsQuery) {
        FirebaseRecyclerOptions<Meeting> options = new FirebaseRecyclerOptions.Builder<Meeting>()
                .setQuery(meetingsQuery, Meeting.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Meeting, MeetingViewHolder>(options) {

            @Override
            public MeetingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MeetingViewHolder(inflater.inflate(R.layout.item_meeting, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(MeetingViewHolder viewHolder, int position, final Meeting model) {
                final DatabaseReference meetingRef = getRef(position);

                final String meetingKey = meetingRef.getKey();
                viewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
                    intent.putExtra(MeetingDetailActivity.EXTRA_MEETING_KEY, meetingKey);
                    startActivity(intent);
                });

                viewHolder.bindToMeeting(model);
            }
        };

        //TODO some how filter options by date
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
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

    public abstract Query getQuery(DatabaseReference databaseReference);

    public abstract List<Meeting> getMeetings();
}
