package com.komarov.meetings.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.komarov.meetings.MainActivity;
import com.komarov.meetings.MeetingDetailActivity;
import com.komarov.meetings.R;
import com.komarov.meetings.adapter.MeetingAdapter;
import com.komarov.meetings.model.Meeting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ilia on 25.11.2017.
 */

public class SearchFragment extends MeetingsListFragment {

    private RecyclerView mRecycler;
    private List<Meeting> mFilteredMeetings;
    private EditText mSearchText;
    private Button mSearchButton;

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
                mFilteredMeetings = ((MainActivity) getActivity()).getRecentMeetings().stream()
                        .filter(meeting -> meeting.getDescription() != null && meeting.getDescription().contains(searchText))
                        .collect(Collectors.toList());
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFilteredMeetings = Collections.emptyList();
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("meetings");
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

    @Override
    public List<Meeting> getMeetings() {
        return mFilteredMeetings;
    }
}
