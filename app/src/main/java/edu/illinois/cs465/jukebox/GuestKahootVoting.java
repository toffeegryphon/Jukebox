package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.illinois.cs465.jukebox.model.PartyInfo;

//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.Observer;


public class GuestKahootVoting extends AppCompatActivity {
    ProgressBar progressTimeLeft;
    MutableLiveData<Boolean> isActive;
    ProgressCountdown countdown;

    ImageButton kahootOne, kahootTwo, kahootThree, kahootFour;
    TextView titleOne, titleTwo, titleThree, titleFour;

    private DocumentReference partyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_voting_kahoot);

        long currentMillis = 5000;
        if (getIntent().hasExtra(ProgressCountdown.REMAINING_MILLIS)) {
            currentMillis = getIntent().getLongExtra(ProgressCountdown.REMAINING_MILLIS, 5000);
        }

        String partyCode = getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "AAAA");
        partyReference =  FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);

        progressTimeLeft = findViewById(R.id.time_skip);
        kahootOne = findViewById(R.id.one_kahoot);
        kahootTwo = findViewById(R.id.two_kahoot);
        kahootThree = findViewById(R.id.three_kahoot);
        kahootFour = findViewById(R.id.four_kahoot);

        titleOne = findViewById(R.id.title_one);
        titleTwo = findViewById(R.id.title_two);
        titleThree = findViewById(R.id.title_three);
        titleFour = findViewById(R.id.title_four);

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

        kahootOne.setOnClickListener(v-> {
            partyReference.update("currentSong", titleOne.getText().toString());
            openGuestDuring(1);
        });
        kahootTwo.setOnClickListener(v-> {
            partyReference.update("currentSong", titleTwo.getText().toString());
            openGuestDuring(2);
        });
        kahootThree.setOnClickListener(v-> {
            partyReference.update("currentSong", titleThree.getText().toString());
            openGuestDuring(3);
        });
        kahootFour.setOnClickListener(v-> {
            partyReference.update("currentSong", titleFour.getText().toString());
            openGuestDuring(4);
        });
    }

    public void openGuestDuring(int choice) {
        Intent intent = new Intent();
        intent.putExtra("vote", choice);
        setResult(RESULT_OK, intent);
        finish();
    }
}
