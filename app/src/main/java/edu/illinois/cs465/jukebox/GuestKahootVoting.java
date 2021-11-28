package edu.illinois.cs465.jukebox;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.Observer;


public class GuestKahootVoting extends AppCompatActivity {
    ProgressBar progressTimeLeft;
    MutableLiveData<Boolean> isActive;
    ProgressCountdown countdown;

    ImageButton kahootOne;
    ImageButton kahootTwo;
    ImageButton kahootThree;
    ImageButton kahootFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_voting_kahoot);

        long currentMillis = 5000;
        if (getIntent().hasExtra(ProgressCountdown.REMAINING_MILLIS)) {
            currentMillis = getIntent().getLongExtra(ProgressCountdown.REMAINING_MILLIS, 5000);
        }

        progressTimeLeft = findViewById(R.id.time_skip);
        kahootOne = findViewById(R.id.one_kahoot);
        kahootTwo = findViewById(R.id.two_kahoot);
        kahootThree = findViewById(R.id.three_kahoot);
        kahootFour = findViewById(R.id.four_kahoot);

        // TODO get DB info on albums to show and countdown time
        progressTimeLeft.setMax(10 * 30);

        isActive = new MutableLiveData<>();
        isActive.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEnabled) {
                if (isEnabled) {
                    kahootOne.setEnabled(isEnabled);
                    kahootTwo.setEnabled(isEnabled);
                    kahootThree.setEnabled(isEnabled);
                    kahootFour.setEnabled(isEnabled);
                } else {
                    openGuestDuring(0); // Time ran out
                }
            }
        });

        countdown = new ProgressCountdown(currentMillis, 30, 5000, progressTimeLeft, isActive);
        countdown.start();

        kahootOne.setOnClickListener(v-> openGuestDuring(1));
        kahootTwo.setOnClickListener(v-> openGuestDuring(2));
        kahootThree.setOnClickListener(v-> openGuestDuring(3));
        kahootFour.setOnClickListener(v-> openGuestDuring(4));
    }

    public void openGuestDuring(int choice) {
        Intent intent = new Intent();
        intent.putExtra("vote", choice);
        setResult(RESULT_OK, intent);
        finish();
    }
}
