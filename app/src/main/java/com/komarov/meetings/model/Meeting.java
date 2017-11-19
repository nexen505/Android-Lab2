package com.komarov.meetings.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Ilia on 11.11.2017.
 */

@IgnoreExtraProperties
public class Meeting implements Serializable {

    public enum Priority {
        PLANNED, URGENT, POSSIBLE
    }

    private String uid;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private Map<String, Boolean> participants = new HashMap<>();
    private int participantsCount = 0;
    private Priority priority;
    public static final String
            DATE_DELIMITER = "/",
            TIME_DELIMITER = ":",
            DATE_PATTERN = "dd/MM/yyyy",
            TIME_PATTERN = "HH:mm",
            DATE_TIME_PATTERN = String.format("%s %s", DATE_PATTERN, TIME_PATTERN);

    public Meeting() {
    }

    public Meeting(String uid, String title, String description, Date startDate, Date endDate, Map<String, Boolean> participants, Priority priority) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participants = participants;
        this.priority = priority;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void addParticipant(String uid) {
        if (uid != null && !uid.isEmpty()) {
            if (participants.containsKey(uid)) {
                participants.remove(uid);
                participantsCount--;
            }
            participantsCount++;
            participants.put(uid, true);
        }

    }

    public void removeParticipant(String uid) {
        if (uid != null && !uid.isEmpty()) {
            if (participants.containsKey(uid)) {
                participants.remove(uid);
                participantsCount++;
            }
            participantsCount--;
            participants.put(uid, false);
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("description", description);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("participants", participants);
        result.put("participantsCount", participantsCount);
        result.put("priority", priority);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meeting)) return false;
        Meeting meeting = (Meeting) o;
        return Objects.equals(title, meeting.title) &&
                Objects.equals(description, meeting.description) &&
                Objects.equals(startDate, meeting.startDate) &&
                Objects.equals(endDate, meeting.endDate) &&
                Objects.equals(participants, meeting.participants) &&
                priority == meeting.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, startDate, endDate, participants, priority);
    }

}
