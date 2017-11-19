package com.komarov.meetings.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.komarov.meetings.model.Meeting;

/**
 * Created by Ilia on 19.11.2017.
 */

public class MeetingViewHolder extends RecyclerView.ViewHolder {

    /*public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;*/

    public MeetingViewHolder(View itemView) {
        super(itemView);

        /*titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);*/
    }

    public void bindToMessage(Meeting meeting, View.OnClickListener toGoClickListener, View.OnClickListener notToGoClickListener) {
       /* titleView.setText(meeting.title);
        authorView.setText(meeting.author);
        numStarsView.setText(String.valueOf(meeting.starCount));
        bodyView.setText(meeting.body);

        starView.setOnClickListener(starClickListener);*/
    }
}
