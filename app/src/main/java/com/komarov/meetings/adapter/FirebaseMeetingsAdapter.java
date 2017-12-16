package com.komarov.meetings.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.komarov.meetings.MeetingDetailActivity;
import com.komarov.meetings.R;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.viewholder.MeetingViewHolder;

/**
 * Created by Ilia on 16.12.2017.
 */

public class FirebaseMeetingsAdapter extends FirebaseRecyclerAdapter<Meeting, MeetingViewHolder> {

    FirebaseRecyclerOptions<Meeting> mOptions;
    Activity mActivity;

    public FirebaseMeetingsAdapter(FirebaseRecyclerOptions<Meeting> options, Activity activity) {
        super(options);
        mOptions = options;
        mActivity = activity;
    }

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
            Intent intent = new Intent(mActivity, MeetingDetailActivity.class);
            intent.putExtra(MeetingDetailActivity.EXTRA_MEETING_KEY, meetingKey);
            mActivity.startActivity(intent);
        });

        viewHolder.bindToMeeting(model);
    }
}
