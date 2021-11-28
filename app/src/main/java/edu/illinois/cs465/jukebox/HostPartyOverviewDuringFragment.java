package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class HostPartyOverviewDuringFragment extends Fragment {
    private DocumentReference partyReference;

    View view;
    ImageView playPauseIcon;
    TextView songName;
    TextView artistName;

    ProgressBar songProgressBar;
    TextView textCurrentTime, textTotalTime;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();

    public HostPartyOverviewDuringFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String partyCode = requireActivity().getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "AAAA");
        partyReference = FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_host_party_overview_during, container, false);

        // Setup start party button
        playPauseIcon = (ImageView) view.findViewById(R.id.imageViewHostDuringPartyPlayPauseButton);
        playPauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Replace if statements with actually checking if music is playing or paused
                if (mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    playPauseIcon.setImageResource(R.drawable.ic_host_play_button);
                } else {
                    mediaPlayer.start();
                    playPauseIcon.setImageResource(R.drawable.ic_host_pause_button);
                    updateProgressBar();
                }
            }
        });

        songName = view.findViewById(R.id.textViewHostDuringPartySongName);
        songName.setSelected(true);

        artistName = view.findViewById(R.id.textViewHostDuringPartyArtistName);
        artistName.setSelected(true);

        songProgressBar = view.findViewById(R.id.progressBarHostDuringPartySongTime);
        textCurrentTime = view.findViewById(R.id.songCurrentTime);
        textTotalTime = view.findViewById(R.id.songTotalTime);
        mediaPlayer = ((HostPartyOverviewDuringActivity) requireActivity()).mediaPlayer;

        // updateSong();

        updater.run();
        updateProgressBar();

        // TODO: Make skip button actually skips the current song
        return view;
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

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateProgressBar();

            long currentDuration = Math.round(mediaPlayer.getCurrentPosition() / 1000.0f) * 1000L;
            long totalDuration = Math.round(mediaPlayer.getDuration() / 1000.0f) * 1000L;
            String currTime = millisecondsToTimer(currentDuration);
            String remTime = "-" + millisecondsToTimer(totalDuration - currentDuration);
            textCurrentTime.setText(currTime);
            textTotalTime.setText(remTime);
        }
    };

    private void updateProgressBar() {
        if (mediaPlayer.isPlaying()) {
            songProgressBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100 * 1000));
            handler.post(updater);
        }
    }

    private String millisecondsToTimer(long millis) {
        return new SimpleDateFormat("m:ss").format(new Date(millis));
    }
}