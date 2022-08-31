package com.wordgamers.rhymbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wordgamers.rhymbox.R;
import com.wordgamers.rhymbox.entities.EventType;
import com.wordgamers.rhymbox.entities.User;
import com.wordgamers.rhymbox.entities.events.AddWordEvent;
import com.wordgamers.rhymbox.entities.events.ChangeTurnEvent;
import com.wordgamers.rhymbox.processors.EventsHandler;
import com.wordgamers.rhymbox.processors.FirebaseEventsHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private static final String GAMES_DB = "games_db";

    private boolean isMyTurn = false;
    private List<User> players;
    private User currentUser;
    private int joiningCode;

    private TextView timer, wordText;
    private EditText wordInput;
    private Button submitWordButton;

    private EventsHandler eventsHandler;
    private CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
        public void onTick(long millisUntilFinished) {
            timer.setText("seconds remaining: " + millisUntilFinished / 1000);
            // logic to set the EditText could go here
        }
        public void onFinish() {
            timer.setText("done!");
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initializeUser();

        players = new ArrayList<>();

        joiningCode = getIntent().getIntExtra("CODE", -1);
        isMyTurn = getIntent().getBooleanExtra("MY_TURN", false);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://rhymbox-default-rtdb.europe-west1.firebasedatabase.app");
        eventsHandler = new FirebaseEventsHandler(database);

        timer = findViewById(R.id.timer);
        wordText = findViewById(R.id.wordText);
        wordInput = findViewById(R.id.wordInput);
        submitWordButton = findViewById(R.id.submitWordButton);

        initializePlayers(joiningCode);

        if (isMyTurn) {
            startCountdown();
            showWord();
        }

        submitWordButton.setOnClickListener(view -> {

            if (!isMyTurn) {
                Toast.makeText(this, "Not your turn, be patient please", Toast.LENGTH_SHORT).show();
                return;
            }

            String word = wordInput.getText().toString();
            if (validationFailed(word)) {
                Toast.makeText(this, "Validation failed for the entered word", Toast.LENGTH_SHORT).show();
                return;
            }
            broadcastWordEvent(joiningCode, word);
            Optional<User> nextUser = findNextPlayer(currentUser.getId());
            Log.d(TAG, "onCreate: Next user is " + nextUser);
            nextUser.ifPresent(user -> sendChangeTurnEvent(user, joiningCode, currentUser));
            if (!nextUser.isPresent()) {
                Toast.makeText(this, "Something went wrong, maybe players left the game?", Toast.LENGTH_SHORT).show();
            }
        });

        eventsHandler.receiveEventsFromThisPointOn(Arrays.asList(GAMES_DB,
                        String.valueOf(joiningCode), "wordEvents"),
                        this::handleEvents);
    }

    private boolean validationFailed(String word) {
        return false;
    }

    private void initializeUser() {
        SharedPreferences prefs = getSharedPreferences("myprefs", MODE_PRIVATE);
        String userId = prefs.getString("USERID", "undefined");
        String username = prefs.getString("USERNAME", "undefined");
        currentUser = User.with(username, userId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendChangeTurnEvent(User nextUser, int code, User currentUser) {
        eventsHandler.sendEvent(Arrays.asList(GAMES_DB,
                String.valueOf(code), "wordEvents"),
                new ChangeTurnEvent(currentUser, nextUser));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Optional<User> findNextPlayer(String currentUserId) {
        // TODO Uncomment below
//        if (players.size() <= 1) {
//            return Optional.empty();
//        }
        Log.d(TAG, "findNextPlayer: " + players + " current user " + currentUser);
        for (int idx = 0; idx < players.size(); ++idx) {
            if (currentUserId.equals(players.get(idx).getId())) {
                int nextUserIdx = (idx + 1) % players.size();
                return Optional.of(players.get(nextUserIdx));
            }
        }
        return Optional.empty();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void broadcastWordEvent(int joiningCode, String word) {
        eventsHandler.sendEvent(Arrays.asList(GAMES_DB,
                String.valueOf(joiningCode), "wordEvents"),
                new AddWordEvent(currentUser, word));
    }

    private void showWord() {
        wordText.setText("Original word : handsome");
    }

    private void initializePlayers(int code) {
        if (players.size() != 0) {
            players.clear();
        }
        eventsHandler.receiveEvents(Arrays.asList(GAMES_DB,
                        String.valueOf(code), "players"),
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Object object = ds.getValue();
                            Map<String, Object> event = (HashMap<String, Object>) object;
                            Log.d(TAG, "Event received " + event);
                            EventType eventType = EventType.valueOf((String) event.get("signalType"));

                            switch (eventType) {
                                case ADD_USER:
                                    Map<String, Object> user = (Map<String, Object>) event.get("user");
                                    Log.d(TAG, "handleJoiners: user" + user);
                                    players.add(User.with((String) user.get("username"),
                                            (String) user.get("id")));
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(GameActivity.this, "Some error occurred ", Toast.LENGTH_SHORT).show();
                    }
                });
        // TODO Remove below code

//                obj -> {
//                    Map<String, Object> event = (HashMap<String, Object>) obj;
//                    Log.d(TAG, "Event received " + event);
//                    EventType eventType = EventType.valueOf((String) event.get("signalType"));
//
//                    switch (eventType) {
//                        case ADD_USER:
//                            Map<String, Object> user = (Map<String, Object>) event.get("user");
//                            Log.d(TAG, "handleJoiners: user" + user);
//                            players.add(User.with((String) user.get("username"),
//                                    (String) user.get("id")));
//                            break;
//                    }
//                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleEvents(Object object) {
        Map<String, Object> rawEvent = (HashMap<String, Object>) object;
        Map<String, Object> event = (Map<String, Object>) rawEvent.entrySet().stream().findAny().get().getValue();
        Log.d(TAG, "Event received " + event);
        EventType eventType = EventType.valueOf((String) event.get("signalType"));
        switch (eventType) {
            case ADD_WORD:
                wordText.setText(wordText.getText().toString() + "\n" + event.get("word"));
                break;
            case CHANGE_TURN:
                Map<String, Object> nextUser = (HashMap<String, Object>) event.get("nextUser");
                String nextUserId = (String) nextUser.get("id");
                if (Objects.equals(nextUserId, currentUser.getId())) {
                    Log.d(TAG, "handleEvents: It's  my turn now " + nextUserId);
                    isMyTurn = true;
                    startCountdown();
                    //showWord();
                }
                break;
            case USER_LEFT:
                Map<String, Object> leftUser = (HashMap<String, Object>) event.get("user");
                Toast.makeText(this, "User " + leftUser.get("username") + " left the game", Toast.LENGTH_SHORT).show();
                initializePlayers(joiningCode);
                break;
        }
    }

    private void startCountdown() {
        countDownTimer.start();
    }

    private void resetCountdown() {
        countDownTimer.cancel();
    }
}