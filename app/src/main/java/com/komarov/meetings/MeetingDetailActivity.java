package com.komarov.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.model.StringDateTime;
import com.komarov.meetings.model.User;
import com.komarov.meetings.service.NetworkService;
import com.komarov.meetings.utils.Utils;

import java.util.List;

public class MeetingDetailActivity extends BaseActivity {
    private static final String TAG = "MeetingDetailActivity";
    public static final String EXTRA_MEETING_KEY = "meeting_key";
    public static final String EXTRA_MEETING = "meeting";

    private DatabaseReference mDatabase, mMeetingRef, mMeetingGlobalRef, mUserRef;
    private ValueEventListener mMeetingListener;

    private TextView mAuthorView, mParticipantsCountView,
            mTitleView, mDescriptionView, mPriorityView,
            mStartDateView, mStartTimeView,
            mEndDateView, mEndTimeView;
    private RelativeLayout mParticipantsLayout;

    private FloatingActionMenu fam;
    private FloatingActionButton fabEdit, fabDelete, fabVisit, fabLeave, fabAddContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);

        String mMeetingKey = getIntent().getStringExtra(EXTRA_MEETING_KEY);
        if (mMeetingKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_MEETING_KEY");
        }

        NetworkService.startActionCheckNetwork(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMeetingRef = mDatabase.child("meetings").child(mMeetingKey);
        mMeetingGlobalRef = mDatabase.child("user-meetings").child(getUid()).child(mMeetingKey);
        mUserRef = mDatabase.child("users").child(getUid());

        initializeViews();
        mParticipantsLayout = findViewById(R.id.layout_participants);
    }

    private void initializeViews() {
        mAuthorView = findViewById(R.id.meeting_author);
        mParticipantsCountView = findViewById(R.id.meeting_participants_count);
        mTitleView = findViewById(R.id.meeting_title);
        mDescriptionView = findViewById(R.id.meeting_description);
        mPriorityView = findViewById(R.id.meeting_priority);
        mStartDateView = findViewById(R.id.meeting_start_date);
        mStartTimeView = findViewById(R.id.meeting_start_time);
        mEndDateView = findViewById(R.id.meeting_end_date);
        mEndTimeView = findViewById(R.id.meeting_end_time);
    }

    private void initializeFabs(View.OnClickListener listener) {
        fam = findViewById(R.id.fab_menu);
        fam.setOnClickListener(view -> {
            if (fam.isOpened()) {
                fam.close(true);
            }
        });

        fabEdit = findViewById(R.id.edit_fab);
        fabDelete = findViewById(R.id.delete_fab);
        fabVisit = findViewById(R.id.visit_fab);
        fabLeave = findViewById(R.id.leave_fab);
        fabAddContacts = findViewById(R.id.contacts_fab);

        fabEdit.setOnClickListener(listener);
        fabDelete.setOnClickListener(listener);
        fabVisit.setOnClickListener(listener);
        fabLeave.setOnClickListener(listener);
        fabAddContacts.setOnClickListener(listener);

    }

    private void initializeFabs(Meeting meeting) {
        initializeFabs(onFabClick(meeting));
        if (fam.isOpened()) {
            fam.close(true);
        }
        filterFabs(meeting);
    }

    private View.OnClickListener onFabClick(Meeting meeting) {
        return view -> {
            if (view == fabEdit) {
                Intent intent = new Intent(this, NewMeetingActivity.class);
                meeting.setKey(mMeetingRef.getKey());
                intent.putExtra(NewMeetingActivity.SOURCE_MEETING_KEY, meeting);
                startActivity(intent);
            } else if (view == fabDelete) {
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setTitle(R.string.confirm_deletion);
                ad.setMessage(R.string.confirm_deletion_msg);
                ad.setPositiveButton(R.string.button_ok, (dialog, arg1) -> {
                    NetworkService.startActionCheckNetwork(this);
                    mMeetingGlobalRef.removeValue();
                    mMeetingRef.removeValue();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
                ad.setNegativeButton(R.string.button_cancel, (dialog, arg1) -> {
                });
                ad.show();
            } else if (view == fabVisit) {
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setTitle(R.string.confirm_visiting);
                ad.setMessage(R.string.confirm_visiting_msg);
                ad.setPositiveButton(R.string.button_ok, (dialog, arg1) -> {
                    visitClicked(mMeetingGlobalRef, mUserRef);
                    visitClicked(mMeetingRef, mUserRef);
                });
                ad.setNegativeButton(R.string.button_cancel, (dialog, arg1) -> {
                });
                ad.show();
            } else if (view == fabLeave) {
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setTitle(R.string.confirm_leaving);
                ad.setMessage(R.string.confirm_leaving_msg);
                ad.setPositiveButton(R.string.button_ok, (dialog, arg1) -> {
                    leaveClicked(mMeetingGlobalRef, mUserRef);
                    leaveClicked(mMeetingRef, mUserRef);
                });
                ad.setNegativeButton(R.string.button_cancel, (dialog, arg1) -> {
                });
                ad.show();
            } else {
                //TODO implement adding from contacts
            }
            fam.close(true);
        };
    }

    public void filterFabs(Meeting meeting) {
        if (meeting != null) {
            if (!getUid().equals(meeting.getUid())) {
                fabEdit.setVisibility(View.GONE);
                fabDelete.setVisibility(View.GONE);
            } else {
                fabEdit.setVisibility(View.VISIBLE);
                fabDelete.setVisibility(View.VISIBLE);
            }
            if (meeting.hasParticipant(getUid())) {
                fabVisit.setVisibility(View.GONE);
                fabLeave.setVisibility(View.VISIBLE);
            } else {
                fabVisit.setVisibility(View.VISIBLE);
                fabLeave.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        NetworkService.startActionCheckNetwork(this);
        mMeetingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Meeting meeting = dataSnapshot.getValue(Meeting.class);
                if (meeting != null) {
                    mAuthorView.setText(meeting.getAuthor());
                    mParticipantsCountView.setText(String.valueOf(meeting.getParticipantsCount()));
                    mTitleView.setText(meeting.getTitle());
                    mDescriptionView.setText(meeting.getDescription());
                    mPriorityView.setText(meeting.getPriority().toString());
                    StringDateTime start = new StringDateTime(meeting.getStartDate()),
                            end = new StringDateTime(meeting.getEndDate());
                    mStartDateView.setText(start.getDate());
                    mStartTimeView.setText(start.getTime());
                    mEndDateView.setText(end.getDate());
                    mEndTimeView.setText(start.getTime());
                    initializeActiveParticipants(meeting);
                    initializeFabs(meeting);
                }
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

    private void initializeActiveParticipants(Meeting meeting) {

        final List<String> activeParticipants = meeting.getActiveParticipants();
        if (activeParticipants != null && activeParticipants.size() > 0) {
            TextView view = new TextView(this);
            String participants = "Участники: ".concat(Utils.join(", ", activeParticipants));
            view.setGravity(Gravity.START);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setText(participants);
            mParticipantsLayout.addView(view, layoutParams);
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mMeetingListener != null) {
            mMeetingRef.removeEventListener(mMeetingListener);
        }

    }

    private void visitClicked(DatabaseReference meetingRef, DatabaseReference userRef) {
        toDecideClicked(meetingRef, userRef, true);
    }

    private void leaveClicked(DatabaseReference meetingRef, DatabaseReference userRef) {
        toDecideClicked(meetingRef, userRef, false);
    }

    private void toDecideClicked(DatabaseReference meetingRef, DatabaseReference userRef, boolean decision) {
        NetworkService.startActionCheckNetwork(this);
        userRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + getUid() + " is unexpectedly null");
                            Toast.makeText(MeetingDetailActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            meetingRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Meeting p = mutableData.getValue(Meeting.class);
                                    if (p == null) {
                                        return Transaction.success(mutableData);
                                    }

                                    if (decision) p.addParticipant(getUid(), user.getUsername());
                                    else p.removeParticipant(getUid());

                                    mutableData.setValue(p);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "meetingTransaction:onComplete:" + databaseError);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
}
