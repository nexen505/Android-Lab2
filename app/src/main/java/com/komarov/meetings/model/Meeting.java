package com.komarov.meetings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Ilia on 11.11.2017.
 */

public class Meeting implements Serializable {

    public enum Priority {
        PLANNED, URGENT, POSSIBLE
    }

    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<Participant> participants;
    private Priority priority;

    public Meeting() {
    }

    public Meeting(String title, String description, Date startDate, Date endDate, List<Participant> participants, Priority priority) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participants = participants;
        this.priority = priority;
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

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
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
