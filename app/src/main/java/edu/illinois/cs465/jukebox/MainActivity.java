package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
                goToHost = new Intent(context, HostPartyOverviewBeforeActivity.class);
            } else {
                goToHost = new Intent(context, HostCreationActivity.class);
            }
            goToHost.setFlags(goToHost.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(goToHost);
        });

        buttonGuest.setOnClickListener(v -> {
            Intent intent = new Intent(context, GuestJoinPartyActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }
}