package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;

public class GuestSuggestionActivity extends AppCompatActivity {

    HashMap<SongFragment, FrameLayout> fragmentTransactionList;
    LinearLayout fragmentLayout;
    int fragCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_suggestion);

        fragmentLayout = findViewById(R.id.guest_suggestion_linear_layout_fragment_list);

        fragmentTransactionList = new HashMap<SongFragment, FrameLayout>();
        for (int i = 0; i < 10; i++){
            addFragment();
        }
    }

    public void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SongFragment songFragment = new SongFragment("fragment" + fragCount, "fragment" + fragCount);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(fragCount);
        fragmentLayout.addView(frameLayout);
        fragmentTransactionList.put(songFragment, frameLayout);
        fragmentTransaction.add(frameLayout.getId(), songFragment, "fragment" + fragCount);
        fragmentTransaction.commit();
        fragCount++;
    }

    public void removeFragment(SongFragment del_fragment) {
        if (fragmentTransactionList.get(del_fragment) == null) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(del_fragment);
        fragmentTransactionList.remove(del_fragment);
        fragmentTransaction.commit();
    }
}