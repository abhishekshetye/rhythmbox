package com.wordgamers.rhymbox.processors;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wordgamers.rhymbox.entities.Event;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FirebaseEventsHandler implements EventsHandler{

    private final static String TAG = "FirebaseEventsHandler";
    private final FirebaseDatabase firebaseDatabaseRef;
    private ValueEventListener valueEventListener;
    private Query query;

    public FirebaseEventsHandler(FirebaseDatabase firebaseDatabaseRef) {
        this.firebaseDatabaseRef = firebaseDatabaseRef;
    }

    @Override
    public void sendEvent(List<String> hierarchy, Event event) {
        Log.d(TAG, "Adding event " + event + " to " + hierarchy);
        DatabaseReference databaseReference = firebaseDatabaseRef.getReference();
        for (String level : hierarchy) {
            databaseReference = databaseReference.child(level);
        }
        databaseReference.push().setValue(event);

//        firebaseDatabaseRef.getReference()
//        firebaseDatabaseRef.getReference("rooms")
//                .child(passCode)
//                .child("events")
//                .push()
//                .setValue(event);
        Log.d(TAG, "Added event");
    }

    @Override
    public void receiveEventsFromThisPointOn(List<String> hierarchy, Consumer<Object> doSomethingWithValue) {
        if (valueEventListener != null) {
            Log.w(TAG, "listenToEvents: One listener is already there in place. Cannot add another listener");
            return;
        }
        DatabaseReference databaseReference = firebaseDatabaseRef.getReference();
        for (String level : hierarchy) {
            databaseReference = databaseReference.child(level);
        }
        query = databaseReference
                .orderByKey()
                .limitToLast(1);
        valueEventListener = valueEventListener(doSomethingWithValue);
        query.addValueEventListener(valueEventListener);
    }

    @Override
    public void receiveEvents(List<String> hierarchy, Consumer<Object> doSomethingWithValue) {
        if (valueEventListener != null) {
            Log.w(TAG, "listenToEvents: One listener is already there in place. Cannot add another listener");
            return;
        }
        DatabaseReference databaseReference = firebaseDatabaseRef.getReference();
        for (String level : hierarchy) {
            databaseReference = databaseReference.child(level);
        }
        query = databaseReference
                .orderByKey();
        valueEventListener = valueEventListenerForAllEvents(doSomethingWithValue);
        query.addValueEventListener(valueEventListener);
    }

    @Override
    public void receiveEvents(List<String> hierarchy, ValueEventListener valueEventListener) {
        DatabaseReference databaseReference = firebaseDatabaseRef.getReference();
        for (String level : hierarchy) {
            databaseReference = databaseReference.child(level);
        }
        query = databaseReference
                .orderByKey();
        query.addValueEventListener(valueEventListener);
    }

    @Override
    public void checkIfValueExists(List<String> hierarchy, Consumer<Map<String, Object>> isPresent, Consumer<Object> error) {
        DatabaseReference databaseReference = firebaseDatabaseRef.getReference();
        for (String level : hierarchy) {
            databaseReference = databaseReference.child(level);
        }

        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object object = dataSnapshot.getValue();
                        Log.d(TAG, "Object retrieved " + object);
                        isPresent.accept((Map<String, Object>) object);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "DB error: " + databaseError.getMessage());
                        error.accept(databaseError);
                    }
                });
    }

    private ValueEventListener valueEventListener(Consumer<Object> doSomethingWithValue) {
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue();
                if (object != null) {
                    Log.d(TAG, "Object retrieved " + object);
                    doSomethingWithValue.accept(object);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError);
            }
        };
    }

    private ValueEventListener valueEventListenerForAllEvents(Consumer<Object> doSomethingWithValue) {
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Object object = ds.getValue();
                    Log.d(TAG, "Object retrieved " + object);
                    doSomethingWithValue.accept(object);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError);
            }
        };
    }
}
