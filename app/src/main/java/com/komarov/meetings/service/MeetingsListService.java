package com.komarov.meetings.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.komarov.meetings.MainActivity;
import com.komarov.meetings.R;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class MeetingsListService extends IntentService {
    public static final String ACTION_LOAD_DATA = "com.komarov.meetings.service.action.ACTION_LOAD_DATA";
    public static final String ACTION_CHECK_DATA = "com.komarov.meetings.service.action.ACTION_CHECK_DATA";

    public static final String USER_ID = "com.komarov.meetings.service.extra.USER_ID";
    public static final String TO_NOTIFY = "com.komarov.meetings.service.extra.TO_NOTIFY";
    public static final String NETWORK = "NETWORK";

    private List<Meeting> myMeetings = new ArrayList<>(), recentMeetings = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private MeetingListListener meetingListener;

    public MeetingsListService() {
        super("MeetingsListService");
    }

    public static void startActionLoad(Context context, String userId, boolean toNotify) {
        Intent intent = new Intent(context, MeetingsListService.class);
        intent.setAction(ACTION_LOAD_DATA);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(TO_NOTIFY, toNotify);
        context.startService(intent);
    }

    public static void startActionCheck(Context context, String userId, boolean toNotify) {
        Intent intent = new Intent(context, MeetingsListService.class);
        intent.setAction(ACTION_LOAD_DATA);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(TO_NOTIFY, toNotify);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD_DATA.equals(action)) {
                final String userId = intent.getStringExtra(USER_ID);
                final boolean toNotify = intent.getBooleanExtra(TO_NOTIFY, false);
                handleActionLoad(userId, toNotify);
            } else if (ACTION_CHECK_DATA.equals(action)) {
                final String userId = intent.getStringExtra(USER_ID);
                final boolean toNotify = intent.getBooleanExtra(TO_NOTIFY, false);
                handleActionCheck(userId, toNotify);
            }
        }
    }

    private void handleActionLoad(final String userId, final boolean toNotify) {
        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            myMeetings = new ArrayList<>();
            recentMeetings = new ArrayList<>();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            meetingListener = new MeetingListListener(db, userId, toNotify);
            db.addValueEventListener(meetingListener); //TODO there's 'permission denied' error
        } else {
            responseIntent.setAction(ACTION_LOAD_DATA);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK, false);
            responseIntent.putExtra(MainActivity.MY_MEETINGS_KEY, (Serializable) myMeetings);
            responseIntent.putExtra(MainActivity.RECENT_MEETINGS_KEY, (Serializable) recentMeetings);
            sendBroadcast(responseIntent);
            stopSelf();
        }
    }

    public class MeetingListListener implements ValueEventListener {
        private DatabaseReference databaseReference;
        private String userId;
        private boolean toNotify;

        MeetingListListener(DatabaseReference ref, String userId, boolean toNotify) {
            this.databaseReference = ref;
            this.userId = userId;
            this.toNotify = toNotify;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            try {
                Iterable<DataSnapshot> usersIterable = dataSnapshot.child(User.USERS_KEY).getChildren();
                Iterable<DataSnapshot> meetingsIterable = dataSnapshot.child(Meeting.MEETINGS_KEY).getChildren();
                Iterable<DataSnapshot> userMeetingsIterable = dataSnapshot.child(Meeting.USER_MEETINGS_KEY).getChildren();

                usersIterable.forEach(snapshot -> {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setKey(snapshot.getKey());
                        users.add(user);
                    }
                });

                List<Meeting> l = createMeetings(meetingsIterable);
                recentMeetings.addAll(l);

                Iterable<DataSnapshot> currentUserMeetingsIterable = null;
                Iterator<DataSnapshot> itr = userMeetingsIterable.iterator();
                while (itr.hasNext() && currentUserMeetingsIterable == null) {
                    DataSnapshot snapshot = itr.next();
                    if (Objects.equals(snapshot.getKey(), userId))
                        currentUserMeetingsIterable = snapshot.getChildren();
                }
                if (currentUserMeetingsIterable != null) {
                    l = createMeetings(userMeetingsIterable);
                    myMeetings.addAll(l.stream().filter(meeting -> Objects.equals(userId, meeting.getUid())).collect(Collectors.toList()));
                }
            } catch (Exception e) {
                Log.e("UpdateMeeting_E", e.getMessage());
            }
            databaseReference.removeEventListener(this);
            if (toNotify) {
                showNotification();
            }

            Intent responseIntent = new Intent();
            responseIntent.setAction(ACTION_LOAD_DATA);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK, false);
            responseIntent.putExtra(MainActivity.MY_MEETINGS_KEY, (Serializable) myMeetings);
            responseIntent.putExtra(MainActivity.RECENT_MEETINGS_KEY, (Serializable) recentMeetings);
            sendBroadcast(responseIntent);
            stopSelf();
        }

        private List<Meeting> createMeetings(Iterable<DataSnapshot> iterable) {
            final List<Meeting> result = new ArrayList<>();
            iterable.forEach(snapshot -> {
                Meeting meeting = snapshot.getValue(Meeting.class);
                if (meeting != null) {
                    meeting.setKey(snapshot.getKey());
                    if (meeting.getParticipants() != null)
                        meeting.setActiveParticipants(meeting.getParticipants().entrySet()
                                .stream()
                                .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                                .map(Map.Entry::getKey)
                                .sorted()
                                .collect(Collectors.toList()));
                    result.add(meeting);
                }
            });
            return result;
        }

        private void showNotification() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Notification.Builder builder = new Notification.Builder(getApplicationContext());

            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setTicker(getString(R.string.notification))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(getString(R.string.notification))
                    .setContentText(getString(R.string.update_notification_text));
            Notification notification = builder.build();

            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.notify(new Random().nextInt(), notification);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("Error:", databaseError.getMessage());
        }
    }

    private void handleActionCheck(final String userId, final boolean toNotify) {
        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            myMeetings = new ArrayList<>();
            recentMeetings = new ArrayList<>();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            meetingListener = new MeetingListListener(db, userId, toNotify);
            db.addValueEventListener(meetingListener);
        } else {
            responseIntent.setAction(ACTION_LOAD_DATA);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK, false);
            responseIntent.putExtra(MainActivity.MY_MEETINGS_KEY, (Serializable) myMeetings);
            responseIntent.putExtra(MainActivity.RECENT_MEETINGS_KEY, (Serializable) recentMeetings);
            sendBroadcast(responseIntent);
            stopSelf();
        }
    }


}
