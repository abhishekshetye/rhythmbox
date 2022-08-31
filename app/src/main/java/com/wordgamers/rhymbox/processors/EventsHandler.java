package com.wordgamers.rhymbox.processors;

import com.google.firebase.database.ValueEventListener;
import com.wordgamers.rhymbox.entities.Event;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface EventsHandler {

    void sendEvent(List<String> hierarchy, Event event);

    void receiveEventsFromThisPointOn(List<String> hierarchy, Consumer<Object> doSomethingWithValue);

    void receiveEvents(List<String> hierarchy, Consumer<Object> doSomethingWithValue);

    void receiveEvents(List<String> hierarchy, ValueEventListener valueEventListener);

    void checkIfValueExists(List<String> hierarchy, Consumer<Map<String, Object>> isPresent, Consumer<Object> error);

}
