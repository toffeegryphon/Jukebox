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
    String hostCode, guestCode;
    int hostMode;
    boolean guestHasStarted;

    Button buttonHost, buttonGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guestHasStarted = false;

        hostPreferences = getSharedPreferences("host", Context.MODE_PRIVATE);
        guestPreferences = getSharedPreferences("guest", Context.MODE_PRIVATE);

        hostMode = hostPreferences.getInt(PartyInfo.HOST_MODE, PartyInfo.HOST_DEFAULT);

        db = FirebaseFirestore.getInstance(); // TODO This should be elsewhere

        buttonHost = (Button) findViewById(R.id.button_host);
        buttonGuest = (Button) findViewById(R.id.button_guest);

        switch (hostMode) {
            case PartyInfo.HOST_DEFAULT:
                break;
            case PartyInfo.HOST_CREATED:
            case PartyInfo.HOST_STARTED:
                hostCode = hostPreferences.getString(PartyInfo.PARTY_CODE, "");
                db.collection("partyInfo").document(hostCode)
                        .get()
                        .addOnSuccessListener(document -> {
                            buttonHost.setText(document.getString("username"));
                        });
                break;
        }

        if (guestPreferences.contains(PartyInfo.PARTY_CODE)) {
            guestCode = guestPreferences.getString(PartyInfo.PARTY_CODE, "");
            db.collection("partyInfo").document(guestCode)
                    .get()
                    .addOnSuccessListener(document -> {
                        PartyInfo info = document.toObject(PartyInfo.class);
                        if (info != null && info.isHasStarted()) {
                            buttonGuest.setText(info.getUsername());
                            guestHasStarted = true;
                        }
                    });

        }

        initListeners();
    }

    private void initListeners() {
        Context context = MainActivity.this;

        buttonHost.setOnClickListener(v -> {
            Intent goToHost;
            switch (hostMode) {
                case PartyInfo.HOST_CREATED:
                    goToHost = new Intent(context, HostPartyOverviewBeforeActivity.class);
                    goToHost.putExtra(PartyInfo.PARTY_CODE, hostCode);
                    break;
                case PartyInfo.HOST_STARTED:
                    goToHost = new Intent(context, HostPartyOverviewDuringActivity.class);
                    goToHost.putExtra(PartyInfo.PARTY_CODE, hostCode);
                    break;
                default:
                    goToHost = new Intent(context, HostCreationActivity.class);
            }
            startActivity(goToHost);
        });

        buttonGuest.setOnClickListener(v -> {
            Intent intent;
            if (guestHasStarted) {
                intent = new Intent(context, GuestVoteActivity.class);
                intent.putExtra(PartyInfo.PARTY_CODE, guestCode);
            } else {
                intent = new Intent(context, GuestJoinPartyActivity.class);
            }
            startActivity(intent);
        });
    }
}