package com.wordgamers.rhymbox.entities.events;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.wordgamers.rhymbox.entities.Event;
import com.wordgamers.rhymbox.entities.EventType;
import com.wordgamers.rhymbox.entities.User;

public class ChangeTurnEvent extends Event {

    private final User nextUser;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ChangeTurnEvent(User user, User nextUser) {
        super(user);
        this.nextUser = nextUser;
    }

    public User getNextUser() {
        return nextUser;
    }

    @Override
    protected EventType getSignal() {
        return EventType.CHANGE_TURN;
    }

    @Override
    public String toString() {
        return "ChangeTurnEvent{" +
                "nextUser=" + nextUser +
                '}';
    }
}
