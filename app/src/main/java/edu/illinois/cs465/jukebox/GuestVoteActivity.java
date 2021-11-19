package edu.illinois.cs465.jukebox;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GuestVoteActivity extends AppCompatActivity {
    ProgressBar progressTimeLeft;
    FloatingActionButton buttonSkip;
    MutableLiveData<Boolean> isActive;
    ProgressCountdown countdown;

    TextView songName;
    TextView artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_vote);

        buttonSkip = findViewById(R.id.button_skip);
        progressTimeLeft = findViewById(R.id.progress_time_left);
        progressTimeLeft.setMax(10 * 30);

        songName = findViewById(R.id.label_song_title);
        songName.setSelected(true);

        artistName = findViewById(R.id.label_song_artist);
        artistName.setSelected(true);

        // TODO These should be fetched from DB
        isActive = new MutableLiveData<>();
        isActive.observe(this, isEnabled -> buttonSkip.setEnabled(isEnabled));

        countdown = new ProgressCountdown(5000, 30, 5000, progressTimeLeft, isActive);
        countdown.start();

        //temporary
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKahoot();
            }
        });
    }

    public void openKahoot() {
        Intent intent = new Intent(this, GuestKahootVoting.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                int choice = data.getIntExtra("vote", 0);
                buttonSkip.setEnabled(false);
            }
        }
    }

    private class ProgressCountdown extends CountDownTimer {
        ProgressBar bar;
        MutableLiveData<Boolean> isActive;
        long maxMillis;



        public ProgressCountdown(long currentMillis, long intervalMillis, long maxMillis, ProgressBar bar, MutableLiveData<Boolean> isActive) {
            super(currentMillis, intervalMillis);

            this.isActive = isActive;
            this.isActive.setValue(true);

            this.maxMillis = maxMillis;
            this.bar = bar;
            this.bar.setProgress((int) (currentMillis * bar.getMax() / maxMillis));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            bar.setProgress((int) (millisUntilFinished * bar.getMax() / maxMillis));
        }

        @Override
        public void onFinish() {
            bar.setProgress(0);
            isActive.setValue(false);
        }
    }
}