package com.wordgamers.rhymbox.entities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;

public abstract class Event {

    private User user;
    private String createdEventTS;
    private String signalType;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected Event(User user) {
        this.user = user;
        createdEventTS = Instant.now().toString();
        signalType =  getSignal().name();
    }

    protected abstract EventType getSignal();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedEventTS() {
        return createdEventTS;
    }

    public void setCreatedEventTS(String createdEventTS) {
        this.createdEventTS = createdEventTS;
    }

    public String getSignalType() {
        return signalType;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }
}