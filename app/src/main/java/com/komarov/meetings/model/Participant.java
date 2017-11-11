package com.komarov.meetings.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Ilia on 11.11.2017.
 */

public class Participant implements Serializable {

    private String name;
    private String position;

    public Participant() {
    }

    public Participant(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        Participant that = (Participant) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, position);
    }

}
