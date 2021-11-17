package edu.illinois.cs465.jukebox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GuestVoteActivity extends AppCompatActivity {
    ProgressBar progressTimeLeft;
    FloatingActionButton buttonSkip;
    MutableLiveData<Boolean> isActive;
    ProgressCountdown countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_vote);

        buttonSkip = findViewById(R.id.button_skip);
        progressTimeLeft = findViewById(R.id.progress_time_left);
        progressTimeLeft.setMax(10 * 30);

        // TODO These should be fetched from DB
        isActive = new MutableLiveData<>();
        isActive.observe(this, isEnabled -> buttonSkip.setEnabled(isEnabled));

        countdown = new ProgressCountdown(5000, 30, 5000, progressTimeLeft, isActive);
        countdown.start();
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