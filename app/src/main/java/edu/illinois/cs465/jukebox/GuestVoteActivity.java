package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class GuestVoteActivity extends AppCompatActivity {
    ProgressBar progressTimeLeft;
    FloatingActionButton buttonSkip;
    MutableLiveData<Boolean> isActive;
    ProgressCountdown countdown;

    TextView songName;
    TextView artistName;

    private DocumentReference partyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_vote);

        buttonSkip = findViewById(R.id.button_skip);
        progressTimeLeft = findViewById(R.id.progress_time_left);
        progressTimeLeft.setMax(10 * 30);

        songName = findViewById(R.id.label_song_title);
        songName.setSelected(true);

        String partyCode = getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "AAAA");
        partyReference = FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);

        artistName = findViewById(R.id.label_song_artist);
        artistName.setSelected(true);

        // TODO These should be fetched from DB
        isActive = new MutableLiveData<>();
        isActive.observe(this, isEnabled -> buttonSkip.setEnabled(isEnabled));

        startCountdown();

        //temporary
        buttonSkip.setOnClickListener(view -> openKahoot());
    }

    public void openKahoot() {
        Intent intent = new Intent(this, GuestKahootVoting.class);
        intent.putExtra(ProgressCountdown.REMAINING_MILLIS, countdown.getRemainingMillis());
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                buttonSkip.setEnabled(false);
                int choice = data.getIntExtra("vote", 0);
                switch (choice) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        Toast.makeText(this, "Vote submitted!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this, "Time ran out, no vote made.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startCountdown() {
        if (countdown != null) {
            countdown.cancel();
        }
        isActive.setValue(true);
        countdown = new ProgressCountdown(5000, 30, 5000, progressTimeLeft, isActive);
        countdown.start();
    }
}