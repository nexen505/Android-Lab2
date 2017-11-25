package com.komarov.meetings.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.komarov.meetings.R;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.viewholder.MeetingViewHolder;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Ilia on 25.11.2017.
 */

public class MeetingAdapter extends RecyclerView.Adapter<MeetingViewHolder> {

    private List<Meeting> mMeetings;
    private Function<Meeting, View.OnClickListener> mListenerFunc;

    public MeetingAdapter(List<Meeting> meetings, Function<Meeting, View.OnClickListener> listenerFunc) {
        mMeetings = meetings;
        mListenerFunc = listenerFunc;
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new MeetingViewHolder(inflater.inflate(R.layout.item_meeting, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder viewHolder, int position) {
        final Meeting meetingRef = mMeetings.get(position);
        viewHolder.itemView.setOnClickListener(mListenerFunc.apply(meetingRef));
        viewHolder.bindToMeeting(meetingRef);
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }
}
