package edu.illinois.cs465.jukebox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.illinois.cs465.jukebox.model.PartyInfo;
import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;

public class GuestVoteActivity extends AppCompatActivity {
    private ProgressBar progressTimeLeft;
    private FloatingActionButton buttonSkip;
    private MutableLiveData<Boolean> isActive;

    private ProgressCountdown countdown;

    private TextView songName, songNameVoteScreen;
    private TextView artistName, artistNameVoteScreen;
    private ImageView songCover;

    private ProgressBar songProgressBar;
    private TextView textCurrentTime, textTotalTime;
    private Handler handler = new Handler();

    private ViewFlipper viewFlipper;

    private DocumentReference partyReference;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicService.MusicServiceListener musicListener;

    private int maxMillis = 20000;

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


        String partyCode = getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "TCAE");
        partyReference = FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);

        songName = findViewById(R.id.textViewGuestDuringPartySongName);
        songName.setSelected(true);
        songNameVoteScreen = findViewById(R.id.label_song_title);
        songNameVoteScreen.setSelected(true);
        artistName = findViewById(R.id.textViewGuestDuringPartyArtistName);
        artistName.setSelected(true);
        artistNameVoteScreen = findViewById(R.id.label_song_artist);
        artistNameVoteScreen.setSelected(true);
        songCover = findViewById(R.id.imageViewGuestDuringPartyAlbumCover);

        songProgressBar = findViewById(R.id.progressBarGuestDuringPartySongTime);
        textCurrentTime = findViewById(R.id.guestDuringSongCurrentTime);
        textTotalTime = findViewById(R.id.guestDuringSongTotalTime);
        
        isActive = new MutableLiveData<>();
        isActive.observe(this, isEnabled -> buttonSkip.setEnabled(isEnabled));

        //temporary
        buttonSkip.setOnClickListener(view -> openKahoot());

        viewFlipper = findViewById(R.id.GuestVoteViewSwitcher);
        viewFlipper.setDisplayedChild(0);
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
                        updateSongInformation();
                        updateCountdown();

                        if (musicService.isMediaPlayerPrepared()) {
                            updater.run();
                        }
                    } else {
                        viewFlipper.setDisplayedChild(2);
                    }
                }

                @Override
                public void onMediaPlayerPrepared() {
                    updateSongInformation();
                    updateCountdown();
                    updater.run();
                }

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
                        if (viewFlipper.getDisplayedChild() != 0) {
                            viewFlipper.setDisplayedChild(0);
                        }
                        updateSongInformation();
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
                int choice = data.getIntExtra("vote", -1);

                if (choice >= 0) {
                    buttonSkip.setEnabled(false);
                    String newSongTitle = data.getStringExtra("songName");

                    if (musicBound) {
                        musicService.voteToSkipPlaySong(choice);
                    }

                    String snackbarText = "Vote for '" + newSongTitle + "' submitted!";
                    Snackbar.make(findViewById(R.id.guestVotingConstraintLayout), snackbarText, Snackbar.LENGTH_SHORT).show();

                } else {
                    String errorMessage = data.getStringExtra("songName");

                    if (choice == -1) {
                        buttonSkip.setEnabled(false); // Not needed but left for legacy
                        viewFlipper.setDisplayedChild(1);
                        Snackbar.make(findViewById(R.id.skipNotAllowedText), errorMessage, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(findViewById(R.id.guestVotingConstraintLayout), errorMessage, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void updateCountdown() {
        if (countdown != null) {
            countdown.cancel();
        }

        partyReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                maxMillis = ((Long) documentSnapshot.getData().get("skipTimer")).intValue() * 1000;
                updateCountdown();
            }
        });

        if (!musicService.isMediaPlayerPrepared()) {
            progressTimeLeft.setMax(maxMillis);
            progressTimeLeft.setProgress(maxMillis);
            return;
        }

        int currentPos = musicService.getPosition();
        int remainingMillis = maxMillis - currentPos;

        if (remainingMillis > 0) {
            isActive.setValue(true);
        } else {
            isActive.setValue(false); // Not needed but left for legacy
        }

        if (remainingMillis <= 0) {
            viewFlipper.setDisplayedChild(1);
        } else if (!musicService.isPlaying()) {
            progressTimeLeft.setMax(maxMillis);
            progressTimeLeft.setProgress(remainingMillis);
        } else {
            countdown = new ProgressCountdown(remainingMillis, 30, maxMillis, progressTimeLeft, isActive);
            countdown.start();

            countdown.registerListener(new ProgressCountdown.ProgressCountdownListener() {
                @Override
                public void onCountdownFinish() {
                    viewFlipper.setDisplayedChild(1);
                }
            });
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            if (musicService.isMediaPlayerPrepared() && musicService.isPlaying()) {
                updateSongTime();
                updateProgressBar();
            }
        }
    };

    // Make sure musicBound is true before calling this
    // The only exception is in onServiceConnected in the case that we are reconnecting to the service
    private void updateSongTime() {
        long currentDuration = Math.round(musicService.getPosition() / 1000.0f) * 1000L;
        long totalDuration = Math.round(musicService.getDuration() / 1000.0f) * 1000L;
        String currTime = millisecondsToTimer(currentDuration);
        String remTime = "-" + millisecondsToTimer(totalDuration - currentDuration);
        textCurrentTime.setText(currTime);
        textTotalTime.setText(remTime);
    }

    // Make sure musicBound is true before calling this
    // The only exception is in onServiceConnected in the case that we are reconnecting to the service
    private void updateProgressBar() {
        songProgressBar.setProgress((int) (((float) musicService.getPosition() / musicService.getDuration()) * 100 * 1000));
        handler.postDelayed(updater, 100);
    }

    // Make sure musicBound is true before calling this
    // The only exception is in onServiceConnected in the case that we are reconnecting to the service
    private void updateSongInformation() {
        SongEntry currSong = musicService.getCurrentSong();
        if (currSong != null) {
            songName.setText(getResources().getString(currSong.name));
            songNameVoteScreen.setText(getResources().getString(currSong.name));
            artistName.setText(getResources().getString(currSong.artist));
            artistNameVoteScreen.setText(getResources().getString(currSong.artist));
            songCover.setImageResource(currSong.image);
        }
    }

    private String millisecondsToTimer(long millis) {
        return new SimpleDateFormat("m:ss").format(new Date(millis));
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