package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    SharedPreferences hostPreferences, guestPreferences;
    String hostCode;
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

        db = FirebaseFirestore.getInstance(); // TODO This should be elsewhere

        buttonHost = (Button) findViewById(R.id.button_host);
        buttonGuest = (Button) findViewById(R.id.button_guest);

        if (wasPartyCreated) {
            hostCode = hostPreferences.getString(PartyInfo.PARTY_CODE, "");
            db.collection("partyInfo").document(hostCode)
                    .get()
                    .addOnSuccessListener(document -> {
                        buttonHost.setText(document.getString("username"));
                    });
        }

        initListeners();
    }

    private void initListeners() {
        Context context = MainActivity.this;

        buttonHost.setOnClickListener(v -> {
            Intent goToHost;
            if (wasPartyCreated) {
                goToHost = new Intent(context, HostPartyOverviewBeforeActivity.class);
                goToHost.putExtra(PartyInfo.PARTY_CODE, hostCode);
            } else {
                goToHost = new Intent(context, HostCreationActivity.class);
            }
            startActivity(goToHost);
        });

        buttonGuest.setOnClickListener(v -> {
            Intent intent = new Intent(context, GuestJoinPartyActivity.class);
            startActivity(intent);
        });
    }
}