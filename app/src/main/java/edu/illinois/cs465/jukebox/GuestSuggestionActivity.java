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

        addGuestSuggestionListItem(R.drawable.songcover_onandon, R.string.songcover_name1, R.string.songcover_artist1, R.string.songcover_url1);
        addGuestSuggestionListItem(R.drawable.songcover_heroestonight, R.string.songcover_name2, R.string.songcover_artist2, R.string.songcover_url2);
        addGuestSuggestionListItem(R.drawable.songcover_invincible, R.string.songcover_name3, R.string.songcover_artist3, R.string.songcover_url3);
        addGuestSuggestionListItem(R.drawable.songcover_myheart, R.string.songcover_name4, R.string.songcover_artist4, R.string.songcover_url4);
        addGuestSuggestionListItem(R.drawable.songcover_blank, R.string.songcover_name5, R.string.songcover_artist5, R.string.songcover_url5);
        addGuestSuggestionListItem(R.drawable.songcover_symbolism, R.string.songcover_name6, R.string.songcover_artist6, R.string.songcover_url6);
        addGuestSuggestionListItem(R.drawable.songcover_whywelose, R.string.songcover_name7, R.string.songcover_artist7, R.string.songcover_url7);
        addGuestSuggestionListItem(R.drawable.songcover_cradles, R.string.songcover_name8, R.string.songcover_artist8, R.string.songcover_url8);
        addGuestSuggestionListItem(R.drawable.songcover_shine, R.string.songcover_name9, R.string.songcover_artist9, R.string.songcover_url9);
        addGuestSuggestionListItem(R.drawable.songcover_invisible, R.string.songcover_name10, R.string.songcover_artist10, R.string.songcover_url10);

        submitButton = findViewById(R.id.guest_suggestion_submit_button);
        submitButton.setOnClickListener(v -> Toast.makeText(this.getApplicationContext(), "Submitted song suggestions!", Toast.LENGTH_SHORT).show());
    }

    public void addGuestSuggestionListItem(int image, int song_name, int artist, int url) {
        addGuestSuggestionListItem(image, song_name, artist, url, new Button(this));
    }

    public void addGuestSuggestionListItem(int image, int song_name, int artist, int url, Button button) {
        EntryItem item = new EntryItem(image, song_name, artist, url, button);
        entryList.add(item);
    }
}