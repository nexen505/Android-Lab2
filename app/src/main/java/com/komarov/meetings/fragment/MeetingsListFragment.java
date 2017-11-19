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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.komarov.meetings.MeetingDetailActivity;
import com.komarov.meetings.R;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.viewholder.MeetingViewHolder;


public abstract class MeetingsListFragment extends Fragment {

    private static final String TAG = "MeetingsListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Meeting, MeetingViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public MeetingsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_meetings, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.meetings_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Meeting>()
                .setQuery(postsQuery, Meeting.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Meeting, MeetingViewHolder>(options) {

            @Override
            public MeetingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MeetingViewHolder(inflater.inflate(R.layout.item_meeting, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(MeetingViewHolder viewHolder, int position, final Meeting model) {
                final DatabaseReference postRef = getRef(position);

                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
                    intent.putExtra(MeetingDetailActivity.EXTRA_MEETING_KEY, postKey);
                    startActivity(intent);
                });

                viewHolder.bindToMessage(model, view -> {
                    DatabaseReference globalPostRef = mDatabase.child("meetings").child(postRef.getKey());
                    DatabaseReference userPostRef = mDatabase.child("user-meetings").child(model.getUid()).child(postRef.getKey());

                    toGoClicked(globalPostRef);
                    toGoClicked(userPostRef);
                }, view -> {
                    DatabaseReference globalPostRef = mDatabase.child("meetings").child(postRef.getKey());
                    DatabaseReference userPostRef = mDatabase.child("user-meetings").child(model.getUid()).child(postRef.getKey());

                    notToGoClicked(globalPostRef);
                    notToGoClicked(userPostRef);
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    private void toGoClicked(DatabaseReference postRef) {
        toDecideClicked(postRef, true);
    }

    private void notToGoClicked(DatabaseReference postRef) {
        toDecideClicked(postRef, false);
    }

    private void toDecideClicked(DatabaseReference postRef, boolean decision) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Meeting p = mutableData.getValue(Meeting.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (decision) p.addParticipant(getUid());
                else p.removeParticipant(getUid());

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
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

    public abstract String getUid();

    public abstract Query getQuery(DatabaseReference databaseReference);
}
