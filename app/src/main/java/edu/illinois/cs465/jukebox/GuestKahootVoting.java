package edu.illinois.cs465.jukebox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import edu.illinois.cs465.jukebox.model.PartyInfo;

//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.Observer;


public class GuestKahootVoting extends AppCompatActivity {
    ProgressBar progressTimeLeft;
    MutableLiveData<Boolean> isActive;
    ProgressCountdown countdown;

    private DocumentReference partyReference;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicService.MusicServiceListener musicListener;
    private ArrayList<SongEntry> songList;
    private LinearLayout[] layouts;
    private ImageButton[] images;
    private TextView[] titles;
    private TextView[] artists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_voting_kahoot);

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

        String partyCode = getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "AAAA");
        partyReference =  FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);

        progressTimeLeft = findViewById(R.id.time_skip);

        layouts = new LinearLayout[4];
        layouts[0] = findViewById(R.id.kahoot_ll_one);
        layouts[1] = findViewById(R.id.kahoot_ll_two);
        layouts[2] = findViewById(R.id.kahoot_ll_three);
        layouts[3] = findViewById(R.id.kahoot_ll_four);

        images = new ImageButton[4];
        images[0] = findViewById(R.id.one_kahoot);
        images[1] = findViewById(R.id.two_kahoot);
        images[2] = findViewById(R.id.three_kahoot);
        images[3] = findViewById(R.id.four_kahoot);

        titles = new TextView[4];
        titles[0] = findViewById(R.id.title_one);
        titles[1] = findViewById(R.id.title_two);
        titles[2] = findViewById(R.id.title_three);
        titles[3] = findViewById(R.id.title_four);

        artists = new TextView[4];
        artists[0] = findViewById(R.id.artist_one);
        artists[1] = findViewById(R.id.artist_two);
        artists[2] = findViewById(R.id.artist_three);
        artists[3] = findViewById(R.id.artist_four);

        isActive = new MutableLiveData<>();
        isActive.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEnabled) {
                if (isEnabled) {
                    for (int i = 0; i < 4; i++) {
                        images[i].setEnabled(isEnabled);
                    }
                } else {
                    openGuestDuring(0, ""); // Time ran out
                }
            }
        });

        images[0].setOnClickListener(v -> {
            partyReference.update("currentSong", titles[0].getText().toString());
            openGuestDuring(1, titles[0].getText().toString());
        });
        images[1].setOnClickListener(v -> {
            partyReference.update("currentSong", titles[1].getText().toString());
            openGuestDuring(2, titles[1].getText().toString());
        });
        images[2].setOnClickListener(v -> {
            partyReference.update("currentSong", titles[2].getText().toString());
            openGuestDuring(3, titles[2].getText().toString());
        });
        images[3].setOnClickListener(v -> {
            partyReference.update("currentSong", titles[3].getText().toString());
            openGuestDuring(4, titles[3].getText().toString());
        });
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();

            musicListener = new MusicService.MusicServiceListener() {
                @Override
                public void onRegister(ArrayList<SongEntry> _songList) {
                    songList = _songList;
                    Log.d("SIZE:", String.valueOf(songList.size()));

                    if (!songList.isEmpty()) {
                        SongEntry currSong = musicService.getCurrentSong();
                        if (currSong != null) {
                            int added = 0;
                            int i = 0;
                            while(added < 4 && i < Math.max(songList.size(), 4)) {
                                if (i >= songList.size()) {
                                    layouts[added].setVisibility(View.INVISIBLE);
                                    i++;
                                    continue;
                                }

                                SongEntry currSongChoice = songList.get(i);
                                if (currSong == currSongChoice && added > 0) {
                                    i++;
                                    continue;
                                }

                                images[added].setImageResource(currSongChoice.image);
                                titles[added].setText(currSongChoice.name);
                                artists[added].setText(currSongChoice.artist);
                                added++;
                                i++;
                            }
                        }
                    } else {
                        for (int i = 0; i < 4; i++) {
                            layouts[i].setVisibility(View.INVISIBLE);
                        }
                    }

                    updateCountdown();
                }

                public void onMediaPlayerPrepared() { }

                @Override
                public void onMediaPlayerPause() {
                    updateCountdown();
                }

                @Override
                public void onMediaPlayerUnpause() {
                    updateCountdown();
                }

                public void onMediaPlayerNewSong() { }
                public void onQueueUpdate(ArrayList<SongEntry> songList) { }
            };

            musicService.registerListener(musicListener);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService.unregisterListener(musicListener);
            musicBound = false;
        }
    };

    private void updateCountdown() {
        if (countdown != null) {
            countdown.cancel();
        }
        isActive.setValue(true);

        // TODO: Get host settings skip timer "maxMillis"
        final long maxMillis = 15000;
        long currentPos = musicService.getPosition();
        long remainingMillis = maxMillis - currentPos;

        if (!musicService.isPlaying()) {
            progressTimeLeft.setMax((int) maxMillis);
            progressTimeLeft.setProgress((int) remainingMillis);
        } else {
            countdown = new ProgressCountdown(remainingMillis, 30, maxMillis, progressTimeLeft, isActive);
            countdown.start();
        }
    }

    public void openGuestDuring(int choice, String newSongTitle) {
        Intent intent = new Intent();
        intent.putExtra("vote", choice);
        intent.putExtra("songName", newSongTitle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
