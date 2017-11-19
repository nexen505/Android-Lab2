package com.komarov.meetings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.model.User;
import com.komarov.meetings.utils.Utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.komarov.meetings.model.Meeting.DATE_DELIMITER;
import static com.komarov.meetings.model.Meeting.TIME_DELIMITER;

public class NewMeetingActivity extends BaseActivity {

    private static final String TAG = "NewMeetingActivity";
    private String[] priorities = Arrays.stream(Meeting.Priority.class.getEnumConstants())
            .map(Enum::name)
            .toArray(String[]::new);

    private static final String REQUIRED = "Required";
    private DatePicker startDatePicker, endDatePicker;
    private TimePicker startTimePicker, endTimePicker;
    private int startYear, startMonth, startDay, startHours, startMinutes;
    private int endYear, endMonth, endDay, endHours, endMinutes;
    private Meeting.Priority meetingPriority;

    private EditText mTitleField, mDescriptionField;
    private Spinner mSpinner;
    private TextView mStartDateTextView, mStartTimeTextView, mEndDateTextView, mEndTimeTextView;
    private FloatingActionButton mSubmitButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meeting);

        mStartDateTextView = findViewById(R.id.startDate);
        mStartTimeTextView = findViewById(R.id.startTime);
        mEndDateTextView = findViewById(R.id.endDate);
        mEndTimeTextView = findViewById(R.id.endTime);

        mTitleField = findViewById(R.id.field_title);
        mDescriptionField = findViewById(R.id.field_description);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSubmitButton = findViewById(R.id.fab_submit_meeting);
        mSubmitButton.setOnClickListener(v -> submitMeeting());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.stream(Meeting.Priority.class.getEnumConstants())
                        .map(Enum::name)
                        .toArray(String[]::new));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner = findViewById(R.id.prioritySpinner);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                meetingPriority = Meeting.Priority.valueOf(priorities[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        final Calendar c = Calendar.getInstance();

        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH) + 1;
        startDay = c.get(Calendar.DAY_OF_MONTH);
        startHours = c.get(Calendar.HOUR_OF_DAY);
        startMinutes = c.get(Calendar.MINUTE);

        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH) + 1;
        endDay = c.get(Calendar.DAY_OF_MONTH);
        endHours = c.get(Calendar.HOUR_OF_DAY);
        endMinutes = c.get(Calendar.MINUTE);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null || bundle.isEmpty()) {
            setDateToTextView(mStartDateTextView, String.valueOf(startDay), String.valueOf(startMonth), String.valueOf(startYear));
            setTimeToTextView(mStartTimeTextView, String.valueOf(startHours), String.valueOf(startMinutes));
            setDateToTextView(mEndDateTextView, String.valueOf(endDay), String.valueOf(endMonth), String.valueOf(endYear));
            setTimeToTextView(mEndTimeTextView, String.valueOf(endHours), String.valueOf(endMinutes));
        }
    }

    private void submitMeeting() {
        final Meeting meeting = validateData();

        if (meeting == null) {
            Log.e(TAG, "Meeting is unexpectedly null");
            Toast.makeText(NewMeetingActivity.this,
                    "Error: could not fetch user.",
                    Toast.LENGTH_SHORT).show();
        }
        setEditingEnabled(false);
        Toast.makeText(this, "Posting meeting...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewMeetingActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewMeeting(userId, meeting);
                        }

                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private Meeting validateData() {
        final String title = mTitleField.getText().toString();
        final String description = mDescriptionField.getText().toString();

        final String startDate = mStartDateTextView.getText().toString();
        final String startTime = mStartTimeTextView.getText().toString();
        final String endDate = mEndDateTextView.getText().toString();
        final String endTime = mEndTimeTextView.getText().toString();
        final Date start = Utils.fromString(String.format("%s %s", startDate, startTime), Meeting.DATE_TIME_PATTERN);
        final Date end = Utils.fromString(String.format("%s %s", endDate, endTime), Meeting.DATE_TIME_PATTERN);

        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return null;
        }

        if (TextUtils.isEmpty(description)) {
            mDescriptionField.setError(REQUIRED);
            return null;
        }

        if (meetingPriority == null) {
            return null;
        }

        final Meeting meeting = new Meeting();
        meeting.setTitle(title);
        meeting.setDescription(description);
        meeting.setPriority(meetingPriority);
        meeting.setStartDate(start);
        meeting.setEndDate(end);
        return meeting;
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mDescriptionField.setEnabled(enabled);
        mSpinner.setEnabled(enabled);
        mStartDateTextView.setEnabled(enabled);
        mStartTimeTextView.setEnabled(enabled);
        mEndDateTextView.setEnabled(enabled);
        mEndTimeTextView.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    private void writeNewMeeting(String userId, Meeting meeting) {
        String key = mDatabase.child("meetings").push().getKey();
        meeting.setUid(userId);
        Map<String, Object> meetingValues = meeting.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/meetings/" + key, meetingValues);
        childUpdates.put("/user-meetings/" + userId + "/" + key, meetingValues);

        mDatabase.updateChildren(childUpdates);
    }

    public void onChooseStartDate(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.date_dialog, null);
        startDatePicker = promptView.findViewById(R.id.datePicker);
        String startDate = ((TextView) view).getText().toString();
        String[] parts = startDate.split(DATE_DELIMITER);
        startYear = Integer.parseInt(parts[0]);
        startMonth = Integer.parseInt(parts[1]);
        startDay = Integer.parseInt(parts[2]);
        openDateDialog(startDay, startMonth, startYear, promptView, startDatePicker, mStartDateTextView);
    }

    public void onChooseEndDate(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.date_dialog, null);
        endDatePicker = promptView.findViewById(R.id.datePicker);
        String endDate = ((TextView) view).getText().toString();
        String[] parts = endDate.split(DATE_DELIMITER);
        endYear = Integer.parseInt(parts[0]);
        endMonth = Integer.parseInt(parts[1]);
        endDay = Integer.parseInt(parts[2]);
        openDateDialog(endDay, endMonth, endYear, promptView, endDatePicker, mEndDateTextView);
    }

    public void openDateDialog(final int d, final int m, final int y,
                               final View promptView, final DatePicker datePicker, final TextView dateTextView) {
        datePicker.init(y, m, d, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(R.string.button_ok, (dialog, id) -> {
                    String day1 = String.valueOf(datePicker.getDayOfMonth());
                    String month1 = String.valueOf(datePicker.getMonth() + 1);
                    String year1 = String.valueOf(datePicker.getYear());
                    setDateToTextView(dateTextView, day1, month1, year1);
                    dialog.cancel();
                })
                .setNegativeButton(R.string.button_cancel, (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void setDateToTextView(final TextView textView, final String day, final String month, final String year) {
        textView.setText(String.format(
                "%s%s%s%s%s",
                day,
                DATE_DELIMITER,
                month,
                DATE_DELIMITER,
                year));
    }

    private void setTimeToTextView(final TextView textView, final String hours, final String minutes) {
        textView.setText(String.format(
                "%s%s%s",
                hours,
                TIME_DELIMITER,
                minutes));
    }

    public void onChooseStartTime(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.time_dialog, null);
        startTimePicker = promptView.findViewById(R.id.timePicker);
        String startTime = ((TextView) view).getText().toString();
        String[] parts = startTime.split(TIME_DELIMITER);
        startHours = Integer.parseInt(parts[0]);
        startMinutes = Integer.parseInt(parts[1]);
        openTimeDialog(startHours, startMinutes, promptView, startTimePicker, mStartTimeTextView);
    }

    public void onChooseEndTime(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.time_dialog, null);
        endTimePicker = promptView.findViewById(R.id.timePicker);
        String endTime = ((TextView) view).getText().toString();
        String[] parts = endTime.split(TIME_DELIMITER);
        endHours = Integer.parseInt(parts[0]);
        endMinutes = Integer.parseInt(parts[1]);
        openTimeDialog(endHours, endMinutes, promptView, endTimePicker, mEndTimeTextView);
    }

    public void openTimeDialog(final int h, final int m,
                               final View promptView, final TimePicker timePicker, final TextView timeTextView) {
        timePicker.setHour(h);
        timePicker.setMinute(m);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton(R.string.button_ok, (dialog, id) -> {
                    String hour = String.valueOf(timePicker.getHour());
                    String minute = String.valueOf(timePicker.getMinute());
                    setTimeToTextView(timeTextView, hour, minute.length() < 2 ? "0" + minute : minute);
                    dialog.cancel();
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
