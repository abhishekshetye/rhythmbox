package com.wordgamers.rhymbox.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.wordgamers.rhymbox.R;
import com.wordgamers.rhymbox.activities.WaitingRoomActivity;
import com.wordgamers.rhymbox.entities.User;
import com.wordgamers.rhymbox.entities.events.AddUserEvent;
import com.wordgamers.rhymbox.processors.EventsHandler;
import com.wordgamers.rhymbox.processors.FirebaseEventsHandler;

import java.util.Arrays;

public class JoiningCodeBottomDrawer extends BottomSheetDialogFragment {

    public static final String GAMES_DB = "games_db";
    private EventsHandler eventsHandler;
    private final String userId;

    public JoiningCodeBottomDrawer(String userId) {
        this.userId = userId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.joining_code_bottom_drawer, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://rhymbox-default-rtdb.europe-west1.firebasedatabase.app");
        eventsHandler = new FirebaseEventsHandler(database);

        EditText joiningCodeInput = view.findViewById(R.id.joiningCodeInput);
        view.findViewById(R.id.submitCodeButton).setOnClickListener(drawerView -> {
            String code = joiningCodeInput.getText().toString();

            eventsHandler.checkIfValueExists(Arrays.asList(GAMES_DB, code), valueFromDB -> {
                if (valueFromDB == null) {
                    Toast.makeText(getActivity(), "Sorry, such code does not exists, please double check the code.", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO limit 2 people for now
                    addUserInTheGame(code);
                    goToWaitingRoom(Integer.parseInt(code));
                }
            }, error -> Toast.makeText(getActivity(), "Sorry, something went wrong, please try again.", Toast.LENGTH_SHORT).show());
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addUserInTheGame(String joiningCode) {
        eventsHandler.sendEvent(Arrays.asList(GAMES_DB,
                        joiningCode,
                        "players"),
                new AddUserEvent(User.with("abhishek", userId)));
    }

    private void goToWaitingRoom(int joiningCode) {
        Intent intent = new Intent(getActivity(), WaitingRoomActivity.class);
        intent.putExtra("CODE", joiningCode);
        startActivity(intent);
    }
}
