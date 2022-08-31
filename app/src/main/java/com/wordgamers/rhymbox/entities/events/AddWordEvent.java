package com.wordgamers.rhymbox.entities.events;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.wordgamers.rhymbox.entities.Event;
import com.wordgamers.rhymbox.entities.EventType;
import com.wordgamers.rhymbox.entities.User;

public class AddWordEvent extends Event {

    private final String word;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AddWordEvent(User user, String word) {
        super(user);
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    @Override
    protected EventType getSignal() {
        return EventType.ADD_WORD;
    }
}
