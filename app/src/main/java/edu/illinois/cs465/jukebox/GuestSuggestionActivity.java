package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GuestSuggestionActivity extends AppCompatActivity {

    ArrayList<EntryItem> entryList;

    RecyclerView recyclerView;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_suggestion);

        recyclerView = findViewById(R.id.guest_suggestion_recycler_view);
        RecyclerViewCustomEdgeDecorator decoration = new RecyclerViewCustomEdgeDecorator(0, 0, true, false);
        recyclerView.addItemDecoration(decoration);

        entryList = new ArrayList<EntryItem>();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, entryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for (int i = 0; i < 10; i++) {
            addGuestSuggestionListItem(R.drawable.ic_launcher_background, "Song " + i, "Artist " + i);
        }

        submitButton = findViewById(R.id.guest_suggestion_submit_button);
        submitButton.setOnClickListener(v -> Toast.makeText(this.getApplicationContext(), "Submitted song suggestions!", Toast.LENGTH_SHORT).show());
    }

    public void addGuestSuggestionListItem(int image, String song_name, String artist) {
        addGuestSuggestionListItem(image, song_name, artist, new Button(this));
    }

    public void addGuestSuggestionListItem(int image, String song_name, String artist, Button button) {
        EntryItem item = new EntryItem(image, song_name, artist, button);
        entryList.add(item);
    }
}