package com.wordgamers.rhymbox.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.wordgamers.rhymbox.R;
import com.wordgamers.rhymbox.entities.EventType;
import com.wordgamers.rhymbox.processors.EventsHandler;
import com.wordgamers.rhymbox.processors.FirebaseEventsHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WaitingRoomActivity extends AppCompatActivity {

    private static final String GAMES_DB = "games_db";
    private static final String TAG = "WaitingRoomActivity";

    private TextView testArea;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        testArea = findViewById(R.id.testArea);

        int joiningCode = getIntent().getExtras().getInt("CODE");
        subscribeToJoiningEvents(joiningCode);

        TextView pinCodeText = findViewById(R.id.pinCodeText);
        pinCodeText.setText("Ask others to join the game using the pin : " + joiningCode);

        boolean isAdmin = getIntent().getBooleanExtra("ADMIN", false);
        if (!isAdmin) {
            findViewById(R.id.startGameButton).setVisibility(View.GONE);
        } else {
            findViewById(R.id.startGameButton).setOnClickListener(view -> {
                startGame(joiningCode);
            });
        }
    }

    private void startGame(int joiningCode) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("CODE", joiningCode);
        intent.putExtra("MY_TURN", true);
        intent.putExtra("USERNAME", "abhishek");
        intent.putExtra("USERID", "3942834");
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void subscribeToJoiningEvents(int joiningCode) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://rhymbox-default-rtdb.europe-west1.firebasedatabase.app");
        EventsHandler eventsHandler = new FirebaseEventsHandler(database);
        eventsHandler.receiveEvents(Arrays.asList(GAMES_DB,
                String.valueOf(joiningCode), "players"),
                this::handleJoiners);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleJoiners(Object object) {
        Map<String, Object> event = (HashMap<String, Object>) object;
        Log.d(TAG, "Event received " + event);
        EventType eventType = EventType.valueOf((String) event.get("signalType"));

        switch (eventType) {
            case ADD_USER:
                Map<String, Object> user = (Map<String, Object>) event.get("user");
                Log.d(TAG, "handleJoiners: user" + user);
                testArea.setText(testArea.getText() + " \n" + user.get("username"));
                break;
        }
    }

    private Consumer<Map<String, Object>> addUserConsumer() {
        return (addUserEvent) -> {
            Log.d(TAG, "addUserConsumer: Called add User consumer with userNames " + addUserEvent);
            Map<String, Object> event = (Map<String, Object>) addUserEvent.get("user");
//            adapter.addUser(User.with((String) event.get("username")));
//            adapter.notifyDataSetChanged();
        };
    }
}