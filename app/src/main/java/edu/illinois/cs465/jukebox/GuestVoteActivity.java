package edu.illinois.cs465.jukebox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class GuestVoteActivity extends AppCompatActivity {
    ProgressBar progressTimeLeft;
    FloatingActionButton buttonSkip;
    MutableLiveData<Boolean> isActive;

    ProgressCountdown countdown;
    private

    TextView songName;
    TextView artistName;

    private DocumentReference partyReference;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicService.MusicServiceListener musicListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_vote);

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

        buttonSkip = findViewById(R.id.button_skip);
        progressTimeLeft = findViewById(R.id.progress_time_left);

        songName = findViewById(R.id.label_song_title);
        songName.setSelected(true);

        String partyCode = getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "AAAA");
        partyReference = FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);

        artistName = findViewById(R.id.label_song_artist);
        artistName.setSelected(true);
        
        isActive = new MutableLiveData<>();
        isActive.observe(this, isEnabled -> buttonSkip.setEnabled(isEnabled));

        //temporary
        buttonSkip.setOnClickListener(view -> openKahoot());
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();

            musicListener = new MusicService.MusicServiceListener() {
                @Override
                public void onRegister(ArrayList<SongEntry> songList) {
                    SongEntry currSong = musicService.getCurrentSong();
                    if (currSong != null) {
                        songName.setText(getResources().getString(currSong.name));
                        artistName.setText(getResources().getString(currSong.artist));
                        updateCountdown();
                    }
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

                @Override
                public void onMediaPlayerNewSong() {
                    SongEntry currSong = musicService.getCurrentSong();
                    if (currSong != null) {
                        songName.setText(getResources().getString(currSong.name));
                        artistName.setText(getResources().getString(currSong.artist));
                        updateCountdown();
                    }
                }

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

    public void openKahoot() {
        Intent intent = new Intent(this, GuestKahootVoting.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                buttonSkip.setEnabled(false);
                int choice = data.getIntExtra("vote", 0);

                if (choice != 0) {
                    String newSongTitle = data.getStringExtra("songName");

                    if (musicBound) {
                        musicService.voteToSkipPlaySong(choice - 1);
                    }

                    String snackbarText = "Vote for '" + newSongTitle + "' submitted!";
                    Snackbar.make(findViewById(R.id.guestVotingConstraintLayout), snackbarText, Snackbar.LENGTH_SHORT).show();

                } else {
                    Snackbar.make(findViewById(R.id.guestVotingConstraintLayout), "You did not vote in time!", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateCountdown() {
        if (countdown != null) {
            countdown.cancel();
        }

        // TODO: Get host settings skip timer "maxMillis"
        final long maxMillis = 15000;
        long currentPos = musicService.getPosition();
        long remainingMillis = maxMillis - currentPos;

        if (remainingMillis > 0) {
            isActive.setValue(true);
        } else {
            isActive.setValue(false);
        }

        if (!musicService.isPlaying() || remainingMillis <= 0) {
            progressTimeLeft.setMax((int) maxMillis);
            progressTimeLeft.setProgress((int) remainingMillis);
        } else {
            countdown = new ProgressCountdown(remainingMillis, 30, maxMillis, progressTimeLeft, isActive);
            countdown.start();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (musicBound) {
            musicService.unregisterListener(musicListener);
        }

        super.onDestroy();
    }
}