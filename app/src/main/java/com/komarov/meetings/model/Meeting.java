package com.komarov.meetings.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Ilia on 11.11.2017.
 */

@IgnoreExtraProperties
public class Meeting implements Serializable {

    public static final String MEETINGS_KEY = "meetings", USER_MEETINGS_KEY = "user-meetings";

    public enum Priority {
        PLANNED, URGENT, POSSIBLE
    }

    private String key;
    private String uid;
    private String author;
    private String title;
    private String description;
    private long creationTime;
    private Date startDate;
    private Date endDate;
    private Map<String, String> participants = new HashMap<>();
    private List<String> activeParticipants = new ArrayList<>();
    private int participantsCount = 0;
    private Priority priority;
    public static final String
            DATE_DELIMITER = "/",
            TIME_DELIMITER = ":",
            DATE_PATTERN = "dd/MM/yyyy",
            TIME_PATTERN = "HH:mm",
            DATE_TIME_PATTERN = String.format("%s %s", DATE_PATTERN, TIME_PATTERN);

    public Meeting() {
        this.creationTime = new Date().getTime();
    }

    public Meeting(String uid, String author, String title, String description, Date startDate, Date endDate, Map<String, String> participants, Priority priority) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participants = participants;
        this.priority = priority;
        this.creationTime = new Date().getTime();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public long getCreationTime() {
        return creationTime;
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

    public Map<String, String> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, String> participants) {
        this.participants = participants;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addParticipant(String uid, String username) {
        if (uid != null && !uid.isEmpty()) {
            if (participants.containsKey(uid)) {
                participantsCount--;
                participants.remove(uid);
            }
            participants.put(uid, username);
            participantsCount++;
        }

    }

    public void removeParticipant(String uid) {
        if (uid != null && !uid.isEmpty()) {
            if (participants.containsKey(uid)) {
                participantsCount--;
                participants.remove(uid);
            }
        }
    }

    public List<String> getActiveParticipants() {
        if (participants == null) return Collections.emptyList();
        return participants.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .sorted()
                .collect(Collectors.toList());
//        return activeParticipants;
    }

    public void setActiveParticipants(List<String> l) {
        activeParticipants = l;
    }

    public boolean hasParticipant(String uid) {
        return getParticipants().containsKey(uid);
    }

    public int getParticipantsCount() {
        return getParticipants().size();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("description", description);
        result.put("creationTime", creationTime);
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
