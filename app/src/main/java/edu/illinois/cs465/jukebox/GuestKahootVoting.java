package edu.illinois.cs465.jukebox;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ImageButton;


public class GuestKahootVoting extends AppCompatActivity {
    ProgressBar skipTime;
    ImageButton kahootOne;
    ImageButton kahootTwo;
    ImageButton kahootThree;
    ImageButton kahootFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_voting_kahoot);

        skipTime = findViewById(R.id.time_skip);
        kahootOne = findViewById(R.id.one_kahoot);
        kahootTwo = findViewById(R.id.two_kahoot);
        kahootThree = findViewById(R.id.three_kahoot);
        kahootFour = findViewById(R.id.four_kahoot);

        // TODO get DB info on albums to show and countdown time
        skipTime.setMax(20);

        kahootOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGuestDuring();
            }
        });

        kahootTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGuestDuring();
            }
        });

        kahootThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGuestDuring();
            }
        });

        kahootFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGuestDuring();
            }
        });
    }

    public void openGuestDuring() {
        Intent intent = new Intent(this, GuestVoteActivity.class);
        startActivity(intent);
    }
}
