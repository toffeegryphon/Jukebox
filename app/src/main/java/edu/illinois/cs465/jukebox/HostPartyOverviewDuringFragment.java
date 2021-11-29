package edu.illinois.cs465.jukebox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import edu.illinois.cs465.jukebox.model.PartyInfo;
import edu.illinois.cs465.jukebox.viewmodel.MusicService;

public class HostPartyOverviewDuringFragment extends Fragment implements MediaController.MediaPlayerControl {
    private DocumentReference partyReference;

    View view;
    ImageView playPauseIcon, prevSongIcon, nextSongIcon;
    TextView songName;
    TextView artistName;
    ImageView songCover;

    ProgressBar songProgressBar;
    TextView textCurrentTime, textTotalTime;
    Handler handler = new Handler();

    private ArrayList<EntryItem> songList;
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MediaController mediaController;
    private MusicService.Listener musicListener;

    public HostPartyOverviewDuringFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String partyCode = requireActivity().getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "AAAA");
        partyReference = FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);

        if(playIntent == null) {
            playIntent = new Intent(this.getContext(), MusicService.class);
            requireActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            requireActivity().startService(playIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_host_party_overview_during, container, false);

        playPauseIcon = (ImageView) view.findViewById(R.id.imageViewHostDuringPartyPlayPauseButton);
        if (musicBound && musicService.isPlaying()) {
            playPauseIcon.setImageResource(R.drawable.ic_host_pause_button);
        } else {
            playPauseIcon.setImageResource(R.drawable.ic_host_play_button);
        }

        prevSongIcon = view.findViewById(R.id.imageViewHostDuringPartyPreviousButton);
        nextSongIcon = view.findViewById(R.id.imageViewHostDuringPartySkipButton);

        songName = view.findViewById(R.id.textViewHostDuringPartySongName);
        songName.setSelected(true);
        artistName = view.findViewById(R.id.textViewHostDuringPartyArtistName);
        artistName.setSelected(true);
        songCover = view.findViewById(R.id.imageViewHostDuringPartyAlbumCover);

        songProgressBar = view.findViewById(R.id.progressBarHostDuringPartySongTime);
        textCurrentTime = view.findViewById(R.id.songCurrentTime);
        textTotalTime = view.findViewById(R.id.songTotalTime);

        songList = new ArrayList<>();

        // Initialize songs (they will be reused in MusicService.java)
        addSongListItem(R.drawable.songcover_onandon, R.string.songcover_name1, R.string.songcover_artist1, R.string.songcover_url1);
        addSongListItem(R.drawable.songcover_heroestonight, R.string.songcover_name2, R.string.songcover_artist2, R.string.songcover_url2);
        addSongListItem(R.drawable.songcover_invincible, R.string.songcover_name3, R.string.songcover_artist3, R.string.songcover_url3);
        addSongListItem(R.drawable.songcover_myheart, R.string.songcover_name4, R.string.songcover_artist4, R.string.songcover_url4);
        addSongListItem(R.drawable.songcover_blank, R.string.songcover_name5, R.string.songcover_artist5, R.string.songcover_url5);
        addSongListItem(R.drawable.songcover_symbolism, R.string.songcover_name6, R.string.songcover_artist6, R.string.songcover_url6);
        addSongListItem(R.drawable.songcover_whywelose, R.string.songcover_name7, R.string.songcover_artist7, R.string.songcover_url7);
        addSongListItem(R.drawable.songcover_cradles, R.string.songcover_name8, R.string.songcover_artist8, R.string.songcover_url8);
        addSongListItem(R.drawable.songcover_shine, R.string.songcover_name9, R.string.songcover_artist9, R.string.songcover_url9);
        addSongListItem(R.drawable.songcover_invisible, R.string.songcover_name10, R.string.songcover_artist10, R.string.songcover_url10);

        setController();

        initListeners();

        return view;
    }

    private void initListeners() {
        playPauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound && musicService.isPlaying()) {
                    handler.removeCallbacks(updater);
                    musicService.pausePlayer();
                    playPauseIcon.setImageResource(R.drawable.ic_host_play_button);
                } else {
                    musicService.startPlayer();
                    playPauseIcon.setImageResource(R.drawable.ic_host_pause_button);
                    updateProgressBar();
                }
            }
        });

        prevSongIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    musicService.playPrev();
                }
            }
        });

        nextSongIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    musicService.playNext();
                }
            }
        });
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            if (musicBound && musicService.isPlaying()) {
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
        EntryItem currSong = musicService.getCurrentSong();
        songName.setText(getResources().getString(currSong.name));
        artistName.setText(getResources().getString(currSong.artist));
        songCover.setImageResource(currSong.image);
    }

    private String millisecondsToTimer(long millis) {
        return new SimpleDateFormat("m:ss").format(new Date(millis));
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();

            if (musicService.getSongQueueSize() == 0) {
                musicService.setSongList(songList);
                musicService.playSong(true);
            } else {
                updateSongTime();
                updateProgressBar();
                updateSongInformation();

                if (musicService.isPlaying()) {
                    playPauseIcon.setImageResource(R.drawable.ic_host_pause_button);
                } else {
                    playPauseIcon.setImageResource(R.drawable.ic_host_play_button);
                }
            }

            musicListener = new MusicService.Listener() {
                @Override
                public void onMediaPlayerPrepared() {
                    updater.run();
                    updateSongInformation();
                }

                @Override
                public void onMediaPlayerPause() {
                    playPauseIcon.setImageResource(R.drawable.ic_host_play_button);
                }

                @Override
                public void onMediaPlayerUnpause() {
                    playPauseIcon.setImageResource(R.drawable.ic_host_pause_button);
                }

                @Override
                public void onMediaPlayerNewSong() {
                    if (musicBound) {
                        updateSongInformation();
                    }
                }
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

    public void addSongListItem(int image, int song_name, int artist, int url) {
        addSongListItem(image, song_name, artist, url, new Button(this.getContext()));
    }

    public void addSongListItem(int image, int song_name, int artist, int url, Button button) {
        EntryItem item = new EntryItem(image, song_name, artist, url, button);
        songList.add(item);
    }

    private void setController(){
        mediaController = new MediaController(requireActivity());

        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playSong(true);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playPrev();
            }
        });

        mediaController.setMediaPlayer(this);
        mediaController.setEnabled(true);
    }

    @Override
    public void start() {
        musicService.startPlayer();
    }

    @Override
    public void pause() {
        musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicService != null && musicBound && musicService.isPlaying()) {
            return musicService.getDuration();
        }
        else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (musicService != null && musicBound && musicService.isPlaying()) {
            return musicService.getPosition();
        }
        else {
            return 0;
        }
    }

    @Override
    public void seekTo(int i) {
        musicService.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        if (musicService != null && musicBound) {
            return musicService.isPlaying();
        }
        else {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onDestroy() {
        if (musicBound) {
            musicService.unregisterListener(musicListener);
        }
        super.onDestroy();
    }
    // TODO: Is this still needed?
//    private void updateSong() {
//        partyReference.addSnapshotListener((value, error) -> {
//            if (value != null) {
//                String currentSong = value.getString("currentSong");
//                if (!currentSong.equals(songName.getText().toString())) {
//                    songName.setText(currentSong);
//                }
//            }
//        });
//    }
}