package com.wordgamers.rhymbox;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.wordgamers.rhymbox.activities.WaitingRoomActivity;
import com.wordgamers.rhymbox.entities.User;
import com.wordgamers.rhymbox.entities.events.AddUserEvent;
import com.wordgamers.rhymbox.entities.events.CreateRoomEvent;
import com.wordgamers.rhymbox.processors.EventsHandler;
import com.wordgamers.rhymbox.processors.FirebaseEventsHandler;
import com.wordgamers.rhymbox.views.JoiningCodeBottomDrawer;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String GAMES_DB = "games_db";
    private EventsHandler eventsHandler;
    private String userName;
    private User currentUser;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = "Abhishek"; // TODO change this

        String userId = String.valueOf(createRandomUserId());
        SharedPreferences.Editor editor = getSharedPreferences("myprefs", MODE_PRIVATE).edit();
        editor.putString("USERNAME", userName);
        editor.putString("USERID", userId);
        editor.apply();

        currentUser = User.with(userName, userId);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://rhymbox-default-rtdb.europe-west1.firebasedatabase.app");
        eventsHandler = new FirebaseEventsHandler(database);

        findViewById(R.id.createButton).setOnClickListener(view -> {
            int joiningCode = createEntryInGamesDB(userId);
            goToWaitingRoomAsAdmin(joiningCode);
        });

        findViewById(R.id.joinButton).setOnClickListener(view -> {
            JoiningCodeBottomDrawer bottomDrawer = new JoiningCodeBottomDrawer(userId);
            bottomDrawer.show(getSupportFragmentManager(), "some tag");
        });
    }

    private void goToWaitingRoomAsAdmin(int joiningCode) {
        Intent intent = new Intent(this, WaitingRoomActivity.class);
        intent.putExtra("CODE", joiningCode);
        intent.putExtra("ADMIN", true);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int createEntryInGamesDB(String userId) {
        int joiningCode = createRandomJoiningNumber();
        eventsHandler.sendEvent(Arrays.asList(GAMES_DB,
                String.valueOf(joiningCode)),
                new CreateRoomEvent(currentUser));
        eventsHandler.sendEvent(Arrays.asList(GAMES_DB,
                        String.valueOf(joiningCode),
                        "players"),
                new AddUserEvent(currentUser));
        return joiningCode;
    }

    private int createRandomJoiningNumber() {
        int min = 0, max = 100000;
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private int createRandomUserId() {
        int min = 0, max = 1000000000;
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}