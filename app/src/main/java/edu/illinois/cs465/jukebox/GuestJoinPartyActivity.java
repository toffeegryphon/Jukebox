package edu.illinois.cs465.jukebox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GuestJoinPartyActivity extends AppCompatActivity {

    Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_join_party);

        joinButton = findViewById(R.id.guest_join_party_button);
        joinButton.setOnClickListener(v -> startActivity(new Intent(this.getApplicationContext(), GuestSuggestionActivity.class)));
    }
}