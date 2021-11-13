package edu.illinois.cs465.jukebox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HostPartyOverviewPostActivity extends AppCompatActivity {

    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_overview_post);

        doneButton = findViewById(R.id.host_post_party_done_button);
        doneButton.setOnClickListener(v -> startActivity(new Intent(this.getApplicationContext(), MainActivity.class)));
    }
}