package com.komarov.meetings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.model.StringDateTime;

public class MeetingDetailActivity extends AppCompatActivity {
    private static final String TAG = "MeetingDetailActivity";
    public static final String EXTRA_MEETING_KEY = "meeting_key";

    private DatabaseReference mMeetingRef;
    private ValueEventListener mMeetingListener;
    private String mMeetingKey;

    private TextView mTitleView, mDescriptionView, mPriorityView,
            mStartDateView, mStartTimeView,
            mEndDateView, mEndTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);

        mMeetingKey = getIntent().getStringExtra(EXTRA_MEETING_KEY);
        if (mMeetingKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        mMeetingRef = FirebaseDatabase.getInstance().getReference()
                .child("meetings").child(mMeetingKey);

        mTitleView = findViewById(R.id.field_title);
        mDescriptionView = findViewById(R.id.field_description);
        mPriorityView = findViewById(R.id.field_priority);
        mStartDateView = findViewById(R.id.field_startDate);
        mStartTimeView = findViewById(R.id.field_startTime);
        mEndDateView = findViewById(R.id.field_endDate);
        mEndTimeView = findViewById(R.id.field_endTime);
    }

    @Override
    public void onStart() {
        super.onStart();

        mMeetingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Meeting meeting = dataSnapshot.getValue(Meeting.class);
                mTitleView.setText(meeting.getTitle());
                mDescriptionView.setText(meeting.getDescription());
                mPriorityView.setText(meeting.getPriority().toString());
                StringDateTime start = new StringDateTime(meeting.getStartDate()),
                        end = new StringDateTime(meeting.getEndDate());
                mStartDateView.setText(start.getDate());
                mStartTimeView.setText(start.getTime());
                mEndDateView.setText(end.getDate());
                mEndTimeView.setText(start.getTime());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadMeeting:onCancelled", databaseError.toException());
                Toast.makeText(MeetingDetailActivity.this, "Failed to load meeting.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mMeetingRef.addValueEventListener(mMeetingListener);

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mMeetingListener != null) {
            mMeetingRef.removeEventListener(mMeetingListener);
        }

    }
}
