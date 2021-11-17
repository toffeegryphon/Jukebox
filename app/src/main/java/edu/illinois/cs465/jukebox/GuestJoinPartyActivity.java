package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class GuestJoinPartyActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private SharedPreferences guestPreferences;

    Button joinButton;
    EditText editCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_join_party);

        db = FirebaseFirestore.getInstance(); // TODO This should be elsewhere

        guestPreferences = getSharedPreferences("guest", Context.MODE_PRIVATE);

        joinButton = findViewById(R.id.guest_join_party_button);
        editCode = findViewById(R.id.edit_code);

        if (guestPreferences.contains(PartyInfo.PARTY_CODE)) {
            editCode.setText(guestPreferences.getString(PartyInfo.PARTY_CODE, ""));
        }

        joinButton.setOnClickListener(v -> db.collection("partyInfo")
                .document(editCode.getText().toString())
                .get()
                .addOnSuccessListener(doc -> {
                    PartyInfo info = doc.toObject(PartyInfo.class);

                    if (info == null) {
                        Toast.makeText(getApplicationContext(), "Invalid Code!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(
                            this.getApplicationContext(),
                            (Objects.requireNonNull(info).isHasStarted()) ? GuestVoteActivity.class : GuestSuggestionActivity.class
                    );
                    intent.putExtra(PartyInfo.PARTY_CODE, info.getPartyCode());
                    getSharedPreferences("guest", MODE_PRIVATE).edit()
                            .putString(PartyInfo.PARTY_CODE, info.getPartyCode())
                            .apply();
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Log.d("TESTING", e.getMessage())));
    }
}