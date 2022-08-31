package com.wordgamers.rhymbox.entities.events;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.wordgamers.rhymbox.entities.Event;
import com.wordgamers.rhymbox.entities.EventType;
import com.wordgamers.rhymbox.entities.User;

public class UserLeftEvent extends Event {

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected UserLeftEvent(User user) {
        super(user);
    }

    @Override
    protected EventType getSignal() {
        return EventType.USER_LEFT;
    }
}
