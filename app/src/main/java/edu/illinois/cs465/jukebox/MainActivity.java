package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class MainActivity extends AppCompatActivity {

    SharedPreferences hostPreferences, guestPreferences;
    boolean wasPartyCreated;
    boolean hasPartyStarted;

    Button buttonHost, buttonGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostPreferences = getSharedPreferences("host", Context.MODE_PRIVATE);

        wasPartyCreated = hostPreferences.getBoolean(PartyInfo.IS_CREATED, false);
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
                String code = hostPreferences.getString(PartyInfo.PARTY_CODE, "");
                Log.d("TESTING", code);
                goToHost.putExtra(PartyInfo.PARTY_CODE, code);
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