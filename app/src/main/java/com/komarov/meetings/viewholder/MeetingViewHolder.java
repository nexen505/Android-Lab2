package com.komarov.meetings.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.komarov.meetings.R;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.model.StringDateTime;

/**
 * Created by Ilia on 19.11.2017.
 */

public class MeetingViewHolder extends RecyclerView.ViewHolder {

    private TextView mAuthorView, mParticipantsCountView,
            mTitleView, mPriorityView,
            mStartDateView, mStartTimeView,
            mEndDateView, mEndTimeView;

    public MeetingViewHolder(View itemView) {
        super(itemView);

        mAuthorView = itemView.findViewById(R.id.meeting_author);
        mParticipantsCountView = itemView.findViewById(R.id.meeting_participants_count);
        mTitleView = itemView.findViewById(R.id.meeting_title);
        mPriorityView = itemView.findViewById(R.id.meeting_priority);
        mStartDateView = itemView.findViewById(R.id.meeting_start_date);
        mStartTimeView = itemView.findViewById(R.id.meeting_start_time);
        mEndDateView = itemView.findViewById(R.id.meeting_end_date);
        mEndTimeView = itemView.findViewById(R.id.meeting_end_time);
    }

    public void bindToMeeting(Meeting meeting) {
        mAuthorView.setText(meeting.getAuthor());
        mParticipantsCountView.setText(String.valueOf(meeting.getParticipantsCount()));
        mTitleView.setText(meeting.getTitle());
        mPriorityView.setText(meeting.getPriority().toString());
        StringDateTime start = new StringDateTime(meeting.getStartDate()),
                end = new StringDateTime(meeting.getEndDate());
        mStartDateView.setText(start.getDate());
        mStartTimeView.setText(start.getTime());
        mEndDateView.setText(end.getDate());
        mEndTimeView.setText(start.getTime());
    }
}
