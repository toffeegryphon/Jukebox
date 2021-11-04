package edu.illinois.cs465.jukebox;

import androidx.appcompat.app.AppCompatActivity;

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
        buttonHost.setOnClickListener(v -> {
            Intent goToHostCreation = new Intent(MainActivity.this, HostCreationActivity.class);
            startActivity(goToHostCreation);
        });

        buttonGuest.setOnClickListener(v -> {
            Intent goToGuest;
            if (hasPartyStarted) {
                goToGuest = new Intent(MainActivity.this, GuestPartyActivity.class);
            } else {
                goToGuest = new Intent(MainActivity.this, GuestSuggestionActivity.class);
            }
            startActivity(goToGuest);
        });
    }
}