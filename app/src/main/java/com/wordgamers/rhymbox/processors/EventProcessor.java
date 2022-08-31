package com.wordgamers.rhymbox.processors;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.wordgamers.rhymbox.entities.EventType;

import java.util.Map;
import java.util.function.Consumer;

public class EventProcessor {

    private static final String TAG = "EventProcessor";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void processEvent(Map<String, Object> mapEvent, Map<EventType, Consumer<Map<String, Object>>> eventHandlerMap) {

        Map<String, Object> event = (Map<String, Object>) mapEvent.entrySet().stream().findAny().get().getValue();
        EventType eventType = EventType.valueOf((String) event.get("signalType"));

        if (!eventHandlerMap.containsKey(eventType)) {
            Log.e(TAG, "processEvent: Error occurred. No handler configured for event " + eventType);
            return;
        }

        eventHandlerMap.get(eventType).accept(event);
    }


}