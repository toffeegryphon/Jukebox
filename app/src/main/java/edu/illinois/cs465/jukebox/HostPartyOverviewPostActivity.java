package edu.illinois.cs465.jukebox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class HostPartyOverviewPostActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_overview_post);

        db = FirebaseFirestore.getInstance(); // TODO This should be elsewhere

        if (getIntent().hasExtra(PartyInfo.PARTY_CODE)) {
            String partyCode = getIntent().getStringExtra(PartyInfo.PARTY_CODE);
            db.collection("partyInfo").document(partyCode)
                    .update("hasStarted", false)
                    .addOnSuccessListener(unused -> Log.d("TESTING", "STARTED!"))
                    .addOnFailureListener(e -> Log.d("TESTING", e.getMessage()));
        }

        doneButton = findViewById(R.id.host_post_party_done_button);
        doneButton.setOnClickListener(v -> startActivity(new Intent(this.getApplicationContext(), MainActivity.class)));
    }
}