package edu.illinois.cs465.jukebox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    boolean wasPartyCreated;
    boolean hasPartyStarted;

    Button buttonHost, buttonGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wasPartyCreated = false;
        hasPartyStarted = false;

        buttonHost = (Button) findViewById(R.id.button_host);
        buttonGuest = (Button) findViewById(R.id.button_guest);

        initListeners();
    }

    private void initListeners() {
        Context context = MainActivity.this;

        buttonHost.setOnClickListener(v -> {
            Intent goToHost;
            if (wasPartyCreated) {
                goToHost = new Intent(context, HostPartyActivity.class);
            } else {
                goToHost = new Intent(context, HostCreationActivity.class);
            }
            startActivity(goToHost);
        });

        buttonGuest.setOnClickListener(v -> {
            Intent goToGuest;
            if (hasPartyStarted) {
                goToGuest = new Intent(context, GuestPartyActivity.class);
            } else {
                goToGuest = new Intent(context, GuestSuggestionActivity.class);
            }
            startActivity(goToGuest);
        });
    }
}